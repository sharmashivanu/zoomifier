package com.zoomactivity;
import java.util.Vector;

import com.zoomifier.adapter.AgendaTimeAdapNew;
import com.zoomifier.adapter.Book;
import com.zoomifier.database.ZoomifierDatabase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PDFReaderActivity extends Activity{
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
	float oldDist;
    int status = 0;
    String documentId,documentName,documentDiscription,documentOrginalId,documentContentType,
    documentContentWidth,documentContentHeight,documentOwnerId;
    Animation webViewZoomOutAnimation,webViewZoomInAnimation;
    TextView textHeader,discriptionText;
    Button starButton,sharebuton,heartButton,infoButton;
    ZoomifierDatabase database;
    Vector<Book> reviewList=new Vector<Book>();
    
    
    int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoomifierview_xml);
		Bundle bundle=getIntent().getExtras();
		try{
		documentId=bundle.getString("documentId");
		documentName=bundle.getString("documentName");
		documentDiscription=bundle.getString("documentDiscription");
		documentOrginalId=bundle.getString("documentOrginalId");
		documentContentType=bundle.getString("documentContentType");
		documentContentWidth=bundle.getString("documentContentWidth");
		documentContentHeight=bundle.getString("documentContentHeight");
		documentOwnerId=bundle.getString("documentOwnerId");
		String imageid=bundle.getString("image");
		position=bundle.getInt("position");

		 webViewZoomOutAnimation=AnimationUtils.loadAnimation(PDFReaderActivity.this,
					R.anim.zoom_out_animation);
		 webViewZoomInAnimation=AnimationUtils.loadAnimation(PDFReaderActivity.this,
					R.anim.zoom_in_popupview);

		overridePendingTransition(R.anim.zoom_out_animation, R.anim.zoom_in_popupview);
		init();
		
			webView.setImageResource(Integer.parseInt(imageid));
	
		
		textHeader.setText(documentName);
		discriptionText.setText(documentDiscription);
		}
		catch(Exception e)
		{
			
		}
        webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				RelativeLayout heartlayout=(RelativeLayout) findViewById(R.id.infodialog);
				heartlayout.setVisibility(View.GONE);
				
				RelativeLayout layout=(RelativeLayout) findViewById(R.id.setting_dialog_layout);
				layout.setVisibility(View.GONE);
				status = 1;
				ImageView img = (ImageView) v;
				ImageView view = (ImageView) v;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:

					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());

					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:

					oldDist = spacing(event);

					if (oldDist > 10f) {

						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;

					}
					break;

				case MotionEvent.ACTION_MOVE:

					if (mode == DRAG) {

						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x,
								event.getY() - start.y);
					} else if (mode == ZOOM) {

						float newDist = spacing(event);
						float f=oldDist-newDist;
							if (f > 200f) {
								 finish();
								
							}

						if (newDist > 10f) {

							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_POINTER_UP:

					mode = NONE;

					break;
				}
				view.setImageMatrix(matrix);

				return true;

			}
		});
        starButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 RelativeLayout heartlayout=(RelativeLayout) findViewById(R.id.infodialog);
				 heartlayout.setVisibility(View.GONE);
				 RelativeLayout layout=(RelativeLayout) findViewById(R.id.setting_dialog_layout);
				 layout.setVisibility(View.GONE);
				 final Dialog userInfoDialog=new Dialog(PDFReaderActivity.this);
					userInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					userInfoDialog.setContentView(R.layout.document_review_dialog);
					userInfoDialog.setCanceledOnTouchOutside(false);
					userInfoDialog.getWindow().setLayout(500, 600);
					userInfoDialog.show();
					final ListView listview=(ListView) userInfoDialog.findViewById(R.id.reviewlist);
					reviewList.clear();
					database.openAndReadReviewTable(documentId);
					for(int i=0;i<ZoomifierDatabase.documentreviewtitle.size();i++)
					{
						//reviewList.add(new Book(ZoomifierDatabase.documentreviewtitle.get(i),ZoomifierDatabase.documentreview.get(i)));
					}
					AgendaTimeAdapNew adapter=new AgendaTimeAdapNew(PDFReaderActivity.this, 112, reviewList);
					listview.setAdapter(adapter);
					Button writereviewButton=(Button) userInfoDialog.findViewById(R.id.review_button);
					writereviewButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							 final Dialog reveiewDialgo=new Dialog(PDFReaderActivity.this);
							 reveiewDialgo.requestWindowFeature(Window.FEATURE_NO_TITLE);
							 reveiewDialgo.setContentView(R.layout.riview_dialog);
							 reveiewDialgo.setCanceledOnTouchOutside(false);
							 
							 reveiewDialgo.getWindow().setLayout(500, 700);
							 reveiewDialgo.show();
						
							 Button submitButton=(Button) reveiewDialgo.findViewById(R.id.submit_button);
							 submitButton.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									EditText title=(EditText)reveiewDialgo.findViewById(R.id.title_edit);
									EditText reviewtext=(EditText)reveiewDialgo.findViewById(R.id.review_edit);
									if(title.getText().toString().equals("")||reviewtext.getText().toString().equals(""))
									{
										Toast.makeText(PDFReaderActivity.this,"Edit title and Review", Toast.LENGTH_LONG).show();
									}
									else
									{
										//database.insertIntoReviewTable(documentId, title.getText().toString(), reviewtext.getText().toString());
										reviewList.clear();
										database.openAndReadReviewTable(documentId);
										for(int i=0;i<ZoomifierDatabase.documentreviewtitle.size();i++)
										{
											//reviewList.add(new Book(ZoomifierDatabase.documentreviewtitle.get(i),ZoomifierDatabase.documentreview.get(i)));
										}
										AgendaTimeAdapNew adapter=new AgendaTimeAdapNew(PDFReaderActivity.this, 112, reviewList);
										listview.setAdapter(adapter);
										reveiewDialgo.dismiss();
										
									}
									
								}
							});
							 Button cancelButton=(Button) reveiewDialgo.findViewById(R.id.cancel_text);
							 cancelButton.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									reveiewDialgo.dismiss();
									
								}
							});
						}
					});
			}
		});
		sharebuton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RelativeLayout heartlayout=(RelativeLayout) findViewById(R.id.infodialog);
				heartlayout.setVisibility(View.GONE);
				RelativeLayout layout=(RelativeLayout) findViewById(R.id.setting_dialog_layout);
				if(layout.getVisibility()==View.VISIBLE)
				{
					layout.setVisibility(View.GONE);
				}
				else
				{
					layout.setVisibility(View.VISIBLE);
				}
				
				
			}
		});
    heartButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				database.updateDocumentLikes(documentId);
				
			}
		});
    infoButton.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		
		RelativeLayout layout=(RelativeLayout) findViewById(R.id.setting_dialog_layout);
		layout.setVisibility(View.GONE);
		
		RelativeLayout infolayout=(RelativeLayout) findViewById(R.id.infodialog);
		if(infolayout.getVisibility()==View.VISIBLE)
		{	
		infolayout.setVisibility(View.GONE);
		}
		else
		{
			infolayout.setVisibility(View.VISIBLE);
		}
	}
});
		
	}
	public void init()
	{
		webView=(ImageView) findViewById(R.id.webview);
		textHeader=(TextView) findViewById(R.id.document_header_text);
		starButton=(Button) findViewById(R.id.button1);
		sharebuton=(Button) findViewById(R.id.button2);
		heartButton=(Button) findViewById(R.id.button3);
		infoButton=(Button) findViewById(R.id.button4);
		database=new ZoomifierDatabase(this);
		discriptionText=(TextView) findViewById(R.id.discription_field);
		
		
	}
