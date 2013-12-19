package com.zoomactivity;

import java.io.IOException;
import java.io.StringReader;

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

import com.zoomactivity.GridActivity.ChangePassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeUserInfoActivity extends Activity {
	EditText firstNmae, lastButton, current_passwrod, new_passwrod,
			verifiypassword;
	TextView email_editText;
	Button saveButton;
	String changeEmail, changeoldPassword, changernewPassword;
	ProgressDialog progressDialog;
	String shareResponse, retrunResponse;
	String email_id;
	Button cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changeuser_info_layout);
		Bundle bundle = getIntent().getExtras();
		email_id = bundle.getString("email");
		init();

		SharedPreferences myPrefsd = getApplicationContext()
				.getSharedPreferences("myPrefs", MODE_PRIVATE);
		String firstNamee = myPrefsd.getString("userfirstName", "");
		String lastNamee = myPrefsd.getString("userLastName", "");
		firstNmae.setText(firstNamee);
		email_editText.setText(email_id);
		lastButton.setText(lastNamee);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView email_editText = (TextView) findViewById(R.id.email_name_field);
				changeEmail = email_editText.getText().toString();
				EditText current_passwrod = (EditText) findViewById(R.id.current_password_fields);
				changeoldPassword = current_passwrod.getText().toString();
				EditText new_passwrod = (EditText) findViewById(R.id.new_password_fields);
				changernewPassword = new_passwrod.getText().toString();
				EditText verifiypassword = (EditText) findViewById(R.id.verify_password_fields);
				if (changeEmail.equals("")) {
					displayAlert("Enter Email");
				} else {
					if (changernewPassword.equals(verifiypassword.getText()
							.toString())) {
						new ChangePassword().execute();

						SharedPreferences myPrefs = ChangeUserInfoActivity.this
								.getSharedPreferences("myPrefs",
										MODE_WORLD_READABLE);
						SharedPreferences.Editor prefsEditor = myPrefs.edit();
						prefsEditor.putString("firstname", firstNmae.getText()
								.toString());
						prefsEditor.putString("lastname", lastButton.getText()
								.toString());
						prefsEditor.commit();
					} else {
						displayAlert("New Password and verify New Passwrod should be same");
					}
				}

			}
		});

	}

	public void init() {
		firstNmae = (EditText) findViewById(R.id.firt_name_field);
		email_editText = (TextView) findViewById(R.id.email_name_field);
		lastButton = (EditText) findViewById(R.id.last_name_field);
		saveButton = (Button) findViewById(R.id.save_button);
		email_editText = (TextView) findViewById(R.id.email_name_field);
		current_passwrod = (EditText) findViewById(R.id.current_password_fields);
		new_passwrod = (EditText) findViewById(R.id.new_password_fields);
		verifiypassword = (EditText) findViewById(R.id.verify_password_fields);
		cancel = (Button) findViewById(R.id.cancel_button);

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
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ChangeUserInfoActivity.this);
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
			finish();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(firstNmae.getWindowToken(), 0);
		}

	}

}
