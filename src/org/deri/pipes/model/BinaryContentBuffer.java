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

package org.deri.pipes.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.deri.pipes.core.ExecBuffer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author robful
 *
 */
public class BinaryContentBuffer implements ExecBuffer, InputStreamProvider {
	Logger logger = LoggerFactory.getLogger(BinaryContentBuffer.class);
	ByteArrayOutputStream content = new ByteArrayOutputStream();
	String contentType = "application/octet-stream";
	private String charset = "UTF-8";
	
	public BinaryContentBuffer(){
	}
	public BinaryContentBuffer(ByteArrayOutputStream content){
		this.content = content;
	}
	public BinaryContentBuffer(ExecBuffer in) throws IOException{
		content = new ByteArrayOutputStream();
		in.stream(content);
	}
	public byte[] getContent() {
		return content.toByteArray();
	}

	/**
	 * Set the data content
	 * @param content
	 * @throws IOException if the content cannot be stored.
	 */
	public void setContent(byte[] content) throws IOException{
		this.content = new ByteArrayOutputStream();
		this.content.write(content);
	}
	
	/**
	 * Set the binary content as UTF-8;
	 * @param content
	 * @throws IOException if the content cannot be written.
	 */
	public void setContent(String content) throws IOException{
		try {
			this.setContent(content,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 support must be provided by the JVM");
		}
	}

	/**
	 * @param content the content as a string
	 * @param charset   character set for encoding.
	 * @throws IOException if the content cannot be written. 
	 */
	public void setContent(String content, String charset) throws IOException {
		this.setContent((content==null?"":content).getBytes(charset));
		this.setCharacterEncoding(charset);
		
	}
	/**
	 * Get the content as a String
	 * @return the content as a String
	 */
	public String getContentAsString(){
		try {
			return content.toString(charset);
		} catch (UnsupportedEncodingException e) {
			logger.warn("The charset ["+charset+"] is not supported - was the charset changed - will use UTF-8?");
			try {
				return content.toString("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException("UTF-8 support must be provided by the JVM");
			}
		}
	}
	/**
	 * Get an InputStream to the content.
	 * @return
	 */
	public InputStream getInputStream(){
		return new ByteArrayInputStream(content.toByteArray());
	}
	
	public BufferedReader getBufferedReader() {
		try {
			return new BufferedReader(new InputStreamReader(getInputStream(),charset));
		} catch (UnsupportedEncodingException e) {
			logger.warn("The charset ["+charset+"] is not supported - was the charset changed - will use UTF-8?");
			return new BufferedReader(new InputStreamReader(getInputStream()));
		}
	}

	/**
	 * @param charset
	 */
	public void setCharacterEncoding(String charset) {
		this.charset = charset;
	}
	
	public String getCharacterEncoding(){
		return charset;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(org.deri.pipes.core.ExecBuffer)
	 */
	@Override
	public void stream(ExecBuffer outputBuffer) throws IOException{
		//TODO: how to do this without knowledge of the other implementations?
		if(outputBuffer instanceof BinaryContentBuffer){
			BinaryContentBuffer bcb = (BinaryContentBuffer)outputBuffer;
			bcb.setContentType(this.getContentType()); // is this right to do?
			bcb.setCharacterEncoding(this.getCharacterEncoding());
			bcb.content.write(content.toByteArray());
		}else if(outputBuffer instanceof XMLStreamBuffer){
			((XMLStreamBuffer)outputBuffer).setStreamSource(content.toByteArray());
		}
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(org.deri.pipes.core.ExecBuffer, java.lang.String)
	 */
	@Override
	public void stream(ExecBuffer outputBuffer, String context) throws IOException{
		if(context != null){
			logger.warn("Will lose context ["+context+"] on appending stream");
		}
		stream(outputBuffer);
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(java.io.OutputStream)
	 */
	@Override
	public void stream(OutputStream out) throws IOException {
		out.write(content.toByteArray());
		
	}
	
	/**
	 * Displays the contents of the buffer in String format.
	 */
	public String toString(){
		return getContentAsString();
	}

}
