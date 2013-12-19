package com.zoomactivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.zoomactivity.R;
import com.zoomactivity.SecondActivity;
import com.zoomactivity.ZoomifierView;
import com.zoomactivity.SecondActivity.LoadCategoryList;

import com.zoomifier.adapter.DocumentLikeAdapter;
import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SearchData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.adapter.SecondGridAdapter;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomifier.imagedownload.PDImagedownloader;

public class DocumetntLikes extends Activity {
	List<Drawable> imageList = new ArrayList<Drawable>();
	List<Drawable> secondListImage = new ArrayList<Drawable>();
	Vector<SecondActivityData> grid_data = new Vector<SecondActivityData>();
	ProgressDialog progressDialog;
	// GridView popupGridView;
	RelativeLayout popupView;
	public static String id;
	String readerId;
	String companyName;
	String retrunResponse, categoryResponse, categoryHeaderName,
			documentHeaderName;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	public static PointF mid = new PointF();
	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public static int mode = NONE;
	public static int i = 0;
	ImageView webView;
	float oldDist;
	int status = 0;
	ZoomifierDatabase database;
	static int search = 0;
	EditText searchbox;
	DocumentLikeAdapter newAdapter;
	ArrayList<SearchData> searchDataList;
	ArrayList<PageData> Pagedata;
	ArrayList<String> arrayList;
	Button searchbutton,redcross_button;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documentlayout);
		Bundle bundle = getIntent().getExtras();
		id = bundle.getString("comapnyId");
		readerId = bundle.getString("readerId");
		companyName = bundle.getString("companyName");

		init();

		grid_data.clear();
		new LoadCategoryList().execute();

		searchbox
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							if (!searchbox.getText().toString().equals("")) {
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										searchbox.getWindowToken(), 0);
								Bundle bundle=new Bundle();
								bundle.putString("searchtext", searchbox.getText().toString());
								bundle.putString("readrId", readerId);
								Intent intent=new Intent(DocumetntLikes.this,SearchResultClass.class);
								intent.putExtras(bundle);
								startActivity(intent);
							}

							return true;
						}
						return false;
					}
				});

	}

	public void init() {
		
		// popupGridView = (GridView) findViewById(R.id.popup_grid_view);
		database = new ZoomifierDatabase(this);
		searchbox = (EditText) findViewById(R.id.search_speaker_edit_text);
		searchbutton = (Button) findViewById(R.id.searchbutton);
		redcross_button = (Button) findViewById(R.id.redcrossbutton);
		redcross_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchbox.setVisibility(View.GONE);
				redcross_button.setVisibility(View.GONE);
				searchbutton.setVisibility(View.VISIBLE);
				
				
			}
		});
		searchbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchbox.setVisibility(View.VISIBLE);
				redcross_button.setVisibility(View.VISIBLE);
				searchbutton.setVisibility(View.GONE);
				
			}
		});
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {

		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public class LoadCategoryList extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new ProgressDialog(DocumetntLikes.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			grid_data.clear();
			secondListImage.clear();
			grid_data.clear();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String categoryRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"GET_READER_FAVORITES\">"
					+ "<params readerid=\""
					+ readerId
					+ "\"/>"
					+ "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
				StringEntity str = new StringEntity(categoryRequest);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				categoryResponse = EntityUtils.toString(entity);
				Log.i("Response for get All Category=========",
						categoryResponse);
				String strsss = categoryResponse;
				strsss = strsss + "s";
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(categoryResponse));
				Log.i("document id========================", categoryResponse);

				int eventType = xpp.getEventType();

				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("documents")) {

						} else if (xpp.getName().equals("document")) {

							grid_data.add(new SecondActivityData(xpp.getAttributeValue(0), xpp.getAttributeValue(1), xpp.getAttributeValue(2), 
									xpp.getAttributeValue(3), xpp.getAttributeValue(4),xpp.getAttributeValue(5), xpp.getAttributeValue(6), xpp.getAttributeValue(7),
									xpp.getAttributeValue(10), " ", " ", " ", " ", " ", " ", xpp.getAttributeValue(9), " ", " ", " "));
								
						
							//database.openAndWriteDataBase();
						
						}

					}

					else if (eventType == XmlPullParser.END_TAG) {

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();
				}
				System.out.println("End document");
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
			int si = grid_data.size();
			progressDialog.dismiss();
			database.openAndWriteDataBase();
			database.openAndReadDocumentLikeTable();

		

			if (grid_data.size() != 0) {
				onDrawLayout();
			}

		}
	}

	

	public void onDrawLayout() {
		HorizontalPager realViewSwitcher = (HorizontalPager) findViewById(R.id.horizontal_pager);
		realViewSwitcher.removeAllViews();
		int numberof_item = grid_data.size();
		int numberofscreen;
		int extrascreenitem = 0;
		int m = numberof_item % 12;
		int d = numberof_item / 12;
		if (m == 0) {
			numberofscreen = d;
		} else {
			numberofscreen = d + 1;
			extrascreenitem = m;
		}
		Display display = ((WindowManager) getSystemService(this.WINDOW_SERVICE))
				.getDefaultDisplay();

		int orientation = display.getOrientation();
		int count = 0;

		int ot = getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_PORTRAIT) {

			for (int k = 1; k <= numberofscreen; k++) {
				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				// mainlayout.setBackgroundResource(R.drawable.shelf_bg);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				int width = display.getWidth();
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				int height = display.getHeight();
				int rowheight = (height - theight - 100) / 4;
				int colunsize = width / 3;
				int selfheight = rowheight / 4 + 30;
				int itemheight = rowheight / 2;

				for (int i = 1; i <= 4; i++) {
					RelativeLayout layout = new RelativeLayout(this);
					RelativeLayout.LayoutParams parma = new RelativeLayout.LayoutParams(
							width, selfheight);

					parma.setMargins(0, rowheight * i - selfheight, 0, 0);
					layout.setLayoutParams(parma);
					if (i == 1) {
						layout.setBackgroundResource(R.drawable.shelf1);
					} else if (i == 2) {
						layout.setBackgroundResource(R.drawable.shelf2);
					} else if (i == 3) {
						layout.setBackgroundResource(R.drawable.shelf3);
					} else if (i == 4) {
						layout.setBackgroundResource(R.drawable.shelf4);
					}
					mainlayout.addView(layout);

				}

				int itemnumber = 9;
				if (k == numberofscreen) {
					if (extrascreenitem != 0) {
						itemnumber = extrascreenitem;
					} else {
						itemnumber = 12;
					}
				} else {
					itemnumber = 12;
				}
				int moduls = itemnumber % 3;
				int divisivle = itemnumber / 3;
				int numberofrow;
				int exteritem;
				if (moduls == 0) {
					numberofrow = divisivle;
					exteritem = 0;

				} else {
					numberofrow = divisivle + 1;
					exteritem = moduls;
				}
				int numberofitem = 3;
				for (int i = 1; i <= numberofrow; i++) {
					if (i == numberofrow) {
						if (exteritem != 0) {
							numberofitem = exteritem;
						}
					}

					for (int l = 0; l < numberofitem; l++) {

						ImageView imageview = new ImageView(this);
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								colunsize, itemheight);

						if (height <= 800)
							parmas.setMargins(colunsize * l + 20, rowheight * i
									- (selfheight + itemheight - 15), 0, -20);
						else
							parmas.setMargins(colunsize * l + 20, rowheight * i
									- (selfheight + itemheight - 25), 0, -20);
						imageview.setLayoutParams(parmas);
						imageview.setPadding(10, 0, 20, 0);

						String url = "http://ve1.zoomifier.net/"
								+ grid_data.get(count).client_id + "/"
								+ grid_data.get(count).documentId
								+ "/ipad/Page1Thb.jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								DocumetntLikes.this);
						imageview.setId(count);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										int id = arg0.getId();

										if (grid_data.get(id).documentContentType
												.equals("DOCUMENT")) {
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", 0);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);

											Intent intent = new Intent(
													DocumetntLikes.this,
													PDFImageViewActivity.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										} else if (grid_data.get(id).documentContentType
												.equals("VIDEO")) {
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", id);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);
											Intent intent = new Intent(
													DocumetntLikes.this,
													ZoomifierVideoPlayer.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										} 
										else if(grid_data.get(id).documentContentType
												.equals("HTML5"))
											{

											
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", id);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);
											Intent intent = new Intent(
													DocumetntLikes.this,
													HtmlContentActivity.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										
											}
											else {
											
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", id);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);
											Intent intent = new Intent(
													DocumetntLikes.this,
													ZoomifierView.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										}

									}
								});
						imagedownlaoder.DisplayImage(url, imageview);
						mainlayout.addView(imageview);
						if (grid_data.get(count).documentContentType
								.equals("VIDEO")) {
							ImageView playimagview = new ImageView(this);
							RelativeLayout.LayoutParams playparmaeter = new RelativeLayout.LayoutParams(
									colunsize / 3, itemheight / 3);
							playparmaeter.setMargins(colunsize * l + colunsize
									/ 3, rowheight * i
									- (selfheight + itemheight / 2 - 15), 0, 0);
							playimagview.setLayoutParams(playparmaeter);
							playimagview
									.setBackgroundResource(R.drawable.transparent);
							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											DocumetntLikes.this.getResources(),
											R.drawable.play_button);
							// playimagview.setPadding(colunsize-20,
							// itemheight-20, colunsize-20, itemheight-20);
							playimagview.setId(count);
							playimagview.setImageBitmap(frtThumbBitmap);
							mainlayout.addView(playimagview);

						}
						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setText(grid_data.get(count).documentName);
						textview.setEllipsize(TextUtils.TruncateAt.END);
						textview.setTextColor(Color.BLACK);
						textview.setLayoutParams(parmas);
						textview.setGravity(Gravity.CENTER);
						mainlayout.addView(textview);
						count++;

					}
				}
				
				realViewSwitcher.addView(mainlayout);
			}
		} else {

			for (int k = 1; k <= numberofscreen; k++) {
				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundResource(R.drawable.shelf_bg);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				int width = display.getWidth();
				int height = display.getHeight();
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				int rowheight = (height - theight - 100) / 3;
				int colunsize = width / 4;
				int selfheight = rowheight / 4 + 30;
				int itemheight = rowheight / 2;

				for (int i = 1; i <= 3; i++) {
					RelativeLayout layout = new RelativeLayout(this);
					RelativeLayout.LayoutParams parma = new RelativeLayout.LayoutParams(
							width, selfheight);

					parma.setMargins(0, rowheight * i - selfheight, 0, 0);
					layout.setLayoutParams(parma);
					if (i == 1) {
						layout.setBackgroundResource(R.drawable.shelf1);
					} else if (i == 2) {
						layout.setBackgroundResource(R.drawable.shelf2);
					} else if (i == 3) {
						layout.setBackgroundResource(R.drawable.shelf3);
					} else if (i == 4) {
						layout.setBackgroundResource(R.drawable.shelf4);
					}
					mainlayout.addView(layout);

				}

				int itemnumber = 9;
				if (k == numberofscreen) {
					if (extrascreenitem != 0) {
						itemnumber = extrascreenitem;
					} else {
						itemnumber = 12;
					}
				} else {
					itemnumber = 12;
				}
				int moduls = itemnumber % 4;
				int divisivle = itemnumber / 4;
				int numberofrow;
				int exteritem;
				if (moduls == 0) {
					numberofrow = divisivle;
					exteritem = 0;

				} else {
					numberofrow = divisivle + 1;
					exteritem = moduls;
				}
				int numberofitem = 4;
				for (int i = 1; i <= numberofrow; i++) {
					if (i == numberofrow) {
						if (exteritem != 0) {
							numberofitem = exteritem;
						}
					}
					for (int l = 0; l < numberofitem; l++) {

						ImageView imageview = new ImageView(this);
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								colunsize, itemheight);

						parmas.setMargins(colunsize * l + 15, rowheight * i
								- (selfheight + itemheight - 15), 10, 0);
						imageview.setLayoutParams(parmas);
						String url = "http://ve1.zoomifier.net/"
								+ grid_data.get(count).client_id + "/"
								+ grid_data.get(count).documentId
								+ "/ipad/Page1Thb.jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								DocumetntLikes.this);
						imagedownlaoder.DisplayImage(url, imageview);
						imageview.setId(count);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										int id = arg0.getId();

										if (grid_data.get(id).documentContentType
												.equals("DOCUMENT")) {
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", 0);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);

											Intent intent = new Intent(
													DocumetntLikes.this,
													PDFImageViewActivity.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										} else if (grid_data.get(id).documentContentType
												.equals("VIDEO")) {
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", id);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);
											Intent intent = new Intent(
													DocumetntLikes.this,
													ZoomifierVideoPlayer.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										} 
										else if(grid_data.get(id).documentContentType
												.equals("HTML5"))
										{
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", id);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);
											Intent intent = new Intent(
													DocumetntLikes.this,
													HtmlContentActivity.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										
										}
											else {
											Bundle bundle = new Bundle();
											bundle.putString(
													"documentId",
													grid_data.get(id).documentId);
											bundle.putString(
													"documentName",
													grid_data.get(id).documentName);
											bundle.putString(
													"documentDiscription",
													grid_data.get(id).documentDiscription);
											bundle.putString(
													"documentOrginalId",
													grid_data.get(id).originalId);
											bundle.putString(
													"documentContentType",
													grid_data.get(id).documentContentType);
											bundle.putString(
													"documentContentWidth",
													grid_data.get(id).documentContentWidth);
											bundle.putString(
													"documentContentHeight",
													grid_data.get(id).documentContentHeight);
											bundle.putString(
													"documentOwnerId",
													grid_data.get(id).documentOwnerId);
											bundle.putInt("position", id);
											bundle.putString("clientid",
													grid_data.get(id).client_id);
											bundle.putString(
													"favorit",
													grid_data.get(id).like_document);
											bundle.putString("userid",
													grid_data.get(id).userID);
											bundle.putString(
													"sharedby",
													grid_data.get(id).SharedName);
											bundle.putString(
													"shareddate",
													grid_data.get(id).SharedDate);
											Intent intent = new Intent(
													DocumetntLikes.this,
													ZoomifierView.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										}

									}
								});
						mainlayout.addView(imageview);
						if (grid_data.get(count).documentContentType
								.equals("VIDEO")) {

							ImageView playimagview = new ImageView(this);
							RelativeLayout.LayoutParams playparmaeter = new RelativeLayout.LayoutParams(
									colunsize / 3, itemheight / 3);
							playparmaeter
									.setMargins(colunsize * l + colunsize / 2
											- 20,
											rowheight
													* i
													- (selfheight + itemheight
															/ 2 - 15), 10, 0);
							playimagview.setLayoutParams(playparmaeter);
							playimagview
									.setBackgroundResource(R.drawable.transparent);
							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											DocumetntLikes.this.getResources(),
											R.drawable.play_button);
							
							// playimagview.setPadding(colunsize-20,
							// itemheight-20, colunsize-20, itemheight-20);
							playimagview.setId(count);
							playimagview.setImageBitmap(frtThumbBitmap);
							playimagview.setId(count);
							mainlayout.addView(playimagview);

						}
						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setPadding(10, 0, 10, 0);
						textview.setEllipsize(TextUtils.TruncateAt.END);
						textview.setText(grid_data.get(count).documentName);
						textview.setTextColor(Color.BLACK);
						textview.setLayoutParams(parmas);
						textview.setGravity(Gravity.CENTER);
						mainlayout.addView(textview);
						count++;

					}
				}
				realViewSwitcher.addView(mainlayout);
			}

		}

	}

}