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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.deri.pipes.core.ExecBuffer;

/**
 * A buffer containing some text.
 * @author robful
 *
 */
public class TextBuffer implements ExecBuffer{

	private String text;

	/**
	 * Create an emtpy text buffer
	 */
	public TextBuffer() {
		text = "";
	}
	/**
	 * Create a text buffer containing this text.
	 * @param text
	 */
	public TextBuffer(String text){
		this.text = text;
	}
	/**
	 * set the text in the buffer.
	 * @param text
	 */
	public void setText(String text){
		this.text = text;
	}
	/**
	 * @param execute
	 */
	public TextBuffer(ExecBuffer buff) throws IOException{
		if(buff instanceof TextBuffer){
			this.text = ((TextBuffer)buff).text;
		}else{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			buff.stream(out);
			String encoding = "UTF-8";
			if(buff instanceof BinaryContentBuffer){
				encoding = ((BinaryContentBuffer)buff).getCharacterEncoding();
			}
			text = new String(out.toByteArray(),encoding);
		}
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(org.deri.pipes.core.ExecBuffer)
	 */
	@Override
	public void stream(ExecBuffer outputBuffer) throws IOException {
		toBinaryContentBuffer().stream(outputBuffer);
	}
	private BinaryContentBuffer toBinaryContentBuffer() throws IOException {
		BinaryContentBuffer x = new BinaryContentBuffer();
		x.setContent(getBytes());
		return x;
	}

	/**
	 * @return
	 */
	private byte[] getBytes() {
		return text.getBytes();
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(org.deri.pipes.core.ExecBuffer, java.lang.String)
	 */
	@Override
	public void stream(ExecBuffer outputBuffer, String context)
			throws IOException {
		toBinaryContentBuffer().stream(outputBuffer,context);
	}

	/**
	 * Writes bytes to the output stream using the standard encoding.
	 */
	@Override
	public void stream(OutputStream output) throws IOException {
		output.write(getBytes());
	}
	/**
	 * Returns the text.
	 */
	public String toString(){
		return text;
	}
	/**
	 * Gets the text encoded in UTF-8.
	 */
	public InputStream getInputStream() throws IOException{
		return new ByteArrayInputStream(text.getBytes("UTF-8"));
	}

}
