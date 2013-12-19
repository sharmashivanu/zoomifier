package com.zoomifier.adapter;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomactivity.R;

import com.zoomifier.imagedownload.PDImagedownloader;

public class SearchAdapter extends ArrayAdapter<SearchData>{
	private Activity activity;
	private ArrayList<SearchData> items;
	public ArrayList<SearchData> originalList;
	public ArrayList<SearchData> filteredList;
	private Filter filter;
	public PDImagedownloader imageLoader;
	public static Dialog dialog;

	public SearchAdapter(Context context, int layoutResourceId,
			ArrayList<SearchData> data) {
		super(context, 12, data);
		this.activity = (Activity) context;
		this.items = data;
		this.originalList = new ArrayList<SearchData>(data);
		this.filteredList = new ArrayList<SearchData>(data);
		imageLoader = new PDImagedownloader(activity);

	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {

			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.pdf_gridview_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.gridtext);
			holder.img = (ImageView) convertView.findViewById(R.id.grid_image);
			convertView.setTag(holder);
			SearchData speakerlist = filteredList.get(position);
			String s = speakerlist.documentName;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < items.size()) {

			final SearchData book = items.get(position);

			holder.name.setText(book.documentName);
			String url="http://ve1.zoomifier.net/"+book.client_id+"/"+book.documentId+"/ipad/Page"+Integer.toString(position+1)+"Thb.jpg";
			imageLoader.DisplayImage(url,holder.img);
			
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
	public SearchData getItem(int position) {
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
				Vector<SearchData> list = new Vector<SearchData>(originalList);
				results.values = list;
				results.count = list.size();
			} else {
				final Vector<SearchData> list = new Vector<SearchData>(originalList);
				final Vector<SearchData> newList = new Vector<SearchData>();
				int count = list.size();
				for (int i = 0; i < count; i++) {
					final SearchData friendsList = list.get(i);
					final String value = friendsList.documentName.toLowerCase();
					if (value.startsWith(prefix)) {
						newList.add(friendsList);
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
			filteredList = (ArrayList<SearchData>) results.values;
			clear();
			int count = filteredList.size();
			for (int i = 0; i < count; i++) {
				SearchData friendsList = (SearchData) filteredList.get(i);
				add(friendsList);
			}
		}

	}
}
