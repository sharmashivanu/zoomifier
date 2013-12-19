package ru.truba.touchgallery.TouchView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zoomactivity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

public class Double2xDownlaoder {
	FileCache fileCache;
	//ImageView imageview;
	ExecutorService executorService;
	String firstImageUrl,secondImageUrl;
	 Handler handler=new Handler();
	 Context context;
	public Double2xDownlaoder(Context context)
	{
		
		 fileCache=new FileCache(context);
		 executorService=Executors.newFixedThreadPool(1);
		 this.context=context;	 
	}
	public void setImage(ImageView imageview,String firstImageUrl,String secondImageUrl,String ftu,String stu,ProgressBar progressbar)
	{
		this.firstImageUrl=firstImageUrl;
		this.secondImageUrl=secondImageUrl;
		Bitmap frtThumbBitmap=null;
		Bitmap secondThumbBitamp = null;
		progressbar.setVisibility(View.VISIBLE);
		PhotoToLoad phototoLoad=new PhotoToLoad(firstImageUrl, secondImageUrl, imageview,progressbar);
	 	executorService.submit(new PhotosLoader(phototoLoad));
		
	}
	
	 private Bitmap getBitmap(String url) 
	    {
	        File f=fileCache.getFile(url);
	        //from SD cache
	        Bitmap b = decodeFile(f);
	        if(b!=null)
	            return b;
	        
	        //from web
	        try {
	            Bitmap bitmap=null;
	            URL imageUrl = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
	            conn.setConnectTimeout(30000);
	            conn.setReadTimeout(50000);
	            conn.setInstanceFollowRedirects(true);
	            InputStream is=conn.getInputStream();
	            OutputStream os = new FileOutputStream(f);
	            Utils.CopyStream(is, os);
	            os.close();
	            conn.disconnect();
	            bitmap = decodeFile(f);
	            return bitmap;
	        } catch (Throwable ex){
	           ex.printStackTrace();
	           Log.i("memory error===", ex.toString());
	           
	          // if(ex instanceof OutOfMemoryError)
	             //  memoryCache.clear();
	           return null;
	        }
	    }
	private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=712;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=2;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	 private class PhotoToLoad
	    {
	        public String fu,su;
	        public ImageView imageView;
	        public ProgressBar progressbar;
	        public PhotoToLoad(String fu,String su, ImageView i,ProgressBar prgressbar){
	           this.fu=fu;
	           this.su=su;
	           this.progressbar=prgressbar;
	           imageView=i;
	        }
	    }
	  class PhotosLoader implements Runnable {
	      
		  PhotoToLoad photoToLoad;
	        PhotosLoader(PhotoToLoad photoToLoad){
	            this.photoToLoad=photoToLoad;
	        }
	        @Override
	        public void run() {
	            try{
	            	 Bitmap firstBitmap;
	            	if(photoToLoad.fu.equals(" "))
	            	{
	            	 firstBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.white_bg);
	            	}
	            	else
	            	{
	                  firstBitmap=getBitmap(photoToLoad.fu);
	            	}
	                 Bitmap secondBitmap=getBitmap(photoToLoad.su);
	                 
	                 if(firstBitmap!=null&&secondBitmap!=null)
	                 {
	                	 Bitmap finalBitmap=combineImages(firstBitmap, secondBitmap);
	                	 BitmapDisplayer bd=new BitmapDisplayer(finalBitmap, photoToLoad);
	  	                 handler.post(bd);
	                	
	                	//photoToLoad.imageView.setImageBitmap(finalBitmap);	
	                 	
	                 }
	            	
	              
	            }catch(Throwable th){
	                th.printStackTrace();
	            }
	        }
	    }
	  
	  public Bitmap combineImages(Bitmap c, Bitmap s) {
			Bitmap cs = null;
			int width, height = 0;
			
			if (c.getWidth() > s.getWidth()) {
				width = c.getWidth() + s.getWidth();
				height = c.getHeight();
			}
			else {
				width = s.getWidth() + s.getWidth();
				height = s.getHeight();
				
			}

			cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			
			Canvas comboImage = new Canvas(cs);

			comboImage.drawBitmap(c, 0f, 0f, null);
			comboImage.drawBitmap(s, c.getWidth(), 0f, null);

			return cs;
		}
	  class BitmapDisplayer implements Runnable
	    {
	        Bitmap bitmap;
	        PhotoToLoad photoToLoad;
	        public BitmapDisplayer(Bitmap b, PhotoToLoad p)
	        {bitmap=b;photoToLoad=p;}
	        public void run()
	        {
	        try{
	        	if(bitmap!=null)
	        	{ photoToLoad.imageView.setScaleType(ScaleType.MATRIX);
	        		photoToLoad.imageView.setImageBitmap(bitmap);
	        		photoToLoad.progressbar.setVisibility(View.GONE);
	        	}}
	        	catch (Exception e) {
					e.printStackTrace();
				}
	        			
        }
	    }
	  private Bitmap thumbdecode(File f){
	        try {
	            //decode image size
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            FileInputStream stream1=new FileInputStream(f);
	            BitmapFactory.decodeStream(stream1,null,o);
	            stream1.close();
	            
	            //Find the correct scale value. It should be the power of 2.
	            final int REQUIRED_SIZE=50;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
	            int scale=2;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale*=2;
	            }
	            
	            //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            FileInputStream stream2=new FileInputStream(f);
	            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
	            stream2.close();
	            return bitmap;
	        } catch (FileNotFoundException e) {
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
}
