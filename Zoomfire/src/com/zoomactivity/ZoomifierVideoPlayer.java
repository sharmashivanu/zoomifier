package com.zoomactivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
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

import com.zoomactivity.PDFImageViewActivity.SendReview;
import com.zoomactivity.ZoomifierVideoPlayer.LikeDocument;
import com.zoomactivity.ZoomifierVideoPlayer.LoadDocument;
import com.zoomactivity.ZoomifierVideoPlayer.ShareEmail;
import com.zoomactivity.ZoomifierVideoPlayer.SharePdf;
import com.zoomactivity.ZoomifierVideoPlayer.UnLikeDocumentDocument;
import com.zoomactivity.ZoomifierView.FindReviewList;
import com.zoomifier.adapter.AgendaTimeAdapNew;
import com.zoomifier.adapter.Book;
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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ZoomifierVideoPlayer extends Activity{

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
	Dialog settingDialog;
	ImageView webView;
	Drawable drawable;
	float oldDist;
	int status = 0;
	String documentId, documentName, documentDiscription, documentOrginalId,
			documentContentType, documentContentWidth, documentContentHeight,
			documentOwnerId, clientid;
	Animation webViewZoomOutAnimation, webViewZoomInAnimation;
	TextView textHeader, discriptionText;
	Button starButton, sharebuton, heartButton, infoButton;
	ZoomifierDatabase database;
	Vector<Book> reviewList = new Vector<Book>();
	String retrunResponse, userId;
	int position;
	String shareResponse;
	String like_document;
     PDImagedownloader imageloader;
     static int numberofstar;
     String review_time;
 	String review_title;
 	String revew_text;
 	ImageView playebutton;
 	TextView sharedBy;
 	String sharedByName,ShardByDate;
 	RelativeLayout transparentlayout;
 	String videourls;
	String filename;
  boolean documetnlikeactivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoomfiervideoplayer);
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
			sharedByName = bundle.getString("sharedby");
			ShardByDate = bundle.getString("shareddate");
			documetnlikeactivity=bundle.getBoolean("lickeactivity");
			userId = bundle.getString("userid");
			clientid = bundle.getString("clientid");
			String imageid = bundle.getString("image");
			position = bundle.getInt("position");
			like_document=bundle.getString("favorit");
			webViewZoomOutAnimation = AnimationUtils.loadAnimation(
					ZoomifierVideoPlayer.this, R.anim.zoom_out_animation);
			webViewZoomInAnimation = AnimationUtils.loadAnimation(
					ZoomifierVideoPlayer.this, R.anim.zoom_in_popupview);
			
			overridePendingTransition(R.anim.zoom_out_animation,
					R.anim.zoom_in_popupview);
			imageloader=new PDImagedownloader(this);
			init();
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
			new FindReviewList().execute();
			textHeader.setText(documentName);
			sharedBy.setText("Shared by  "+sharedByName+" on "+ShardByDate);
			discriptionText.setText(documentDiscription);
			webView.setScaleType(ScaleType.FIT_XY);
			Display display = ((WindowManager) getSystemService(ZoomifierVideoPlayer.this.WINDOW_SERVICE))
					.getDefaultDisplay();
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display displays = wm.getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();

			int orientation = display.getOrientation();
			int ot = getResources().getConfiguration().orientation;
			if(ot==Configuration.ORIENTATION_PORTRAIT)
			{
				if(height>1000)
				webView.setPadding(0, 350, 0, 350);
				else
					webView.setPadding(0, 150, 0, 150);
			}
			else
			{	
				if(width>1000)
					webView.setPadding(200, 90, 200, 90);
					else
					webView.setPadding(50, 30, 50, 30);
				
			}
		
			String url="http://ve1.zoomifier.net/"+clientid+"/"+documentId+"/ipad/Page1Thb.jpg";
			imageloader.DisplayImage(url, webView);
	playebutton.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
				try{
					    filename="http://ve1.zoomifier.net/"+clientid+"/"+documentId+"/ipad/"+documentId+".mp4".replaceAll("/","");
						String filena=filename.replaceAll("/","");
						String lastfilename=filena.replaceAll(".", "");
						//String newfilename=lastfilename.replace(":","");
					File file = new File(Environment.getExternalStorageDirectory()+"/Zoomifier/"+clientid+documentId+".mp4" );
					if(file.exists())
					{
					      Intent intent=new Intent(Intent.ACTION_VIEW);
						// String videourls="http://ve1.zoomifier.net/"+clientid+"/"+documentId+"/ipad/"+documentId+".mp4";
				         intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory()+"/Zoomifier/"+clientid+documentId+".mp4"),"video/*");
				     	 startActivity(intent);
					}
					else
					{  
					 videourls="http://ve1.zoomifier.net/"+clientid+"/"+documentId+"/ipad/"+documentId+".mp4";
					 Intent intent=new Intent(Intent.ACTION_VIEW);
					 new VideoDoanloader().execute();
			         intent.setDataAndType(Uri.parse(videourls),"video/*");
			     	 startActivity(intent);
					 }
			       
				
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

					return false;
				}
			});
			
			database.openAndWriteDataBase();
			String like=database.openAndReadDocumentLikeUnlike(documentId);
			
			if(like.equals("0"))
			{
				heartButton.setBackgroundResource(R.drawable.heart_minus);
			}
			else if(like.equals("1"))
			{
				heartButton.setBackgroundResource(R.drawable.heart_plus);
			}
		
			
		} catch (Exception e) {

		}
	
		starButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stardialog();

			}
		});
		Button authorizedButton = (Button) findViewById(R.id.authorized_button);
		authorizedButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText emailField = (EditText) findViewById(R.id.email_field);
				if (emailField.getText().toString().equals("")) {
					displayAlert("Please enter email address");
				} 
				else if(!checkEmail(emailField.getText().toString()))
				{
					displayAlert("Please enter a valid email address");
				}
					
				else {
					new ShareEmail().execute();
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
					layout.setVisibility(View.GONE);
					emailField.setText("");
					
				}
			}
		});
		
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
					Display display = ((WindowManager) getSystemService(ZoomifierVideoPlayer.this.WINDOW_SERVICE))
							.getDefaultDisplay();

					int orientation = display.getOrientation();
					if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||orientation==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
					{
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
						params.width=LayoutParams.FILL_PARENT;
						layout.setLayoutParams(params);
					}
					else
					{	int pixel =ZoomifierVideoPlayer.this.getWindowManager().getDefaultDisplay().getWidth();
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
						int wid=pixel/2;
						params.width=wid;
						layout.setLayoutParams(params);
					}
					layout.setVisibility(View.VISIBLE);
					transparentlayout.setVisibility(View.VISIBLE);

				}

			}
		});
		heartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				database.openAndWriteDataBase();
				String like=database.openAndReadDocumentLikeUnlike(documentId);
				if(like.equals("0"))
				{
					new LikeDocument().execute();
					/*database.updateDocumentLikes(documentId);
					heartButton.setBackgroundResource(R.drawable.heart_plus);*/
					
				}
				else if(like.equals("1"))
				{
					
					new UnLikeDocumentDocument().execute();
				/*	database.updateDocumentUnlikes(documentId);
					heartButton.setBackgroundResource(R.drawable.heart_minus);*/
				}
				

			}
		});
		infoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);
				transparentlayout.setVisibility(View.GONE);

				RelativeLayout infolayout = (RelativeLayout) findViewById(R.id.infodialog);
				if (infolayout.getVisibility() == View.VISIBLE) {
					infolayout.setVisibility(View.GONE);
					transparentlayout.setVisibility(View.GONE);
				} else {
					
							Display display = ((WindowManager) getSystemService(ZoomifierVideoPlayer.this.WINDOW_SERVICE))
									.getDefaultDisplay();

							int orientation = display.getOrientation();
							if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||orientation==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
							{
								RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infolayout.getLayoutParams();
								params.width=LayoutParams.FILL_PARENT;
								infolayout.setLayoutParams(params);
							}
							else
							{	int pixel =ZoomifierVideoPlayer.this.getWindowManager().getDefaultDisplay().getWidth();
								RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) infolayout.getLayoutParams();
								int wid=pixel/2;
								params.width=wid;
								infolayout.setLayoutParams(params);
							}
					infolayout.setVisibility(View.VISIBLE);
					transparentlayout.setVisibility(View.VISIBLE);
					
				}
			}
		});

	}

	public void init() {
		webView = (ImageView) findViewById(R.id.webview);
		textHeader = (TextView) findViewById(R.id.document_header_text);
		starButton = (Button) findViewById(R.id.button1);
		sharebuton = (Button) findViewById(R.id.button2);
		heartButton = (Button) findViewById(R.id.button3);
		infoButton = (Button) findViewById(R.id.button4);
		database = new ZoomifierDatabase(this);
		discriptionText = (TextView) findViewById(R.id.discription_field);
		playebutton=(ImageView) findViewById(R.id.play_button);
				sharedBy=(TextView) findViewById(R.id.share_field);
				transparentlayout = (RelativeLayout) findViewById(R.id.transparentlayout);
	}

	public class LoadDocument extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ZoomifierVideoPlayer.this);
			progressDialog.setMessage("Please Wait.Downloading your data...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			String url = "http://ve1.zoomifier.net/" + clientid + "/"
					+ documentId + "/ipad/Page1Thb.jpg";
			drawable = LoadImageFromWebOperations(url);
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
			webView.setImageDrawable(drawable);

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

	public void settingDialog() {
		settingDialog = new Dialog(ZoomifierVideoPlayer.this,
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

	public class ShareEmail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ZoomifierVideoPlayer.this);
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

							
							String id = xpp.getAttributeValue(0);
							String contenttype = xpp.getAttributeValue(1);
							String clintId = xpp.getAttributeValue(2);
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

	public class SharePdf extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ZoomifierVideoPlayer.this);
			progressDialog.setMessage("Submitting data...");
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
				displayAlert("Document shared successfully");
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);

			} else {
				
				RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);
			}

		}

	}

	public void stardialog() {
		RelativeLayout heartlayout = (RelativeLayout) findViewById(R.id.infodialog);
		heartlayout.setVisibility(View.GONE);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
		layout.setVisibility(View.GONE);
		final Dialog userInfoDialog = new Dialog(ZoomifierVideoPlayer.this);
		userInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		userInfoDialog.setContentView(R.layout.document_review_dialog);
		userInfoDialog.setCanceledOnTouchOutside(false);
		//userInfoDialog.getWindow().setLayout(500, 600);
		userInfoDialog.show();
		final ListView listview = (ListView) userInfoDialog
				.findViewById(R.id.reviewlist);
		reviewList.clear();
		
		ImageView reImage1=(ImageView) userInfoDialog.findViewById(R.id.restar1);
		ImageView reImage2=(ImageView) userInfoDialog.findViewById(R.id.restar2);
		ImageView reImage3=(ImageView) userInfoDialog.findViewById(R.id.restar3);
		ImageView reImage4=(ImageView) userInfoDialog.findViewById(R.id.restar4);
		ImageView reImage5=(ImageView) userInfoDialog.findViewById(R.id.restar5);
		TextView startTextView=(TextView) userInfoDialog.findViewById(R.id.rating_text);
		int totalstar=0;
		
		database.openAndReadReviewTable(documentId);
		for (int i = 0; i < ZoomifierDatabase.documentreviewtitle.size(); i++) {
			reviewList.add(new Book(ZoomifierDatabase.documentreviewtitle
					.get(i), ZoomifierDatabase.documentreview.get(i),
					ZoomifierDatabase.time.get(i),
					ZoomifierDatabase.noforeviewstar.get(i)));
			totalstar=totalstar+Integer.parseInt(ZoomifierDatabase.noforeviewstar.get(i));
		}
		int avragestar=0;
		if(ZoomifierDatabase.documentreviewtitle.size()!=0)
		{
		 avragestar=totalstar/ZoomifierDatabase.documentreviewtitle.size();
		
		if(avragestar==1)
		{
			reImage1.setImageResource(R.drawable.golden_star);
		}
		else if(avragestar==2)
		{
			reImage1.setImageResource(R.drawable.golden_star);
			reImage2.setImageResource(R.drawable.golden_star);
		}
		else if(avragestar==3)
		{
			reImage1.setImageResource(R.drawable.golden_star);
			reImage2.setImageResource(R.drawable.golden_star);
			reImage3.setImageResource(R.drawable.golden_star);
		}
		else if(avragestar==4)
		{
			reImage1.setImageResource(R.drawable.golden_star);
			reImage2.setImageResource(R.drawable.golden_star);
			reImage3.setImageResource(R.drawable.golden_star);
			reImage4.setImageResource(R.drawable.golden_star);
		}
		else if(avragestar==5)
		{
			reImage1.setImageResource(R.drawable.golden_star);
			reImage2.setImageResource(R.drawable.golden_star);
			reImage3.setImageResource(R.drawable.golden_star);
			reImage4.setImageResource(R.drawable.golden_star);
			reImage5.setImageResource(R.drawable.golden_star);
		}
		}
		
		startTextView.setText(Integer.toString(reviewList.size())+" Rating");
		
		AgendaTimeAdapNew adapter = new AgendaTimeAdapNew(ZoomifierVideoPlayer.this,
				112, reviewList);
		listview.setAdapter(adapter);
		Button writereviewButton = (Button) userInfoDialog
				.findViewById(R.id.review_button);

		writereviewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				final Dialog reveiewDialgo = new Dialog(ZoomifierVideoPlayer.this);
				reveiewDialgo.requestWindowFeature(Window.FEATURE_NO_TITLE);
				reveiewDialgo.setContentView(R.layout.riview_dialog);
				reveiewDialgo.setCanceledOnTouchOutside(false);
				//reveiewDialgo.getWindow().setLayout(500, 700);
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

					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
					}
					public void onTextChanged(CharSequence s, int start, int before,
							int count) {
						if(!title.getText().toString().equals("")&&!reviewtext.getText().toString().equals("")&&imageview1.getTag().equals(R.drawable.golden_star))
						{
							submitButton.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						}
						else
						{
							submitButton.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}
						
					}

				};
				title.addTextChangedListener(filterTextWatcher);
				reviewtext.addTextChangedListener(filterTextWatcher);

				imageview1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
						/*if(imageview2.getTag().equals(R.drawable.black_star))
						{*/
						if (imageview1.getTag().equals(R.drawable.black_star)) {
							imageview1.setImageResource(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							imageview1.setTag(R.drawable.golden_star);
						} else {

							if(imageview5.getTag().equals(R.drawable.golden_star))
							   {
									imageview5.setImageResource(R.drawable.black_star);
									imageview5.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   if(imageview4.getTag().equals(R.drawable.golden_star))
							   {
									imageview4.setImageResource(R.drawable.black_star);
									imageview4.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   if(imageview3.getTag().equals(R.drawable.golden_star))
							   {
									imageview3.setImageResource(R.drawable.black_star);
									imageview3.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   if(imageview2.getTag().equals(R.drawable.golden_star))
							   {
									imageview2.setImageResource(R.drawable.black_star);
									imageview2.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							
							imageview1.setImageResource(R.drawable.black_star);
							imageview1.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if(!title.getText().toString().equals("")&&!reviewtext.getText().toString().equals("")&&imageview1.getTag().equals(R.drawable.golden_star))
						{
							submitButton.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						}
						else
						{
							submitButton.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}
						//}

					}
				});

				imageview2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					/*if(imageview1.getTag().equals(R.drawable.golden_star)&&imageview3.getTag().equals(R.drawable.black_star))
					{*/
						if (imageview2.getTag().equals(R.drawable.black_star)) {
							if(imageview1.getTag().equals(R.drawable.black_star))
							{
							imageview1.setImageResource(R.drawable.golden_star);
							imageview1.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							
							if(imageview2.getTag().equals(R.drawable.black_star))
							{
							imageview2.setImageResource(R.drawable.golden_star);
							imageview2.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							
						} else {
							   if(imageview5.getTag().equals(R.drawable.golden_star))
							   {
									imageview5.setImageResource(R.drawable.black_star);
									imageview5.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   if(imageview4.getTag().equals(R.drawable.golden_star))
							   {
									imageview4.setImageResource(R.drawable.black_star);
									imageview4.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   if(imageview3.getTag().equals(R.drawable.golden_star))
							   {
									imageview3.setImageResource(R.drawable.black_star);
									imageview3.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							
							imageview2.setImageResource(R.drawable.black_star);
							imageview2.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if(!title.getText().toString().equals("")&&!reviewtext.getText().toString().equals("")&&imageview1.getTag().equals(R.drawable.golden_star))
						{
							submitButton.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						}
						else
						{
							submitButton.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					}

					//}
				});

				imageview3.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					/*if(imageview2.getTag().equals(R.drawable.golden_star)&&imageview4.getTag().equals(R.drawable.black_star))
					{*/	
						if (imageview3.getTag().equals(R.drawable.black_star)) {
							if(imageview1.getTag().equals(R.drawable.black_star))
							{
							imageview1.setImageResource(R.drawable.golden_star);
							imageview1.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							
							if(imageview2.getTag().equals(R.drawable.black_star))
							{
							imageview2.setImageResource(R.drawable.golden_star);
							imageview2.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							if(imageview3.getTag().equals(R.drawable.black_star))
							{
							imageview3.setImageResource(R.drawable.golden_star);
							imageview3.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
						} else {
							   if(imageview5.getTag().equals(R.drawable.golden_star))
							   {
									imageview5.setImageResource(R.drawable.black_star);
									imageview5.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   if(imageview4.getTag().equals(R.drawable.golden_star))
							   {
									imageview4.setImageResource(R.drawable.black_star);
									imageview4.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							  
							
							imageview3.setImageResource(R.drawable.black_star);
							imageview3.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						
						}
						if(!title.getText().toString().equals("")&&!reviewtext.getText().toString().equals("")&&imageview1.getTag().equals(R.drawable.golden_star))
						{
							submitButton.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						}
						else
						{
							submitButton.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

					//}
					}
				});

				imageview4.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					/*if(imageview3.getTag().equals(R.drawable.golden_star)&&imageview5.getTag().equals(R.drawable.black_star))
						{*/
						if (imageview4.getTag().equals(R.drawable.black_star)) {
							if(imageview1.getTag().equals(R.drawable.black_star))
							{
							imageview1.setImageResource(R.drawable.golden_star);
							imageview1.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							
							if(imageview2.getTag().equals(R.drawable.black_star))
							{
							imageview2.setImageResource(R.drawable.golden_star);
							imageview2.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							if(imageview3.getTag().equals(R.drawable.black_star))
							{
							imageview3.setImageResource(R.drawable.golden_star);
							imageview3.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							if(imageview4.getTag().equals(R.drawable.black_star))
							{
							imageview4.setImageResource(R.drawable.golden_star);
							imageview4.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
						} else {
							  if(imageview5.getTag().equals(R.drawable.golden_star))
							   {
									imageview5.setImageResource(R.drawable.black_star);
									imageview5.setTag(R.drawable.black_star);
									numberofstar = numberofstar - 1;
							   }
							   
							
							imageview4.setImageResource(R.drawable.black_star);
							imageview4.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						
					if(!title.getText().toString().equals("")&&!reviewtext.getText().toString().equals("")&&imageview1.getTag().equals(R.drawable.golden_star))
					{
						submitButton.setBackgroundResource(R.drawable.red_submit);
						submitButton.setTag(R.drawable.red_submit);
					}
					else
					{
						submitButton.setBackgroundResource(R.drawable.black_submit);
						submitButton.setTag(R.drawable.black_submit);
					}

					}
					//}
				});

				imageview5.setOnClickListener(new View.OnClickListener() {

					@Override
					
					public void onClick(View v) {
					/*	if(imageview4.getTag().equals(R.drawable.golden_star))
						{*/
						if (imageview5.getTag().equals(R.drawable.black_star)) {
							if(imageview1.getTag().equals(R.drawable.black_star))
							{
							imageview1.setImageResource(R.drawable.golden_star);
							imageview1.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							
							if(imageview2.getTag().equals(R.drawable.black_star))
							{
							imageview2.setImageResource(R.drawable.golden_star);
							imageview2.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							if(imageview3.getTag().equals(R.drawable.black_star))
							{
							imageview3.setImageResource(R.drawable.golden_star);
							imageview3.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							if(imageview4.getTag().equals(R.drawable.black_star))
							{
							imageview4.setImageResource(R.drawable.golden_star);
							imageview4.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
							if(imageview5.getTag().equals(R.drawable.black_star))
							{
							imageview5.setImageResource(R.drawable.golden_star);
							imageview5.setTag(R.drawable.golden_star);
							numberofstar = numberofstar + 1;
							}
						} else {
							
							imageview5.setImageResource(R.drawable.black_star);
							imageview5.setTag(R.drawable.black_star);
							numberofstar = numberofstar - 1;
						}
						if(!title.getText().toString().equals("")&&!reviewtext.getText().toString().equals("")&&imageview1.getTag().equals(R.drawable.golden_star))
						{
							submitButton.setBackgroundResource(R.drawable.red_submit);
							submitButton.setTag(R.drawable.red_submit);
						}
						else
						{
							submitButton.setBackgroundResource(R.drawable.black_submit);
							submitButton.setTag(R.drawable.black_submit);
						}

						//}

					}
				});

				
				submitButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
					
						
						if(submitButton.getTag().equals(R.drawable.red_submit)) {
							Calendar calendar = Calendar.getInstance();
							int mYear = calendar.get(Calendar.YEAR);
						    int	mMonth = calendar.get(Calendar.MONTH);
					       int mDay = calendar.get(Calendar.DAY_OF_MONTH);
					       Calendar cal=Calendar.getInstance();
					       SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
					       String month_name = month_date.format(cal.getTime());
					       int date= calendar.get(Calendar.DATE);
					       review_title=title.getText().toString();
					     
					       String completDate=Integer.toString(date)+", "+month_name+" "+Integer.toString(mYear);
					       review_time=completDate;
					       revew_text=reviewtext.getText().toString();
						   database.insertIntoReviewTable(documentId, title
									.getText().toString(), reviewtext.getText()
									.toString(),
									Integer.toString(numberofstar),completDate );
							new SendReview().execute();
							reviewList.clear();
							ImageView reImage1=(ImageView) userInfoDialog.findViewById(R.id.restar1);
							ImageView reImage2=(ImageView) userInfoDialog.findViewById(R.id.restar2);
							ImageView reImage3=(ImageView) userInfoDialog.findViewById(R.id.restar3);
							ImageView reImage4=(ImageView) userInfoDialog.findViewById(R.id.restar4);
							ImageView reImage5=(ImageView) userInfoDialog.findViewById(R.id.restar5);
							TextView startTextView=(TextView) userInfoDialog.findViewById(R.id.rating_text);
							int totalstar=0;
							
							database.openAndReadReviewTable(documentId);
							for (int i = 0; i < ZoomifierDatabase.documentreviewtitle
									.size(); i++) {
								reviewList.add(new Book(
										ZoomifierDatabase.documentreviewtitle
												.get(i),
										ZoomifierDatabase.documentreview.get(i),
										ZoomifierDatabase.time.get(i),
										ZoomifierDatabase.noforeviewstar.get(i)));
								totalstar=totalstar+Integer.parseInt(ZoomifierDatabase.noforeviewstar.get(i));
							}
							int avragestar=0;
							if(ZoomifierDatabase.documentreviewtitle.size()!=0)
							{
							 avragestar=totalstar/ZoomifierDatabase.documentreviewtitle.size();
							if(avragestar==1)
							{
								reImage1.setImageResource(R.drawable.golden_star);
							}
							else if(avragestar==2)
							{
								reImage1.setImageResource(R.drawable.golden_star);
								reImage2.setImageResource(R.drawable.golden_star);
							}
							else if(avragestar==3)
							{
								reImage1.setImageResource(R.drawable.golden_star);
								reImage2.setImageResource(R.drawable.golden_star);
								reImage3.setImageResource(R.drawable.golden_star);
							}
							else if(avragestar==4)
							{
								reImage1.setImageResource(R.drawable.golden_star);
								reImage2.setImageResource(R.drawable.golden_star);
								reImage3.setImageResource(R.drawable.golden_star);
								reImage4.setImageResource(R.drawable.golden_star);
							}
							else if(avragestar==5)
							{
								reImage1.setImageResource(R.drawable.golden_star);
								reImage2.setImageResource(R.drawable.golden_star);
								reImage3.setImageResource(R.drawable.golden_star);
								reImage4.setImageResource(R.drawable.golden_star);
								reImage5.setImageResource(R.drawable.golden_star);
							}
							}
							startTextView.setText(Integer.toString(reviewList.size())+" Rating");
							AgendaTimeAdapNew adapter = new AgendaTimeAdapNew(
									ZoomifierVideoPlayer.this, 112, reviewList);
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
	public class LikeDocument extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ZoomifierVideoPlayer.this);
			progressDialog.setMessage("Data is Liked");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"ADD_READER_FAVORITE\">"
					+ "<params readerid=\"" + userId
					+ "\" clientid=\"" + clientid + "\"/>" + "</operation>"
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
			if(shareResponse.equals("success"))
			{   database.openAndWriteDataBase();
				database.updateDocumentLikes(documentId);
				heartButton.setBackgroundResource(R.drawable.heart_plus);
				if(documetnlikeactivity)
				{
					  Bundle bundle = new Bundle();
					    bundle.putString("readerId", userId);
						
						bundle.putString("likesignal", "");
					  Intent intent = new Intent(ZoomifierVideoPlayer.this,
							DocumetntLikes.class);
					 intent.putExtras(bundle);
					 startActivity(intent);
					 finish();
				}
			}
			
			

		}

	}
	public class UnLikeDocumentDocument extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ZoomifierVideoPlayer.this);
			progressDialog.setMessage("Data is Unliked");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"DELETE_READER_FAVORITE\">"
					+ "<params readerid=\"" + userId
					+ "\" clientid=\"" + clientid + "\"/>" + "</operation>"
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
			if(shareResponse.equals("success"))
			{   database.openAndWriteDataBase();
				database.updateDocumentUnlikes(documentId);
				heartButton.setBackgroundResource(R.drawable.heart_minus);
				if(documetnlikeactivity)
				{
					  Bundle bundle = new Bundle();
					    bundle.putString("readerId", userId);
						
						bundle.putString("likesignal", "");
					  Intent intent = new Intent(ZoomifierVideoPlayer.this,
							DocumetntLikes.class);
					 intent.putExtras(bundle);
					 startActivity(intent);
					 finish();
				}
			}
		}

	}
	public class SendReview extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ZoomifierVideoPlayer.this);
			progressDialog.setMessage("Submitting Data...");
			progressDialog.show();
			shareResponse = "";
		}

		@Override
		protected Void doInBackground(Void... params) {
			EditText emailField = (EditText) findViewById(R.id.email_field);
			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>"
					+ "<operation opcode=\"SET_CONTENT_REVIEW\">"
					+ "<params clientid=\"" + clientid + "\" contentid=\""
					+ documentId + "\" readerid=\""+userId+ "\" title=\""+review_title+ "\" rating=\""+Integer.toOctalString(numberofstar)+
					"\" review=\""+revew_text+ "\"/>" + "</operation>" + "</request>";
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
			/*	database.openAndWriteDataBase();
				database.updateDocumentLikes(documentId);
				heartButton.setBackgroundResource(R.drawable.heart_plus);*/
			}

		}

	}
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

			private boolean checkEmail(String email) {
			return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
			}
	
			public class FindReviewList extends AsyncTask<Void, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				/*	progressDialog = new ProgressDialog(ZoomifierView.this);
					progressDialog.setMessage("Please Wait.Downloading your data...");
					progressDialog.show();*/
					shareResponse = "";
				}

				@Override
				protected Void doInBackground(Void... params) {
					
					String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<request>" + "<operation opcode=\"GET_DOCUMENT_REVIEWS\">"
							+ "<params docid=\"" +documentId+"\"/>" + "</operation>"
							+ "</request>";
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
						if(str.contains("review"))
						{
							database.deletealreviw();
						}
						while (eventType != XmlPullParser.END_DOCUMENT) {
							if (eventType == XmlPullParser.START_DOCUMENT) {

							} else if (eventType == XmlPullParser.START_TAG) {
								Log.i("Start tag ", xpp.getName());
								if (xpp.getName().equals("response")) {
									shareResponse = xpp.getAttributeValue(0);

								} else if (xpp.getName().equals("review")) {
		 
					
									try {

										database.openAndWriteDataBase();
										   database.insertIntoReviewTable(documentId, xpp.getAttributeValue(1), 
												   xpp.getAttributeValue(6),
													xpp.getAttributeValue(2),xpp.getAttributeValue(5));
										
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
					//progressDialog.dismiss();
					if (shareResponse.equals("success")) {
						displayAlert("Done Successfully");
						RelativeLayout layout = (RelativeLayout) findViewById(R.id.setting_dialog_layout);
						layout.setVisibility(View.GONE);

					} /*
					 * else { Toast.makeText(PDFImageViewActivity.this,
					 * "Done Successfully", Toast.LENGTH_LONG).show(); RelativeLayout
					 * layout = (RelativeLayout)
					 * findViewById(R.id.setting_dialog_layout);
					 * layout.setVisibility(View.GONE); }
					 */

				}

			}
			
			public class VideoDoanloader extends AsyncTask<Void, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				}

				@Override
				protected Void doInBackground(Void... params) {
					
					VideoDownloader videoDoanloader=new VideoDownloader();
					
					
					videoDoanloader.DownloadFile(videourls,  clientid+documentId+".mp4");
					return null;
				}

				@Override
				protected void onProgressUpdate(Void... values) {

					super.onProgressUpdate(values);

				}

				@Override
				protected void onPostExecute(Void result) {}

			}

	
}
