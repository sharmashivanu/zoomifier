package com.zoomifier.adapter;

import java.util.Vector;

import com.zoomactivity.DocumetntLikes;
import com.zoomactivity.GridActivity;
import com.zoomactivity.R;
import com.zoomactivity.SecondActivity;
import com.zoomifier.imagedownload.PDImagedownloader;

import android.app.Activity;
import android.app.ActivityOptions;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GridViewAdapter extends ArrayAdapter<GridData> {
	private Activity activity;
	private Vector<GridData> items;
	public Vector<GridData> originalList;
	public Vector<GridData> filteredList;
	private Filter filter;
	public PDImagedownloader imageLoader;
	public static Dialog dialog;

	public GridViewAdapter(Context context, int layoutResourceId,
			Vector<GridData> data) {
		super(context, R.layout.grid_view_item_1, data);
		this.activity = (Activity) context;
		this.items = data;
		this.originalList = new Vector<GridData>(data);
		this.filteredList = new Vector<GridData>(data);
		imageLoader = new PDImagedownloader(activity);

	}
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {

			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.grid_view_item_1, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.gridtext);
			holder.img = (ImageView) convertView.findViewById(R.id.grid_image);
			convertView.setTag(holder);
			GridData speakerlist = filteredList.get(position);
			String s = speakerlist.companyName;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

	if (position < items.size()) {
			final GridData book = items.get(position);
			if(book.companyId.equals(""))
			{
				holder.name.setText("");
				holder.img.setVisibility(View.INVISIBLE);
			}
	    	else
			{
			if(position==0)
			{holder.img.setVisibility(View.VISIBLE);
				holder.name.setText(book.companyName);
				holder.img.setImageResource(R.drawable.heart_icon);
				holder.img.setPadding(20, 45, 20, 10);
		
			}else
			{	
			holder.img.setVisibility(View.VISIBLE);
			holder.name.setText(book.companyName);
			String url = "http://ve1.zoomifier.net/" + book.companyId
					+ "/images/" + book.companyId + ".jpg";
			//holder.img.setBackgroundResource();
		    imageLoader.DisplayImage(url,holder.img);
		    
			}
		
			}
			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (position == 0) {
						    Bundle bundle = new Bundle();
						    bundle.putString("readerId", book.readerId);
							bundle.putString("comapnyId", book.companyId);
							bundle.putString("companyName", book.companyName);
							bundle.putString("likesignal", "");
						  Intent intent = new Intent(activity,
								DocumetntLikes.class);
						 intent.putExtras(bundle);
						 activity.startActivity(intent);
					} 
					else {
						
					if(book.companyId.equals(""))
						{
							
					}
						else{
						    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		                      	                        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		                        //bitmap.eraseColor(colour);
		                        Bundle bundle = new Bundle();
		                        bundle = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0).toBundle();
		                        bundle.putString("readerId", book.readerId);
								bundle.putString("comapnyId", book.companyId);
								bundle.putString("companyName", book.companyName);
								bundle.putString("grid","");
								bundle.putString("likesignal", "");
								Intent intent = new Intent(activity,
										SecondActivity.class);
								intent.putExtras(bundle);
								activity.startActivity(intent,bundle);
		                        
		                    }
						    else{
					    Bundle bundle = new Bundle();
					    bundle.putString("readerId", book.readerId);
						bundle.putString("comapnyId", book.companyId);
						bundle.putString("companyName", book.companyName);
						bundle.putString("grid","");
						bundle.putString("likesignal", "");
						Intent intent = new Intent(activity,
								SecondActivity.class);
						intent.putExtras(bundle);
						activity.startActivity(intent);
						    }
						}
					}

				}
			});
			}
		
		return convertView;
	}

	private static class ViewHolder {
		TextView name, headingTV, desgination;
		LinearLayout headingLL, nameLL;
		ImageView img;
		// RelativeLayout nameLL;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public GridData getItem(int position) {
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
				Vector<GridData> list = new Vector<GridData>(originalList);
				results.values = list;
				results.count = list.size();
			} else {
				final Vector<GridData> list = new Vector<GridData>(originalList);
				final Vector<GridData> newList = new Vector<GridData>();
			     newList.add(new GridData("111", "My Likes", "",""));
				int count = list.size();
				for (int i = 0; i < count; i++) {
					final GridData friendsList = list.get(i);
					final String value = friendsList.companyName.toLowerCase();
				
					if (value.startsWith(prefix)) {
						newList.add(friendsList);
					}
				}
			  if(newList.size()>=2)
			  {
				if(newList.get(0).companyId.equals(newList.get(1).companyId))
				{
					newList.remove(1);
				}
			  }
				Display display = ((WindowManager) activity.getSystemService(activity.WINDOW_SERVICE))
						.getDefaultDisplay();
				int orientation=display.getOrientation();
			if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||orientation==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){/*
				if(newList.size()<15)
				{

					int size=15-newList.size();
					for (int i = 0; i < size; i++) {
						newList.add(new GridData("", "yuyu", ""));
					}
					
					int mod = newList.size() % 3;
					int s;
					if(mod==0)
					{
						s=0;
					}
					else
					{
						 s=3-mod;
					}
					
					for (int i = 0; i < s; i++) {
						newList.add(new GridData("", "yuyu", ""));
					}
				}
				
				else
					{
					int mod = newList.size() % 3;
					int s;
					if(mod==0)
					{
						s=0;
					}
					else
					{
						 s=3-mod;
					}
					
					for (int i = 0; i < s; i++) {
						newList.add(new GridData("", "yuyu", ""));
					}
					
					}
			*/}
			else
			{/*
				if(newList.size()<6)
				{

					int size=6-newList.size();
					for (int i = 0; i < size; i++) {
						newList.add(new GridData("", "yuyu", ""));
					}
					
					int mod = newList.size() % 3;
					int s;
					if(mod==0)
					{
						s=0;
					}
					else
					{
						 s=3-mod;
					}
					
					for (int i = 0; i < s; i++) {
						newList.add(new GridData("", "yuyu", ""));
					}
				}
				
				else
					{
					int mod = newList.size() % 3;
					int s;
					if(mod==0)
					{
						s=0;
					}
					else
					{
						 s=3-mod;
					}
					
					for (int i = 0; i < s; i++) {
						newList.add(new GridData("", "yuyu", ""));
					}
					
					}
			*/}
				
				results.values = newList;
				results.count = newList.size();
				
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredList = (Vector<GridData>) results.values;
			clear();
			int count = filteredList.size();
			for (int i = 0; i < count; i++) {
				GridData friendsList = (GridData) filteredList.get(i);
				add(friendsList);
			}
		}

	}
}
