package com.zoomifier.adapter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.Executors;

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

import ru.truba.touchgallery.TouchView.TouchImageView;
import ru.truba.touchgallery.TouchView.WrapMotionEvent;

import com.imagemanagement.FileCache;
import com.zoomactivity.BarCodeWebViewActivity;
import com.zoomactivity.HtmlContentActivity;
import com.zoomactivity.LibraryClass;
import com.zoomactivity.PDFImageViewActivity;
import com.zoomactivity.PdfReaderGridView;
import com.zoomactivity.R;
import com.zoomactivity.SecondActivity;
import com.zoomactivity.ZoomifierVideoPlayer;
import com.zoomactivity.ZoomifierView;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomifier.imagedownload.BarCodeImageDownloader;

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
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SecondGridAdapter extends ArrayAdapter<SecondActivityData> {
	private Activity activity;
	private Vector<SecondActivityData> items;
	public Vector<SecondActivityData> originalList;
	public Vector<SecondActivityData> filteredList;
	private Filter filter;
	public PDImagedownloader imageLoader;
	BarCodeImageDownloader barimage;
	public static Dialog dialog;
	String readerId, clientId, docId;
	float newx, oldx;
	Button oldbutton;
	ArrayList<Integer> drawble = new ArrayList<Integer>();
	 SecondActivityData book;
	 SecondActivityData clikdata;
	 ZoomifierDatabase database;
	 int deletpostion;


	public SecondGridAdapter(Context context, int layoutResourceId,
			Vector<SecondActivityData> data) {
		super(context, 12, data);
		this.activity = (Activity) context;
		this.items = data;
		drawble.clear();
		this.originalList = new Vector<SecondActivityData>(data);
		this.filteredList = new Vector<SecondActivityData>(data);
		imageLoader = new PDImagedownloader(activity);
		barimage = new BarCodeImageDownloader(activity);
		database=new ZoomifierDatabase(activity);
		
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.gridview_item, null);
			holder = new ViewHolder();
			holder.document_name = (TextView) convertView
					.findViewById(R.id.document_name);
			holder.document_corportation = (TextView) convertView
					.findViewById(R.id.corporation_name);
			holder.shared_date = (TextView) convertView
					.findViewById(R.id.shared_date);
			holder.img = (ImageView) convertView.findViewById(R.id.grid_image);
			holder.deletButton = (Button) convertView
					.findViewById(R.id.delete_button);
			holder.relativelayout = (RelativeLayout) convertView
					.findViewById(R.id.item_layout);
			oldbutton = holder.deletButton;
			// holder.playimage = (ImageView)
			// convertView.findViewById(R.id.playimage);
			convertView.setTag(holder);
			SecondActivityData speakerlist = filteredList.get(position);
			

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < items.size()) {
			holder.deletButton.setVisibility(View.GONE);

			 book = items.get(position);
			if (book.documentId.equals("qrcode")) {
				if (!book.documentDiscription.equals(""))
					holder.document_name.setText(book.documentDiscription);
				else
					holder.document_name.setText(book.documentName);
				barimage.DisplayImage(book.documentName, holder.img);
				holder.document_corportation.setText("Scanned");
				holder.shared_date.setText(book.SharedDate);
			} else {
				holder.document_name.setText(book.documentName);
				if (book.SharedName != null)
					holder.document_corportation.setText(book.clientName);
				if (book.SharedDate != null)
					holder.shared_date.setText(book.SharedDate);
				String url = "http://ve1.zoomifier.net/" + book.client_id + "/"
						+ book.documentId + "/ipad/Page1Thb.jpg";
				imageLoader.DisplayImage(url, holder.img);
			}

		}
      holder.deletButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			  clikdata=items.get(position);
			  deletpostion=position;
			  docId=clikdata.documentId;
			  clientId=clikdata.client_id;
			  readerId=clikdata.userID;
			  new DeleteDocument().execute();
			  holder.deletButton.setVisibility(View.GONE);
		
			
		}
	});
		holder.relativelayout.setOnTouchListener(new View.OnTouchListener() {
      
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
		      clikdata=items.get(position);
                
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					oldx = event.getX();
					newx = event.getX();
					break;

				case MotionEvent.ACTION_POINTER_DOWN:

					break;

				case MotionEvent.ACTION_UP:
					 newx = event.getX();
				 if (oldbutton.getVisibility() == View.VISIBLE) {
						oldbutton.setVisibility(View.GONE);
					}
					float diffence = oldx - newx;
					if (diffence > 20) {
						holder.deletButton.setVisibility(View.VISIBLE);
						oldbutton = holder.deletButton;
					}
					

					else if (diffence < -20) {
						holder.deletButton.setVisibility(View.VISIBLE);
						oldbutton = holder.deletButton;
					}
					else if(diffence==0)
					{

						if (clikdata.documentId.equals("qrcode")) {
							Bundle bundle = new Bundle();
							bundle.putString("url", clikdata.documentName);
							Intent intent = new Intent(activity,
									BarCodeWebViewActivity.class);
							intent.putExtras(bundle);
							activity.startActivity(intent);
						} else {

							if ( clikdata.documentContentType
									.equals("DOCUMENT")) {
								Bundle bundle = new Bundle();
								bundle.putString("documentId",
										 clikdata.documentId);
								bundle.putString("documentName",
										 clikdata.documentName);
								bundle.putString("documentDiscription",
										 clikdata.documentDiscription);
								bundle.putString("originalId",
										 clikdata.originalId);
								bundle.putString("documentContentType",
										 clikdata.documentContentType);
								bundle.putString("documentContentWidth",
										 clikdata.documentContentWidth);
								bundle.putString("documentContentHeight",
										 clikdata.documentContentHeight);
								bundle.putString("documentOwnerId",
										 clikdata.documentOwnerId);
								bundle.putInt("position", 0);
								bundle.putString("clientid",
										 clikdata.client_id);
								bundle.putString("userid",  clikdata.userID);
								bundle.putString("favorit",
										 clikdata.like_document);
								bundle.putString("sharedby",
										 clikdata.SharedName);
								bundle.putString("shareddate",
										 clikdata.SharedDate);

								Intent intent = new Intent(activity,
										PDFImageViewActivity.class);
								intent.putExtras(bundle);
								ArrayList<String> arraylist = new ArrayList<String>();
								PageData pageData = new PageData(arraylist);
								intent.putExtra("pagedata", pageData);
								activity.startActivity(intent);
							} else if ( clikdata.documentContentType
									.equals("VIDEO")) {
								Bundle bundle = new Bundle();
								bundle.putString("documentId",
										 clikdata.documentId);
								bundle.putString("documentName",
										book.documentName);
								bundle.putString("documentDiscription",
										 clikdata.documentDiscription);
								bundle.putString("originalId",
										 clikdata.originalId);
								bundle.putString("documentContentType",
										 clikdata.documentContentType);
								bundle.putString("documentContentWidth",
										 clikdata.documentContentWidth);
								bundle.putString("documentContentHeight",
										 clikdata.documentContentHeight);
								bundle.putString("documentOwnerId",
										 clikdata.documentOwnerId);
								bundle.putInt("position", 0);
								bundle.putString("clientid",
										 clikdata.client_id);
								bundle.putString("userid",  clikdata.userID);
								bundle.putString("favorit",
										 clikdata.like_document);
								bundle.putString("sharedby",
										 clikdata.SharedName);
								bundle.putString("shareddate",
										 clikdata.SharedDate);
								Intent intent = new Intent(activity,
										ZoomifierVideoPlayer.class);
								intent.putExtras(bundle);
								ArrayList<String> arraylist = new ArrayList<String>();
								PageData pageData = new PageData(arraylist);
								intent.putExtra("pagedata", pageData);
								activity.startActivity(intent);
							}
							 else if(clikdata.documentContentType
									.equals("HTML5")){
									Bundle bundle = new Bundle();
									bundle.putString("documentId",
											 clikdata.documentId);
									bundle.putString("documentName",
											 clikdata.documentName);
									bundle.putString("documentDiscription",
											 clikdata.documentDiscription);
									bundle.putString("originalId",
											 clikdata.originalId);
									bundle.putString("documentContentType",
											 clikdata.documentContentType);
									bundle.putString("documentContentWidth",
											 clikdata.documentContentWidth);
									bundle.putString("documentContentHeight",
											 clikdata.documentContentHeight);
									bundle.putString("documentOwnerId",
											 clikdata.documentOwnerId);
									bundle.putInt("position", 0);
									bundle.putString("clientid",
											 clikdata.client_id);
									bundle.putString("favorit",
											 clikdata.like_document);
									bundle.putString("userid", book.userID);
									bundle.putString("sharedby",
											 clikdata.SharedName);
									bundle.putString("shareddate",
											 clikdata.SharedDate);
									Intent intent = new Intent(activity,
											HtmlContentActivity.class);
									intent.putExtras(bundle);
									ArrayList<String> arraylist = new ArrayList<String>();
									PageData pageData = new PageData(arraylist);
									intent.putExtra("pagedata", pageData);
									activity.startActivity(intent);
								}
							else {
								Bundle bundle = new Bundle();
								bundle.putString("documentId",
										 clikdata.documentId);
								bundle.putString("documentName",
										 clikdata.documentName);
								bundle.putString("documentDiscription",
										 clikdata.documentDiscription);
								bundle.putString("originalId",
										 clikdata.originalId);
								bundle.putString("documentContentType",
										 clikdata.documentContentType);
								bundle.putString("documentContentWidth",
										 clikdata.documentContentWidth);
								bundle.putString("documentContentHeight",
										 clikdata.documentContentHeight);
								bundle.putString("documentOwnerId",
										 clikdata.documentOwnerId);
								bundle.putInt("position", 0);
								bundle.putString("clientid",
										 clikdata.client_id);
								bundle.putString("favorit",
										 clikdata.like_document);
								bundle.putString("userid", book.userID);
								bundle.putString("sharedby",
										 clikdata.SharedName);
								bundle.putString("shareddate",
										 clikdata.SharedDate);
								Intent intent = new Intent(activity,
										ZoomifierView.class);
								intent.putExtras(bundle);
								ArrayList<String> arraylist = new ArrayList<String>();
								PageData pageData = new PageData(arraylist);
								intent.putExtra("pagedata", pageData);
								activity.startActivity(intent);
							}
						}

					
					}
					break;

				case MotionEvent.ACTION_POINTER_UP:

					break;
				case MotionEvent.ACTION_MOVE:

					break;
				}

				return true;
			}
		});
		return convertView;
	}

	private static class ViewHolder {
		TextView document_name, document_corportation, shared_date;
		LinearLayout headingLL, nameLL;
		RelativeLayout relativelayout;
		ImageView img, playimage;
		Button deletButton;

	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public SecondActivityData getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new FriendsNameFilter();
		return filter;
	}

	private class FriendsNameFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			String prefix = constraint.toString().toLowerCase();
			if (prefix.equals("") || prefix.length() == 0) {
				Vector<SecondActivityData> list = new Vector<SecondActivityData>(
						originalList);
				results.values = list;
				results.count = list.size();
			} else {
				final Vector<SecondActivityData> list = new Vector<SecondActivityData>(
						originalList);
				final Vector<SecondActivityData> newList = new Vector<SecondActivityData>();
				int count = list.size();

				for (int i = 0; i < count; i++) {
					final SecondActivityData friendsList = list.get(i);
					final String value = friendsList.documentName.toLowerCase();
					if (value.startsWith(prefix)) {
						newList.add(friendsList);
					}
				}

				Display display = ((WindowManager) activity
						.getSystemService(activity.WINDOW_SERVICE))
						.getDefaultDisplay();

				int orientation = display.getOrientation();

				int ot = activity.getResources().getConfiguration().orientation;

				if (Configuration.ORIENTATION_PORTRAIT == ot) {
				} else {
				}

				results.values = newList;
				results.count = newList.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredList = (Vector<SecondActivityData>) results.values;
			clear();
			int count = filteredList.size();
			for (int i = 0; i < count; i++) {
				SecondActivityData friendsList = (SecondActivityData) filteredList
						.get(i);
				add(friendsList);
			}
		}

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
					+ clientId + "\" docid=\"" + docId + "\"/>"
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
               items.remove(deletpostion);
               database.deleteDocumentRow(docId);
               notifyDataSetChanged();
				}
				catch (Exception e) {
					
				}
               
			}

		}

	}
	
	
	public void displayAlert(String msg) {
		new AlertDialog.Builder(activity)
				.setMessage(msg)
				.setTitle("Delete")
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

}
