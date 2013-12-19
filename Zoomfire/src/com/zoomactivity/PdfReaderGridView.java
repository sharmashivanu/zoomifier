package com.zoomactivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Pattern;

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

import com.zoomactivity.PDFImageViewActivity.LikeDocument;
import com.zoomactivity.PDFImageViewActivity.SearchPDF;
import com.zoomactivity.PDFImageViewActivity.SendReview;
import com.zoomactivity.PDFImageViewActivity.ShareEmail;
import com.zoomactivity.PDFImageViewActivity.UnLikeDocumentDocument;
import com.zoomifier.adapter.AgendaTimeAdapNew;
import com.zoomifier.adapter.Book;
import com.zoomifier.adapter.PDFGridData;
import com.zoomifier.adapter.PDFGridViewAdapter;
import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomifier.imagedownload.PDImagedownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PdfReaderGridView extends Activity {
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	public static PointF mid = new PointF();
	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public static int mode = NONE;
	public static int i = 0;
	ProgressDialog progressDialog;
	Drawable drawabl;
	Dialog settingDialog;
	float oldDist;
	int status = 0;
	String documentId, documentName, documentDiscription, documentOrginalId,
			documentContentType, documentContentWidth, documentContentHeight,
			documentOwnerId;
	Animation webViewZoomOutAnimation, webViewZoomInAnimation;
	TextView textHeader, discriptionText;
	Button starButton, sharebuton, heartButton, infoButton,searchbutton,redcross_button;
	ZoomifierDatabase database;
	String categoryResponse;
	Vector<Book> reviewList = new Vector<Book>();
	Vector<PDFGridData> thumbLis = new Vector<PDFGridData>();
	// GridView thumGridView;
	public static ArrayList<Drawable> drawable = new ArrayList<Drawable>();
	ArrayList<String> searchPages = new ArrayList<String>();
	int position;
	String clientid;
	String userId;
	String numberofpage;
	int noofpage;
	String shareResponse, retrunResponse;
	static int numberofstar;
	String review_time;
	String review_title;
	String revew_text;
	static boolean thumbview;
	PageData pagedata;
	RelativeLayout transparentlayout;
	LinearLayout layout;
	EditText searchEditText;
	String searchDocumentId,searchString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdfgridvew);
		Bundle bundle = getIntent().getExtras();
		try {
			documentId = bundle.getString("documentId");
			documentName = bundle.getString("documentName");
			documentDiscription = bundle.getString("documentDiscription");
			documentOrginalId = bundle.getString("documentOrginalId");
			documentContentType = bundle.getString("documentContentType");
			documentContentWidth = bundle.getString("documentContentWidth");
			documentContentHeight = bundle.getString("documentContentHeight");
			documentOwnerId = bundle.getString("documentOwnerId");
			String imageid = bundle.getString("image");
			position = bundle.getInt("position");
			clientid = bundle.getString("clientid");
			noofpage = bundle.getInt("noofpage");
			userId = bundle.getString("userid");
			Intent intetnt = getIntent();
			pagedata = (PageData) intetnt.getSerializableExtra("pagedata");
			webViewZoomOutAnimation = AnimationUtils.loadAnimation(
					PdfReaderGridView.this, R.anim.zoom_out_animation);
			webViewZoomInAnimation = AnimationUtils.loadAnimation(
					PdfReaderGridView.this, R.anim.zoom_in_popupview);

			overridePendingTransition(R.anim.zoom_out_animation,
					R.anim.zoom_in_popupview);
			init();
			int ot = getResources().getConfiguration().orientation;

		} catch (Exception e) {
			e.printStackTrace();
		}

		new LoadDocument().execute();

		textHeader.setText(documentName);
		discriptionText.setText(documentDiscription);

		

		sharebuton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
				heartlayout.setVisibility(View.GONE);
				transparentlayout.setVisibility(View.GONE);
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				if (layout.getVisibility() == View.VISIBLE) {
					layout.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				} else {
					Display display = ((WindowManager) getSystemService(PdfReaderGridView.this.WINDOW_SERVICE))
							.getDefaultDisplay();

					int orientation = display.getOrientation();
					if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
							|| orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout
								.getLayoutParams();
						params.width = LayoutParams.FILL_PARENT;
						layout.setLayoutParams(params);
					} else {
						int pixel = PdfReaderGridView.this.getWindowManager()
								.getDefaultDisplay().getWidth();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout
								.getLayoutParams();
						int wid = pixel / 2;
						params.width = wid;
						layout.setLayoutParams(params);
					}
					layout.setVisibility(View.VISIBLE);
					transparentlayout.setVisibility(View.VISIBLE);
				}

			}
		});

		Button authorizedButton = (Button) findViewById(R.id.authorized_button);
		authorizedButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText emailField = (EditText) findViewById(R.id.email_field);
				if (emailField.getText().toString().equals("")) {
					displayAlert("Please enter email address");
				} else if (!checkEmail(emailField.getText().toString())) {
					displayAlert("Please enter a valid email address");
				} else {
					new ShareEmail().execute();
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
					layout.setVisibility(View.GONE);
					emailField.setText("");
				}
			}
		});
		heartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				database.openAndWriteDataBase();
				String like = database
						.openAndReadDocumentLikeUnlike(documentId);
				if (like.equals("0")) {
					new LikeDocument().execute();
					/*
					 * database.updateDocumentLikes(documentId);
					 * heartButton.setBackgroundResource(R.drawable.heart_plus);
					 */

				} else if (like.equals("1")) {

					new UnLikeDocumentDocument().execute();
					/*
					 * database.updateDocumentUnlikes(documentId);
					 * heartButton.setBackgroundResource
					 * (R.drawable.heart_minus);
					 */
				}

			}
		});
		infoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);

				RelativeLayout infolayout = (RelativeLayout) findViewById(R.id.infodialog);
				if (infolayout.getVisibility() == View.VISIBLE) {
					infolayout.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				} else {
					Display display = ((WindowManager) getSystemService(PdfReaderGridView.this.WINDOW_SERVICE))
							.getDefaultDisplay();

					int orientation = display.getOrientation();
					if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
							|| orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infolayout
								.getLayoutParams();
						params.width = LayoutParams.FILL_PARENT;
						infolayout.setLayoutParams(params);
					} else {
						int pixel = PdfReaderGridView.this.getWindowManager()
								.getDefaultDisplay().getWidth();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infolayout
								.getLayoutParams();
						int wid = pixel / 2;
						params.width = wid;
						infolayout.setLayoutParams(params);
					}
					infolayout.setVisibility(View.VISIBLE);
					transparentlayout.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	public void init() {
		// webView=(ImageView) findViewById(R.id.webview);
		textHeader = (TextView) findViewById(R.id.document_header_text);
		starButton = (Button) findViewById(R.id.button1);
		sharebuton = (Button) findViewById(R.id.button2);
		heartButton = (Button) findViewById(R.id.button3);
		infoButton = (Button) findViewById(R.id.button4);
		layout = (LinearLayout) findViewById(R.id.pagedivider);
		transparentlayout = (RelativeLayout) findViewById(R.id.transparentlayout);
		searchEditText = (EditText) findViewById(R.id.search_speaker_edit_text);
		transparentlayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
				heartlayout.setVisibility(View.GONE);
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				heartlayout.setVisibility(View.GONE);
				layout.setVisibility(View.GONE);
				transparentlayout.setVisibility(View.GONE);
			}
		});
		searchEditText
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (!searchEditText.getText().toString()
							.equals("")) {
						new SearchPDF().execute();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								searchEditText.getWindowToken(), 0);
					}

					return true;
				}
				return false;
			}
		});
		database = new ZoomifierDatabase(this);
		discriptionText = (TextView) findViewById(R.id.discription_field);
		searchbutton = (Button) findViewById(R.id.searchbutton);
		redcross_button = (Button) findViewById(R.id.redcrossbutton);
		redcross_button = (Button) findViewById(R.id.redcrossbutton);
		redcross_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchEditText.setVisibility(View.GONE);
				redcross_button.setVisibility(View.GONE);
				searchbutton.setVisibility(View.VISIBLE);
			
				sharebuton.setVisibility(View.VISIBLE);
				heartButton.setVisibility(View.VISIBLE);
				infoButton.setVisibility(View.VISIBLE);
			}
		});
		searchbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchEditText.setVisibility(View.VISIBLE);
				redcross_button.setVisibility(View.VISIBLE);
				searchbutton.setVisibility(View.GONE);
			
				sharebuton.setVisibility(View.GONE);
				heartButton.setVisibility(View.GONE);
				infoButton.setVisibility(View.GONE);
			}

		});


	}
	public class SearchPDF extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			shareResponse = "";
			searchPages.clear();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"READER_QUERY\">"
					+ "<params text=\"" + searchEditText.getText().toString()
					+ "\" catid=\"" + "" + "\" docid=\"" + documentId
					+ "\" clientid=\"" + clientid + "\" readerid=\"" + userId
					+ "\"/>" + "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/textsearchws/rest/search");
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
						if (xpp.getName().equals("Document")) {
							searchDocumentId = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("SearchResults")) {
							searchString = xpp.getAttributeValue(0);
						}else if (xpp.getName().equals("Page")) {

							try {
								if (searchDocumentId.equals(documentId)) {

									searchPages.add(xpp.getAttributeValue(0));

								}

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
			for (int l = 0; l < searchPages.size(); l++) {
				PDFGridData pdfdata = thumbLis.get(Integer
						.parseInt(searchPages.get(l)) - 1);
				pdfdata.signal = 1;
			}
			if (thumbLis.size() != 0) {
				onDrawLayout();
			}
		}

	}

	public class LoadDocument extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * progressDialog = new ProgressDialog(PdfReaderGridView.this);
			 * progressDialog
			 * .setMessage("Please Wait.Downloading your data...");
			 * progressDialog.show();
			 */
			thumbLis.clear();
			drawable.clear();
			numberofpage = "";
		}

		@Override
		protected Void doInBackground(Void... params) {

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				// progressDialog.dismiss();
				for (int i = 0; i < noofpage; i++) {

					thumbLis.add(new PDFGridData(documentId, Integer
							.toString(i + 1), documentDiscription,
							documentOrginalId, documentContentType,
							documentContentWidth, documentContentHeight,
							documentOwnerId, clientid, userId, "0", "", "", 0));

				}
				for (int l = 0; l < pagedata.page.size(); l++) {
					PDFGridData pdfdata = thumbLis.get(Integer
							.parseInt(pagedata.page.get(l)) - 1);
					pdfdata.signal = 1;
				}
				if (thumbLis.size() != 0) {
					onDrawLayout();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void settingDialog() {
		settingDialog = new Dialog(PdfReaderGridView.this,
				android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settingDialog.setContentView(R.layout.setting_dialog);
		settingDialog.setCanceledOnTouchOutside(false);
		settingDialog.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);
		// settingDialog.getWindow().setLayout(300, 600);

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

	public class SendReview extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PdfReaderGridView.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"SET_CONTENT_REVIEW\">"
					+ "<params clientid=\"" + clientid + "\" contentid=\""
					+ documentId + "\" readerid=\"" + userId + "\" title=\""
					+ review_title + "\" rating=\""
					+ Integer.toOctalString(numberofstar) + "\" review=\""
					+ revew_text + "\"/>" + "</operation>" + "</request>";
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
							 * y String id = xpp.getAttributeValue(0); String
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
				/*
				 * database.openAndWriteDataBase();
				 * database.updateDocumentLikes(documentId);
				 * heartButton.setBackgroundResource(R.drawable.heart_plus);
				 */
			}

		}

	}

	public class UnLikeDocumentDocument extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PdfReaderGridView.this);
			progressDialog.setMessage("Data is UnLiked");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"DELETE_READER_FAVORITE\">"
					+ "<params readerid=\"" + userId + "\" clientid=\""
					+ clientid + "\"/>" + "</operation>" + "</request>";
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
							 */
							String id = xpp.getAttributeValue(0);
							String contenttype = xpp.getAttributeValue(1);
							String clintId = xpp.getAttributeValue(2);
							try {

								database.openAndWriteDataBase();
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
				database.openAndWriteDataBase();
				database.updateDocumentUnlikes(documentId);
				heartButton.setBackgroundResource(R.drawable.heart_minus);
			}
		}

	}

	public class LikeDocument extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PdfReaderGridView.this);
			progressDialog.setMessage("Data is Liked");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"ADD_READER_FAVORITE\">"
					+ "<params readerid=\"" + userId + "\" clientid=\""
					+ clientid + "\"/>" + "</operation>" + "</request>";
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
			progressDialog.dismiss();
			if (shareResponse.equals("success")) {
				database.openAndWriteDataBase();
				database.updateDocumentLikes(documentId);
				heartButton.setBackgroundResource(R.drawable.heart_plus);
			}

		}

	}

	public class ShareEmail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(PdfReaderGridView.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"FORWARD_DOCUMENT\">"
					+ "<params email=\"" + emailField.getText().toString()
					+ "\" docid=\"" + documentId + "\" clientid=\"" + clientid
					+ "\" readerid=\"" + userId + "\"/>" + "</operation>"
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
							 */
							String id = xpp.getAttributeValue(0);
							String contenttype = xpp.getAttributeValue(1);
							String clintId = xpp.getAttributeValue(2);
							try {

								database.openAndWriteDataBase();
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
				displayAlert("Done Successfully");
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);

			}
		}

	}

	public void displayAlert(String msg) {
		new AlertDialog.Builder(this)
				.setMessage(msg)
				.setTitle("Share")
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	public void stardialog() {
		RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
		heartlayout.setVisibility(View.GONE);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
		layout.setVisibility(View.GONE);
		final Dialog userInfoDialog = new Dialog(PdfReaderGridView.this);
		userInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		userInfoDialog.setContentView(R.layout.document_review_dialog);
		userInfoDialog.setCanceledOnTouchOutside(false);
		// userInfoDialog.getWindow().setLayout(500, 600);
		userInfoDialog.show();
		final ListView listview = (ListView) userInfoDialog
				.findViewById(R.id.reviewlist);
		reviewList.clear();

		ImageView reImage1 = (ImageView) userInfoDialog
				.findViewById(R.id.restar1);
		ImageView reImage2 = (ImageView) userInfoDialog
				.findViewById(R.id.restar2);
		ImageView reImage3 = (ImageView) userInfoDialog
				.findViewById(R.id.restar3);
		ImageView reImage4 = (ImageView) userInfoDialog
				.findViewById(R.id.restar4);
		ImageView reImage5 = (ImageView) userInfoDialog
				.findViewById(R.id.restar5);
		TextView startTextView = (TextView) userInfoDialog
				.findViewById(R.id.rating_text);
		int totalstar = 0;

		database.openAndReadReviewTable(documentId);
		for (int i = 0; i < ZoomifierDatabase.documentreviewtitle.size(); i++) {
			reviewList.add(new Book(ZoomifierDatabase.documentreviewtitle
					.get(i), ZoomifierDatabase.documentreview.get(i),
					ZoomifierDatabase.time.get(i),
					ZoomifierDatabase.noforeviewstar.get(i)));
			totalstar = totalstar
					+ Integer.parseInt(ZoomifierDatabase.noforeviewstar.get(i));
		}
		int avragestar = 0;
		if (ZoomifierDatabase.documentreviewtitle.size() != 0) {
			avragestar = totalstar
					/ ZoomifierDatabase.documentreviewtitle.size();

			if (avragestar == 1) {
				reImage1.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 2) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 3) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
				reImage3.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 4) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
				reImage3.setImageResource(R.drawable.golden_star);
				reImage4.setImageResource(R.drawable.golden_star);
			} else if (avragestar == 5) {
				reImage1.setImageResource(R.drawable.golden_star);
				reImage2.setImageResource(R.drawable.golden_star);
				reImage3.setImageResource(R.drawable.golden_star);
				reImage4.setImageResource(R.drawable.golden_star);
				reImage5.setImageResource(R.drawable.golden_star);
			}
		}

		startTextView.setText(Integer.toString(reviewList.size()) + " Rating");

		AgendaTimeAdapNew adapter = new AgendaTimeAdapNew(
				PdfReaderGridView.this, 112, reviewList);
		listview.setAdapter(adapter);
		Button writereviewButton = (Button) userInfoDialog
				.findViewById(R.id.review_button);

		writereviewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog reveiewDialgo = new Dialog(PdfReaderGridView.this);
				reveiewDialgo.requestWindowFeature(Window.FEATURE_NO_TITLE);
				reveiewDialgo.setContentView(R.layout.riview_dialog);
				reveiewDialgo.setCanceledOnTouchOutside(false);
				// reveiewDialgo.getWindow().setLayout(500, 700);
				reveiewDialgo.show();
				numberofstar = 0;
				final ImageView imageview1 = (ImageView) reveiewDialgo
						.findViewById(R.id.star1);
				final EditText title = (EditText) reveiewDialgo
						.findViewById(R.id.title_edit);
				final EditText reviewtext = (EditText) reveiewDialgo
						.findViewById(R.id.review_edit);
				imageview1.setTag(R.drawable.black_star);
				final ImageView imageview2 = (ImageView) reveiewDialgo
						.findViewById(R.id.star2);
				imageview2.setTag(R.drawable.black_star);
				final ImageView imageview3 = (ImageView) reveiewDialgo
						.findViewById(R.id.star3);
				imageview3.setTag(R.drawable.black_star);
				final ImageView imageview4 = (ImageView) reveiewDialgo
						.findViewById(R.id.star4);
				imageview4.setTag(R.drawable.black_star);
				final ImageView imageview5 = (ImageView) reveiewDialgo
						.findViewById(R.id.star5);
				imageview5.setTag(R.drawable.black_star);
				final Button submitButton = (Button) reveiewDialgo
						.findViewById(R.id.submit_button);
				submitButton.setBackgroundResource(R.drawable.black_submit);
				submitButton.setTag(R.drawable.black_submit);
				TextWatcher filterTextWatcher = new TextWatcher() {

					public void afterTextChanged(Editable s) {
					}

					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}

				};
				title.addTextChangedListener(filterTextWatcher);
				reviewtext.addTextChangedListener(filterTextWatcher);

				imageview1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						/*
						 * if(imageview2.getTag().equals(R.drawable.black_star))
						 * {
						 */
						if (imageview1.getTag().equals(R.drawable.black_star)) {
							imageview1.setImageResource(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							imageview1.setTag(R.drawable.golden_star);
						} else {

							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.golden_star)) {
								imageview4
										.setImageResource(R.drawable.black_star);
								imageview4.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.golden_star)) {
								imageview3
										.setImageResource(R.drawable.black_star);
								imageview3.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview2.getTag().equals(
									R.drawable.golden_star)) {
								imageview2
										.setImageResource(R.drawable.black_star);
								imageview2.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview1.setImageResource(R.drawable.black_star);
							imageview1.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}
						// }

					}
				});

				imageview2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview1.getTag().equals(R.drawable.golden_star)&&
						 * imageview3.getTag().equals(R.drawable.black_star)) {
						 */
						if (imageview2.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

						} else {
							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.golden_star)) {
								imageview4
										.setImageResource(R.drawable.black_star);
								imageview4.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.golden_star)) {
								imageview3
										.setImageResource(R.drawable.black_star);
								imageview3.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview2.setImageResource(R.drawable.black_star);
							imageview2.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}

					// }
				});

				imageview3.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview2.getTag().equals(R.drawable.golden_star)&&
						 * imageview4.getTag().equals(R.drawable.black_star)) {
						 */
						if (imageview3.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.black_star)) {
								imageview3
										.setImageResource(R.drawable.golden_star);
								imageview3.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
						} else {
							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.golden_star)) {
								imageview4
										.setImageResource(R.drawable.black_star);
								imageview4.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview3.setImageResource(R.drawable.black_star);
							imageview3.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;

						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

						// }
					}
				});

				imageview4.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview3.getTag().equals(R.drawable.golden_star)&&
						 * imageview5.getTag().equals(R.drawable.black_star)) {
						 */
						if (imageview4.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.black_star)) {
								imageview3
										.setImageResource(R.drawable.golden_star);
								imageview3.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.black_star)) {
								imageview4
										.setImageResource(R.drawable.golden_star);
								imageview4.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
						} else {
							if (imageview5.getTag().equals(
									R.drawable.golden_star)) {
								imageview5
										.setImageResource(R.drawable.black_star);
								imageview5.setTag(R.drawable.black_star);
								numberofstar = numberofstar - 1;
							}

							imageview4.setImageResource(R.drawable.black_star);
							imageview4.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}

						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}
					// }
				});

				imageview5.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*
						 * if(imageview4.getTag().equals(R.drawable.golden_star))
						 * {
						 */
						if (imageview5.getTag().equals(R.drawable.black_star)) {
							if (imageview1.getTag().equals(
									R.drawable.black_star)) {
								imageview1
										.setImageResource(R.drawable.golden_star);
								imageview1.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}

							if (imageview2.getTag().equals(
									R.drawable.black_star)) {
								imageview2
										.setImageResource(R.drawable.golden_star);
								imageview2.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview3.getTag().equals(
									R.drawable.black_star)) {
								imageview3
										.setImageResource(R.drawable.golden_star);
								imageview3.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview4.getTag().equals(
									R.drawable.black_star)) {
								imageview4
										.setImageResource(R.drawable.golden_star);
								imageview4.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
							if (imageview5.getTag().equals(
									R.drawable.black_star)) {
								imageview5
										.setImageResource(R.drawable.golden_star);
								imageview5.setTag(R.drawable.golden_star);
								numberofstar = numberofstar + 1;
							}
						} else {

							imageview5.setImageResource(R.drawable.black_star);
							imageview5.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if (!title.getText().toString().equals("")
								&& !reviewtext.getText().toString().equals("")
								&& imageview1.getTag().equals(
										R.drawable.golden_star)) {
							submitButton
									.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						} else {
							submitButton
									.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

						// }

					}
				});

				submitButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (submitButton.getTag().equals(R.drawable.red_submit)) {
							Calendar calendar = Calendar.getInstance();
							int mYear = calendar.get(Calendar.YEAR);
							int mMonth = calendar.get(Calendar.MONTH);
							int mDay = calendar.get(Calendar.DAY_OF_MONTH);
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat month_date = new SimpleDateFormat(
									"MMMMMMMMM");
							String month_name = month_date.format(cal.getTime());
							int date = calendar.get(Calendar.DATE);
							review_title = title.getText().toString();

							String completDate = Integer.toString(date) + ", "
									+ month_name + " "
									+ Integer.toString(mYear);
							review_time = completDate;
							revew_text = reviewtext.getText().toString();
							database.insertIntoReviewTable(documentId, title
									.getText().toString(), reviewtext.getText()
									.toString(),
									Integer.toString(numberofstar), completDate);
							new SendReview().execute();
							reviewList.clear();
							ImageView reImage1 = (ImageView) userInfoDialog
									.findViewById(R.id.restar1);
							ImageView reImage2 = (ImageView) userInfoDialog
									.findViewById(R.id.restar2);
							ImageView reImage3 = (ImageView) userInfoDialog
									.findViewById(R.id.restar3);
							ImageView reImage4 = (ImageView) userInfoDialog
									.findViewById(R.id.restar4);
							ImageView reImage5 = (ImageView) userInfoDialog
									.findViewById(R.id.restar5);
							TextView startTextView = (TextView) userInfoDialog
									.findViewById(R.id.rating_text);
							int totalstar = 0;

							database.openAndReadReviewTable(documentId);
							for (int i = 0; i < ZoomifierDatabase.documentreviewtitle
									.size(); i++) {
								reviewList.add(new Book(
										ZoomifierDatabase.documentreviewtitle
												.get(i),
										ZoomifierDatabase.documentreview.get(i),
										ZoomifierDatabase.time.get(i),
										ZoomifierDatabase.noforeviewstar.get(i)));
								totalstar = totalstar
										+ Integer
												.parseInt(ZoomifierDatabase.noforeviewstar
														.get(i));
							}
							int avragestar = 0;
							if (ZoomifierDatabase.documentreviewtitle.size() != 0) {
								avragestar = totalstar
										/ ZoomifierDatabase.documentreviewtitle
												.size();
								if (avragestar == 1) {
									reImage1.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 2) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 3) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
									reImage3.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 4) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
									reImage3.setImageResource(R.drawable.golden_star);
									reImage4.setImageResource(R.drawable.golden_star);
								} else if (avragestar == 5) {
									reImage1.setImageResource(R.drawable.golden_star);
									reImage2.setImageResource(R.drawable.golden_star);
									reImage3.setImageResource(R.drawable.golden_star);
									reImage4.setImageResource(R.drawable.golden_star);
									reImage5.setImageResource(R.drawable.golden_star);
								}
							}
							startTextView.setText(Integer.toString(reviewList
									.size()) + " Rating");
							AgendaTimeAdapNew adapter = new AgendaTimeAdapNew(
									PdfReaderGridView.this, 112, reviewList);
							listview.setAdapter(adapter);
							reveiewDialgo.dismiss();

						}

					}
				});
				Button cancelButton = (Button) reveiewDialgo
						.findViewById(R.id.cancel_text);
				cancelButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						reveiewDialgo.dismiss();

					}
				});
			}
		});
	}

	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	public void onDrawLayout() {
		layout.removeAllViews();
		HorizontalPager realViewSwitcher = (HorizontalPager) findViewById(R.id.horizontal_pager);
		realViewSwitcher.removeAllViews();
		realViewSwitcher.setOnScreenSwitchListener(onScreenSwitchListener);
		int numberof_item = thumbLis.size();
		int numberofscreen;
		int extrascreenitem = 0;
		int m = numberof_item % 12;
		int d = numberof_item / 12;
		if (m == 0) {
			numberofscreen = d;
			extrascreenitem = 0;
		} else {
			numberofscreen = d + 1;
			extrascreenitem = m;
		}
		Display display = ((WindowManager) getSystemService(this.WINDOW_SERVICE))
				.getDefaultDisplay();

		int orientation = display.getOrientation();
		int c = 0;

		int ot = getResources().getConfiguration().orientation;
		if (ot == Configuration.ORIENTATION_PORTRAIT) {

			for (int k = 1; k <= numberofscreen; k++) {

				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundColor(Color.WHITE);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				display = wm.getDefaultDisplay();
				int width = display.getWidth();
				int height = display.getHeight();
				int theight = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
								.getDisplayMetrics());

				int rowheight;
				if (height > 800)
					rowheight = (height - theight - 130) / 4;
				else
					rowheight = (height - theight - 80) / 4;
				int colunsize = width / 3;
				int selfheight = rowheight / 4 + 30;
				int itemheight = rowheight / 2;
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
						parmas.setMargins(colunsize * l, rowheight * i
								- (selfheight + itemheight - 15), 10, 0);
						imageview.setLayoutParams(parmas);
						String url = null;
						try {
							url = "http://ve1.zoomifier.net/"
									+ thumbLis.get(c).client_id + "/"
									+ thumbLis.get(c).documentId + "/ipad/Page"
									+ Integer.toString(c + 1) + "Thb.jpg";
						} catch (Exception e) {
							e.printStackTrace();
						}

						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								PdfReaderGridView.this);
						imageview.setId(c);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										int id = arg0.getId();
										Bundle bundle = new Bundle();
										bundle.putString("documentId",
												documentId);
										bundle.putString("documentName",
												documentName);
										bundle.putString("documentDiscription",
												documentDiscription);
										bundle.putString("documentOrginalId",
												documentOrginalId);
										bundle.putString("documentContentType",
												documentContentType);
										bundle.putString(
												"documentContentWidth",
												documentContentWidth);
										bundle.putString(
												"documentContentHeight",
												documentContentHeight);
										bundle.putString("documentOwnerId",
												documentOwnerId);
										bundle.putInt("position", id);
										bundle.putString("clientid", clientid);
										bundle.putString("userid", userId);
										bundle.putInt("noofpage", noofpage);

										Intent intent = new Intent(
												PdfReaderGridView.this,
												PDFImageViewActivity.class);
										intent.putExtras(bundle);
										intent.putExtra("pagedata", pagedata);
										PdfReaderGridView.this
												.startActivity(intent);
										finish();
									}
								});
						imagedownlaoder.DisplayImage(url, imageview);
						mainlayout.addView(imageview);

						TextView textview = new TextView(this);
						parmas = new RelativeLayout.LayoutParams(colunsize,
								selfheight);
						parmas.setMargins(colunsize * l + 4, rowheight * i
								- selfheight, 0, 0);
						textview.setSingleLine();
						textview.setText(thumbLis.get(c).documentName);
						textview.setEllipsize(TextUtils.TruncateAt.END);
						if (thumbLis.get(c).signal == 1) {
							textview.setTextColor(Color.RED);
						} else {
							textview.setTextColor(Color.BLACK);
						}
						textview.setLayoutParams(parmas);
						textview.setGravity(Gravity.CENTER);
						mainlayout.addView(textview);

						c++;

					}
				}
				realViewSwitcher.addView(mainlayout);
				LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
						10, 10);
				ImageView pagedivider = new ImageView(PdfReaderGridView.this);
				pagedivider.setLayoutParams(parma);
				if (k == 1)
					pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);
				layout.addView(pagedivider);
			}
		} else {
			int width = display.getWidth();
			int height = display.getHeight();
			int theight = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
							.getDisplayMetrics());
			int rowheight;
			if (height >= 720)
				rowheight = (height - theight - 130) / 3;
			else
				rowheight = (height - theight - 80) / 3;
			int colunsize = width / 4;
			int selfheight = rowheight / 4 + 30;
			int itemheight = rowheight / 2;
			for (int k = 1; k <= numberofscreen; k++) {
				RelativeLayout mainlayout = new RelativeLayout(this);
				mainlayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mainlayout.setBackgroundColor(Color.WHITE);
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				display = wm.getDefaultDisplay();

				int itemnumber;
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
								+ thumbLis.get(c).client_id + "/"
								+ thumbLis.get(c).documentId + "/ipad/Page"
								+ Integer.toString(c + 1) + "Thb.jpg";
						PDImagedownloader imagedownlaoder = new PDImagedownloader(
								PdfReaderGridView.this);
						imagedownlaoder.DisplayImage(url, imageview);
						imageview.setId(c);
						imageview
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										int id = arg0.getId();
										Bundle bundle = new Bundle();
										bundle.putString("documentId",
												documentId);
										bundle.putString("documentName",
												documentName);
										bundle.putString("documentDiscription",
												documentDiscription);
										bundle.putString("documentOrginalId",
												documentOrginalId);
										bundle.putString("documentContentType",
												documentContentType);
										bundle.putString(
												"documentContentWidth",
												documentContentWidth);
										bundle.putString(
												"documentContentHeight",
												documentContentHeight);
										bundle.putString("documentOwnerId",
												documentOwnerId);
										bundle.putInt("position", id);
										bundle.putString("clientid", clientid);
										bundle.putString("userid", userId);
										bundle.putInt("noofpage", noofpage);

										Intent intent = new Intent(
												PdfReaderGridView.this,
												PDFImageViewActivity.class);
										intent.putExtras(bundle);
										intent.putExtra("pagedata", pagedata);
										PdfReaderGridView.this
												.startActivity(intent);
										finish();
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
						textview.setText(thumbLis.get(c).documentName);
						if (thumbLis.get(c).signal == 1) {
							textview.setTextColor(Color.RED);
						} else {
							textview.setTextColor(Color.BLACK);
						}
						textview.setLayoutParams(parmas);
						textview.setGravity(Gravity.CENTER);
						mainlayout.addView(textview);
						c++;

					}
				}
				realViewSwitcher.addView(mainlayout);
				LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
						10, 10);
				ImageView pagedivider = new ImageView(PdfReaderGridView.this);
				pagedivider.setLayoutParams(parma);
				if (k == 1)
					pagedivider.setImageResource(R.drawable.blue_dot);
				else
					pagedivider.setImageResource(R.drawable.gray_dot);
				layout.addView(pagedivider);
			}

		}

	}

	private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener = new HorizontalPager.OnScreenSwitchListener() {
		@Override
		public void onScreenSwitched(final int screen) {
			for (int i = 0; i < layout.getChildCount(); i++) {
				ImageView imageView = (ImageView) layout.getChildAt(i);
				imageView.setImageResource(R.drawable.gray_dot);
			}
			ImageView imageView = (ImageView) layout.getChildAt(screen);
			imageView.setImageResource(R.drawable.blue_dot);

		}
	};}
