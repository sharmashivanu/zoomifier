package com.zoomactivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.zoomactivity.PDFImageViewActivity.SearchPDF;
import com.zoomifier.adapter.GridData;
import com.zoomifier.adapter.GridViewAdapter;
import com.zoomifier.adapter.PDFGridViewAdapter;
import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SearchData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.adapter.SecondGridAdapter;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomifier.imagedownload.PDImagedownloader;

import android.app.Activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Build;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

public class GridActivity extends Activity {
	// GridView gridView;
	RelativeLayout layout, webView_layout, layout2, setting_Dialog;
	TextView categoryHeader, documentHeader;
	int status = 0;
	Button settingButton, redcross_button, searchbutton;
	Button signOut;
	Button barCodeScaneButton;
	ProgressDialog progressDialog;
	String retrunResponse, categoryResponse, categoryHeaderName,
			documentHeaderName;
	String barCodeValue;
	String id, name;
	String shareResponse;
	public static String readerId, clientId, userEmail;
	List<String> comapnyId = new ArrayList<String>();
	List<String> companyName = new ArrayList<String>();
	Vector<GridData> grid_data = new Vector<GridData>();
	String documentWebViewUrl;
	Drawable drwable;
	Dialog settingDialog;
	static int postion;
	ImageView webView;
	boolean settingDialogSignal;
	TextView textHeader;
	ZoomifierDatabase database;
	EditText searchbox;
	GridViewAdapter adapter;
	String barcodedocid, barcodedoctype, barcodedocownerid, barcodeclientid;
	static String barcodedata;
	static boolean restart;
	String changeEmail, changeoldPassword, changernewPassword;
	static String email_id;
	ArrayList<SearchData> searchDataList;
	ArrayList<PageData> Pagedata;
	ArrayList<String> arrayList;
	private static final int ZBAR_SCANNER_REQUEST = 0;
	RelativeLayout library_button;
	String delete_clientId;
	int deletPostion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);
		Bundle bundle = getIntent().getExtras();
		readerId = bundle.getString("readerId");
		clientId = bundle.getString("clientId");
		userEmail = bundle.getString("email_id");
		init();
		this.overridePendingTransition(R.anim.slide_left_to_right,
				R.anim.slide_right_to_left);
		barcodedata = null;
		grid_data.clear();
		restart = false;
		SharedPreferences myPrefsd = getApplicationContext()
				.getSharedPreferences("myPrefs", MODE_PRIVATE);
		String databased = myPrefsd.getString("clientlist", "");
		if (databased.equals("success")) {
			try {
				SharedPreferences myPrefs = getApplicationContext()
						.getSharedPreferences("myPrefs", MODE_PRIVATE);
				readerId = myPrefs.getString("userId", "");
				userEmail = myPrefs.getString("userEmail", "");
				grid_data.clear();
				database.openAndWriteDataBase();
				database.openAndReadCompanyTable();

				for (int i = 0; i < ZoomifierDatabase.companyId.size(); i++) {

					grid_data.add(new GridData(ZoomifierDatabase.companyId
							.get(i), ZoomifierDatabase.companyName.get(i),
							readerId, ZoomifierDatabase.imageformat.get(i)));

				}

				Display display = ((WindowManager) getSystemService(GridActivity.this.WINDOW_SERVICE))
						.getDefaultDisplay();
				new SendingAnalyticsdata().execute();
				int orientation = display.getOrientation();
				Collections.sort(grid_data);
				grid_data.add(0, new GridData("111", "My Likes", readerId, ""));
				database.openAndReadQRTable();
				if (ZoomifierDatabase.qrName.size() > 0)
					grid_data.add(1, new GridData("qrcode", "Scaned", readerId,
							""));

				int ot = getResources().getConfiguration().orientation;
				if (grid_data.size() != 0) {
					onDrawLayout();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		else {
			new loadList().execute();
		}

		barCodeScaneButton = (Button) findViewById(R.id.barcode_scane);
		barCodeScaneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					Intent intent = new Intent(GridActivity.this,
							ZBarScannerActivity.class);
					startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		settingDialogSignal = false;
		signOut.setText("(sign out)  " + userEmail);
		email_id = userEmail;

		settingDialog();

		settingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (setting_Dialog.isShown()) {
					setting_Dialog.setVisibility(View.GONE);

				} else {
					setting_Dialog.setVisibility(View.VISIBLE);
					Display display = ((WindowManager) getSystemService(GridActivity.this.WINDOW_SERVICE))
							.getDefaultDisplay();

					int orientation = display.getOrientation();
					if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
							|| orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) setting_Dialog
								.getLayoutParams();
						params.width = LayoutParams.FILL_PARENT;
						setting_Dialog.setLayoutParams(params);
					} else {
						int pixel = GridActivity.this.getWindowManager()
								.getDefaultDisplay().getWidth();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) setting_Dialog
								.getLayoutParams();
						int wid = pixel / 2;
						params.width = wid;
						setting_Dialog.setLayoutParams(params);
					}
					signOut = (Button) findViewById(R.id.email_field);
					signOut.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							setting_Dialog.setVisibility(View.GONE);
							Intent mainIntent = new Intent(GridActivity.this,
									MainActivity.class);
							startActivity(mainIntent);
							SharedPreferences myPrefs = GridActivity.this
									.getSharedPreferences("myPrefs",
											MODE_WORLD_READABLE);
							SharedPreferences.Editor prefsEditor = myPrefs
									.edit();
							prefsEditor.putString("login", "unsccuess");
							prefsEditor.putString("databasecreation",
									"unsuccess");
							prefsEditor.commit();
							GridActivity.this
									.deleteDatabase("zoomifierdatabase");

							finish();
						}
					});
					Button signUserInfo = (Button) findViewById(R.id.chan_userinfo_btn);
					signUserInfo.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent(GridActivity.this,
									ChangeUserInfoActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("email", email_id);
							intent.putExtras(bundle);
							startActivity(intent);
							setting_Dialog.setVisibility(View.GONE);
						}
					});
				}

			}

		});

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
								Bundle bundle = new Bundle();
								bundle.putString("searchtext", searchbox
										.getText().toString());
								bundle.putString("readrId", readerId);
								Intent intent = new Intent(GridActivity.this,
										SearchResultClass.class);
								intent.putExtras(bundle);
								startActivity(intent);
							}

							return true;
						}
						return false;
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ZBAR_SCANNER_REQUEST:
			if (resultCode == RESULT_OK) {

				barCodeValue = data.getStringExtra(ZBarConstants.SCAN_RESULT);
				new loadBarCodeList().execute();
			} else if (resultCode == RESULT_CANCELED && data != null) {
				String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
				if (!TextUtils.isEmpty(error)) {
					Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}

	}

	public void init() {
		settingButton = (Button) findViewById(R.id.setting_button);
		signOut = (Button) findViewById(R.id.email_field);
		categoryHeader = (TextView) findViewById(R.id.poup_header_text);
		documentHeader = (TextView) findViewById(R.id.document_header_text);
		setting_Dialog = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
		webView_layout = (RelativeLayout) findViewById(R.id.webview_layout);
		// gridView = (GridView) findViewById(R.id.grid_view);
		searchbox = (EditText) findViewById(R.id.search_speaker_edit_text);
		library_button = (RelativeLayout) findViewById(R.id.library_layout);
		database = new ZoomifierDatabase(this);
		library_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (MyLikesActivity.myactivit != null)
					MyLikesActivity.myactivit.finish();
				finish();

			}
		});
		searchbutton = (Button) findViewById(R.id.searchbutton);
		redcross_button = (Button) findViewById(R.id.redcrossbutton);
		redcross_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchbox.setVisibility(View.GONE);
				redcross_button.setVisibility(View.GONE);
				searchbutton.setVisibility(View.VISIBLE);
				settingButton.setVisibility(View.VISIBLE);
				barCodeScaneButton.setVisibility(View.VISIBLE);
			}
		});
		searchbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchbox.setVisibility(View.VISIBLE);
				redcross_button.setVisibility(View.VISIBLE);
				searchbutton.setVisibility(View.GONE);
				settingButton.setVisibility(View.GONE);
				barCodeScaneButton.setVisibility(View.GONE);
			}
		});

	}

	public class loadList extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			database = new ZoomifierDatabase(GridActivity.this);
			database.openAndWriteDataBase();

			super.onPreExecute();
			progressDialog = new ProgressDialog(GridActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			grid_data.clear();

			database.openAndWriteDataBase();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"GET_READER_CLIENTS\">"
					+ "<params readerid=\"" + readerId + "\"/>"
					+ "</operation>" + "</request>";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
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
						if (xpp.getName().equals("clients")) {

						} else if (xpp.getName().equals("client")) {

							grid_data.add(new GridData(
									xpp.getAttributeValue(0), xpp
											.getAttributeValue(1), readerId,
									xpp.getAttributeValue(2)));
							try {

								database.openAndWriteDataBase();
								database.insertIntoCompanyTable(
										xpp.getAttributeValue(0),
										xpp.getAttributeValue(1),
										xpp.getAttributeValue(2));

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
			progressDialog.dismiss();
			userEmail = "";

			Display display = ((WindowManager) getSystemService(GridActivity.this.WINDOW_SERVICE))
					.getDefaultDisplay();

			int orientation = display.getOrientation();
			Collections.sort(grid_data);
			grid_data.add(0, new GridData("111", "My Likes", readerId, ""));
			if (grid_data.size() != 0) {
				SharedPreferences myPrefs = getApplicationContext()
						.getSharedPreferences("myPrefs", MODE_PRIVATE);
				SharedPreferences.Editor prefsEditor = myPrefs.edit();
				prefsEditor.putString("clientlist", "success");
				prefsEditor.commit();
				onDrawLayout();
			}
		}

	}

	public void settingDialog() {
		settingDialog = new Dialog(GridActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settingDialog.setContentView(R.layout.setting_dialog);
		settingDialog.setCanceledOnTouchOutside(false);
		settingDialog.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
		settingDialog.getWindow().setLayout(300, 600);

	}

	@Override
	protected void onRestart() {

		super.onRestart();

		/*
		 * if (restart) { new loadList().execute(); }
		 */

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		setting_Dialog.setVisibility(View.GONE);
		return super.onTouchEvent(event);
	}

	public static boolean isNumeric(String str) {
		try {
			int i = Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public class loadBarCodeList extends AsyncTask<Void, Void, Void> {
		String documentid = "";
		String documentName = "";
		String documcontenttype = "";
		String documentwidth = "";
		String documnetHeight = "";
		String documnetdesc = "";
		String documentriginalid = "";
		String pagecount = "";
		String clientid = "";
		String ownerid = "";
		String clientname = "";
		String imageformat = "";
		String sharedId;
		String sharedName;
		String sharedDate;
		String documentlike;
		String isShared;
		String retrunResponse = "";
		String barcodedocid = "";
		String barcodedoctype = "";
		String barcodeclientid = "";
		String barcodedocownerid = "";
		String signal = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GridActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			clientid = "";

		}

		@Override
		protected Void doInBackground(Void... params) {

			if (barCodeValue.contains("http://ve4.zoomifier.net/qr.php?")) {

				int postionofe = barCodeValue.indexOf("=");
				int latindex = barCodeValue.indexOf("&");
				String qruserid = barCodeValue.substring(postionofe + 1,
						latindex);

				int start_doc = barCodeValue.indexOf("d");
				int last_doc = barCodeValue.indexOf("c");
				String qrdocid = barCodeValue.substring(start_doc + 2,
						last_doc - 1);
				int lastindexofand = barCodeValue.lastIndexOf("&");
				String barclient = barCodeValue.substring(last_doc + 2,
						lastindexofand);
				database.FindDocument(qrdocid);
				if (ZoomifierDatabase.documentID.size() != 0) {

					documentid = ZoomifierDatabase.documentID.get(0);
					documentName = ZoomifierDatabase.documentName.get(0);
					documcontenttype = ZoomifierDatabase.documentcontetntype
							.get(0);
					documentwidth = ZoomifierDatabase.documentcontntwidth
							.get(0);
					documnetHeight = ZoomifierDatabase.documentcontenntheight
							.get(0);
					documnetdesc = ZoomifierDatabase.documentDiscription.get(0);
					documentriginalid = ZoomifierDatabase.documentorginalID
							.get(0);
					pagecount = ZoomifierDatabase.pagecount.get(0);
					clientid = ZoomifierDatabase.documentclientid.get(0);
					ownerid = ZoomifierDatabase.documentownerid.get(0);
					clientname = ZoomifierDatabase.clientname.get(0);
					imageformat = ZoomifierDatabase.imageformat.get(0);
					sharedId = ZoomifierDatabase.sharedId.get(0);
					sharedDate = ZoomifierDatabase.sharedDate.get(0);
					sharedName = ZoomifierDatabase.sharedName.get(0);
					documentlike = ZoomifierDatabase.documentlike.get(0);
					isShared = ZoomifierDatabase.isshared.get(0);

				} else {
					try {
						String document1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
								+ "<request>"
								+ "<operation opcode=\"SHARE_DOCUMENT\">"
								+ "<params email=\""
								+ email_id
								+ "\" docid=\""
								+ qrdocid
								+ "\" clientid=\""
								+ barclient
								+ "\"  userid=\""
								+ qruserid
								+ "\"/>"
								+ "</operation>" + "</request>";
						;

						HttpClient httpclient = new DefaultHttpClient();
						HttpPost post = new HttpPost(
								"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
						StringEntity str = new StringEntity(document1);
						str.setContentType("application/xml; charset=utf-8");
						str.setContentEncoding(new BasicHeader(
								HTTP.CONTENT_TYPE,
								"application/xml; charset=utf-8"));
						post.setEntity(str);
						HttpResponse response = httpclient.execute(post);
						HttpEntity entity = response.getEntity();
						categoryResponse = EntityUtils.toString(entity);
						String categoryResopnsess = categoryResponse;
						Log.i("document id========================",
								categoryResopnsess);

					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (Exception ioe) {
						ioe.printStackTrace();
					}
					try {
						XmlPullParserFactory factory = XmlPullParserFactory
								.newInstance();
						factory.setNamespaceAware(true);
						XmlPullParser xpp = factory.newPullParser();
						xpp.setInput(new StringReader(categoryResponse));

						int eventType = xpp.getEventType();
						while (eventType != XmlPullParser.END_DOCUMENT) {
							if (eventType == XmlPullParser.START_DOCUMENT) {

							} else if (eventType == XmlPullParser.START_TAG) {
								Log.i("Start tag ", xpp.getName());
								if (xpp.getName().equals("Document")) {
									// numberofpage=xpp.getAttributeValue(6);
								} else if (xpp.getName().equals("document")) {

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
					try {
						String document1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
								+ "<request>"
								+ "<operation opcode=\"GET_DOCUMENT_DATA\">"
								+ "<params documentid=\""
								+ qrdocid
								+ "\"/>"
								+ "</operation>" + "</request>";

						HttpClient httpclient = new DefaultHttpClient();
						HttpPost post = new HttpPost(
								"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
						StringEntity str = new StringEntity(document1);
						str.setContentType("application/xml; charset=utf-8");
						str.setContentEncoding(new BasicHeader(
								HTTP.CONTENT_TYPE,
								"application/xml; charset=utf-8"));
						post.setEntity(str);
						HttpResponse response = httpclient.execute(post);
						HttpEntity entity = response.getEntity();
						categoryResponse = EntityUtils.toString(entity);
						String categoryResopnsess = categoryResponse;
						Log.i("document id========================",
								categoryResopnsess);

					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (Exception ioe) {
						ioe.printStackTrace();
					}
					try {
						XmlPullParserFactory factory = XmlPullParserFactory
								.newInstance();
						factory.setNamespaceAware(true);
						XmlPullParser xpp = factory.newPullParser();
						xpp.setInput(new StringReader(categoryResponse));
						Log.i("document id========================",
								categoryResponse);
						String str = categoryResponse;
						int eventType = xpp.getEventType();
						while (eventType != XmlPullParser.END_DOCUMENT) {
							if (eventType == XmlPullParser.START_DOCUMENT) {

							} else if (eventType == XmlPullParser.START_TAG) {
								Log.i("Start tag ", xpp.getName());
								if (xpp.getName().equals("Document")) {
									// numberofpage=xpp.getAttributeValue(6);
								} else if (xpp.getName().equals("document")) {
									documentid = xpp.getAttributeValue(0);
									documentName = xpp.getAttributeValue(1);
									documcontenttype = xpp.getAttributeValue(2);
									documentwidth = xpp.getAttributeValue(3);
									documnetHeight = xpp.getAttributeValue(4);
									documnetdesc = xpp.getAttributeValue(5);
									documentriginalid = xpp
											.getAttributeValue(6);
									pagecount = xpp.getAttributeValue(7);
									clientid = xpp.getAttributeValue(8);
									ownerid = xpp.getAttributeValue(10);
									clientname = xpp.getAttributeValue(11);
									imageformat = xpp.getAttributeValue(12);

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
					database.findVender(clientid);
					if (ZoomifierDatabase.companyId.size() == 0) {
						database.insertIntoCompanyTable(clientid, clientname,
								imageformat);
						grid_data.add(new GridData(clientid, clientname,
								readerId, imageformat));
					}

					database.insertIntoDocumentTable(documentid, documentName,
							documnetdesc, documentriginalid, documcontenttype,
							documentwidth, documnetHeight, ownerid, "0", " ",
							" ", " ", " ", "", "", clientid, clientname,
							imageformat, readerId);

				}
			} else if (isNumeric(barCodeValue)) {

				String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<request>"
						+ "<operation opcode=\"GET_BARCODE_DOCUMENT\">"
						+ "<params barcodevalue=\"" + barCodeValue + "\"/>"
						+ "</operation>" + "</request>";

				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost post = new HttpPost(
							"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
					StringEntity str = new StringEntity(document);
					str.setContentType("application/xml; charset=utf-8");
					str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/xml; charset=utf-8"));
					post.setEntity(str);
					HttpResponse response = httpclient.execute(post);
					HttpEntity entity = response.getEntity();
					retrunResponse = EntityUtils.toString(entity);
					String retruString = retrunResponse;
					Log.i("Resposne========", retruString);

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
							if (xpp.getName().equals("documents")) {

							} else if (xpp.getName().equals("document")) {

								barcodedocid = xpp.getAttributeValue(0);
								barcodedoctype = xpp.getAttributeValue(1);
								barcodeclientid = xpp.getAttributeValue(2);
								barcodedocownerid = xpp.getAttributeValue(2);
								try {
									database.openAndWriteDataBase();

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
				try {
					String document2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<request>"
							+ "<operation opcode=\"SHARE_DOCUMENT\">"
							+ "<params email=\""
							+ email_id
							+ "\" docid=\""
							+ barcodedocid
							+ "\" clientid=\""
							+ barcodeclientid
							+ "\"  userid=\""
							+ readerId
							+ "\"/>"
							+ "</operation>" + "</request>";
					;

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost post = new HttpPost(
							"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
					StringEntity str = new StringEntity(document2);
					str.setContentType("application/xml; charset=utf-8");
					str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/xml; charset=utf-8"));
					post.setEntity(str);
					HttpResponse response = httpclient.execute(post);
					HttpEntity entity = response.getEntity();
					categoryResponse = EntityUtils.toString(entity);
					String categoryResopnsess = categoryResponse;
					Log.i("document id========================",
							categoryResopnsess);

				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (Exception ioe) {
					ioe.printStackTrace();
				}
				try {
					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(new StringReader(categoryResponse));
					Log.i("document id========================",
							categoryResponse);
					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_DOCUMENT) {

						} else if (eventType == XmlPullParser.START_TAG) {
							Log.i("Start tag ", xpp.getName());
							if (xpp.getName().equals("Document")) {
								// numberofpage=xpp.getAttributeValue(6);
							} else if (xpp.getName().equals("document")) {
								documentid = xpp.getAttributeValue(0);
								documentName = xpp.getAttributeValue(1);
								documcontenttype = xpp.getAttributeValue(2);
								documentwidth = xpp.getAttributeValue(3);
								documnetHeight = xpp.getAttributeValue(4);
								documnetdesc = xpp.getAttributeValue(5);
								documentriginalid = xpp.getAttributeValue(6);
								pagecount = xpp.getAttributeValue(7);
								clientid = xpp.getAttributeValue(8);
								ownerid = xpp.getAttributeValue(10);

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

				try {
					String document1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<request>"
							+ "<operation opcode=\"GET_DOCUMENT_DATA\">"
							+ "<params documentid=\""
							+ documentid
							+ "\"/>"
							+ "</operation>" + "</request>";

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost post = new HttpPost(
							"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
					StringEntity str = new StringEntity(document1);
					str.setContentType("application/xml; charset=utf-8");
					str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/xml; charset=utf-8"));
					post.setEntity(str);
					HttpResponse response = httpclient.execute(post);
					HttpEntity entity = response.getEntity();
					categoryResponse = EntityUtils.toString(entity);

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				try {
					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(new StringReader(categoryResponse));

					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_DOCUMENT) {

						} else if (eventType == XmlPullParser.START_TAG) {
							Log.i("Start tag ", xpp.getName());
							if (xpp.getName().equals("Document")) {
								// numberofpage=xpp.getAttributeValue(6);
							} else if (xpp.getName().equals("document")) {
								documentid = xpp.getAttributeValue(0);
								documentName = xpp.getAttributeValue(1);
								documcontenttype = xpp.getAttributeValue(2);
								documentwidth = xpp.getAttributeValue(3);
								documnetHeight = xpp.getAttributeValue(4);
								documnetdesc = xpp.getAttributeValue(5);
								documentriginalid = xpp.getAttributeValue(6);
								pagecount = xpp.getAttributeValue(7);
								clientid = xpp.getAttributeValue(8);
								ownerid = xpp.getAttributeValue(10);

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

			} else {
				try {
					signal = "outqrcode";
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					String startTime = dateFormat.format(cal.getTime());
					database.insertIntoQRTable(barCodeValue, "0", startTime, "");
					if (ZoomifierDatabase.qrName.size() == 0)
						grid_data.add(1, new GridData("qrcode", "Scaned",
								readerId, ""));

				} catch (Exception e) {

					e.printStackTrace();
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				progressDialog.dismiss();

				if (signal.equals("outqrcode")) {
					Bundle bundle = new Bundle();
					bundle.putString("url", barCodeValue);
					Intent intent = new Intent(GridActivity.this,
							BarCodeWebViewActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
					barCodeValue = "";
					onDrawLayout();

				} else {
					if (documcontenttype.equals("DOCUMENT")) {
						Bundle bundle = new Bundle();
						bundle.putString("documentId", documentid);
						bundle.putString("documentName", documentName);
						bundle.putString("documentDiscription", documnetdesc);
						bundle.putString("originalId", documentriginalid);
						bundle.putString("documentContentType",
								documcontenttype);
						bundle.putString("documentContentWidth", documentwidth);
						bundle.putString("documentContentHeight",
								documnetHeight);
						bundle.putString("documentOwnerId", ownerid);
						bundle.putInt("position", 0);
						bundle.putString("clientid", clientid);
						bundle.putString("userid", readerId);
						bundle.putString("favorit", documentlike);
						bundle.putString("sharedby", sharedName);
						bundle.putString("shareddate", sharedDate);

						Intent intent = new Intent(GridActivity.this,
								PDFImageViewActivity.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
						onDrawLayout();
						barCodeValue = "";
					} else if (documcontenttype.equals("VIDEO")) {
						Bundle bundle = new Bundle();
						bundle.putString("documentId", documentid);
						bundle.putString("documentName", documentName);
						bundle.putString("documentDiscription", documnetdesc);
						bundle.putString("originalId", documentriginalid);
						bundle.putString("documentContentType",
								documcontenttype);
						bundle.putString("documentContentWidth", documentwidth);
						bundle.putString("documentContentHeight",
								documnetHeight);
						bundle.putString("documentOwnerId", ownerid);
						bundle.putInt("position", 0);
						bundle.putString("clientid", clientid);
						bundle.putString("userid", readerId);
						bundle.putString("favorit", documentlike);
						bundle.putString("sharedby", sharedName);
						bundle.putString("shareddate", sharedDate);
						Intent intent = new Intent(GridActivity.this,
								ZoomifierVideoPlayer.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						onDrawLayout();
						startActivity(intent);
						barCodeValue = "";
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("documentId", documentid);
						bundle.putString("documentName", documentName);
						bundle.putString("documentDiscription", documnetdesc);
						bundle.putString("originalId", documentriginalid);
						bundle.putString("documentContentType",
								documcontenttype);
						bundle.putString("documentContentWidth", documentwidth);
						bundle.putString("documentContentHeight",
								documnetHeight);
						bundle.putString("documentOwnerId", ownerid);
						bundle.putInt("position", 0);
						bundle.putString("clientid", clientId);
						bundle.putString("favorit", documentlike);
						bundle.putString("userid", readerId);
						bundle.putString("sharedby", sharedName);
						bundle.putString("shareddate", sharedDate);
						Intent intent = new Intent(GridActivity.this,
								ZoomifierView.class);
						onDrawLayout();
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
						barCodeValue = "";
					}

				}

			} catch (Exception e) {

			}
		}

	}

	public class ChangePassword extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(GridActivity.this);
			progressDialog.setMessage("Submitting Data....");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {

			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"CHANGE_READER_PASSWORD\">"
					+ "<params email=\"" + changeEmail + "\" oldpassword=\""
					+ changeoldPassword + "\" newpassword=\""
					+ changernewPassword + "\"/>" + "</operation>"
					+ "</request>";

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

							/*
							 * grid_data.add(new GridData(
							 * xpp.getAttributeValue(0), xpp
							 * .getAttributeValue(1), readerId));
							 * 
							 * String id = xpp.getAttributeValue(0); String
							 * contenttype = xpp.getAttributeValue(1); String
							 * clintId = xpp.getAttributeValue(2);
							 */
							try {

								// database.openAndWriteDataBase();
								// database.insertIntoCompanyTable(xpp.getAttributeValue(0),
								// xpp.getAttributeValue(1));
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
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {

				displayAlert("Change Successfully");
			}

		}

	}

	public void displayAlert(String msg) {
		new AlertDialog.Builder(this)
				.setMessage(msg)
				.setTitle("Zoomifier")
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	public void onDrawLayout() {

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
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
		display = ((WindowManager) getSystemService(this.WINDOW_SERVICE))
				.getDefaultDisplay();

		int orientation = display.getOrientation();
		int count = 0;

		int ot = getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_PORTRAIT) {
			for (int k = 1; k <= numberofscreen; k++) {

				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(width,
						LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundResource(R.drawable.shelf_bg);
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				int rowheight;
				if (height > 800)
					rowheight = (height - theight - 130) / 4;
				else
					rowheight = (height - theight - 100) / 4;
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
						int folderwidth = (colunsize / 3) * 2;
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								folderwidth, itemheight);
						int leftmargin = (colunsize / 3) / 2;
						if (height > 900)
							parmas.setMargins(colunsize * l + leftmargin,
									rowheight * i
											- (selfheight + itemheight - 25),
									10, 0);
						else
							parmas.setMargins(colunsize * l + leftmargin,
									rowheight * i
											- (selfheight + itemheight - 15),
									10, 0);

						imageview.setLayoutParams(parmas);
						imageview.setId(count);
						imageview.setBackgroundResource(R.drawable.folder);
						int paddingleft = folderwidth / 5;
						int paddingtop = folderwidth / 5 + 10;
						imageview.setPadding(paddingleft, paddingtop,
								paddingleft, 2);
						String url;
						if (grid_data.get(count).imageFormat.equals("png"))
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".png";
						else
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".jpg";

						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								GridActivity.this);
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
										if (id == 0) {
											Bundle bundle = new Bundle();
											bundle.putString("readerId",
													grid_data.get(id).readerId);
											bundle.putString("comapnyId",
													grid_data.get(id).companyId);
											bundle.putString(
													"companyName",
													grid_data.get(id).companyName);
											bundle.putString("likesignal", "");
											Intent intent = new Intent(
													GridActivity.this,
													DocumetntLikes.class);
											intent.putExtras(bundle);
											startActivity(intent);
										} else {

											if (grid_data.get(id).companyId
													.equals("qrcode")) {
												{
													Bundle bundle = new Bundle();
													bundle.putString(
															"readerId",
															readerId);
													Intent intent = new Intent(
															GridActivity.this,
															ScaneFolderView.class);
													intent.putExtras(bundle);
													startActivity(intent);

												}

											} else {
												if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
													Bitmap bitmap = Bitmap.createBitmap(
															arg0.getWidth(),
															arg0.getHeight(),
															Bitmap.Config.ARGB_8888);
													// bitmap.eraseColor(colour);
													Bundle bundle = new Bundle();
													bundle = ActivityOptions
															.makeThumbnailScaleUpAnimation(
																	arg0,
																	bitmap, 0,
																	0)
															.toBundle();
													bundle.putString(
															"readerId",
															grid_data.get(id).readerId);
													bundle.putString(
															"comapnyId",
															grid_data.get(id).companyId);
													bundle.putString(
															"companyName",
															grid_data.get(id).companyName);
													bundle.putString("grid", "");
													bundle.putString(
															"likesignal", "");
													Intent intent = new Intent(
															GridActivity.this,
															SecondActivity.class);
													intent.putExtras(bundle);
													startActivity(intent,
															bundle);

												} else {
													Bundle bundle = new Bundle();
													bundle.putString(
															"readerId",
															grid_data.get(id).readerId);
													bundle.putString(
															"comapnyId",
															grid_data.get(id).companyId);
													bundle.putString(
															"companyName",
															grid_data.get(id).companyName);
													bundle.putString("grid", "");
													bundle.putString(
															"likesignal", "");
													Intent intent = new Intent(
															GridActivity.this,
															SecondActivity.class);
													intent.putExtras(bundle);
													startActivity(intent);
												}
											}
										}

									}
								});
						if (count == 0) {
							if (height > 800)
								imageview.setPadding(paddingleft,
										paddingtop + 35, paddingleft, 15);
							else
								imageview.setPadding(paddingleft,
										paddingtop + 10, paddingleft, 10);
							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											GridActivity.this.getResources(),
											R.drawable.heart_icon);
							imageview.setImageBitmap(frtThumbBitmap);
						} else {
							if (grid_data.get(count).companyId.equals("qrcode")) {

								if (height > 800)
									imageview.setPadding(paddingleft,
											paddingtop + 20, paddingleft, 10);
								else
									imageview.setPadding(paddingleft,
											paddingtop + 10, paddingleft, 10);
								Bitmap frtThumbBitmap = BitmapFactory
										.decodeResource(GridActivity.this
												.getResources(),
												R.drawable.qr_code_icon);
								imageview.setImageBitmap(frtThumbBitmap);

							} else {

								imagedownlaoder.DisplayImage(url, imageview);
							}
						}

						mainlayout.addView(imageview);

						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setText(grid_data.get(count).companyName);
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
				ImageView pagedivider = new ImageView(GridActivity.this);
				pagedivider.setLayoutParams(parma);
				if (k == 1)
					pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);

			}
		} else {

			int theight = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
							.getDisplayMetrics());
			height = display.getHeight();
			int rowheight;
			if (height >= 720)
				rowheight = (height - theight - 130) / 3;
			else
				rowheight = (height - theight - 100) / 3;

			int colunsize = width / 4;
			int selfheight = rowheight / 4 + 30;
			int itemheight = rowheight / 2;
			for (int k = 1; k <= numberofscreen; k++) {
				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new RelativeLayout.LayoutParams(
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
						int folderwidth = colunsize / 2;
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								folderwidth, itemheight);
						int leftmargin = (colunsize / 2) / 2;
						parmas.setMargins(colunsize * l + leftmargin, rowheight
								* i - (selfheight + itemheight - 15), 10, 0);
						imageview.setLayoutParams(parmas);
						imageview.setBackgroundResource(R.drawable.folder);
						imageview.setId(count);
						int paddingleft = folderwidth / 5;
						int paddingtop = folderwidth / 5;
						imageview.setPadding(paddingleft, paddingtop,
								paddingleft, 2);
						String url;
						if (grid_data.get(count).imageFormat.equals("png"))
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".png";
						else
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								GridActivity.this);
						if (count == 0) {
							imageview.setPadding(paddingleft, paddingtop,
									paddingleft, 10);
							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											GridActivity.this.getResources(),
											R.drawable.heart_icon);
							imageview.setImageBitmap(frtThumbBitmap);
						} else {
							if (grid_data.get(count).companyId.equals("qrcode")) {
								imageview.setPadding(paddingleft, paddingtop,
										paddingleft, 10);
								Bitmap frtThumbBitmap = BitmapFactory
										.decodeResource(GridActivity.this
												.getResources(),
												R.drawable.qr_code_icon);
								imageview.setImageBitmap(frtThumbBitmap);
							} else
								imagedownlaoder.DisplayImage(url, imageview);
						}

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
										if (id == 0) {
											Bundle bundle = new Bundle();
											bundle.putString("readerId",
													grid_data.get(id).readerId);
											bundle.putString("comapnyId",
													grid_data.get(id).companyId);
											bundle.putString(
													"companyName",
													grid_data.get(id).companyName);
											bundle.putString("likesignal", "");
											Intent intent = new Intent(
													GridActivity.this,
													DocumetntLikes.class);
											intent.putExtras(bundle);
											startActivity(intent);
										} else {

											if (grid_data.get(id).companyId
													.equals("qrcode")) {
												{
													Bundle bundle = new Bundle();
													bundle.putString(
															"readerId",
															readerId);
													Intent intent = new Intent(
															GridActivity.this,
															ScaneFolderView.class);
													intent.putExtras(bundle);
													startActivity(intent);

												}

											} else {
												if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
													Bitmap bitmap = Bitmap.createBitmap(
															arg0.getWidth(),
															arg0.getHeight(),
															Bitmap.Config.ARGB_8888);
													// bitmap.eraseColor(colour);
													Bundle bundle = new Bundle();
													bundle = ActivityOptions
															.makeThumbnailScaleUpAnimation(
																	arg0,
																	bitmap, 0,
																	0)
															.toBundle();
													bundle.putString(
															"readerId",
															grid_data.get(id).readerId);
													bundle.putString(
															"comapnyId",
															grid_data.get(id).companyId);
													bundle.putString(
															"companyName",
															grid_data.get(id).companyName);
													bundle.putString("grid", "");
													bundle.putString(
															"likesignal", "");
													Intent intent = new Intent(
															GridActivity.this,
															SecondActivity.class);
													intent.putExtras(bundle);
													startActivity(intent,
															bundle);

												} else {
													Bundle bundle = new Bundle();
													bundle.putString(
															"readerId",
															grid_data.get(id).readerId);
													bundle.putString(
															"comapnyId",
															grid_data.get(id).companyId);
													bundle.putString(
															"companyName",
															grid_data.get(id).companyName);
													bundle.putString("grid", "");
													bundle.putString(
															"likesignal", "");
													Intent intent = new Intent(
															GridActivity.this,
															SecondActivity.class);
													intent.putExtras(bundle);
													startActivity(intent);
												}
											}
										}

									}
								});
						mainlayout.addView(imageview);

						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setEllipsize(TextUtils.TruncateAt.END);
						textview.setText(grid_data.get(count).companyName);
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

			}

		}

	}

	public class SendingAnalyticsdata extends AsyncTask<Void, Void, Void> {
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			response = " ";

		}

		@Override
		protected Void doInBackground(Void... params) {

			database.openAndReadPageAnalytic();
			StringBuffer reqest = new StringBuffer();
			reqest.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			reqest.append("<events>");
			int size = ZoomifierDatabase.pageAnayltic_docid.size();
			for (int i = 0; i < ZoomifierDatabase.pageAnayltic_docid.size(); i++) {
				reqest.append("<event");
				reqest.append(" ");
				reqest.append("clientid=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_clientid.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("object1=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_first_object
						.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("object2=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_second_object
						.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("eventtype=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_eventtype.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("documentid=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnayltic_docid.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("timespent=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_endtime.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("locationid=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_location.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("sharedto=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_share_to.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("sharetype=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_share_type.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("searchtags=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_searchtag.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("contenttype=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_content_type
						.get(i));
				reqest.append("\"");
				reqest.append(" ");
				reqest.append("timestamp=");
				reqest.append("\"");
				reqest.append(ZoomifierDatabase.pageAnalytic_starttime.get(i));
				reqest.append("\"");
				reqest.append("/>");
			}
			reqest.append("</events>");
			String document = reqest.toString();
			try {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/eventcapturews/rest/events");
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

						if (xpp.getName().equals("result")) {

							response = xpp.getAttributeValue(0);
						}

					} else if (eventType == XmlPullParser.END_TAG) {

						if (xpp.getName().equals("Document")) {

						}

					} else if (eventType == XmlPullParser.TEXT) {

					}
					eventType = xpp.next();

				}
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
			if (response.equals("success"))
				database.deletAnayticsData();
			else if (response.equals("failure"))
				database.deletAnayticsData();

		}

	}

	public void deleteLayout() {

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
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
		display = ((WindowManager) getSystemService(this.WINDOW_SERVICE))
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
				mainlayout.setLayoutParams(new LayoutParams(width,
						LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundResource(R.drawable.shelf_bg);
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());
				mainlayout.addView(touchlayout);
				int rowheight;
				if (height > 800)
					rowheight = (height - theight - 130) / 4;
				else
					rowheight = (height - theight - 100) / 4;
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
						int folderwidth = (colunsize / 3) * 2;
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								folderwidth, itemheight);
						int leftmargin = (colunsize / 3) / 2;
						if (height > 900)
							parmas.setMargins(colunsize * l + leftmargin,
									rowheight * i
											- (selfheight + itemheight - 25),
									10, 0);
						else
							parmas.setMargins(colunsize * l + leftmargin,
									rowheight * i
											- (selfheight + itemheight - 15),
									10, 0);

						imageview.setLayoutParams(parmas);
						imageview.setId(count);
						if (count == 0)
							imageview.setBackgroundResource(R.drawable.folder);
						else
							imageview
									.setBackgroundResource(R.drawable.cross_folder);
						int paddingleft = folderwidth / 5;
						int paddingtop =folderwidth / 5+10;
						imageview.setPadding(paddingleft, paddingtop,
								paddingleft, 2);
						String url;
						if (grid_data.get(count).imageFormat.equals("png"))
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".png";
						else
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".jpg";

						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								GridActivity.this);
						imageview.setId(count);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {

										int id = arg0.getId();
										if (id != 0) {
											delete_clientId = grid_data.get(id).companyId;
											deletPostion = id;
											yesNoAlertDialog();
										}

									}
								});
						if (count == 0) {
							if (height > 800)
								imageview.setPadding(paddingleft,
										paddingtop + 35, paddingleft, 15);
							else
								imageview.setPadding(paddingleft,
										paddingtop + 10, paddingleft, 10);
							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											GridActivity.this.getResources(),
											R.drawable.heart_icon);
							imageview.setImageBitmap(frtThumbBitmap);
						} else {
							if (grid_data.get(count).companyId.equals("qrcode")) {

								if (height > 800)
									imageview.setPadding(paddingleft,
											paddingtop + 20, paddingleft, 10);
								else
									imageview.setPadding(paddingleft,
											paddingtop + 10, paddingleft, 10);
								Bitmap frtThumbBitmap = BitmapFactory
										.decodeResource(GridActivity.this
												.getResources(),
												R.drawable.qr_code_icon);
								imageview.setImageBitmap(frtThumbBitmap);

							} else {

								imagedownlaoder.DisplayImage(url, imageview);
							}
						}
						mainlayout.addView(imageview);

						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setText(grid_data.get(count).companyName);
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
				ImageView pagedivider = new ImageView(GridActivity.this);
				pagedivider.setLayoutParams(parma);
				if (k == 1)
					pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);

			}
		} else {
			int theight = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
							.getDisplayMetrics());
			height = display.getHeight();
			int rowheight;
			if (height >= 720)
				rowheight = (height - theight - 130) / 3;
			else
				rowheight = (height - theight - 100) / 3;

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
				mainlayout.setLayoutParams(new RelativeLayout.LayoutParams(
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
						int folderwidth = colunsize / 2;
						RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
								folderwidth, itemheight);
						int leftmargin = (colunsize / 2) / 2;
						parmas.setMargins(colunsize * l + leftmargin, rowheight
								* i - (selfheight + itemheight - 15), 10, 0);
						imageview.setLayoutParams(parmas);
						if (count == 0)
							imageview.setBackgroundResource(R.drawable.folder);
						else
							imageview
									.setBackgroundResource(R.drawable.cross_folder);
						imageview.setId(count);
						int paddingleft = folderwidth / 5;
						int paddingtop = folderwidth / 5+10;
						imageview.setPadding(paddingleft, paddingtop,
								paddingleft, 2);
						String url;
						if (grid_data.get(count).imageFormat.equals("png"))
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".png";
						else
							url = "http://ve1.zoomifier.net/"
									+ grid_data.get(count).companyId
									+ "/images/clients/"
									+ grid_data.get(count).companyId + ".jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								GridActivity.this);
						if (count == 0) {
							imageview.setPadding(paddingleft, paddingtop,
									paddingleft, 10);
							Bitmap frtThumbBitmap = BitmapFactory
									.decodeResource(
											GridActivity.this.getResources(),
											R.drawable.heart_icon);
							imageview.setImageBitmap(frtThumbBitmap);
						} else {
							if (grid_data.get(count).companyId.equals("qrcode")) {
								imageview.setPadding(paddingleft, paddingtop,
										paddingleft, 10);
								Bitmap frtThumbBitmap = BitmapFactory
										.decodeResource(GridActivity.this
												.getResources(),
												R.drawable.qr_code_icon);
								imageview.setImageBitmap(frtThumbBitmap);
							} else
								imagedownlaoder.DisplayImage(url, imageview);
						}

						imageview.setId(count);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {

										int id = arg0.getId();
										if (id != 0) {
											delete_clientId = grid_data.get(id).companyId;
											deletPostion = id;
											yesNoAlertDialog();
										}

									}
								});
						mainlayout.addView(imageview);

						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setEllipsize(TextUtils.TruncateAt.END);
						textview.setText(grid_data.get(count).companyName);
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

			}

		}

	}

	public void yesNoAlertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle("Delete");
		alertDialog.setMessage("Do you really want to delete this Vendor ?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (grid_data.get(deletPostion).companyId
								.equals("qrcode")) {
							database.deleteallQrTable();
							grid_data.remove(deletPostion);
							deleteLayout();
						} else
							new DeleteClient().execute();

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

	public class DeleteClient extends AsyncTask<Void, Void, Void> {
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
					+ "<operation opcode=\"DELETE_READER_TENANT\">"
					+ "<params readerid=\"" + readerId + "\" clientid=\""
					+ delete_clientId + "\"/>" + "</operation>" + "</request>";
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
				try {
					// displayAlert("Delete Successfully");
					grid_data.remove(deletPostion);
					database.deleteVender(delete_clientId);
					database.deleteClientDocument(delete_clientId);
					deleteLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

}
