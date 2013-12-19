/*
 Copyright (c) 2012 Robert Foss, Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ru.truba.touchgallery.TouchView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import ru.truba.touchgallery.TouchView.Single2xDownloader.BitmapDisplayer;
import ru.truba.touchgallery.TouchView.Single2xDownloader.PhotosLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ImageView.ScaleType;

import com.zoomactivity.GridActivity;
import com.zoomactivity.PDFImageViewActivity;
import com.zoomactivity.R;

@SuppressLint("NewApi")
public class TouchImageView extends ImageView {

	// private static final String TAG = "Touch";
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	public static String url;
	static final long DOUBLE_PRESS_INTERVAL = 1200;
	static final float FRICTION = 0.9f;
	// We can be in one of these 4 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	static final int CLICK = 10;
	int mode = NONE;
	public boolean imagedownload;
	float redundantXSpace, redundantYSpace;
	float right, bottom, origWidth, origHeight, bmWidth, bmHeight;
	float width, height;
	PointF last = new PointF();
	PointF mid = new PointF();
	PointF start = new PointF();
	float[] m;
	float matrixX, matrixY;
	float saveScale = 1f;
	float minScale = 1f;
	float maxScale = 4f;
	float oldDist = 1f;
	public static Matrix ma;
	ExecutorService executorService;

	PointF lastDelta = new PointF(0, 0);
	float velocity = 0;

	long lastPressTime = 0, lastDragTime = 0;
	boolean allowInert = false;

	private Context mContext;
	private Timer mClickTimer;
	private OnClickListener mOnClickListener;
	private Object mScaleDetector;
	private Handler mTimerHandler = null;
	String firstImageUrl, secondImageUrl;
	public ProgressBar progressBar;
	public boolean isImageDownload;
	TouchImageView imageview;
	FileCache fileCache;
	Handler handler = new Handler();
	MotionEvent ra;
	float mscalefa;
	float midx, midy;
	float postx;
	float posty;

	// Scale mode on DoubleTap
	private boolean zoomToOriginalSize = false;

	public boolean isZoomToOriginalSize() {
		return this.zoomToOriginalSize;
	}

	public void setZoomToOriginalSize(boolean zoomToOriginalSize) {
		this.zoomToOriginalSize = zoomToOriginalSize;
	}

	public boolean onLeftSide = false, onTopSide = false, onRightSide = false,
			onBottomSide = false;

	public TouchImageView(Context context) {
		super(context);
		super.setClickable(true);
		this.mContext = context;
		init();
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setClickable(true);
		this.mContext = context;
		init();
	}

	public void setUrl(String firstImageUrl, String secondImageUrl,
			ProgressBar progressBar, TouchImageView imageview,
			boolean isImageDownload) {
		this.firstImageUrl = firstImageUrl;
		this.secondImageUrl = secondImageUrl;
		this.progressBar = progressBar;
		this.imageview = imageview;
		this.isImageDownload = isImageDownload;
	}

	protected void init() {
		mTimerHandler = new TimeHandler(this);
		matrix.setTranslate(1f, 1f);
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
		if (Build.VERSION.SDK_INT >= 8) {
			mScaleDetector = new ScaleGestureDetector(mContext,
					new ScaleListener());

		}
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent rawEvent) {
				WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

				if (mScaleDetector != null) {
					((ScaleGestureDetector) mScaleDetector)
							.onTouchEvent(rawEvent);
					ra = rawEvent;
				}

				fillMatrixXY();
				PointF curr = new PointF(event.getX(), event.getY());
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					allowInert = false;
					savedMatrix.set(matrix);
					last.set(event.getX(), event.getY());
					start.set(last);
					mode = DRAG;
					break;

				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);

					if (oldDist > 200f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						isImageDownload = true;
						mode = ZOOM;

					}
					break;

				case MotionEvent.ACTION_UP:
					allowInert = true;
					mode = NONE;

					int xDiff = (int) Math.abs(event.getX() - start.x);
					int yDiff = (int) Math.abs(event.getY() - start.y);

					if (xDiff < CLICK && yDiff < CLICK) {
						if (PDFImageViewActivity.headerView.getVisibility() == View.VISIBLE) {
							PDFImageViewActivity.headerView
									.setVisibility(View.GONE);
							PDFImageViewActivity.seekbarlayout
									.setVisibility(View.GONE);
						} else {
							PDFImageViewActivity.headerView
									.setVisibility(View.VISIBLE);
							PDFImageViewActivity.seekbarlayout
									.setVisibility(View.VISIBLE);
						}

						// Perform scale on double click
						long pressTime = System.currentTimeMillis();
						if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
							if (mClickTimer != null)
								mClickTimer.cancel();
							if (saveScale == 1) {
								final float targetScale = maxScale / saveScale;
								matrix.postScale(targetScale, targetScale,
										start.x, start.y);
								saveScale = maxScale;

							} else {
								matrix.postScale(minScale / saveScale, minScale
										/ saveScale, width / 2, height / 2);
								saveScale = minScale;

							}
							calcPadding();
							checkAndSetTranslate(0, 0);
							lastPressTime = 0;
						} else {
							lastPressTime = pressTime;
							mClickTimer = new Timer();
							mClickTimer.schedule(new Task(), 300);
						}
						if (saveScale == minScale) {
							scaleMatrixToBounds();
						}
					}

					break;

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					velocity = 0;
					savedMatrix.set(matrix);
					oldDist = spacing(event);
					// Log.d(TAG, "mode=NONE");
					break;
				case MotionEvent.ACTION_MOVE:

					allowInert = false;
					float diX = curr.x - last.x;
					float difY = curr.y - last.y;

					if (mode == DRAG) {

						float deltaX = curr.x - last.x;
						float deltaY = curr.y - last.y;

						long dragTime = System.currentTimeMillis();

						velocity = (float) distanceBetween(curr, last)
								/ (dragTime - lastDragTime) * FRICTION;
						lastDragTime = dragTime;

						checkAndSetTranslate(deltaX, deltaY);
						lastDelta.set(deltaX, deltaY);
						last.set(curr.x, curr.y);
					} else if (mScaleDetector == null && mode == ZOOM) {
						// progressBar.setVisibility(View.VISIBLE);

						float newDist = spacing(event);
						if (rawEvent.getPointerCount() < 2)
							break;
						// There is one serious trouble: when you scaling with
						// two fingers, then pick up first finger of gesture,
						// ACTION_MOVE being called.
						// Magic number 50 for this case
						if (10 > Math.abs(oldDist - newDist)
								|| Math.abs(oldDist - newDist) > 50)
							break;
						float mScaleFactor = newDist / oldDist;
						oldDist = newDist;
						float origScale = saveScale;
						saveScale *= mScaleFactor;
						if (saveScale > maxScale) {
							saveScale = maxScale;
							mScaleFactor = maxScale / origScale;
						} else if (saveScale < minScale) {
							saveScale = minScale;
							mScaleFactor = minScale / origScale;
						}

						calcPadding();
						if (origWidth * saveScale <= width
								|| origHeight * saveScale <= height) {
							matrix.postScale(mScaleFactor, mScaleFactor,
									width / 2, height / 2);
							if (mScaleFactor < 1) {
								fillMatrixXY();
								if (mScaleFactor < 1) {
									scaleMatrixToBounds();
								}
							}
						} else {

							PointF mid = midPointF(event);

							matrix.postScale(mScaleFactor, mScaleFactor, mid.x,
									mid.y);
							mscalefa = mScaleFactor;
							midx = mid.x;
							midy = mid.y;

							fillMatrixXY();
							if (mScaleFactor < 1) {
								if (matrixX < -right) {
									matrix.postTranslate(-(matrixX + right), 0);
									postx = -(matrixX + right);
									posty = 0;
								} else if (matrixX > 0) {
									matrix.postTranslate(-matrixX, 0);
									postx = -matrixX;
									posty = 0;

								}
								if (matrixY < -bottom) {
									matrix.postTranslate(0, -(matrixY + bottom));
									postx = 0;
									posty = -(matrixY + bottom);
								} else if (matrixY > 0) {
									matrix.postTranslate(0, -matrixY);
									postx = 0;
									posty = matrixY;
								}
							}
						}
						checkSiding();
					}
					break;
				}

				if (!firstImageUrl.equals(url) && isImageDownload) {
					url = firstImageUrl;
					fileCache = new FileCache(mContext);
					executorService = Executors.newFixedThreadPool(1);
					PhotoToLoad phototoLoad = new PhotoToLoad(
							TouchImageView.this);
					progressBar.setVisibility(View.VISIBLE);
					executorService.submit(new PhotosLoader(phototoLoad));
					isImageDownload = false;
					// setImageMatrix(matrix);
					// / progressBar.setVisibility(View.VISIBLE);
					// Bitmap firstBitmap=getBitmap(firstImageUrl);
					// setImageBitmap(firstBitmap);

					// progressBar.setVisibility(View.GONE);
					/*
					 * Single2xDownloader single2x = new
					 * Single2xDownloader(mContext);
					 * single2x.setImage(imageview, firstImageUrl, " ",
					 * progressBar, matrix); imagedownload = true;
					 */

				}

				setImageMatrix(matrix);
				invalidate();
				return true;
			}
		});
	}

	public void resetScale() {
		fillMatrixXY();
		matrix.postScale(minScale / saveScale, minScale / saveScale, width / 2,
				height / 2);
		saveScale = minScale;

		calcPadding();
		checkAndSetTranslate(0, 0);
		scaleMatrixToBounds();

		setImageMatrix(matrix);
		invalidate();
	}

	public boolean pagerCanScroll() {
		if (mode != NONE)
			return false;
		return saveScale == minScale;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!allowInert)
			return;
		final float deltaX = lastDelta.x * velocity, deltaY = lastDelta.y
				* velocity;
		if (deltaX > width || deltaY > height) {
			return;
		}
		velocity *= FRICTION;
		if (Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1)
			return;
		checkAndSetTranslate(deltaX, deltaY);
		setImageMatrix(matrix);
	}

	private void checkAndSetTranslate(float deltaX, float deltaY) {
		float scaleWidth = Math.round(origWidth * saveScale);
		float scaleHeight = Math.round(origHeight * saveScale);
		fillMatrixXY();

		if (scaleWidth < width) {
			deltaX = 0;
			if (matrixY + deltaY > 0)
				deltaY = -matrixY;
			else if (matrixY + deltaY < -bottom)
				deltaY = -(matrixY + bottom);
		} else if (scaleHeight < height) {
			deltaY = 0;
			if (matrixX + deltaX > 0)
				deltaX = -matrixX;
			else if (matrixX + deltaX < -right)
				deltaX = -(matrixX + right);
		} else {
			if (matrixX + deltaX > 0)
				deltaX = -matrixX;
			else if (matrixX + deltaX < -right)
				deltaX = -(matrixX + right);

			if (matrixY + deltaY > 0)
				deltaY = -matrixY;
			else if (matrixY + deltaY < -bottom)
				deltaY = -(matrixY + bottom);
		}
		matrix.postTranslate(deltaX, deltaY);
		checkSiding();
	}

	private void checkSiding() {
		fillMatrixXY();
		// Log.d(TAG, "x: " + matrixX + " y: " + matrixY + " left: " + right / 2
		// + " top:" + bottom / 2);
		float scaleWidth = Math.round(origWidth * saveScale);
		float scaleHeight = Math.round(origHeight * saveScale);
		onLeftSide = onRightSide = onTopSide = onBottomSide = false;
		if (-matrixX < 10.0f)
			onLeftSide = true;
		// Log.d("GalleryViewPager",
		// String.format("ScaleW: %f; W: %f, MatrixX: %f", scaleWidth, width,
		// matrixX));
		if ((scaleWidth >= width && (matrixX + scaleWidth - width) < 10)
				|| (scaleWidth <= width && -matrixX + scaleWidth <= width))
			onRightSide = true;
		if (-matrixY < 10.0f)
			onTopSide = true;
		if (Math.abs(-matrixY + height - scaleHeight) < 10.0f)
			onBottomSide = true;
	}

	private void calcPadding() {
		right = width * saveScale - width - (2 * redundantXSpace * saveScale);
		bottom = height * saveScale - height
				- (2 * redundantYSpace * saveScale);
	}

	private void fillMatrixXY() {
		matrix.getValues(m);
		matrixX = m[Matrix.MTRANS_X];
		matrixY = m[Matrix.MTRANS_Y];
	}

	private void scaleMatrixToBounds() {
		if (Math.abs(matrixX + right / 2) > 0.5f)
			matrix.postTranslate(-(matrixX + right / 2), 0);
		if (Math.abs(matrixY + bottom / 2) > 0.5f)
			matrix.postTranslate(0, -(matrixY + bottom / 2));
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		bmWidth = bm.getWidth();
		bmHeight = bm.getHeight();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		// Fit to screen.
		float scale;
		float scaleX = width / bmWidth;
		float scaleY = height / bmHeight;
		scale = Math.min(scaleX, scaleY);
		matrix.setScale(scale, scale);
		// minScale = scale;
		setImageMatrix(matrix);
		saveScale = 1f;
		// Center the image
		redundantYSpace = height - (scale * bmHeight);
		redundantXSpace = width - (scale * bmWidth);
		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;
		matrix.postTranslate(redundantXSpace, redundantYSpace);
		origWidth = width - 2 * redundantXSpace;
		origHeight = height - 2 * redundantYSpace;
		calcPadding();
		setImageMatrix(matrix);
	}

	private double distanceBetween(PointF left, PointF right) {
		return Math.sqrt(Math.pow(left.x - right.x, 2)
				+ Math.pow(left.y - right.y, 2));
	}

	/** Determine the space between the first two fingers */
	private float spacing(WrapMotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, WrapMotionEvent event) {
		// ...
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private PointF midPointF(WrapMotionEvent event) {
		// ...
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new PointF(x / 2, y / 2);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mOnClickListener = l;
	}

	private class Task extends TimerTask {
		public void run() {
			mTimerHandler.sendEmptyMessage(0);
		}
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = (float) Math.min(
					Math.max(.95f, detector.getScaleFactor()), 1.05);
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}
			right = width * saveScale - width
					- (2 * redundantXSpace * saveScale);
			bottom = height * saveScale - height
					- (2 * redundantYSpace * saveScale);
			if (origWidth * saveScale <= width
					|| origHeight * saveScale <= height) {
				matrix.postScale(mScaleFactor, mScaleFactor, width / 2,
						height / 2);
				if (mScaleFactor < 1) {
					matrix.getValues(m);
					float x = m[Matrix.MTRANS_X];
					float y = m[Matrix.MTRANS_Y];
					if (mScaleFactor < 1) {
						if (Math.round(origWidth * saveScale) < width) {
							if (y < -bottom)
								matrix.postTranslate(0, -(y + bottom));
							else if (y > 0)
								matrix.postTranslate(0, -y);
						} else {
							if (x < -right)
								matrix.postTranslate(-(x + right), 0);
							else if (x > 0)
								matrix.postTranslate(-x, 0);
						}
					}
				}
			} else {
				matrix.postScale(mScaleFactor, mScaleFactor,
						detector.getFocusX(), detector.getFocusY());
				matrix.getValues(m);
				float x = m[Matrix.MTRANS_X];
				float y = m[Matrix.MTRANS_Y];
				if (mScaleFactor < 1) {
					if (x < -right)
						matrix.postTranslate(-(x + right), 0);
					else if (x > 0)
						matrix.postTranslate(-x, 0);
					if (y < -bottom)
						matrix.postTranslate(0, -(y + bottom));
					else if (y > 0)
						matrix.postTranslate(0, -y);
				}
			}
			return true;

		}
	}

	static class TimeHandler extends Handler {
		private final WeakReference<TouchImageView> mService;

		TimeHandler(TouchImageView view) {
			mService = new WeakReference<TouchImageView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			mService.get().performClick();
			if (mService.get().mOnClickListener != null)
				mService.get().mOnClickListener.onClick(mService.get());

		}
	}

	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 712;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Bitmap getBitmap(String url)

	{

		File f = fileCache.getFile(url);
		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null) {
			return b;
		}

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			conn.disconnect();
			bitmap = decodeFile(f);
			return bitmap;

		} catch (Throwable ex) {
			ex.printStackTrace();
			Log.i("memory error===", ex.toString());

			// if(ex instanceof OutOfMemoryError)
			// memoryCache.clear();
			return null;
		}
	}

	private class PhotoToLoad {
		public String fu;
		public ImageView imageView;

		public PhotoToLoad(ImageView imageView) {
			this.imageView = imageView;

		}
	}

	class PhotosLoader implements Runnable {

		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {

				if (secondImageUrl.equals("none")) {

					Bitmap firstBitmap = getBitmap(firstImageUrl);
					BitmapDisplayer bd = new BitmapDisplayer(firstBitmap,
							photoToLoad);
					handler.post(bd);

				} else {

					Bitmap firstBitmap, secondBitmap;
					if (firstImageUrl.equals(" "))
						firstBitmap = BitmapFactory.decodeResource(
								mContext.getResources(), R.drawable.white_bg);
					else
						firstBitmap = getBitmap(firstImageUrl);

					if (secondImageUrl.equals(" "))
						secondBitmap = BitmapFactory.decodeResource(
								mContext.getResources(), R.drawable.white_bg);
					else
						secondBitmap = getBitmap(secondImageUrl);

					if (firstBitmap != null && secondBitmap != null) {
						Bitmap finalBitmap = combineImages(firstBitmap,
								secondBitmap);
						BitmapDisplayer bd = new BitmapDisplayer(finalBitmap,
								photoToLoad);
						handler.post(bd);
					}
				}

			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			try {
				if (bitmap != null) {
					photoToLoad.imageView.setImageBitmap(bitmap);
					photoToLoad.imageView.setScaleType(ScaleType.MATRIX);
					photoToLoad.imageView.setImageMatrix(matrix);
					invalidate();
					postInvalidate();
					progressBar.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				progressBar.setVisibility(View.GONE);
			}

		}
	}

	public Bitmap combineImages(Bitmap c, Bitmap s) {
		Bitmap cs = null;
		int width, height = 0;
		if (c.getWidth() > s.getWidth()) {
			width = c.getWidth() + s.getWidth();
			height = c.getHeight();
		} else {
			width = s.getWidth() + s.getWidth();
			height = s.getHeight();

		}
		cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas comboImage = new Canvas(cs);

		comboImage.drawBitmap(c, 0f, 0f, null);
		comboImage.drawBitmap(s, c.getWidth(), 0f, null);

		return cs;
	}

}