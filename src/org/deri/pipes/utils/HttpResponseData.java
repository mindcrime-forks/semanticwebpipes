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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.deri.pipes.model.BinaryContentBuffer;

/**
 * @author robful
 *
 */
public class HttpResponseData implements Serializable{
	final static String ENCODING= "ISO-8859-1";
	String body;
	int response;
	long expires;
	String charSet;
	String contentType;
	String lastModified;
	private transient HttpMethodBase method;
	void setBody(byte[] body) {
		try {
			this.body = new String(body,ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	void setResponse(int response) {
		this.response = response;
	}
	void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	void setContentType(String contentType) {
		this.contentType = contentType;
	}
	void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public byte[] getBody() {
		try {
			return body.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public int getResponse() {
		return response;
	}
	public String getCharSet() {
		return charSet;
	}
	public String getContentType() {
		return contentType;
	}
	public String getLastModified() {
		return lastModified;
	}
	long getExpires() {
		return expires;
	}
	void setExpires(long expires) {
		this.expires = expires;
	}
	/**
	 * @return
	 */
	public BinaryContentBuffer toBinaryContentBuffer() throws IOException {
		BinaryContentBuffer buffer = new BinaryContentBuffer();
		buffer.setCharacterEncoding(getCharSet());
		buffer.setContentType(getContentType());
		buffer.setContent(getBody());
		return buffer;
	}
}
