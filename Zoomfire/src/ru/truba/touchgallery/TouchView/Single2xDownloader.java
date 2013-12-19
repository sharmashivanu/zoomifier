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

import ru.truba.touchgallery.TouchView.TouchImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import com.zoomactivity.R;

public class Single2xDownloader {
	FileCache fileCache;
	//ImageView imageview;
	ExecutorService executorService;
	Context context;
	Handler handler=new Handler();

	public Single2xDownloader(Context context)
	{
		  this.context=context;
		  fileCache=new FileCache(context);
		  executorService=Executors.newFixedThreadPool(1);
		  
	}
	public void setImage(TouchImageView imageview,String imageUrl,String thumburl,ProgressBar progressbar,Matrix matrix)
	{
	  progressbar.setVisibility(View.VISIBLE);
		
		PhotoToLoad phototoLoad=new PhotoToLoad(imageUrl, thumburl, imageview,progressbar,matrix);
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
	            conn.setReadTimeout(30000);
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
            int scale=1;
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
	 private class PhotoToLoad
	    {
	        public String fu,tu;
	        public ImageView imageView;
	        public ProgressBar progressbar;
	        public Matrix matrix;
	        public PhotoToLoad(String fu,String tu, ImageView i,ProgressBar progressbar,Matrix matrix){
	           this.fu=fu;
	           this.tu=tu;
	           this.progressbar=progressbar;
	           imageView=i;
	           this.matrix=matrix;
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
	           
	                   /* Bitmap thumbBitmap=getBitmap(photoToLoad.tu);
	                    File f=fileCache.getFile(photoToLoad.fu);
	        	        Bitmap b = decodeFile(f);
	        	        if(b!=null)
	        	        {
	                	  BitmapDisplayer bd=new BitmapDisplayer(b, photoToLoad);
	  	                  handler.post(bd);
	        	        }
	        	        else
	        	        {     File thumbfile=fileCache.getFile(photoToLoad.tu);
	        	               Bitmap thumbbitmap = decodeFile(thumbfile);
	        	               ThumbDisplayer thumd=new ThumbDisplayer(thumbBitmap, photoToLoad);
	        	               handler.post(thumd);*/
	        	        	   Bitmap firstBitmap=getBitmap(photoToLoad.fu);
	        	        	   BitmapDisplayer bd=new BitmapDisplayer(firstBitmap, photoToLoad);
	 	  	                   handler.post(bd);
	        	      // }
	                	//photoToLoad.imageView.setImageBitmap(finalBitmap);	
	                 	
	             
	            	
	              
	            }catch(Throwable th){
	                th.printStackTrace();
	            }
	        }
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
	        		
	        	{   photoToLoad.progressbar.setVisibility(View.GONE);
	        		photoToLoad.imageView.setImageBitmap(bitmap);
	        		photoToLoad.imageView.setScaleType(ScaleType.MATRIX);
	        		photoToLoad.imageView.setImageMatrix(TouchImageView.ma);
	        		int i=0;
	        		
	        	}}
	        	catch (Exception e) {
					e.printStackTrace();
				}
	        			
        }
	    }
	  class ThumbDisplayer implements Runnable
	    {
	        Bitmap bitmap;
	        PhotoToLoad photoToLoad;
	        public ThumbDisplayer(Bitmap b, PhotoToLoad p)
	        {bitmap=b;photoToLoad=p;}
	        public void run()
	        {
	        	//photoToLoad.progressbar.setVisibility(View.VISIBLE);
	        	try{
	        	if(bitmap!=null)
	        	{
	        		photoToLoad.imageView.setImageBitmap(bitmap);
	        	}}
	        	catch (Exception e) {
					e.printStackTrace();
				}
	        			
      }
	    }
	  
	  
	     
}
