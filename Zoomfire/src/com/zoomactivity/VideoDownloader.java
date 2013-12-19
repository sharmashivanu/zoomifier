package com.zoomactivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class VideoDownloader {

	public void DownloadFile(String fileURL, String fileName) {
        try {
            String RootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "Zoomifier";
            File RootFile = new File(RootDir);
            RootFile.mkdir();
            // File root = Environment.getExternalStorageDirectory();
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(RootFile,
                    fileName));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;

            while ((len1 = in.read(buffer)) > 0) {                          
                f.write(buffer, 0, len1);               
            }       
            f.close();


        } catch (Exception e) {
        	
        	File file = new File(Environment.getExternalStorageDirectory()+"/zoomifier/"+ fileName );
        	file.delete();
            Log.d("Error....", e.toString());
        }

    }

}