/*public class LoadDocument extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			 super.onPreExecute();
			  progressDialog = new ProgressDialog(PDFReaderActivity.this);
			  progressDialog
			  .setMessage("Please Wait.Downloading your data...");
			  progressDialog.show();			 
		}
		@Override
		protected Void doInBackground(Void... params) {
              String clientId="109";
		
			  String Readerdocument="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
	   	    			 +"<request>" 
	   	    	   		 +"<operation opcode=\"GET_READER_DOCUMENTS\">"
	   	    			 +"<params readerid=\""+readerId+"\" clientid=\""+clientId+"\"/>"
	   	    	         +"</operation>"
	   	    			 +"</request>";
			  
			  String deleteReaderTelnant=
					  "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			   	    			 +"<request>" 
			   	    	   		 +"<operation opcode=\"DELETE_READER_TENANT\">"
			   	    			 +"<params readerid=\""+readerId+"\" clientid=\""+clientId+"\"/>"
			   	    	         +"</operation>"
			   	    			 +"</request>";
			  String forgotPassword="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
	   	    			 +"<request>" 
	   	    	   		 +"<operation opcode=\"FORGOT_USER_PASSWORD\">"
	   	    			 +"<params email=\""+"mailparangat@gmail.com"+"\"/>"
	   	    	         +"</operation>"
	   	    			 +"</request>";
			  
			  
			  try{
                     HttpClient httpclient = new DefaultHttpClient();
                     HttpPost post = new HttpPost("http://ve2.zoomifier.net:8080/PortalInterfaceWS/rest/publishing");
                     StringEntity str = new StringEntity(forgotPassword);
                     str.setContentType("application/xml; charset=utf-8");
                     str.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/xml; charset=utf-8"));
                     post.setEntity(str);
                     HttpResponse response = httpclient.execute(post);
                     HttpEntity entity = response.getEntity();
                    String categoryResponse = EntityUtils.toString(entity);
                    Log.i("Response For GET_READER_DOCUMENTS=========", categoryResponse);
                   } catch ( IOException ioe ) {
                    ioe.printStackTrace();
                   }
          	   try{
                  
          	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                  factory.setNamespaceAware(true);
                  XmlPullParser xpp = factory.newPullParser();
                   xpp.setInput( new StringReader (categoryResponse));
                   String documen=categoryResponse;
                  int eventType = xpp.getEventType();
                   while (eventType != XmlPullParser.END_DOCUMENT) {
                  if(eventType == XmlPullParser.START_DOCUMENT) {
                      
                      } else if(eventType == XmlPullParser.START_TAG) {
                        Log.i("Start tag ",xpp.getName());
                        if(xpp.getName().equals("categories")){
                      	
                         }else if(xpp.getName().equals("category")){
                        	 
                      	  categoryId.add(xpp.getAttributeValue(0));
                      	  categoryName.add(xpp.getAttributeValue(1));
                      	  categoryOrder.add(xpp.getAttributeValue(2));
                      	  categoryImageid.add(xpp.getAttributeValue(3));
                      	  
                        }
                     
                  }
                  
                  else if(eventType == XmlPullParser.END_TAG) {
                      
                  } else if(eventType == XmlPullParser.TEXT) {
                      
                  }
                  eventType = xpp.next();
                 }
                 System.out.println("End document");
          	   }
          	   catch(Exception e)
          	   {
          		   e.printStackTrace();
          	   }
          	   
        	   //drwable=LoadImageFromWebOperations(documentWebViewUrl);
        	   
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
			//NewGridAdapter newAdapter=new NewGridAdapter(GridActivity.this);
		    //popupGridView.setAdapter(newAdapter);
			//webView.setImageDrawable(drwable);
		
			
	}
	
}*/
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
public void settingDialog()
{
settingDialog =new Dialog(PDFReaderActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
	settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    settingDialog.setContentView(R.layout.setting_dialog);
    settingDialog.setCanceledOnTouchOutside(false);
    settingDialog.getWindow().setGravity(Gravity.TOP|Gravity.RIGHT);
	//settingDialog.getWindow().setLayout(300, 600);
	
}


}
