package com.zoomifier.adapter;

import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomactivity.DocumetntLikes;
import com.zoomactivity.PDFImageViewActivity;
import com.zoomactivity.PdfReaderGridView;
import com.zoomactivity.R;
import com.zoomactivity.SecondActivity;

import com.zoomifier.imagedownload.PDImagedownloader;

public class PDFGridViewAdapter extends ArrayAdapter<PDFGridData>{
	private Activity activity;
	private Vector<PDFGridData> items;
	public Vector<PDFGridData> originalList;
	public Vector<PDFGridData> filteredList;
	private Filter filter;
	public PDImagedownloader imageLoader;
	public static Dialog dialog;

	public PDFGridViewAdapter(Context context, int layoutResourceId,
			Vector<PDFGridData> data) {
		super(context, 12, data);
		this.activity = (Activity) context;
		this.items = data;
		this.originalList = new Vector<PDFGridData>(data);
		this.filteredList = new Vector<PDFGridData>(data);
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
			PDFGridData speakerlist = filteredList.get(position);
			String s = speakerlist.documentName;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < items.size()) {

			final PDFGridData book = items.get(position);
            
			holder.name.setText(book.documentName);
			if(book.signal==1)
			{
				holder.name.setTextColor(Color.RED);
			}
			else
			{
				holder.name.setTextColor(Color.BLACK);
			}
			String url="http://ve1.zoomifier.net/"+book.client_id+"/"+book.documentId+"/ipad/Page"+Integer.toString(position+1)+"Thb.jpg";
			imageLoader.DisplayImage(url,holder.img);
			/*holder.img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				
						Bundle bundle = new Bundle();
						bundle.putInt("position",position);
						Intent intent = new Intent(activity,PDFImageViewActivity.class);
						intent.putExtras(bundle);
						activity.startActivity(intent);
					}

				
			});*/
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
	public PDFGridData getItem(int position) {
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
				Vector<PDFGridData> list = new Vector<PDFGridData>(originalList);
				results.values = list;
				results.count = list.size();
			} else {
				final Vector<PDFGridData> list = new Vector<PDFGridData>(originalList);
				final Vector<PDFGridData> newList = new Vector<PDFGridData>();
				int count = list.size();
				for (int i = 0; i < count; i++) {
					final PDFGridData friendsList = list.get(i);
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
			filteredList = (Vector<PDFGridData>) results.values;
			clear();
			int count = filteredList.size();
			for (int i = 0; i < count; i++) {
				PDFGridData friendsList = (PDFGridData) filteredList.get(i);
				add(friendsList);
			}
		}

	}
	
}
