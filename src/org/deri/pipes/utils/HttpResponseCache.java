/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.deri.pipes.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.jcs.JCS;
import org.apache.log4j.Logger;

/**
 * Utility class for retrieving HTTP response from cache. The cache
 * is used by pipes for minimising http traffic. The result of each
 * HTTP GET request is stored in the cache for a minimum of
 * MINIMUM_CACHE_TIME_MILLIS, during which time the response for any other same request
 * will be retrieved from the cache. After MINIMUM_CACHE_TIME_MILLIS a HTTP HEAD
 * request is used to check the Last-Modified header to see if the content changed,
 * if not the cache is updated for another MINIMUM_CACHE_TIME_MILLIS. If the content
 * has changed, it is fetched with HTTP GET and placed into the cache.
 * 
 * TODO: use streams rather than storing the full response in memory.
 * @author robful
 *
 */
public class HttpResponseCache {
	/**
	 * 
	 */
	private static final String HEADER_USER_AGENT = "User-Agent";
	/**
	 * 
	 */
	private static final String HEADER_LAST_MODIFIED = "Last-Modified";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	/**
	 * Minimum time in milliseconds to cache http response.
	 * If zero or less, responses will not be cached.
	 */
	public static long MINIMUM_CACHE_TIME_MILLIS=300000;//5 minutes
	/**
	 * Maximum size of content retrieved.
	 */
	public static int MAX_CONTENT_SIZE = 5000000;//about 5mb max for now
	static Logger logger = Logger.getLogger(HttpResponseCache.class);
	public static HttpResponseData getResponseData(HttpClient client,String location) throws Exception{
		return getResponseData(client,location,(Map)null);
	}
	/**
	 * @param client
	 * @param location
	 * @param location2
	 * @return
	 */
	public static HttpResponseData getResponseData(HttpClient client,
			String location, Map<String, String> requestHeaders) throws Exception{
		synchronized(client){
			if(MINIMUM_CACHE_TIME_MILLIS <=0){
				logger.debug("caching disabled.");
				return getDataFromRequest(client, location,requestHeaders);
			}
			String cacheKey = makeCacheKey(location,requestHeaders);
			if(requestHeaders == null){
				requestHeaders = new HashMap<String,String>();
			}
			if(requestHeaders.get(HEADER_USER_AGENT)==null){
				requestHeaders.put(HEADER_USER_AGENT, getDefaultUserAgent());
			}
			JCS jcs = null;
			try{
				jcs = JCS.getInstance("httpResponseCache");
			}catch(Exception e){
				logger.warn("Problem getting JCS cache"+e,e);
			}
			if(jcs != null){
				try{
					HttpResponseData data = (HttpResponseData)jcs.get(cacheKey);
					if(data != null){
						if(data.getExpires()>System.currentTimeMillis()){
							logger.info("Retrieved from cache (not timed out):" + location);
							return data;
						}
						if(location.length()<2000){
							HeadMethod headMethod = new HeadMethod(location);
							headMethod.setFollowRedirects(true);
							addRequestHeaders(headMethod,requestHeaders);

							try{
								int response = client.executeMethod(headMethod);
								Header lastModifiedHeader = headMethod.getResponseHeader(HEADER_LAST_MODIFIED);
								if(response == data.getResponse()){
									if(lastModifiedHeader == null){
										logger.debug("Not using cache (No last modified header available) for "+location);
									}else if(lastModifiedHeader !=null && data.getLastModified().equals(lastModifiedHeader.getValue())){
										setExpires(data,headMethod);
										jcs.put(cacheKey, data);
										logger.info("Retrieved from cache (used HTTP HEAD request to check "+HEADER_LAST_MODIFIED+") :"+location);
										return data;
									}else{
										logger.debug("Not using cache (last modified changed) for "+location);
									}
								}
							}finally{
								headMethod.releaseConnection();
							}
						}

					}
				}catch(Exception e){
					logger.warn("Problem retrieving from cache for "+location,e);
				}
			}
			HttpResponseData data = getDataFromRequest(client, location, requestHeaders);
			if(jcs != null){
				try{
					jcs.put(cacheKey, data);
					logger.debug("cached "+location);
				}catch(Exception e){
					logger.warn("Could not store response for "+location+" in cache",e);
				}	
			}

			return data;
		}
	}
	/**
	 * @param data
	 * @param headMethod
	 */
	private static void setExpires(HttpResponseData data, HttpMethodBase method) {
		long expires = System.currentTimeMillis()+MINIMUM_CACHE_TIME_MILLIS;
		Header expiresHeader = method.getResponseHeader("Expires");
		if(expiresHeader != null){
			try{
				Date expiresDate = DateUtil.parseDate(expiresHeader.getValue());
				if(expiresDate.getTime() > expires){
					logger.info("Setting cache time according to expiresHeader=["+expiresHeader.getValue()+"]");
					expires = expiresDate.getTime();
				}else{
					logger.debug("Ignoring expires header ["+expiresHeader.getValue()+"]");
				}
			}catch(Exception e){
				logger.debug("Problem parsing expires header ["+expiresHeader.getValue()+"]");
			}
		}
		data.setExpires(expires);

	}
	private static HttpResponseData getDataFromRequest(HttpClient client,
			String location, Map<String, String> requestHeaders)
	throws IOException, HttpException {
		HttpMethodBase method = new GetMethod(location);
		method.setFollowRedirects(true);
		try{
			if(location.length() > 2000 && location.indexOf('?')>=0){
				logger.info("Using post method because request location is very long");
				PostMethod postMethod = new PostMethod(location.substring(0,location.indexOf('?')));
				String urlDecoded = URLDecoder.decode(location.substring(location.indexOf('?')+1),"UTF-8");
				String[] parts = urlDecoded.split("\\&");
				for(String part : parts){
					String[] keyval = part.split("=", 2);
					if(keyval.length == 2){
						postMethod.addParameter(keyval[0], keyval[1]);
					}else{
						postMethod.addParameter(keyval[0], "");
					}
				}
				method = postMethod;
			}
			addRequestHeaders(method,requestHeaders);
			int response = client.executeMethod(method);
			HttpResponseData data = new HttpResponseData();
			setExpires(data,method);
			data.setResponse(response);
			data.setCharSet(method.getResponseCharSet());
			Header lastModifiedHeader = method.getResponseHeader(HEADER_LAST_MODIFIED);
			if(lastModifiedHeader != null){
				data.setLastModified(lastModifiedHeader.getValue());
			}
			Header contentTypeHeader = method.getResponseHeader(HEADER_CONTENT_TYPE);
			if(contentTypeHeader != null){
				data.setContentType(contentTypeHeader.getValue());
			}
			data.setBody(method.getResponseBody(MAX_CONTENT_SIZE));

			return data;
		}finally{
			method.releaseConnection();
		}
	}
	private static String getDefaultUserAgent() {
		//todo: use a system property if set.
		return "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en)";
	}
	/**
	 * @param headMethod
	 * @param requestHeaders
	 */
	private static void addRequestHeaders(HttpMethod method,
			Map<String, String> requestHeaders) {
		if(requestHeaders!=null){
			for(String key : requestHeaders.keySet()){
				method.addRequestHeader(key, requestHeaders.get(key));
			}
		}
	}
	/**
	 * @param location
	 * @param requestHeaders
	 * @return
	 */
	private static String makeCacheKey(String location,
			Map<String, String> requestHeaders) {
		if(requestHeaders == null || requestHeaders.size()==0){
			return getMD5(location);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(location);
		List<String> keys = new ArrayList<String>();
		keys.addAll(requestHeaders.keySet());
		Collections.sort(keys);
		for(String key : keys){
			sb.append('[').append(key).append('=').append(requestHeaders.get(key)).append(']');
		}
		return getMD5(sb.toString());
	}
	/**
	 * @param string
	 * @return
	 */
	static String getMD5(String string) {
		try{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			BigInteger bigInt = new BigInteger(1, digest.digest(string.getBytes("UTF-8")));
			String md5 = bigInt.toString(16);
			if(logger.isDebugEnabled()){
				logger.debug("using md5=["+md5+"] for "+string);
			}
			return md5;
		}catch(Throwable t){
			logger.info("couldn't calculate md5 because:"+t,t);
			return string;
		}
	}
}
