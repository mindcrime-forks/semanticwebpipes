package org.deri.pipes.utils;

import info.aduna.lang.FileFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlLoader {
	static Logger logger = LoggerFactory.getLogger(UrlLoader.class);
	public static InputStream openConnection(String url, FileFormat format)
	throws IOException, MalformedURLException {
		URL netURL = new URL(url.trim());
		String protocol = netURL.getProtocol().toLowerCase();
		if("http".equals(protocol)|| "https".equals(protocol)){
			return getHttpConnection(url, format, netURL);
		}
		//TODO: allow file protocols in some scenarios?
		throw new IOException("Unsupported protocol: "+protocol+" for fetching url=["+url+"]");
	}
	
	private static InputStream getHttpConnection(String url, FileFormat format,
			URL netURL) throws IOException {
		HttpURLConnection httpURLConnection = (HttpURLConnection)netURL.openConnection();
		httpURLConnection.setRequestProperty("Accept", format.getDefaultMIMEType());
		httpURLConnection.connect();
		int code = httpURLConnection.getResponseCode();
		String message = httpURLConnection.getResponseMessage();
		logger.info("connected to ["+url+"] responseCode=["+code+"] "+message);
		return httpURLConnection.getInputStream();
	}

}
