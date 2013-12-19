package com.zoomactivity;

import java.util.ArrayList;
import java.util.Vector;

import com.zoomifier.adapter.PageData;
import com.zoomifier.adapter.SearchData;
import com.zoomifier.adapter.SecondActivityData;
import com.zoomifier.database.ZoomifierDatabase;
import com.zoomifier.imagedownload.PDImagedownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ScaneFolderView extends Activity {
	LinearLayout layout;
	Vector<SecondActivityData> grid_data = new Vector<SecondActivityData>();
	EditText searchbox;
	ArrayList<SearchData> searchDataList;
	ArrayList<PageData>   Pagedata;
	ZoomifierDatabase database;
	String readerId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scnae_folder_layout);
		Bundle bundle = getIntent().getExtras();
		readerId=bundle.getString("readerId");
		init();
		database.openAndReadQRTable();
		for (int i = 0; i < ZoomifierDatabase.qrName.size(); i++) {
			grid_data.add(new SecondActivityData("qrcode",
					ZoomifierDatabase.qrName.get(i), ZoomifierDatabase.qrtitle.get(i), " ", " ",
					" ", " ", " ", ZoomifierDatabase.qrLike.get(i),
					" ", " ", " ", ZoomifierDatabase.scanedate.get(i),
					" ", " ", " ", " ", " ", readerId));
		}
		if(grid_data.size()!=0)
		{
			onDrawLayout();
		}

	}
	
