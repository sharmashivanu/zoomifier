package com.zoomactivity;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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

import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SearchAdapter;
import com.zoomifier.adapter.SearchData;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class SearchResultClass extends Activity {
	GridView gridview;
	ArrayList<SearchData> searchlist;
	ArrayList<PageData> pageData;
	TextView textView;
	String searchString, readerID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchlisultfile);

		Bundle bundle = getIntent().getExtras();
		searchString = bundle.getString("searchtext");
		readerID = bundle.getString("readrId");

		init();

		int ot = getResources().getConfiguration().orientation;
		if (Configuration.ORIENTATION_PORTRAIT == ot) {
			gridview.setNumColumns(3);
		} else {
			gridview.setNumColumns(4);
		}
		textView.setText("Search Result for" + " " + "\"" + searchString + "\"");
		new SearchPDF().execute();
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				SearchData book = searchlist.get(arg2);
				if (book.documentContentType.equals("DOCUMENT")) {

					Bundle bundle = new Bundle();
					bundle.putString("documentId", book.documentId);
					bundle.putString("documentName", book.documentName);
					bundle.putString("documentDiscription",
							book.documentDiscription);
					bundle.putString("documentOrginalId",
							book.documentOrginalId);
					bundle.putString("documentContentType",
							book.documentContentType);
					bundle.putString("documentContentWidth",
							book.documentContentWidth);
					bundle.putString("documentContentHeight",
							book.documentContentHeight);
					bundle.putString("documentOwnerId", book.documentOwnerId);
					bundle.putInt("position", 0);
					bundle.putString("clientid", book.client_id);
					bundle.putString("searchstring", searchString);
					bundle.putString("userid", book.userID);
					bundle.putString("favorit", book.like_document);
					bundle.putString("sharedby", book.SharedName);
					bundle.putString("shareddate", book.SharedDate);
					Intent intent = new Intent(SearchResultClass.this,
							PDFImageViewActivity.class);
					intent.putExtras(bundle);
					intent.putExtra("pagedata", pageData.get(arg2));
					int size = pageData.get(arg2).page.size();
					SearchResultClass.this.startActivity(intent);
				} else if (book.documentContentType.equals("VIDEO")) {
					Bundle bundle = new Bundle();
					bundle.putString("documentId", book.documentId);
					bundle.putString("documentName", book.documentName);
					bundle.putString("documentDiscription",
							book.documentDiscription);
					bundle.putString("documentOrginalId",
							book.documentOrginalId);
					bundle.putString("documentContentType",
							book.documentContentType);
					bundle.putString("documentContentWidth",
							book.documentContentWidth);
					bundle.putString("documentContentHeight",
							book.documentContentHeight);
					bundle.putString("searchstring", searchString);
					bundle.putString("documentOwnerId", book.documentOwnerId);
					bundle.putInt("position", 0);
					bundle.putString("clientid", book.client_id);
					bundle.putString("userid", book.userID);
					bundle.putString("favorit", book.like_document);
					bundle.putString("sharedby", book.SharedName);
					bundle.putString("shareddate", book.SharedDate);

					Intent intent = new Intent(SearchResultClass.this,
							ZoomifierVideoPlayer.class);
					intent.putExtras(bundle);

					SearchResultClass.this.startActivity(intent);
				}
				else if(book.documentContentType.equals("HTML5"))
					{
					Bundle bundle = new Bundle();
					bundle.putString("documentId", book.documentId);
					bundle.putString("documentName", book.documentName);
					bundle.putString("documentDiscription",
							book.documentDiscription);
					bundle.putString("documentOrginalId",
							book.documentOrginalId);
					bundle.putString("documentContentType",
							book.documentContentType);
					bundle.putString("documentContentWidth",
							book.documentContentWidth);
					bundle.putString("documentContentHeight",
							book.documentContentHeight);
					bundle.putString("searchstring", searchString);
					bundle.putString("documentOwnerId", book.documentOwnerId);
					bundle.putInt("position", 0);
					bundle.putString("clientid", book.client_id);
					bundle.putString("favorit", book.like_document);
					bundle.putString("userid", book.userID);
					bundle.putString("sharedby", book.SharedName);
					bundle.putString("shareddate", book.SharedDate);
					Intent intent = new Intent(SearchResultClass.this,
							HtmlContentActivity.class);
					intent.putExtras(bundle);

					SearchResultClass.this.startActivity(intent);
					}
					else {
					
					Bundle bundle = new Bundle();
					bundle.putString("documentId", book.documentId);
					bundle.putString("documentName", book.documentName);
					bundle.putString("documentDiscription",
							book.documentDiscription);
					bundle.putString("documentOrginalId",
							book.documentOrginalId);
					bundle.putString("documentContentType",
							book.documentContentType);
					bundle.putString("documentContentWidth",
							book.documentContentWidth);
					bundle.putString("documentContentHeight",
							book.documentContentHeight);
					bundle.putString("searchstring", searchString);
					bundle.putString("documentOwnerId", book.documentOwnerId);
					bundle.putInt("position", 0);
					bundle.putString("clientid", book.client_id);
					bundle.putString("favorit", book.like_document);
					bundle.putString("userid", book.userID);
					bundle.putString("sharedby", book.SharedName);
					bundle.putString("shareddate", book.SharedDate);
					Intent intent = new Intent(SearchResultClass.this,
							ZoomifierView.class);
					intent.putExtras(bundle);

					SearchResultClass.this.startActivity(intent);
				}

			}

		});
	}

	public void init() {
		gridview = (GridView) findViewById(R.id.webview);
		textView = (TextView) findViewById(R.id.document_header_text);
	}

	public class SearchPDF extends AsyncTask<Void, Void, Void> {
		String searchText;
		String shareResponse = "";
		ArrayList<PageData> Pagedata;
		String retrunResponse;
		ArrayList<String> arrayList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			searchlist = new ArrayList<SearchData>();
			Pagedata = new ArrayList<PageData>();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String document = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request>" + "<operation opcode=\"READER_QUERY\">"
					+ "<params text=\"" + searchString + "\" catid=\"" + ""
					+ "\" docid=\"" + "" + "\" clientid=\"" + ""
					+ "\" readerid=\"" + readerID + "\"/>" + "</operation>"
					+ "</request>";
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

				String documetnId = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {

					} else if (eventType == XmlPullParser.START_TAG) {
						Log.i("Start tag ", xpp.getName());
						if (xpp.getName().equals("Document")) {
							arrayList = new ArrayList<String>();
							documetnId = xpp.getAttributeValue(0);
							searchlist.add(new SearchData(xpp
									.getAttributeValue(0), xpp
									.getAttributeValue(9), xpp
									.getAttributeValue(1), "", xpp
									.getAttributeValue(5), xpp
									.getAttributeValue(6), xpp
									.getAttributeValue(8), "", xpp
									.getAttributeValue(4), readerID, "Unlike",
									"", "",xpp.getAttributeValue(7)));

						} else if (xpp.getName().equals("SearchResults")) {
							searchText = xpp.getAttributeValue(0);
						} else if (xpp.getName().equals("Page")) {

							arrayList.add(xpp.getAttributeValue(0));
						}

					} else if (eventType == XmlPullParser.END_TAG) {

						if (xpp.getName().equals("Document")) {

							Pagedata.add(new PageData(arrayList));

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
			SearchAdapter adapter = new SearchAdapter(SearchResultClass.this,
					12, searchlist);
			gridview.setAdapter(adapter);
			/*
			 * if (searchDataList.size() != 0) { Bundle bundle = new Bundle();
			 * bundle.putString("searchtext", searchText); Intent intent = new
			 * Intent(LibraryClass.this, SearchResultClass.class);
			 * intent.putExtras(bundle); intent.putExtra("searchdata",
			 * searchDataList); int size = Pagedata.get(0).page.size();
			 * intent.putExtra("pagedata", Pagedata); startActivity(intent);
			 */
			// }

		}

	}
}
