package com.zoomactivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
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

import com.zoomifier.adapter.*;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomifier.imagedownload.PDImagedownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts.Intents.Insert;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextUtils;

public class SecondActivity extends Activity {

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
	int random;
	float oldDist;
	int status = 0;
	ZoomifierDatabase database;
	static int search = 0;
	EditText searchbox;
	SecondGridAdapter newAdapter;
	String gridsignal;
	String barcodedocId, barcodeclientid, barcodecotenttype, barcodereaderid,
			barcodeownerid;
	ArrayList<SearchData> searchDataList;
	ArrayList<PageData> Pagedata;
	ArrayList<String> arrayList;
	LinearLayout layout;
	String deleteClientId,deleteDocId;
	int deletePostion;
	Button searchbutton,redcross_button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_layout);
		Bundle bundle = getIntent().getExtras();
		gridsignal = bundle.getString("grid");
	
			id = bundle.getString("comapnyId");
			readerId = bundle.getString("readerId");
			companyName = bundle.getString("companyName");
		
		init();

		database.openAndWriteDataBase();
		database.openAndReadClientID();
	
		search = 0;
		for (int i = 0; i < ZoomifierDatabase.documentclientid.size(); i++) {
			if (ZoomifierDatabase.documentclientid.get(i).equals(id)) {
				search = search + 1;
				break;
			}
		}
		if (search > 0) {

			database.openAndReadDocumentTable(id);
			grid_data.clear();
			for (int i = 0; i < ZoomifierDatabase.documentID.size(); i++) {
				grid_data.add(new SecondActivityData(
						ZoomifierDatabase.documentID.get(i),
						ZoomifierDatabase.documentName.get(i),
						ZoomifierDatabase.documentDiscription.get(i),
						ZoomifierDatabase.documentorginalID.get(i),
						ZoomifierDatabase.documentcontetntype.get(i),
						ZoomifierDatabase.documentcontntwidth.get(i),
						ZoomifierDatabase.documentcontenntheight.get(i),
						ZoomifierDatabase.documentownerid.get(i),
						ZoomifierDatabase.documentlike.get(i),
						ZoomifierDatabase.sharedName.get(i),
						ZoomifierDatabase.isshared.get(i),
						ZoomifierDatabase.sharedId.get(i),
						ZoomifierDatabase.sharedDate.get(i),
						ZoomifierDatabase.order.get(i),
						ZoomifierDatabase.pagecount.get(i),
						ZoomifierDatabase.documentclientid.get(i),
						ZoomifierDatabase.clientname.get(i),
						ZoomifierDatabase.imageformat.get(i), readerId));
				if (ZoomifierDatabase.documentcontetntype.get(i).equals("html")) {
					String clinetid = ZoomifierDatabase.documentclientid.get(i);
					String str = "fsfs";
				}
			}
			Display display = ((WindowManager) getSystemService(SecondActivity.this.WINDOW_SERVICE))
					.getDefaultDisplay();
			Collections.sort(grid_data);
			if (grid_data.size() != 0) {
				onDrawLayout();
			}

		} else {
			new LoadCategoryList().execute();
		}

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
								Intent intent=new Intent(SecondActivity.this,SearchResultClass.class);
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
		 layout=(LinearLayout) findViewById(R.id.pagedivider);
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

	public class LoadCategoryList extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new ProgressDialog(SecondActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			grid_data.clear();
			secondListImage.clear();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String categoryRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"GET_READER_DOCUMENTS\">"
					+ "<params readerid=\""
					+ readerId
					+ "\" clientid=\""
					+ id
					+ "\"/>" + "</operation>" + "</request>";
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

							/*grid_data.add(new SecondActivityData(xpp
									.getAttributeValue(0), xpp
									.getAttributeValue(1), xpp
									.getAttributeValue(2), xpp
									.getAttributeValue(3), xpp
									.getAttributeValue(4), xpp
									.getAttributeValue(5), xpp
									.getAttributeValue(6), xpp
									.getAttributeValue(7), id, readerId,
									"unlike", xpp.getAttributeValue(9), xpp
											.getAttributeValue(12)));*/
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
			progressDialog.dismiss();
			database.openAndWriteDataBase();
			try {
				for (int i = 0; i < grid_data.size(); i++) {
				/*	database.insertIntoDocumentTable(
							grid_data.get(i).documentId,
							grid_data.get(i).documentName,
							grid_data.get(i).documentDiscription,
							grid_data.get(i).documentOrginalId,
							grid_data.get(i).documentContentWidth,
							grid_data.get(i).documentContentType,
							grid_data.get(i).documentContentHeight,
							grid_data.get(i).documentOwnerId, id, readerId,
							grid_data.get(i).SharedName,
							grid_data.get(i).SharedDate);*/
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Collections.sort(grid_data);
			if (grid_data.size() != 0)
				onDrawLayout();
			else
				displayAlert("No Data Found", "Error");
		

		}

		private Drawable LoadImageFromWebOperations(String url) {
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				Drawable d = Drawable.createFromStream(is, "drawable");
				String ar = "afaf";
				return d;
			} catch (Exception e) {
				System.out.println("Exc=" + e);
				return null;
			}
		}

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

	
	public void onDrawLayout() {
		layout.removeAllViews();
		HorizontalPager realViewSwitcher = (HorizontalPager) findViewById(R.id.horizontal_pager);
		realViewSwitcher.setOnScreenSwitchListener(onScreenSwitchListener);
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
				RelativeLayout touchlayout = new RelativeLayout(this);
				RelativeLayout.LayoutParams touchparameter = new RelativeLayout.LayoutParams(
						android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
						android.widget.RelativeLayout.LayoutParams.FILL_PARENT);
				touchlayout.setLayoutParams(touchparameter);
				touchlayout.setBackgroundColor(Color.parseColor("#00000000"));
				touchlayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onDrawLayout();
					}
				});

				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mainlayout.addView(touchlayout);
				// mainlayout.setBackgroundResource(R.drawable.shelf_bg);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				int width = display.getWidth();
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				int height = display.getHeight();
				int rowheight;
				if (height > 800)
					rowheight = (height - theight - 130) / 4;
				else
					rowheight = (height - theight - 80) / 4;
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
								SecondActivity.this);
						imageview.setId(count);
						imageview
						.setOnLongClickListener(new View.OnLongClickListener() {

							@Override
							public boolean onLongClick(View arg0) {
								deleteLayout();
								return false;
							}
						});
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
													"originalId",
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
													SecondActivity.this,
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
													"originalId",
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
													SecondActivity.this,
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
													"originalId",
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
													SecondActivity.this,
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
													"originalId",
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
													SecondActivity.this,
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
											SecondActivity.this.getResources(),
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
				LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
						10, 10);
					ImageView  pagedivider=new ImageView(SecondActivity.this);
				pagedivider.setLayoutParams(parma);
				if(k==1)
				pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);
			    layout.addView(pagedivider);
				
			}
		} else {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			display = wm.getDefaultDisplay();
			int width = display.getWidth();	
			int theight = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
							.getDisplayMetrics());
		   int  height=display.getHeight();
		   int rowheight;
		   if(height>=720)
				 rowheight = (height - theight -130) / 3;
		   else
			   rowheight = (height - theight -80) / 3;
		
			int colunsize = width / 4;
			int selfheight = rowheight / 4 + 30;
			int itemheight = rowheight / 2;

			for (int k = 1; k <= numberofscreen; k++) {
				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundResource(R.drawable.shelf_bg);
			
				

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
								SecondActivity.this);
						imagedownlaoder.DisplayImage(url, imageview);
						imageview.setId(count);
						imageview
						.setOnLongClickListener(new View.OnLongClickListener() {

							@Override
							public boolean onLongClick(View arg0) {
								deleteLayout();
								return false;
							}
						});
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
													"originalId",
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
													SecondActivity.this,
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
													"originalId",
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
													SecondActivity.this,
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
													"originalId",
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
													SecondActivity.this,
													HtmlContentActivity.class);
											intent.putExtras(bundle);
											ArrayList<String> arraylist = new ArrayList<String>();
											PageData pageData = new PageData(
													arraylist);
											intent.putExtra("pagedata",
													pageData);
											startActivity(intent);
										
											}else {
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
													"originalId",
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
													SecondActivity.this,
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
											SecondActivity.this.getResources(),
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
				LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
						10, 10);
					ImageView  pagedivider=new ImageView(SecondActivity.this);
				pagedivider.setLayoutParams(parma);
				if(k==1)
				pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);
			    layout.addView(pagedivider);
			}

		}
		
	}
	  private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener =
	            new HorizontalPager.OnScreenSwitchListener() {
	                @Override
	                public void onScreenSwitched(final int screen) {
	                	for(int i=0;i<layout.getChildCount();i++)
	                	{
	                		ImageView imageView=(ImageView) layout.getChildAt(i);
	                		imageView.setImageResource(R.drawable.gray_dot);
	                	}
	                	  ImageView imageView=(ImageView) layout.getChildAt(screen);
	                	  imageView.setImageResource(R.drawable.blue_dot);
	                	
	                }
	            };

	public void displayAlert(String msg, String title) {
		new AlertDialog.Builder(this)
				.setMessage(msg)
				.setTitle(title)
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}
	
	public class DeleteDocument extends AsyncTask<Void, Void, Void> {
		String shareResponse, retrunResponse;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {

			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"DELETE_READER_DOCUMENT\">"
					+ "<params readerid=\"" + readerId + "\" clientid=\""
					+ deleteClientId + "\" docid=\"" + deleteDocId + "\"/>"
					+ "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
				StringEntity str = new StringEntity(document);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				retrunResponse = EntityUtils.toString(entity);
				Log.i("Resposne========", retrunResponse);
				Log.i("Response for Get Reader Cliets=======", retrunResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(retrunResponse));
				String str = retrunResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							shareResponse = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("document")) {

							try {

							} catch (SQLiteException e) {
								Log.i("SQL ERROR ======", e.toString());
							} catch (Exception e) {
								Log.i("SQL ERROR ======", e.toString());
							}
						}

					} else if (eventType == XmlPullParser.END_TAG) {

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

			if (shareResponse.equals("success")) {
				try{
                // displayAlert("Delete Successfully");
                 grid_data.remove(deletePostion);
                 database.deleteDocumentRow(deleteDocId);
                deleteLayout();
				}
				catch (Exception e) {
					
				}
               
			}

		}

	}
	public void deleteLayout()
	{


		layout.removeAllViews();
		HorizontalPager realViewSwitcher = (HorizontalPager) findViewById(R.id.horizontal_pager);
		realViewSwitcher.setOnScreenSwitchListener(onScreenSwitchListener);
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
				
				RelativeLayout touchlayout = new RelativeLayout(this);
				RelativeLayout.LayoutParams touchparameter = new RelativeLayout.LayoutParams(
						android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
						android.widget.RelativeLayout.LayoutParams.FILL_PARENT);
				touchlayout.setLayoutParams(touchparameter);
				touchlayout.setBackgroundColor(Color.parseColor("#00000000"));
				touchlayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onDrawLayout();
					}
				});

				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				// mainlayout.setBackgroundResource(R.drawable.shelf_bg);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				mainlayout.addView(touchlayout);
				int width = display.getWidth();
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				int height = display.getHeight();
				int rowheight;
				if (height > 800)
					rowheight = (height - theight - 130) / 4;
				else
					rowheight = (height - theight - 80) / 4;
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
						ImageView deleteImageview = new ImageView(this);
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								colunsize, itemheight);

						RelativeLayout.LayoutParams deletParameter = new RelativeLayout.LayoutParams(
								35, 35);

						if (height <= 800) {
							parmas.setMargins(colunsize * l + 20, rowheight * i
									- (selfheight + itemheight - 15), 0, -20);
							deletParameter.setMargins(colunsize * l + 20,
									rowheight * i
											- (selfheight + itemheight-5),
									0, -20);
						} else {
							parmas.setMargins(colunsize * l + 20, rowheight * i
									- (selfheight + itemheight - 25), 0, -20);
							deletParameter.setMargins(colunsize * l + 20,
									rowheight * i
											- (selfheight + itemheight -4),
									0, -20);

						}

						imageview.setLayoutParams(parmas);
						deleteImageview.setLayoutParams(deletParameter);
						imageview.setPadding(10, 0, 20, 0);
						deleteImageview.setPadding(10, 0, 0, 0);

						String url = "http://ve1.zoomifier.net/"
								+ grid_data.get(count).client_id + "/"
								+ grid_data.get(count).documentId
								+ "/ipad/Page1Thb.jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								SecondActivity.this);
						imageview.setId(count);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
						                 int id=arg0.getId();
									deletePostion=id;
									deleteDocId=grid_data.get(id).documentId;
									deleteClientId=grid_data.get(id).client_id;
									yesNoAlertDialog();
									}
								});
						imagedownlaoder.DisplayImage(url, imageview);
						Bitmap crossbitmap = BitmapFactory.decodeResource(
								SecondActivity.this.getResources(),
								R.drawable.cross);
						mainlayout.addView(imageview);
						deleteImageview.setImageBitmap(crossbitmap);
						mainlayout.addView(deleteImageview);
						if (grid_data.get(count).documentContentType
								.equals("VIDEO")) {
							ImageView playimagview = new ImageView(this);
							RelativeLayout.LayoutParams playparmaeter = new RelativeLayout.LayoutParams(
									colunsize / 3, itemheight / 3);
							playparmaeter.setMargins(colunsize * l + colunsize
									/ 3, rowheight * i
									- (selfheight + itemheight / 2 - 15), 0, 0);
							playimagview.setLayoutParams(playparmaeter);

							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											SecondActivity.this.getResources(),
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
				LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
						10, 10);
				ImageView pagedivider = new ImageView(SecondActivity.this);
				pagedivider.setLayoutParams(parma);
				if (k == 1)
					pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);
				layout.addView(pagedivider);

			}
		} else {
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			display = wm.getDefaultDisplay();
			int width = display.getWidth();
			int theight = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
							.getDisplayMetrics());
			int height = display.getHeight();
			int rowheight;
			if (height >= 720)
				rowheight = (height - theight - 130) / 3;
			else
				rowheight = (height - theight - 80) / 3;

			int colunsize = width / 4;
			int selfheight = rowheight / 4 + 30;
			int itemheight = rowheight / 2;

			for (int k = 1; k <= numberofscreen; k++) {
				
				RelativeLayout touchlayout = new RelativeLayout(this);
				RelativeLayout.LayoutParams touchparameter = new RelativeLayout.LayoutParams(
						android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
						android.widget.RelativeLayout.LayoutParams.FILL_PARENT);
				touchlayout.setLayoutParams(touchparameter);
				touchlayout.setBackgroundColor(Color.parseColor("#00000000"));
				touchlayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onDrawLayout();
					}
				});

				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundResource(R.drawable.shelf_bg);
                mainlayout.addView(touchlayout);
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
						ImageView deleteImageView = new ImageView(this);
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								colunsize, itemheight);
						RelativeLayout.LayoutParams deleteParamter = new RelativeLayout.LayoutParams(
								25, 25);

						parmas.setMargins(colunsize * l + 15, rowheight * i
								- (selfheight + itemheight - 15), 10, 0);
						deleteParamter.setMargins(colunsize * l + 15, rowheight
								* i - (selfheight + itemheight), 10, 0);
						imageview.setLayoutParams(parmas);
						deleteImageView.setLayoutParams(deleteParamter);
						String url = "http://ve1.zoomifier.net/"
								+ grid_data.get(count).client_id + "/"
								+ grid_data.get(count).documentId
								+ "/ipad/Page1Thb.jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								SecondActivity.this);
						imagedownlaoder.DisplayImage(url, imageview);
						imageview.setId(count);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										int id=arg0.getId();
										deletePostion=id;
									deleteDocId=grid_data.get(id).documentId;
									deleteClientId=grid_data.get(id).client_id;
									yesNoAlertDialog();
									
									}
								});
						mainlayout.addView(imageview);
						Bitmap crossBitmap = BitmapFactory.decodeResource(
								SecondActivity.this.getResources(),
								R.drawable.cross);
						deleteImageView.setImageBitmap(crossBitmap);
						mainlayout.addView(deleteImageView);
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

							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											SecondActivity.this.getResources(),
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
				LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
						10, 10);
				ImageView pagedivider = new ImageView(SecondActivity.this);
				pagedivider.setLayoutParams(parma);
				if (k == 1)
					pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);
				layout.addView(pagedivider);
			}

		}

	
	}
	public void yesNoAlertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle("Delete");
		alertDialog.setMessage("Do you really want to delete this Content ?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new DeleteDocument().execute();

					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				});

		alertDialog.show();
	}
}
