package com.zoomifier.adapter;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomactivity.DocumetntLikes;
import com.zoomactivity.PDFImageViewActivity;
import com.zoomactivity.PdfReaderGridView;
import com.zoomactivity.R;
import com.zoomactivity.SecondActivity;
import com.zoomactivity.ZoomifierVideoPlayer;
import com.zoomactivity.ZoomifierView;


import com.zoomifier.imagedownload.PDImagedownloader;


public class DocumentLikeAdapter extends ArrayAdapter<SecondActivityData>{
	private Activity activity;
	private Vector<SecondActivityData> items;
	public Vector<SecondActivityData> originalList;
	public Vector<SecondActivityData> filteredList;
	private Filter filter;
	public  PDImagedownloader imageLoader; 
	public static Dialog dialog;
	GridView gridView;
	ArrayList<Integer> drawble=new ArrayList<Integer>();
	public  DocumentLikeAdapter(Context context, int layoutResourceId, Vector<SecondActivityData> data, GridView gridView) {
		super(context,12,data);
		this.activity = (Activity) context;
		this.items = data;
		drawble.clear();
		this.originalList = new Vector<SecondActivityData>(data);
		this.filteredList = new Vector<SecondActivityData>(data);
		imageLoader=new  PDImagedownloader(activity);
	   
	    this.gridView=gridView;
		 	
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {

			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.gridview_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.gridtext);
			holder.img = (ImageView) convertView.findViewById(R.id.grid_image);	
		//	holder.playimage = (ImageView) convertView.findViewById(R.id.playimage);	
			convertView.setTag(holder);
			SecondActivityData speakerlist = filteredList.get(position);
			String s=speakerlist.documentName;

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < items.size()) {
		
			final SecondActivityData book = items.get(position);
		
		       if(book.documentId.equals(""))
		       {
		    	   holder.name.setText("");
		    	   holder.img.setVisibility(View.INVISIBLE);
		    	   holder.playimage.setVisibility(View.GONE);
		       }
		       else{
		    	   holder.img.setVisibility(View.VISIBLE);
		    		holder.name.setText(book.documentName);
					String url="http://ve1.zoomifier.net/"+book.client_id+"/"+book.documentId+"/ipad/Page1Thb.jpg";
					//String url=http://ve1.zoomifier.net/109/437/ipad/Page1Thb.jpg
					imageLoader.DisplayImage(url,holder.img);
					if(book.documentContentType.equals("VIDEO"))
					{
					   holder.playimage.setVisibility(View.VISIBLE);
					
					}
					else
					{
						 holder.playimage.setVisibility(View.GONE);
					}
		       }
			
				//holder.img.setBackgroundResource(Integer.parseInt(book.imageid));
				holder.img.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(book.documentContentType.equals("DOCUMENT"))
						{
							Bundle bundle=new Bundle();
							bundle.putString("documentId",book.documentId);
							bundle.putString("documentName",book.documentName);
							bundle.putString("documentDiscription",book.documentDiscription);
							bundle.putString("originalId",book.originalId);
							bundle.putString("documentContentType",book.documentContentType);
							bundle.putString("documentContentWidth",book.documentContentWidth);
							bundle.putString("documentContentHeight",book.documentContentHeight);
							bundle.putString("documentOwnerId",book.documentOwnerId);
							bundle.putInt("position",0);
							bundle.putString("clientid", book.client_id);
							bundle.putString("userid", book.userID);
							bundle.putString("favorit", book.like_document);
							bundle.putString("sharedby", book.SharedName);
							bundle.putString("shareddate", book.SharedDate);
						
							Intent intent=new Intent(activity,PDFImageViewActivity.class);
						    intent.putExtras(bundle);
							activity.startActivity(intent);
							activity.finish();
						}
						else if(book.documentContentType.equals("VIDEO"))
						{
							Bundle bundle=new Bundle();
							bundle.putString("documentId",book.documentId);
							bundle.putString("documentName",book.documentName);
							bundle.putString("documentDiscription",book.documentDiscription);
							bundle.putString("originalId",book.originalId);
							bundle.putString("documentContentType",book.documentContentType);
							bundle.putString("documentContentWidth",book.documentContentWidth);
							bundle.putString("documentContentHeight",book.documentContentHeight);
							bundle.putString("documentOwnerId",book.documentOwnerId);
							bundle.putInt("position",position);
							bundle.putString("clientid", book.client_id);
							bundle.putString("userid", book.userID);
							bundle.putString("favorit", book.like_document);
							bundle.putString("sharedby", book.SharedName);
							bundle.putString("shareddate", book.SharedDate);
							
							
							Intent intent=new Intent(activity,ZoomifierVideoPlayer.class);
						    intent.putExtras(bundle);
							activity.startActivity(intent);
							activity.finish();
						}
						else
						{
						Bundle bundle=new Bundle();
						bundle.putString("documentId",book.documentId);
						bundle.putString("documentName",book.documentName);
						bundle.putString("documentDiscription",book.documentDiscription);
						bundle.putString("originalId",book.originalId);
						bundle.putString("documentContentType",book.documentContentType);
						bundle.putString("documentContentWidth",book.documentContentWidth);
						bundle.putString("documentContentHeight",book.documentContentHeight);
						bundle.putString("documentOwnerId",book.documentOwnerId);
						bundle.putInt("position",position);
						bundle.putString("clientid", book.client_id);
						bundle.putString("favorit", book.like_document);
						bundle.putString("userid", book.userID);
						bundle.putString("sharedby", book.SharedName);
						bundle.putString("shareddate", book.SharedDate);
						Intent intent=new Intent(activity,ZoomifierView.class);
					    intent.putExtras(bundle);
						activity.startActivity(intent);
						activity.finish();
						}
						
					}
				});
				holder.playimage.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(book.documentContentType.equals("DOCUMENT"))
						{
							Bundle bundle=new Bundle();
							bundle.putString("documentId",book.documentId);
							bundle.putString("documentName",book.documentName);
							bundle.putString("documentDiscription",book.documentDiscription);
							bundle.putString("originalId",book.originalId);
							bundle.putString("documentContentType",book.documentContentType);
							bundle.putString("documentContentWidth",book.documentContentWidth);
							bundle.putString("documentContentHeight",book.documentContentHeight);
							bundle.putString("documentOwnerId",book.documentOwnerId);
							bundle.putInt("position",0);
							bundle.putString("clientid", book.client_id);
							bundle.putString("userid", book.userID);
							bundle.putString("favorit", book.like_document);
							bundle.putString("sharedby", book.SharedName);
							bundle.putString("shareddate", book.SharedDate);
							activity.finish();
						
							Intent intent=new Intent(activity,PDFImageViewActivity.class);
						    intent.putExtras(bundle);
							activity.startActivity(intent);
						}
						else if(book.documentContentType.equals("VIDEO"))
						{
							Bundle bundle=new Bundle();
							bundle.putString("documentId",book.documentId);
							bundle.putString("documentName",book.documentName);
							bundle.putString("documentDiscription",book.documentDiscription);
							bundle.putString("originalId",book.originalId);
							bundle.putString("documentContentType",book.documentContentType);
							bundle.putString("documentContentWidth",book.documentContentWidth);
							bundle.putString("documentContentHeight",book.documentContentHeight);
							bundle.putString("documentOwnerId",book.documentOwnerId);
							bundle.putInt("position",position);
							bundle.putString("clientid", book.client_id);
							bundle.putString("userid", book.userID);
							bundle.putString("favorit", book.like_document);
							bundle.putString("sharedby", book.SharedName);
							bundle.putString("shareddate", book.SharedDate);
							
							
							Intent intent=new Intent(activity,ZoomifierVideoPlayer.class);
						    intent.putExtras(bundle);
							activity.startActivity(intent);
							activity.finish();
						}
						else
						{
						Bundle bundle=new Bundle();
						bundle.putString("documentId",book.documentId);
						bundle.putString("documentName",book.documentName);
						bundle.putString("documentDiscription",book.documentDiscription);
						bundle.putString("originalId",book.originalId);
						bundle.putString("documentContentType",book.documentContentType);
						bundle.putString("documentContentWidth",book.documentContentWidth);
						bundle.putString("documentContentHeight",book.documentContentHeight);
						bundle.putString("documentOwnerId",book.documentOwnerId);
						bundle.putInt("position",position);
						bundle.putString("clientid", book.client_id);
						bundle.putString("favorit", book.like_document);
						bundle.putString("userid", book.userID);
						bundle.putString("sharedby", book.SharedName);
						bundle.putString("shareddate", book.SharedDate);
						Intent intent=new Intent(activity,ZoomifierView.class);
						activity.finish();
					    intent.putExtras(bundle);
						activity.startActivity(intent);
						}
						
					}
				});
				
				 
	          
		}
		return convertView;
	}

	private static class ViewHolder {
		TextView name,headingTV,desgination;
		LinearLayout headingLL,nameLL;
		ImageView img,playimage;
		//RelativeLayout nameLL;
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
				
				Display display = ((WindowManager) activity.getSystemService(activity.WINDOW_SERVICE))
						.getDefaultDisplay();

			
				
		
				//}
				results.values = newList;
				results.count = newList.size();
			}
			return results;
		}
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredList = (Vector<SecondActivityData>)results.values;
			clear();
			int count = filteredList.size();
			for(int i=0; i<count; i++){
				SecondActivityData friendsList = (SecondActivityData)filteredList.get(i);
				add(friendsList);
			}
		}
		
	}
	}

