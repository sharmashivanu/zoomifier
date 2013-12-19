package com.zoomactivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.imagemanagement.FileCache;
import com.zoomifier.database.ZoomifierDatabase;

public class BarCodeWebViewActivity extends Activity {
	WebView webview;
	String qrurl;
	ProgressBar progressBar;
	TextView textView;
	FileCache fileCache;
	ZoomifierDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_webview);
		try {
			Bundle bunele = getIntent().getExtras();
			qrurl = bunele.getString("url");
			init();
			WebSettings settings = webview.getSettings();
			settings.setJavaScriptEnabled(true);
			webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			final AlertDialog alertDialog = new AlertDialog.Builder(this)
					.create();

			webview.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					Log.i("TAG", "Processing webview url click...");
					view.loadUrl(url);
					return true;
				}
				public void onPageFinished(WebView view, String url) {

					if (progressBar.getVisibility() == View.VISIBLE) {
						progressBar.setVisibility(View.GONE);
						textView.setVisibility(View.GONE);
					}
					File f = fileCache.getFile(qrurl);
					Bitmap ba = decodeFile(f);
					if (ba == null) {
						if(view.getTitle()!=null&&!view.getTitle().equals(""))
					      database.updateQRTitle(view.getTitle(), qrurl);
						Picture picture = view.capturePicture();
						if(picture.getHeight()>0&&picture.getWidth()>0)
						{
						Bitmap b = Bitmap.createBitmap(picture.getWidth(),
								picture.getHeight(), Bitmap.Config.ARGB_8888);
						Canvas c = new Canvas(b);
						picture.draw(c);
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(f);
							if (fos != null) {
								b.compress(Bitmap.CompressFormat.JPEG, 100, fos);

								fos.close();
							}
						} catch (Exception e) {
								e.printStackTrace();
						}
						}
					}
				}

				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					Log.e("TAG", "Error: " + description);

					alertDialog.setTitle("Error");
					alertDialog.setMessage(description);
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							});
					alertDialog.show();
				}
			});
			webview.loadUrl(qrurl);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void init() {
		fileCache = new FileCache(BarCodeWebViewActivity.this);
		textView = (TextView) findViewById(R.id.progressbartext);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		webview = (WebView) findViewById(R.id.webview);
		database=new ZoomifierDatabase(this);

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

}
