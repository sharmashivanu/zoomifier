package com.zoomactivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;
import android.widget.Toast;
public class ImageDownLoad {
	
	public void dowloadimage(String url,String name){
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	if(!isSDPresent)
	{
		
	}
	else{
		try{
		/*URL u = new URL(url);
		HttpURLConnection c = (HttpURLConnection) u.openConnection();
		c.setRequestMethod("GET");
		//c.setDoOutput(true);
		c.setDoInput(true);
		c.connect();

		
		File file = new File(Environment.getExternalStorageDirectory()+ "/idial/image/");
		if(!file.exists()){
		file.mkdirs();
		}
         int size="http://idialit.com/imageuploads/".length();
         name=name.substring(size);
       
		File outputFile = new File(file,name);
	
		FileOutputStream fos = new FileOutputStream(outputFile);

		InputStream is = c.getInputStream();
    	byte[] buffer = new byte[1024*4];
		int len1 = 0;
		while ((len1 = is.read(buffer)) != -1) {

		fos.write(buffer, 0, len1);
		}
		fos.close();
		is.close();
*/
			URL imageurl = new URL(url);
            URLConnection connection = imageurl.openConnection();
            connection.connect();
            InputStream input = new BufferedInputStream(imageurl.openStream());
            File file = new File(Environment.getExternalStorageDirectory()+ "/idialit/image/");
    		if(!file.exists()){
    		file.mkdirs();
    		}
    		int size="http://idialit.com/imageuploads/".length();
            name=name.substring(size);
            
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/idialit/image/"+name);
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
 
            output.flush();
            output.close();
            input.close();


		} catch (IOException e) {
			
			e.printStackTrace();

		}
	}

		}
	

}
