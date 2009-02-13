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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
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
	public static long MINIMUM_CACHE_TIME_MILLIS=60000;//60 seconds
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
		GetMethod getMethod = new GetMethod(location);
		if(MINIMUM_CACHE_TIME_MILLIS <=0){
			logger.debug("caching disabled.");
			return getDataFromRequest(client, getMethod,requestHeaders);
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
				long checkTimeMillis = System.currentTimeMillis();
				if(data != null){
					if((data.getLastVerified()+MINIMUM_CACHE_TIME_MILLIS)>checkTimeMillis){
						logger.info("Retrieved from cache (not timed out):" + location);
						return data;
					}
					HeadMethod headMethod = new HeadMethod(location);
					addRequestHeaders(headMethod,requestHeaders);
				
					int response = client.executeMethod(headMethod);
					Header lastModifiedHeader = headMethod.getResponseHeader(HEADER_LAST_MODIFIED);
					if(response == data.getResponse()){
						if(lastModifiedHeader == null){
							logger.debug("Not using cache (No last modified header available) for "+location);
						}else if(lastModifiedHeader !=null && data.getLastModified().equals(lastModifiedHeader.getValue())){
							data.setLastVerified(checkTimeMillis);
							jcs.put(cacheKey, data);
							logger.info("Retrieved from cache (used HTTP HEAD request to check "+HEADER_LAST_MODIFIED+") :"+location);
							return data;
						}else{
							logger.debug("Not using cache (last modified changed) for "+location);
						}
					}
				}
			}catch(Exception e){
				logger.warn("Problem retrieving from cache for "+location,e);
			}
		}
		HttpResponseData data = getDataFromRequest(client, getMethod,
				requestHeaders);
		Header lastModifiedHeader = getMethod.getResponseHeader(HEADER_LAST_MODIFIED);
		if(lastModifiedHeader != null){
			data.setLastModified(lastModifiedHeader.getValue());
		}
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
	private static HttpResponseData getDataFromRequest(HttpClient client,
			GetMethod getMethod, Map<String, String> requestHeaders)
			throws IOException, HttpException {
		addRequestHeaders(getMethod,requestHeaders);
		int response = client.executeMethod(getMethod);
		HttpResponseData data = new HttpResponseData();
		data.setLastVerified(System.currentTimeMillis());
		data.setResponse(response);
		data.setCharSet(getMethod.getResponseCharSet());
		Header contentTypeHeader = getMethod.getResponseHeader(HEADER_CONTENT_TYPE);
		if(contentTypeHeader != null){
			data.setContentType(contentTypeHeader.getValue());
		}
		data.setBody(getMethod.getResponseBody(MAX_CONTENT_SIZE));
		return data;
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
			return location;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(location);
		List<String> keys = new ArrayList<String>();
		keys.addAll(requestHeaders.keySet());
		Collections.sort(keys);
		for(String key : keys){
			sb.append('[').append(key).append('=').append(requestHeaders.get(key)).append(']');
		}
		return sb.toString();
	}
}
