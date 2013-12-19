package com.zoomactivity;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.zoomactivity.MyLikesActivity.loadBarCodeList;
import com.zoomactivity.MyLikesActivity.ChangePassword;
import com.zoomactivity.MyLikesActivity.LoadCategoryList;
import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.adapter.SecondGridAdapter;
import com.zoomifier.database.ZoomifierDatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyLikesActivity extends Activity {
	ListView listView;
	Button  settingButton, signOut,barCodeScaneButton,searchbutton,
	redcross_button;
	RelativeLayout  my_likes_button,folder_view_button;
	Vector<SecondActivityData> grid_data = new Vector<SecondActivityData>();
	ProgressDialog progressDialog;
	String readerId, categoryResponse,clientId, userEmail;
	ZoomifierDatabase database;
	RelativeLayout layout, webView_layout, layout2, setting_Dialog;
	String email_id;
	String changeEmail, changeoldPassword, changernewPassword;
	private static final int ZBAR_SCANNER_REQUEST = 0;
	String barCodeValue;
	public static Activity myactivit;
   RelativeLayout transparentlayout;
   EditText searchEditField;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylikes_layout);
		this.overridePendingTransition(R.anim.slide_left_to_right,
                R.anim.slide_right_to_left);
		myactivit=this;
		Bundle bundle = getIntent().getExtras();
		readerId = bundle.getString("readerId");
		clientId = bundle.getString("clientId");
		email_id = userEmail = bundle.getString("email_id");
		init();
		SharedPreferences myPrefs = getApplicationContext()
				.getSharedPreferences("myPrefs", MODE_PRIVATE);
		readerId = myPrefs.getString("userId", "");
		email_id = userEmail = myPrefs.getString("userEmail", "");
		signOut.setText("(sign out)  " + userEmail);
		grid_data.clear();

		new LoadCategoryList().execute();

		my_likes_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		barCodeScaneButton = (Button) findViewById(R.id.barcode_scane);
		barCodeScaneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					 Intent intent = new Intent(MyLikesActivity.this,ZBarScannerActivity.class);
					 startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (grid_data.get(arg2).documentContentType
						.equals("DOCUMENT")) {
					Bundle bundle = new Bundle();
					bundle.putString(
							"documentId",
							grid_data.get(arg2).documentId);
					bundle.putString(
							"documentName",
							grid_data.get(arg2).documentName);
					bundle.putString(
							"documentDiscription",
							grid_data.get(arg2).documentDiscription);
					bundle.putString(
							"originalId",
							grid_data.get(arg2).originalId);
					bundle.putString(
							"documentContentType",
							grid_data.get(arg2).documentContentType);
					bundle.putString(
							"documentContentWidth",
							grid_data.get(arg2).documentContentWidth);
					bundle.putString(
							"documentContentHeight",
							grid_data.get(arg2).documentContentHeight);
					bundle.putString(
							"documentOwnerId",
							grid_data.get(arg2).documentOwnerId);
					bundle.putInt("position", 0);
					bundle.putString("clientid",
							grid_data.get(arg2).client_id);
					bundle.putString("userid",
							grid_data.get(arg2).userID);
					bundle.putString(
							"favorit",
							grid_data.get(arg2).like_document);
					bundle.putString(
							"sharedby",
							grid_data.get(arg2).SharedName);
					bundle.putString(
							"shareddate",
							grid_data.get(arg2).SharedDate);

					Intent intent = new Intent(
							MyLikesActivity.this,
							PDFImageViewActivity.class);
					intent.putExtras(bundle);
					ArrayList<String> arraylist = new ArrayList<String>();
					PageData pageData = new PageData(
							arraylist);
					intent.putExtra("pagedata",
							pageData);
					startActivity(intent);
				} else if (grid_data.get(arg2).documentContentType
						.equals("VIDEO")) {
					Bundle bundle = new Bundle();
					bundle.putString(
							"documentId",
							grid_data.get(arg2).documentId);
					bundle.putString(
							"documentName",
							grid_data.get(arg2).documentName);
					bundle.putString(
							"documentDiscription",
							grid_data.get(arg2).documentDiscription);
					bundle.putString(
							"originalId",
							grid_data.get(arg2).originalId);
					bundle.putString(
							"documentContentType",
							grid_data.get(arg2).documentContentType);
					bundle.putString(
							"documentContentWidth",
							grid_data.get(arg2).documentContentWidth);
					bundle.putString(
							"documentContentHeight",
							grid_data.get(arg2).documentContentHeight);
					bundle.putString(
							"documentOwnerId",
							grid_data.get(arg2).documentOwnerId);
					bundle.putInt("position", arg2);
					bundle.putString("clientid",
							grid_data.get(arg2).client_id);
					bundle.putString("userid",
							grid_data.get(arg2).userID);
					bundle.putString(
							"favorit",
							grid_data.get(arg2).like_document);
					bundle.putString(
							"sharedby",
							grid_data.get(arg2).SharedName);
					bundle.putString(
							"shareddate",
							grid_data.get(arg2).SharedDate);
					Intent intent = new Intent(
							MyLikesActivity.this,
							ZoomifierVideoPlayer.class);
					intent.putExtras(bundle);
					ArrayList<String> arraylist = new ArrayList<String>();
					PageData pageData = new PageData(
							arraylist);
					intent.putExtra("pagedata",
							pageData);
					startActivity(intent);
				}
				else if(grid_data.get(arg2).documentContentType
						.equals("HTML5"))
				{
					Bundle bundle = new Bundle();
					bundle.putString(
							"documentId",
							grid_data.get(arg2).documentId);
					bundle.putString(
							"documentName",
							grid_data.get(arg2).documentName);
					bundle.putString(
							"documentDiscription",
							grid_data.get(arg2).documentDiscription);
					bundle.putString(
							"originalId",
							grid_data.get(arg2).originalId);
					bundle.putString(
							"documentContentType",
							grid_data.get(arg2).documentContentType);
					bundle.putString(
							"documentContentWidth",
							grid_data.get(arg2).documentContentWidth);
					bundle.putString(
							"documentContentHeight",
							grid_data.get(arg2).documentContentHeight);
					bundle.putString(
							"documentOwnerId",
							grid_data.get(arg2).documentOwnerId);
					bundle.putInt("position", arg2);
					bundle.putString("clientid",
							grid_data.get(arg2).client_id);
					bundle.putString(
							"favorit",
							grid_data.get(arg2).like_document);
					bundle.putString("userid",
							grid_data.get(arg2).userID);
					bundle.putString(
							"sharedby",
							grid_data.get(arg2).SharedName);
					bundle.putString(
							"shareddate",
							grid_data.get(arg2).SharedDate);
					Intent intent = new Intent(
							MyLikesActivity.this,
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
							grid_data.get(arg2).documentId);
					bundle.putString(
							"documentName",
							grid_data.get(arg2).documentName);
					bundle.putString(
							"documentDiscription",
							grid_data.get(arg2).documentDiscription);
					bundle.putString(
							"originalId",
							grid_data.get(arg2).originalId);
					bundle.putString(
							"documentContentType",
							grid_data.get(arg2).documentContentType);
					bundle.putString(
							"documentContentWidth",
							grid_data.get(arg2).documentContentWidth);
					bundle.putString(
							"documentContentHeight",
							grid_data.get(arg2).documentContentHeight);
					bundle.putString(
							"documentOwnerId",
							grid_data.get(arg2).documentOwnerId);
					bundle.putInt("position", arg2);
					bundle.putString("clientid",
							grid_data.get(arg2).client_id);
					bundle.putString(
							"favorit",
							grid_data.get(arg2).like_document);
					bundle.putString("userid",
							grid_data.get(arg2).userID);
					bundle.putString(
							"sharedby",
							grid_data.get(arg2).SharedName);
					bundle.putString(
							"shareddate",
							grid_data.get(arg2).SharedDate);
					Intent intent = new Intent(
							MyLikesActivity.this,
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

		folder_view_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("readerId", readerId);
				bundle.putString("clientId", clientId);
				bundle.putString("email_id", email_id);
				
				Intent intetn = new Intent(MyLikesActivity.this,
						GridActivity.class);
				intetn.putExtras(bundle);
				startActivity(intetn);

			}
		});
transparentlayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setting_Dialog.setVisibility(View.GONE);
				transparentlayout.setVisibility(View.GONE);
				
			}
		});
		settingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (setting_Dialog.isShown()) {
					setting_Dialog.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				} else {
					transparentlayout.setVisibility(View.VISIBLE);
					setting_Dialog.setVisibility(View.VISIBLE);
					signOut = (Button) findViewById(R.id.email_field);
					signOut.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							setting_Dialog.setVisibility(View.GONE);
							Intent mainIntent = new Intent(
									MyLikesActivity.this, MainActivity.class);
							startActivity(mainIntent);
							SharedPreferences myPrefs = MyLikesActivity.this
									.getSharedPreferences("myPrefs",
											MODE_WORLD_READABLE);
							SharedPreferences.Editor prefsEditor = myPrefs
									.edit();
							prefsEditor.putString("login", "unsccuess");
							prefsEditor.putString("databasecreation",
									"unsuccess");
							prefsEditor.putString("clientlist", "unsuccess");
							prefsEditor.commit();
							prefsEditor.commit();
							MyLikesActivity.this
									.deleteDatabase("zoomifierdatabase");

							finish();
						}
					});
					Button signUserInfo = (Button) findViewById(R.id.chan_userinfo_btn);
					signUserInfo.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							setting_Dialog.setVisibility(View.GONE);
							final Dialog userInfoDialog = new Dialog(
									MyLikesActivity.this);
							userInfoDialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							userInfoDialog
									.setContentView(R.layout.change_user_info);

							userInfoDialog.setCanceledOnTouchOutside(false);
							userInfoDialog.getWindow().setLayout(400, 500);
							userInfoDialog.show();
							Button cancelButton = (Button) userInfoDialog
									.findViewById(R.id.cancel_button);
							cancelButton
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											userInfoDialog.dismiss();

										}
									});

							SharedPreferences myPrefsd = getApplicationContext()
									.getSharedPreferences("myPrefs",
											MODE_PRIVATE);
							String firstNamee = myPrefsd.getString(
									"userfirstName", "");
							String lastNamee = myPrefsd.getString(
									"userLastName", "");
							final EditText firstNmae = (EditText) userInfoDialog
									.findViewById(R.id.firt_name_field);
							firstNmae.setText(firstNamee);
							TextView email_editText = (TextView) userInfoDialog
									.findViewById(R.id.email_name_field);
							email_editText.setText(email_id);
							final EditText lastButton = (EditText) userInfoDialog
									.findViewById(R.id.last_name_field);
							lastButton.setText(lastNamee);
							Button saveButton = (Button) userInfoDialog
									.findViewById(R.id.save_button);
							saveButton
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											TextView email_editText = (TextView) userInfoDialog
													.findViewById(R.id.email_name_field);
											changeEmail = email_editText
													.getText().toString();
											EditText current_passwrod = (EditText) userInfoDialog
													.findViewById(R.id.current_password_fields);
											changeoldPassword = current_passwrod
													.getText().toString();
											EditText new_passwrod = (EditText) userInfoDialog
													.findViewById(R.id.new_password_fields);
											changernewPassword = new_passwrod
													.getText().toString();
											EditText verifiypassword = (EditText) userInfoDialog
													.findViewById(R.id.verify_password_fields);

											if (changeEmail.equals("")) {
												displayAlert("Enter Email");
											} else {
												if (changernewPassword
														.equals(verifiypassword
																.getText()
																.toString())) {
													new ChangePassword()
															.execute();
													userInfoDialog.dismiss();
													SharedPreferences myPrefs = MyLikesActivity.this
															.getSharedPreferences(
																	"myPrefs",
																	MODE_WORLD_READABLE);
													SharedPreferences.Editor prefsEditor = myPrefs
															.edit();
													prefsEditor
															.putString(
																	"userfirstName",
																	firstNmae
																			.getText()
																			.toString());
													prefsEditor
															.putString(
																	"userLastName",
																	lastButton
																			.getText()
																			.toString());
													prefsEditor.commit();
												} else {
													displayAlert("New Password and verify New Passwrod should be same");
												}
											}

										}
									});
						}
					});
				}

			}

		});

	}

	public void init() {
		folder_view_button = (RelativeLayout) findViewById(R.id.folder_layout);
		listView = (ListView) findViewById(R.id.library_listview);
		my_likes_button = (RelativeLayout) findViewById(R.id.library_layout);
		database = new ZoomifierDatabase(this);
		settingButton = (Button) findViewById(R.id.setting_button);
		signOut = (Button) findViewById(R.id.email_field);
		setting_Dialog = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
		transparentlayout=(RelativeLayout) findViewById(R.id.transparentlayout);
		searchbutton = (Button) findViewById(R.id.searchbutton);
		redcross_button = (Button) findViewById(R.id.redcrossbutton);
		searchEditField = (EditText) findViewById(R.id.search_speaker_edit_text);
		redcross_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchEditField.setVisibility(View.GONE);
				redcross_button.setVisibility(View.GONE);
				searchbutton.setVisibility(View.VISIBLE);
				settingButton.setVisibility(View.VISIBLE);
				barCodeScaneButton.setVisibility(View.VISIBLE);
			}
		});
		searchbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchEditField.setVisibility(View.VISIBLE);
				redcross_button.setVisibility(View.VISIBLE);
				searchbutton.setVisibility(View.GONE);
				settingButton.setVisibility(View.GONE);
				barCodeScaneButton.setVisibility(View.GONE);
			}
		});
		searchEditField
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (!searchEditField.getText().toString().equals("")) {
						//new SearchPDF().execute();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								searchEditField.getWindowToken(), 0);
						Bundle bundle=new Bundle();
						bundle.putString("searchtext", searchEditField.getText().toString());
						bundle.putString("readrId", readerId);
						Intent intent=new Intent(MyLikesActivity.this,SearchResultClass.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}

					return true;
				}
				return false;
			}
		});


	}

	public class LoadCategoryList extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new ProgressDialog(MyLikesActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			grid_data.clear();
			
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
				String str=categoryResponse;

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
							
							database.openAndWriteDataBase();
						

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
				SecondGridAdapter listAdapter = new SecondGridAdapter(
						MyLikesActivity.this, 12, grid_data);
				listView.setAdapter(listAdapter);
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

	public class ChangePassword extends AsyncTask<Void, Void, Void> {
		String shareResponse, retrunResponse;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MyLikesActivity.this);
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

				displayAlert("Change Successfully");
			}

		}

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
			progressDialog = new ProgressDialog(MyLikesActivity.this);
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
					if(ZoomifierDatabase.companyId.size()==0)
					database.insertIntoCompanyTable(clientid, clientname, imageformat);
					
					database.insertIntoDocumentTable(documentid, documentName,
							documnetdesc, documentriginalid, documcontenttype,
							documentwidth, documnetHeight, ownerid, "0", " ",
							" ", " ", " ", "", "", clientid, clientname,
							imageformat, readerId);
			/*		grid_data.add(new SecondActivityData(documentid,
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
					database.insertIntoQRTable(barCodeValue, "0", startTime,"");
					
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

				SecondGridAdapter listAdapter = new SecondGridAdapter(
						MyLikesActivity.this, 12, grid_data);
				listView.setAdapter(listAdapter);

				// listView.deferNotifyDataSetChanged();

				if (signal.equals("outqrcode")) {
					Bundle bundle = new Bundle();
					bundle.putString("url", barCodeValue);
					Intent intent = new Intent(MyLikesActivity.this,
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

						Intent intent = new Intent(MyLikesActivity.this,
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
						Intent intent = new Intent(MyLikesActivity.this,
								ZoomifierVideoPlayer.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
					}
					else if(documcontenttype.equals("HTML5"))
					{
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
						Intent intent = new Intent(MyLikesActivity.this,
								HtmlContentActivity.class);
						intent.putExtras(bundle);
						ArrayList<String> arraylist = new ArrayList<String>();
						PageData pageData = new PageData(arraylist);
						intent.putExtra("pagedata", pageData);
						startActivity(intent);
					}
						else {
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
						Intent intent = new Intent(MyLikesActivity.this,
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

}
