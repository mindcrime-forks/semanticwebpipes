package org.deri.execeng.core;

import info.aduna.lang.FileFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */

public abstract class ExecBuffer {
	 public static StringBuffer log= new StringBuffer();
     public abstract void stream(ExecBuffer outputBuffer);
     public abstract void stream(ExecBuffer outputBuffer,String context);
     public abstract void stream(java.io.OutputStream output);
     protected InputStream openConnection(String url, FileFormat format)
     throws IOException, MalformedURLException {
    	 URL netURL = new URL(url.trim());
		URLConnection urlConnection = netURL.openConnection();
		if(urlConnection instanceof HttpURLConnection){
    	 HttpURLConnection httpURLConnection=(HttpURLConnection)urlConnection;
    	 httpURLConnection.setRequestProperty("Accept", format.getDefaultMIMEType());
		}
    	 urlConnection.connect();
    	 return urlConnection.getInputStream();
     }


}
