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

package org.deri.pipes.rdf;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.StringOrSource;
import org.deri.pipes.model.BinaryContentBuffer;
import org.deri.pipes.utils.HttpResponseCache;
import org.deri.pipes.utils.HttpResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Performs a Http Get into a binary content buffer using the
 * HttpClient provided by the Context.
 * @author robful
 *
 */
@XStreamAlias("http-get")
public class HttpGetBox implements Operator{
	transient Logger logger = LoggerFactory.getLogger(HttpGetBox.class);
	StringOrSource location;
	@XStreamAsAttribute
	boolean resolveHtmlLinks = false;
	/**
	 * HTTP ACCEPT header (optional)
	 */
	@XStreamAsAttribute
	String acceptContentType;

	public void setLocation(String location) {
		this.location = new StringOrSource(location);
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.Operator#execute(org.deri.pipes.core.Context)
	 */
	@Override
	public ExecBuffer execute(Context context) throws Exception {
		HttpClient client = context.getHttpClient();
		Map<String,String> headers = new HashMap<String,String>();
		if(acceptContentType != null && acceptContentType.trim().length()>0){
			headers.put("Accept",acceptContentType.trim());
		}
		String url = location.expand(context);
		HttpResponseData data = HttpResponseCache.getResponseData(client, url);
		if(data.getResponse() != 200){
			logger.warn("The http get request to ["+location+"] response code was  ["+data.getResponse()+"]");
		}
		
		BinaryContentBuffer buffer = new BinaryContentBuffer();
		buffer.setContent(data.getBody());
		buffer.setCharacterEncoding(data.getCharSet());
		if(data.getContentType() != null){
			buffer.setContentType(data.getContentType());
		}
		if(resolveHtmlLinks && buffer.getContentType().toLowerCase().indexOf("html")>=0){
			try{
				return LinkResolver.rewriteUrls(buffer, url);
			}catch(Exception e){
				logger.warn("Could not rewrite URLs",e);
			}
		}
		return buffer;
	}
	

}
