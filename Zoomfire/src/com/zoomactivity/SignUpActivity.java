package com.zoomactivity;

import java.io.IOException;
import java.io.StringReader;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends Activity {
	String response;
	String singUpresponse;
	Dialog loginInterfaceDialog;
	String returnResponse = null;
	String responseResult, userUserid, userFirstname, userLastname, userEmail,
			userLocationid, clientClientid, clientName, clientDescription;
	Button loginButton, signUpButton, welcomeButton;
	EditText signEmail, signPassword, signUpEmail, signUpPassword,
			signUpverifiyPassword, signUpFirstName, singUpLastName,
			signUpcompanyName;
	ProgressDialog progressDialog;
	String clientId;
	TextView changeuserinfo;
	// static ZoomifierDatabase database;
	String retrunResponse;
	String shareResponse;
	String email;
	TextView privacyTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_layout);
		init();

		
		
		signUpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (signUpEmail.getText().toString().equals("")) {
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

			}
		});
		welcomeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignUpActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			}
		});
		
			//
		String link="By clicking Register you indicate that you have read and agreed to the "
				+ "<a href=\"http://www.zoomifier.com/termsofservice.html\">Terms of Use</a> and <a href=\"http://www.zoomifier.com/privacy_policy.html\">Privacy Policy</a>";
			privacyTextView
					.setText(Html
							.fromHtml(link));
			Linkify.addLinks(privacyTextView, Linkify.ALL);
			privacyTextView.setMovementMethod(LinkMovementMethod.getInstance());
			//stripUnderlines(privacyTextView);
		

	}

	public void init() {
		/*
		 * changeuserinfo = (TextView) loginInterfaceDialog
		 * .findViewById(R.id.forgot_password_text); ignEmail = (EditText)
		 * loginInterfaceDialog .findViewById(R.id.signin_username);
		 * signPassword = (EditText) loginInterfaceDialog
		 * .findViewById(R.id.signin_password); loginButton = (Button)
		 * loginInterfaceDialog .findViewById(R.id.signin_btn);
		 */
		signUpEmail = (EditText) findViewById(R.id.signup_username);
		signUpverifiyPassword = (EditText) findViewById(R.id.signup_verifypassword);
		signUpPassword = (EditText) findViewById(R.id.signup_password);
		signUpButton = (Button) findViewById(R.id.sign_up);
		singUpLastName = (EditText) findViewById(R.id.last_name);
		signUpFirstName = (EditText) findViewById(R.id.first_name);
		signUpcompanyName = (EditText) findViewById(R.id.company_name);
		welcomeButton = (Button) findViewById(R.id.welcome_button);
		privacyTextView = (TextView) findViewById(R.id.privacytextview);
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

	public class SignUpThread extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SignUpActivity.this);
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

					prefsEditor.putString("readername", userFirstname + " "
							+ userLastname);
					prefsEditor.commit();
					Intent gridIntent = new Intent(SignUpActivity.this,
							LibraryClass.class);
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

/*	public class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	private void stripUnderlines(TextView textView) {
		Spannable s = (Spannable) textView.getText();
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		for (URLSpan span : spans) {
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			s.removeSpan(span);
			span = new URLSpanNoUnderline(span.getURL());
			s.setSpan(span, start, end, 0);
		}
		textView.setText(s);
	}*/

}
