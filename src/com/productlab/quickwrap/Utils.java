package com.productlab.quickwrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
 
public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public static File saveURLToExternalStorage(String u) {
    	URL url;
		try {
			url = new URL(u);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
    	InputStream input = null;
    	String basename = new File(url.getFile()).getName();
    	try {
	    	input = url.openStream();
    	    //The sdcard directory e.g. '/sdcard' can be used directly, or 
    	    //more safely abstracted with getExternalStorageDirectory()
    	    File storagePath = Environment.getExternalStorageDirectory();
    	    File outFile = new File(storagePath, basename);
    	    OutputStream output = new FileOutputStream(outFile);
	        byte[] buffer = new byte[1024];
	        int bytesRead = 0;
	        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
	            output.write(buffer, 0, bytesRead);
	        }
    	    input.close();
    	    output.close();
    	    return outFile;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	} 
    }
    
    public static Bitmap saveURLToBitmap(String u) {
    	try {
	    	URL url = new URL(u);
	    	Bitmap image = BitmapFactory.decodeStream(url.openStream());
	    	return image;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    }
    
    public static String saveURLToGallery(Context context, String url) {
    	Bitmap img = saveURLToBitmap(url);
    	if (img == null) return null;
    	
    	try {
			String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), img, "Spotflash Download", "Spotflash Download");
			return path;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
}