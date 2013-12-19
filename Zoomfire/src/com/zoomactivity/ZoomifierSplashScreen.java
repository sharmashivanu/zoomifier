package com.zoomactivity;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.adapter.SecondGridAdapter;
import com.zoomifier.database.ZoomifierDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ZoomifierSplashScreen extends Activity{
	RelativeLayout spalashlayout;
	TextView reanderName;
	Button scaneButton, libraryButton;
	private static final int ZBAR_SCANNER_REQUEST = 0;
	String barCodeValue;
	String categoryResponse;
	ProgressDialog progressDialog;
	ZoomifierDatabase database;
	String docId, email_id, readerId;
	String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		spalashlayout = (RelativeLayout) findViewById(R.id.splashlayout);
		database = new ZoomifierDatabase(this);
		SharedPreferences myPrefs = ZoomifierSplashScreen.this
				.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		String loginscreen = myPrefs.getString("login", "");
		String userName = myPrefs.getString("readername", "");
		barCodeValue="";

		if (loginscreen.equals("success")) {
			try {
				reanderName = (TextView) findViewById(R.id.welcome_textviews);
				reanderName.setText("Welcome " + userName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			reanderName = (TextView) findViewById(R.id.welcome_textviews);
			reanderName.setText("Welcome Community user");
		}
		scaneButton = (Button) findViewById(R.id.scanbutton);

		scaneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences myPrefs = ZoomifierSplashScreen.this
						.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
				String loginscreen = myPrefs.getString("login", "");
				String userName = myPrefs.getString("readername", "");

				if (loginscreen.equals("success")) {

					try {
						readerId = myPrefs.getString("userId", "");
						email_id = myPrefs.getString("userEmail", "");

						Intent intent = new Intent(ZoomifierSplashScreen.this,
								ZBarScannerActivity.class);
						startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Intent intent = new Intent(ZoomifierSplashScreen.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}

			}
		});
		libraryButton = (Button) findViewById(R.id.library_button);
		libraryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences myPrefs = ZoomifierSplashScreen.this
						.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
				String loginscreen = myPrefs.getString("login", "");
				String userName = myPrefs.getString("readername", "");

				if (loginscreen.equals("success")) {
					reanderName.setText("Welcome " + userName);
					Bundle bundle = new Bundle();
					bundle.putString("email_id", "");
					Intent intent = new Intent(ZoomifierSplashScreen.this,
							LibraryClass.class);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(ZoomifierSplashScreen.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}

			}
		});

		/*
		 * spalashlayout.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { SharedPreferences myPrefs =
		 * ZoomifierSplashScreen.this.getSharedPreferences("myPrefs",
		 * MODE_WORLD_READABLE); String
		 * loginscreen=myPrefs.getString("login",""); String
		 * userName=myPrefs.getString("readername","");
		 * 
		 * if(loginscreen.equals("success")) {
		 * reanderName.setText("Welcome "+userName); Bundle bundle=new Bundle();
		 * bundle.putString("email_id",""); Intent intent=new
		 * Intent(ZoomifierSplashScreen.this,GridActivity.class);
		 * intent.putExtras(bundle); startActivity(intent); finish(); } else {
		 * Intent intent=new
		 * Intent(ZoomifierSplashScreen.this,MainActivity.class);
		 * startActivity(intent); finish(); }
		 * 
		 * 
		 * } });
		 */
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ZBAR_SCANNER_REQUEST:
			if (resultCode == RESULT_OK) {

				barCodeValue = data.getStringExtra(ZBarConstants.SCAN_RESULT);
				if (barCodeValue == null || barCodeValue.equals(""))
					Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
				else
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
			progressDialog = new ProgressDialog(ZoomifierSplashScreen.this);
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
					database.insertIntoDocumentTable(documentid, documentName,
							documnetdesc, documentriginalid, documcontenttype,
							documentwidth, documnetHeight, ownerid, "0", " ",
							" ", " ", " ", "", "", clientid, clientname,
							imageformat, readerId);
					/*grid_data.add(new SecondActivityData(documentid,
							documentName, documnetdesc, documentriginalid,
							documcontenttype, documentwidth, documnetHeight,
							ownerid, "0", " ", " ", " ", " ", "", "", clientid,
							clientname, imageformat, readerId));*/
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
					/*grid_data.add(new SecondActivityData("qrcode",
							barCodeValue, "", "", "", "", "", "", "0", " ",
							" ", " ", startTime, "", "", "", "", "", ""));*/
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
					Intent intent = new Intent(ZoomifierSplashScreen.this,
							BarCodeWebViewActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);

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

						Intent intent = new Intent(ZoomifierSplashScreen.this,
								PDFImageViewActivity.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
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
						Intent intent = new Intent(ZoomifierSplashScreen.this,
								ZoomifierVideoPlayer.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
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
						bundle.putString("clientid", clientid);
						bundle.putString("favorit", documentlike);
						bundle.putString("userid", readerId);
						bundle.putString("sharedby", sharedName);
						bundle.putString("shareddate", sharedDate);
						Intent intent = new Intent(ZoomifierSplashScreen.this,
								ZoomifierView.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
					}

				}

			} catch (Exception e) {

			}
		}

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

	/*@Override
	protected void onRestart() {
		
		super.onRestart();
		if(!barCodeValue.equals(""))
		{
			
			Bundle bundle = new Bundle();
			bundle.putString("email_id", email_id);
			Intent intent = new Intent(ZoomifierSplashScreen.this,
					LibraryClass.class);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}
	}*/
	
}
