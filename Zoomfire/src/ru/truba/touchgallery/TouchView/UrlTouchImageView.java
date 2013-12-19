/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ru.truba.touchgallery.TouchView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout; 
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.imagemanagement.BitmapDownloader;
import com.imagemanagement.SingleImageBitmapdownloader;
import com.zoomifier.imagedownload.PDImagedownloader;
import com.zoomactivity.R;
import ru.truba.touchgallery.TouchView.InputStreamWrapper.InputStreamProgressListener;

public class UrlTouchImageView extends RelativeLayout {
	protected ProgressBar mProgressBar;
	protected ImageView progressImageView;
	protected TouchImageView mImageView;
	protected TouchImageView mImageView1;
	protected ImageView circule_imageview;
	
	ProgressBar progressBar;
	protected Context mContext;
	
	String orientation;

	public UrlTouchImageView(Context ctx,String orientation) {
		 super(ctx);
		 mContext = ctx;
	 	 this.orientation=orientation;
		 init();
		

	}

	public UrlTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	@SuppressWarnings("deprecation")
	protected void init() {
 /* if(orientation.equals("portriate"))
      {*/
		mImageView = new TouchImageView(mContext);
		progressBar = new ProgressBar(mContext);
		 
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		
		LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);
		mImageView.setLayoutParams(params);
		mImageView.setScaleType(ScaleType.MATRIX);
		LayoutParams paramss = new LayoutParams(40,
				40);
		paramss.addRule(RelativeLayout.CENTER_IN_PARENT);

		progressBar.setLayoutParams(paramss);

		this.addView(mImageView);
		this.addView(progressBar);
		mImageView.setVisibility(GONE);
		mProgressBar = new ProgressBar(mContext, null,
				android.R.attr.progressBarStyleHorizontal);
		params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(30, 0, 30, 0);
		mProgressBar.setLayoutParams(params);
		mProgressBar.setIndeterminate(false);
		mProgressBar.setMax(100);
		//this.addView(mProgressBar);
		mProgressBar.setVisibility(View.GONE);
		// circule_imageview.setVisibility(View.GONE);
		
 
	}

	public void setUrl(String imageUrl) {
		new ImageLoadTask().execute(imageUrl);
	}

	// No caching load
	public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {
		String url;

		@Override
		protected Bitmap doInBackground(String... strings) {
			url = strings[0];
			Bitmap bm = null;

			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
        if(orientation.equals("landscape"))
           {
  			mImageView.setScaleType(ScaleType.MATRIX);
  			
  			String firstUrl = null;
  			String secondUrl = null;
  			int firstpostion=url.indexOf(",");
  			firstUrl=url.substring(0, firstpostion);
  			secondUrl=url.substring(firstpostion+1, url.length());
  			mImageView.setVisibility(VISIBLE);
  			mProgressBar.setVisibility(GONE);
  			String firstthumb = null;
  			String secondthumb=null;
  			String first2x = null;
  			String second2x = null;
  			try {
  				if(firstUrl.equals(" "))
  				{
  					firstthumb=" ";
  					first2x=" ";
  				}
  				else
  				{
  				int i = firstUrl.lastIndexOf("e");
  				int l = firstUrl.lastIndexOf("-");
  				String subString = firstUrl.substring(i + 1, l);
  				firstthumb = firstUrl.substring(0, i + 1) + Integer.parseInt(subString)
  						+ "Thb.jpg";
  				int postion=firstUrl.indexOf("-");
   				String subString2=url.substring(0,postion);
   				
   				first2x=subString2+"-2x.jpg";
  				
  				}
  				if(secondUrl.equals(" "))
  				{
  					secondthumb=" ";
  					second2x=" ";
  				}
  				else
  				{
  					int i = secondUrl.lastIndexOf("e");
  	  		        int l = secondUrl.lastIndexOf("-");
  	  			    String subString = secondUrl.substring(i + 1, l);
  	  			    secondthumb = secondUrl.substring(0, i + 1) + Integer.parseInt(subString)
  	  						+ "Thb.jpg";
  	  			int postion=secondUrl.indexOf("-");
   				String subString2=secondUrl.substring(0,postion);
   				
   				second2x=subString2+"-2x.jpg";
  				}
  			} catch (Exception e) {

  			}
  			boolean isImageDownload=false;
   		    mImageView.setUrl(first2x,second2x, progressBar,mImageView,isImageDownload);
  			BitmapDownloader bitmapDownloader=new BitmapDownloader(mContext);
  			bitmapDownloader.setImage(mImageView, firstUrl, secondUrl, firstthumb, secondthumb, progressBar);
  			}
           else
           {   
   		   
   			mImageView.setScaleType(ScaleType.MATRIX);
   			String thumb = null;
   			try {
   				int i = url.lastIndexOf("e");
   				int l = url.lastIndexOf("-");
   				String subString = url.substring(i + 1, l);
   				thumb = url.substring(0, i + 1) + Integer.parseInt(subString)
   						+ "Thb.jpg";
   			} catch (Exception e) {

   			}
   			mImageView.setVisibility(VISIBLE);
   			mProgressBar.setVisibility(GONE);
   			String Doubliexurl = null;
   			SingleImageBitmapdownloader imagedownloader=new SingleImageBitmapdownloader(mContext);
   			try{
   				int postion=url.indexOf("-");
   				String subString=url.substring(0,postion);
   				
   			 Doubliexurl=subString+"-2x.jpg";
   			}
   			catch(Exception e)
   			{
   				
   			}
   			
   			boolean isImageDownload=false;
   		    mImageView.setUrl(Doubliexurl,"none", progressBar,mImageView,isImageDownload);
   			imagedownloader.setImage(mImageView,url, thumb, progressBar,isImageDownload);
   			
   			
              }
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mProgressBar.setProgress(values[0]);
		}
	}
}