public void init()
{
	 database = new ZoomifierDatabase(this);
	 searchbox = (EditText) findViewById(R.id.search_speaker_edit_text);
	 layout=(LinearLayout) findViewById(R.id.pagedivider);
}
public void onDrawLayout()
{

	layout.removeAllViews();
	HorizontalPager realViewSwitcher = (HorizontalPager) findViewById(R.id.horizontal_pager);
	realViewSwitcher.setOnScreenSwitchListener(onScreenSwitchListener);
	realViewSwitcher.removeAllViews();
	int numberof_item = grid_data.size();
	int numberofscreen;
	int extrascreenitem = 0;
	int m = numberof_item % 12;
	int d = numberof_item / 12;
	if (m == 0) {
		numberofscreen = d;
	} else {
		numberofscreen = d + 1;
		extrascreenitem = m;
	}
	Display display = ((WindowManager) getSystemService(this.WINDOW_SERVICE))
			.getDefaultDisplay();

	int orientation = display.getOrientation();
	int count = 0;

	int ot = getResources().getConfiguration().orientation;
	
	if (ot == Configuration.ORIENTATION_PORTRAIT) {

		for (int k = 1; k <= numberofscreen; k++) {
			RelativeLayout mainlayout = new RelativeLayout(this);
			mainlayout.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			// mainlayout.setBackgroundResource(R.drawable.shelf_bg);
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			display = wm.getDefaultDisplay();
			int width = display.getWidth();
			int theight = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
							.getDisplayMetrics());
			int height = display.getHeight();
			int rowheight;
			if (height > 800)
				rowheight = (height - theight - 130) / 4;
			else
				rowheight = (height - theight - 80) / 4;
			int colunsize = width / 3;
			int selfheight = rowheight / 4 + 30;
			int itemheight = rowheight / 2;

			for (int i = 1; i <= 4; i++) {
				RelativeLayout layout = new RelativeLayout(this);
				RelativeLayout.LayoutParams parma = new RelativeLayout.LayoutParams(
						width, selfheight);

				parma.setMargins(0, rowheight * i - selfheight, 0, 0);
				layout.setLayoutParams(parma);
				if (i == 1) {
					layout.setBackgroundResource(R.drawable.shelf1);
				} else if (i == 2) {
					layout.setBackgroundResource(R.drawable.shelf2);
				} else if (i == 3) {
					layout.setBackgroundResource(R.drawable.shelf3);
				} else if (i == 4) {
					layout.setBackgroundResource(R.drawable.shelf4);
				}
				mainlayout.addView(layout);

			}

			int itemnumber = 9;
			if (k == numberofscreen) {
				if (extrascreenitem != 0) {
					itemnumber = extrascreenitem;
				} else {
					itemnumber = 12;
				}
			} else {
				itemnumber = 12;
			}
			int moduls = itemnumber % 3;
			int divisivle = itemnumber / 3;
			int numberofrow;
			int exteritem;
			if (moduls == 0) {
				numberofrow = divisivle;
				exteritem = 0;

			} else {
				numberofrow = divisivle + 1;
				exteritem = moduls;
			}
			int numberofitem = 3;
			for (int i = 1; i <= numberofrow; i++) {
				if (i == numberofrow) {
					if (exteritem != 0) {
						numberofitem = exteritem;
					}
				}

				for (int l = 0; l < numberofitem; l++) {

					ImageView imageview = new ImageView(this);
					RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
							colunsize, itemheight);

					if (height <= 800)
						parmas.setMargins(colunsize * l + 20, rowheight * i
								- (selfheight + itemheight - 15), 0, -20);
					else
						parmas.setMargins(colunsize * l + 20, rowheight * i
								- (selfheight + itemheight - 25), 0, -20); 
					imageview.setLayoutParams(parmas);
					imageview.setPadding(10, 0, 20, 0);

					String url = grid_data.get(count).documentName;
					PDImagedownloader imagedownlaoder = new PDImagedownloader(
							ScaneFolderView.this);
					imageview.setId(count);
					imageview
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									int id=arg0.getId();
									Bundle bundle = new Bundle();
									bundle.putString("url", grid_data.get(id).documentName);
									Intent intent = new Intent(ScaneFolderView.this,
											BarCodeWebViewActivity.class);
									intent.putExtras(bundle);
									startActivity(intent);
								}
							});
					imagedownlaoder.DisplayImage(url, imageview);
					mainlayout.addView(imageview);
					if (grid_data.get(count).documentContentType
							.equals("VIDEO")) {
						ImageView playimagview = new ImageView(this);
						RelativeLayout.LayoutParams playparmaeter = new RelativeLayout.LayoutParams(
								colunsize / 3, itemheight / 3);
						playparmaeter.setMargins(colunsize * l + colunsize
								/ 3, rowheight * i
								- (selfheight + itemheight / 2 - 15), 0, 0);
						playimagview.setLayoutParams(playparmaeter);
					
						Bitmap frtThumbBitmap = BitmapFactory
								.decodeResource(
										ScaneFolderView.this.getResources(),
										R.drawable.play_button);
						// playimagview.setPadding(colunsize-20,
						// itemheight-20, colunsize-20, itemheight-20);
						playimagview.setId(count);
						playimagview.setImageBitmap(frtThumbBitmap);
						mainlayout.addView(playimagview);

					}
					TextView textview = new TextView(this);
					parmas = new RelativeLayout.LayoutParams(colunsize,
							selfheight);
					parmas.setMargins(colunsize * l + 4, rowheight * i
							- selfheight, 0, 0);
					textview.setSingleLine();
					textview.setText(grid_data.get(count).documentName);
					textview.setEllipsize(TextUtils.TruncateAt.END);
					textview.setTextColor(Color.BLACK);
					textview.setLayoutParams(parmas);
					textview.setGravity(Gravity.CENTER);
					mainlayout.addView(textview);
					count++;

				}
			}
			
			
			realViewSwitcher.addView(mainlayout);
			LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
					10, 10);
				ImageView  pagedivider=new ImageView(ScaneFolderView.this);
			pagedivider.setLayoutParams(parma);
			if(k==1)
			pagedivider.setImageResource(R.drawable.blue_dot);
			else
				pagedivider.setImageResource(R.drawable.gray_dot);
		    layout.addView(pagedivider);
			
		}
	} else {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		int width = display.getWidth();	
		int theight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
						.getDisplayMetrics());
	   int  height=display.getHeight();
	   int rowheight;
	   if(height>=720)
			 rowheight = (height - theight -130) / 3;
	   else
		   rowheight = (height - theight -80) / 3;
	
		int colunsize = width / 4;
		int selfheight = rowheight / 4 + 30;
		int itemheight = rowheight / 2;

		for (int k = 1; k <= numberofscreen; k++) {
			RelativeLayout mainlayout = new RelativeLayout(this);
			mainlayout.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mainlayout.setBackgroundResource(R.drawable.shelf_bg);
		
			

			for (int i = 1; i <= 3; i++) {
				RelativeLayout layout = new RelativeLayout(this);
				RelativeLayout.LayoutParams parma = new RelativeLayout.LayoutParams(
						width, selfheight);

				parma.setMargins(0, rowheight * i - selfheight, 0, 0);
				layout.setLayoutParams(parma);
				if (i == 1) {
					layout.setBackgroundResource(R.drawable.shelf1);
				} else if (i == 2) {
					layout.setBackgroundResource(R.drawable.shelf2);
				} else if (i == 3) {
					layout.setBackgroundResource(R.drawable.shelf3);
				} else if (i == 4) {
					layout.setBackgroundResource(R.drawable.shelf4);
				}
				mainlayout.addView(layout);

			}

			int itemnumber = 9;
			if (k == numberofscreen) {
				if (extrascreenitem != 0) {
					itemnumber = extrascreenitem;
				} else {
					itemnumber = 12;
				}
			} else {
				itemnumber = 12;
			}
			int moduls = itemnumber % 4;
			int divisivle = itemnumber / 4;
			int numberofrow;
			int exteritem;
			if (moduls == 0) {
				numberofrow = divisivle;
				exteritem = 0;

			} else {
				numberofrow = divisivle + 1;
				exteritem = moduls;
			}
			int numberofitem = 4;
			for (int i = 1; i <= numberofrow; i++) {
				if (i == numberofrow) {
					if (exteritem != 0) {
						numberofitem = exteritem;
					}
				}
				for (int l = 0; l < numberofitem; l++) {

					ImageView imageview = new ImageView(this);
					RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(
							colunsize, itemheight);

					parmas.setMargins(colunsize * l + 15, rowheight * i
							- (selfheight + itemheight - 15), 10, 0);
					imageview.setLayoutParams(parmas);
					String url = grid_data.get(count).documentName;
					
					PDImagedownloader imagedownlaoder = new PDImagedownloader(
							ScaneFolderView.this);
					imagedownlaoder.DisplayImage(url, imageview);
					imageview.setId(count);
					imageview
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {

									int id = arg0.getId();

									if (grid_data.get(id).documentContentType
											.equals("DOCUMENT")) {
										Bundle bundle = new Bundle();
										bundle.putString(
												"documentId",
												grid_data.get(id).documentId);
										bundle.putString(
												"documentName",
												grid_data.get(id).documentName);
										bundle.putString(
												"documentDiscription",
												grid_data.get(id).documentDiscription);
										bundle.putString(
												"originalId",
												grid_data.get(id).originalId);
										bundle.putString(
												"documentContentType",
												grid_data.get(id).documentContentType);
										bundle.putString(
												"documentContentWidth",
												grid_data.get(id).documentContentWidth);
										bundle.putString(
												"documentContentHeight",
												grid_data.get(id).documentContentHeight);
										bundle.putString(
												"documentOwnerId",
												grid_data.get(id).documentOwnerId);
										bundle.putInt("position", 0);
										bundle.putString("clientid",
												grid_data.get(id).client_id);
										bundle.putString("userid",
												grid_data.get(id).userID);
										bundle.putString(
												"favorit",
												grid_data.get(id).like_document);
										bundle.putString(
												"sharedby",
												grid_data.get(id).SharedName);
										bundle.putString(
												"shareddate",
												grid_data.get(id).SharedDate);

										Intent intent = new Intent(
												ScaneFolderView.this,
												PDFImageViewActivity.class);
										intent.putExtras(bundle);
										ArrayList<String> arraylist = new ArrayList<String>();
										PageData pageData = new PageData(
												arraylist);
										intent.putExtra("pagedata",
												pageData);
										startActivity(intent);
									} else if (grid_data.get(id).documentContentType
											.equals("VIDEO")) {
										Bundle bundle = new Bundle();
										bundle.putString(
												"documentId",
												grid_data.get(id).documentId);
										bundle.putString(
												"documentName",
												grid_data.get(id).documentName);
										bundle.putString(
												"documentDiscription",
												grid_data.get(id).documentDiscription);
										bundle.putString(
												"originalId",
												grid_data.get(id).originalId);
										bundle.putString(
												"documentContentType",
												grid_data.get(id).documentContentType);
										bundle.putString(
												"documentContentWidth",
												grid_data.get(id).documentContentWidth);
										bundle.putString(
												"documentContentHeight",
												grid_data.get(id).documentContentHeight);
										bundle.putString(
												"documentOwnerId",
												grid_data.get(id).documentOwnerId);
										bundle.putInt("position", id);
										bundle.putString("clientid",
												grid_data.get(id).client_id);
										bundle.putString("userid",
												grid_data.get(id).userID);
										bundle.putString(
												"favorit",
												grid_data.get(id).like_document);
										bundle.putString(
												"sharedby",
												grid_data.get(id).SharedName);
										bundle.putString(
												"shareddate",
												grid_data.get(id).SharedDate);
										Intent intent = new Intent(
												ScaneFolderView.this,
												ZoomifierVideoPlayer.class);
										intent.putExtras(bundle);
										ArrayList<String> arraylist = new ArrayList<String>();
										PageData pageData = new PageData(
												arraylist);
										intent.putExtra("pagedata",
												pageData);
										startActivity(intent);
									} else {
										Bundle bundle = new Bundle();
										bundle.putString(
												"documentId",
												grid_data.get(id).documentId);
										bundle.putString(
												"documentName",
												grid_data.get(id).documentName);
										bundle.putString(
												"documentDiscription",
												grid_data.get(id).documentDiscription);
										bundle.putString(
												"originalId",
												grid_data.get(id).originalId);
										bundle.putString(
												"documentContentType",
												grid_data.get(id).documentContentType);
										bundle.putString(
												"documentContentWidth",
												grid_data.get(id).documentContentWidth);
										bundle.putString(
												"documentContentHeight",
												grid_data.get(id).documentContentHeight);
										bundle.putString(
												"documentOwnerId",
												grid_data.get(id).documentOwnerId);
										bundle.putInt("position", id);
										bundle.putString("clientid",
												grid_data.get(id).client_id);
										bundle.putString(
												"favorit",
												grid_data.get(id).like_document);
										bundle.putString("userid",
												grid_data.get(id).userID);
										bundle.putString(
												"sharedby",
												grid_data.get(id).SharedName);
										bundle.putString(
												"shareddate",
												grid_data.get(id).SharedDate);
										Intent intent = new Intent(
												ScaneFolderView.this,
												ZoomifierView.class);
										intent.putExtras(bundle);
										ArrayList<String> arraylist = new ArrayList<String>();
										PageData pageData = new PageData(
												arraylist);
										intent.putExtra("pagedata",
												pageData);
										startActivity(intent);
									}

								}
							});
					mainlayout.addView(imageview);
					if (grid_data.get(count).documentContentType
							.equals("VIDEO")) {

						ImageView playimagview = new ImageView(this);
						RelativeLayout.LayoutParams playparmaeter = new RelativeLayout.LayoutParams(
								colunsize / 3, itemheight / 3);
						playparmaeter
								.setMargins(colunsize * l + colunsize / 2
										- 20,
										rowheight
												* i
												- (selfheight + itemheight
														/ 2 - 15), 10, 0);
						
						playimagview.setLayoutParams(playparmaeter);
					
						Bitmap frtThumbBitmap = BitmapFactory
								.decodeResource(
										ScaneFolderView.this.getResources(),
										R.drawable.play_button);
						// playimagview.setPadding(colunsize-20,
						// itemheight-20, colunsize-20, itemheight-20);
						playimagview.setId(count);
						playimagview.setImageBitmap(frtThumbBitmap);
						playimagview.setId(count);
						mainlayout.addView(playimagview);

					}
					TextView textview = new TextView(this);
					parmas = new RelativeLayout.LayoutParams(colunsize,
							selfheight);
					parmas.setMargins(colunsize * l + 4, rowheight * i
							- selfheight, 0, 0);
					textview.setSingleLine();
					textview.setPadding(10, 0, 10, 0);
					textview.setEllipsize(TextUtils.TruncateAt.END);
					textview.setText(grid_data.get(count).documentName);
					textview.setTextColor(Color.BLACK);
					textview.setLayoutParams(parmas);
					textview.setGravity(Gravity.CENTER);
					mainlayout.addView(textview);
					count++;
				}
			}
			realViewSwitcher.addView(mainlayout);
			LinearLayout.LayoutParams parma = new LinearLayout.LayoutParams(
					10, 10);
				ImageView  pagedivider=new ImageView(ScaneFolderView.this);
			pagedivider.setLayoutParams(parma);
			if(k==1)
			pagedivider.setImageResource(R.drawable.blue_dot);
			else
				pagedivider.setImageResource(R.drawable.gray_dot);
		    layout.addView(pagedivider);
		}

	}
	

}
private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener =
new HorizontalPager.OnScreenSwitchListener() {
    @Override
    public void onScreenSwitched(final int screen) {
    	for(int i=0;i<layout.getChildCount();i++)
    	{
    		ImageView imageView=(ImageView) layout.getChildAt(i);
    		imageView.setImageResource(R.drawable.gray_dot);
    	}
    	  ImageView imageView=(ImageView) layout.getChildAt(screen);
    	  imageView.setImageResource(R.drawable.blue_dot);
    	
    }
};
public void displayAlert(String msg, String title) {
	new AlertDialog.Builder(this)
			.setMessage(msg)
			.setTitle(title)
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
