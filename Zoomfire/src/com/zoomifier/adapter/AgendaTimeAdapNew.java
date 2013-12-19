package com.zoomifier.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;  

import com.zoomactivity.R;

public class AgendaTimeAdapNew extends ArrayAdapter<Book>{

	
	private Activity activity;
	private Vector<Book> items;
	public Vector<Book> originalList;
	public Vector<Book> filteredList;
	private Filter filter;
	
	

	public AgendaTimeAdapNew(Context context, int layoutResourceId, Vector<Book> data) {
		super(context,R.layout.review_list_item,data);

	   
		this.activity = (Activity) context;
		this.items = data;
		this.originalList = new Vector<Book>(data);
		this.filteredList = new Vector<Book>(data);
			
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {

			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.review_list_item, null);
			
			holder = new ViewHolder();
			convertView.setTag(holder);
			holder.name=(TextView) convertView.findViewById(R.id.nameTV);
			holder.image1=(ImageView) convertView.findViewById(R.id.star1);
			holder.image2=(ImageView) convertView.findViewById(R.id.star2);
			holder.image3=(ImageView) convertView.findViewById(R.id.star3);
			holder.image4=(ImageView) convertView.findViewById(R.id.star4);
			holder.image5=(ImageView) convertView.findViewById(R.id.star5);
			holder.desgination=(TextView) convertView.findViewById(R.id.nameTVV);
			holder.time=(TextView) convertView.findViewById(R.id.time_text);
			Book speakerlist = filteredList.get(position);
			String s=speakerlist.title;

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < items.size()) {
			
			

			final Book book = items.get(position);
		
				holder.name.setText(Integer.toString(position+1)+"-"+book.title);
				holder.desgination.setText(book.reviewName);
				int i=Integer.parseInt(book.numberofstar);
				holder.time.setText(book.time);
				
				if(i==1)
				{
					holder.image1.setImageResource(R.drawable.golden_star);
				}
				else if(i==2)
				{
					holder.image1.setImageResource(R.drawable.golden_star);
					holder.image2.setImageResource(R.drawable.golden_star);
					
				}
				else if(i==3)
				{
					holder.image1.setImageResource(R.drawable.golden_star);
					holder.image2.setImageResource(R.drawable.golden_star);
					holder.image3.setImageResource(R.drawable.golden_star);
				}
				else if(i==4)
				{
					holder.image1.setImageResource(R.drawable.golden_star);
					holder.image2.setImageResource(R.drawable.golden_star);
					holder.image3.setImageResource(R.drawable.golden_star);
					holder.image4.setImageResource(R.drawable.golden_star);
				}
				else if(i==5)
				{
					holder.image1.setImageResource(R.drawable.golden_star);
					holder.image2.setImageResource(R.drawable.golden_star);
					holder.image3.setImageResource(R.drawable.golden_star);
					holder.image4.setImageResource(R.drawable.golden_star);
					holder.image5.setImageResource(R.drawable.golden_star);
				}
				else
				{
					
				}
			
			}
			
		
		

		return convertView;
	}

	private static class ViewHolder {
		TextView name,headingTV,desgination,time;
		LinearLayout headingLL,nameLL;
		ImageView image1,image2,image3,image4,image5;
		 
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Book getItem(int position) {
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
				Vector<Book> list = new Vector<Book>(
						originalList);
				results.values = list;
				results.count = list.size();
				
			} else {
								final Vector<Book> list = new Vector<Book>(
						originalList);
				final Vector<Book> newList = new Vector<Book>();
				int count = list.size();

				for (int i = 0; i < count; i++) {
					final Book friendsList = list.get(i);
					
					final String value = friendsList.title.toLowerCase();
					 final int val=value.lastIndexOf(" ");
					
					if (value.startsWith(prefix)) {
						newList.add(friendsList);
					}
					
					else if(value.substring(val+1, value.length()).startsWith(prefix))
					{  
					 if(val>=0)
					 {
					   newList.add(friendsList);
					  }
					
					}
								
				}
				results.values = newList;
				results.count = newList.size();
				
			}
			
			return results;
		}
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredList = (Vector<Book>)results.values;
			clear();
			int count = filteredList.size();
			for(int i=0; i<count; i++){
				Book friendsList = (Book)filteredList.get(i);
				add(friendsList);
			}
		}
		
}
}


