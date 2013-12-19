package com.zoomactivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.zoomifier.database.ZoomifierDatabase;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteException;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	String response;
	String singUpresponse;
	Dialog loginInterfaceDialog;
	String returnResponse = null;
	String responseResult, userUserid, userFirstname, userLastname, userEmail,
			userLocationid, clientClientid, clientName, clientDescription;
	Button loginButton, signUpButton;
	EditText signEmail, signPassword, signUpEmail, signUpPassword,
			signUpverifiyPassword, signUpFirstName, singUpLastName,
			signUpcompanyName;
	ProgressDialog progressDialog;
	String clientId;
	TextView changeuserinfo;
	//static ZoomifierDatabase database;
	String retrunResponse;
	String shareResponse;
	String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dialogGenrater();
		init();
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainActivity.this,SignInActivity.class);
				startActivity(intent);
				finish();
			
				/*if (signEmail.getText().toString().equals("")) {
					displayAlert("Enter email Id", "Sign In");
				} else if (signPassword.getText().toString().equals("")) {
					displayAlert("Enter Password", "Sign In");
				}

				else {
					new loadList().execute();
				}*/
			}
		});
		/*changeuserinfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog alertDialog = new AlertDialog.Builder(
						MainActivity.this).create();
				Context c = getBaseContext();

				final EditText text = new EditText(MainActivity.this);
				text.setSingleLine();
				// Add text to dialog
				alertDialog.setView(text);
				alertDialog.setTitle("Forgot Password");
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								email = text.getText().toString();
								if (!email.equals("")) {
									if (checkEmail(email)) {
										new ChangeUserInfo().execute();
									} else {
										displayAlert("Enter valid Email Id",
												"Forgot Password");
									}
								}

							}
						});
				alertDialog.setButton2("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								alertDialog.dismiss();
							}
						});
				alertDialog.show();

			}
		});*/

		signUpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			/*	if (signUpEmail.getText().toString().equals("")) {
					displayAlert("Enter Email", "Sign Up");
				} else if (signUpFirstName.getText().toString().equals("")) {
					displayAlert("Enter First Name", "Sign Up");

				} else if (singUpLastName.getText().toString().equals("")) {
					displayAlert("Enter Last Name", "Sing Up");

				} else if (signUpcompanyName.getText().toString().equals("")) {
					displayAlert("Enter Company Name", "Sign Up");
				}

				else if (signUpPassword.getText().toString().equals("")) {
					displayAlert("Enter Password", "Sign Up");

				} else if (!signUpverifiyPassword.getText().toString()
						.equals(signUpPassword.getText().toString())) {
					displayAlert("Password does not match", "Sign Up");
				} else if (!checkEmail(signUpEmail.getText().toString())) {
					displayAlert("Enter valid email", "Sign Up");

				} else {
					new SignUpThread().execute();
				}
*/
				Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
				startActivity(intent);
			}
		});

	}

	public class loadList extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Please Wait Downloading Your Data");
			progressDialog.show();
			responseResult = "";

		}

		@Override
		protected Void doInBackground(Void... params) {

			String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"AUTHENTICATE_READER\">"
					+ "<params email=\"" + signEmail.getText().toString()
					+ "\" password=\"" + signPassword.getText().toString()
					+ "\"/>" + "</operation>" + "</request>";
			String id = "78";
			String categoryid = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"GET_ALL_CATEGORIES\">"
					+ "<params clientid=\"" + id + "\"/>" + "</operation>"
					+ "</request>";
			String clintId = "";
			String cateoryid = "";
			String locationId = "";
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"GET_DOCUMENTS\">"
					+ "<params clientid=\"" + clintId + "\" categoryid=\""
					+ categoryid + "\" locationid=\"" + locationId + "\"/>"
					+ "</operation>" + "</request>";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve1.zoomifier.net:8080/zoomifierws/rest/interface");
				StringEntity str = new StringEntity(body);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				returnResponse = EntityUtils.toString(entity);
				String returnresposne = returnResponse;

				Log.i("retrun Resopnese for Authonticate Reader=======",
						returnResponse);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(returnResponse));
				Log.i("Return Response===================", returnResponse);
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							responseResult = xpp.getAttributeValue(0);
						} else if (xpp.getName().equals("reader")) {
							userUserid = xpp.getAttributeValue(0);
							userFirstname = xpp.getAttributeValue(1);
							userLastname = xpp.getAttributeValue(2);
							userEmail = xpp.getAttributeValue(3);
							userLocationid = xpp.getAttributeValue(4);
							Log.i("user Id=", userUserid);
						} else if (xpp.getName().equals("client")) {
							clientId = xpp.getAttributeValue(0);
							Log.i("client Id", clientId);

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
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			/*
			 * Toast.makeText(MainActivity.this, responseResult,
			 * Toast.LENGTH_LONG) .show();
			 */
			try {

				if (responseResult.equals("success")) {
					SharedPreferences myPrefs = getApplicationContext()
							.getSharedPreferences("myPrefs", MODE_PRIVATE);
					SharedPreferences.Editor prefsEditor = myPrefs.edit();
					prefsEditor.putString("login", "success");
					prefsEditor.putString("userEmail", userEmail);
					prefsEditor.putString("userId", userUserid);
					prefsEditor.putString("userfirstName", userFirstname);
					prefsEditor.putString("userLastName", userLastname);
				
					prefsEditor.putString("readername", userFirstname+" "+userLastname);
					prefsEditor.commit();

					Intent gridIntent = new Intent(MainActivity.this,
							GridActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("readerId", userUserid);
					bundle.putString("clientId", clientId);
					bundle.putString("email_id", userEmail);
					/*database = new ZoomifierDatabase(MainActivity.this);
					database.openAndWriteDataBase();*/
					gridIntent.putExtras(bundle);
					startActivity(gridIntent);
					finish();
					loginInterfaceDialog.dismiss();

				} else {
					displayAlert(
							"Encountered error while connecting to server",
							"Connection Error");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void init() {
		/*changeuserinfo = (TextView) loginInterfaceDialog
				.findViewById(R.id.forgot_password_text);*/
		/*signEmail = (EditText) loginInterfaceDialog
				.findViewById(R.id.signin_username);
		signPassword = (EditText) loginInterfaceDialog
				.findViewById(R.id.signin_password);*/
		loginButton = (Button) loginInterfaceDialog
				.findViewById(R.id.signin_btn);
		/*signUpEmail = (EditText) loginInterfaceDialog
				.findViewById(R.id.signup_username);
		signUpverifiyPassword = (EditText) loginInterfaceDialog
				.findViewById(R.id.signup_verifypassword);
		signUpPassword = (EditText) loginInterfaceDialog
				.findViewById(R.id.signup_password);*/
		signUpButton = (Button) loginInterfaceDialog.findViewById(R.id.sign_up);
	/*	singUpLastName = (EditText) loginInterfaceDialog
				.findViewById(R.id.last_name);
		signUpFirstName = (EditText) loginInterfaceDialog
				.findViewById(R.id.first_name);
		signUpcompanyName = (EditText) loginInterfaceDialog
				.findViewById(R.id.company_name);*/

	}

	public class SignUpThread extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Please Wait Downloading Your Data");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"SIGN_UP\">"
					+ "<params email=\"" + signUpEmail.getText().toString()
					+ "\" password=\"" + signUpPassword.getText().toString()
					+ "\" firstname=\"" + signUpFirstName.getText().toString()
					+ "\" lastname=\"" + singUpLastName.getText().toString()
					+ "\" companyname=\""
					+ signUpcompanyName.getText().toString() + "\"/>"
					+ "</operation>" + "</request>";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
				StringEntity str = new StringEntity(body);
				str.setContentType("application/xml; charset=utf-8");
				str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/xml; charset=utf-8"));
				post.setEntity(str);
				HttpResponse response = httpclient.execute(post);
				HttpEntity entity = response.getEntity();
				returnResponse = EntityUtils.toString(entity);
				System.out.print("afa");

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(new StringReader(returnResponse));
				Log.i("Sign Up Response==========", returnResponse);
				String str = returnResponse;
				int eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("response")) {
							responseResult = xpp.getAttributeValue(0);

						} else if (xpp.getName().equals("reader")) {
							userUserid = xpp.getAttributeValue(0);
							userFirstname = xpp.getAttributeValue(1);
							userLastname = xpp.getAttributeValue(2);
							userEmail = xpp.getAttributeValue(3);
							userLocationid = xpp.getAttributeValue(4);
							Log.i("user Id=", userUserid);
						} else if (xpp.getName().equals("client")) {
							clientId = xpp.getAttributeValue(0);
							Log.i("client Id", clientId);

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
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			try {
				if (responseResult.equals("success")) {
					SharedPreferences myPrefs = getApplicationContext()
							.getSharedPreferences("myPrefs", MODE_PRIVATE);
					SharedPreferences.Editor prefsEditor = myPrefs.edit();
					prefsEditor.putString("login", "success");
					prefsEditor.putString("userEmail", userEmail);
					prefsEditor.putString("userId", userUserid);
					prefsEditor.putString("userfirstName", userFirstname);
					prefsEditor.putString("userLastName", userLastname);
				
					prefsEditor.putString("readername", userFirstname+" "+userLastname);
					prefsEditor.commit();
					Intent gridIntent = new Intent(MainActivity.this,
							GridActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("readerId", userUserid);
					bundle.putString("clientId", clientId);
					bundle.putString("email_id", userEmail);
					gridIntent.putExtras(bundle);
					startActivity(gridIntent);
					finish();
					loginInterfaceDialog.dismiss();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onStart() {

		super.onStart();

	}

	public void dialogGenrater() {
		loginInterfaceDialog = new Dialog(this);
		loginInterfaceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loginInterfaceDialog.setContentView(R.layout.chosser_dialog);
		Display display = ((WindowManager) getSystemService(MainActivity.this.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		int newWith = 0;
		int newHeight = 0;

		int orientation = display.getOrientation();
		if(orientation==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE||orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
		{
			newWith = width - width /5;
			newHeight = height - height /2;
		}	
		else
		{
			newWith = width - width /2;
			newHeight = height - height /5;
		}
		loginInterfaceDialog.setCanceledOnTouchOutside(false);
		loginInterfaceDialog.getWindow().setLayout(newWith, newHeight);
		loginInterfaceDialog.show();
		loginInterfaceDialog
				.setOnKeyListener(new DialogInterface.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK
								&& event.getAction() == KeyEvent.ACTION_UP) {
							finish();
						}
						return false;
					}
				});

	}

	public class ChangeUserInfo extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"FORGOT_READER_PASSWORD\">"
					+ "<params email=\"" + email + "\"/>" + "</operation>"
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
				/*
				 * database.openAndWriteDataBase();
				 * database.updateDocumentLikes(documentId);
				 * heartButton.setBackgroundResource(R.drawable.heart_plus);
				 */
			}

		}

	}

	
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

	
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	
	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

}
