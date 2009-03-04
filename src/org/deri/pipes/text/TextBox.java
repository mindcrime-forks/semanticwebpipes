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
package org.deri.pipes.text;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.Operator;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.deri.pipes.model.SesameTupleBuffer;
import org.deri.pipes.model.TextBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
@XStreamAlias("text")
public class TextBox implements Operator{
	public static final String SPARQL_FORMAT = "sparqlxml";
	public static final String RDFXML_FORMAT = "rdfxml";
	public static final String TEXTPLAIN_FORMAT = "text/plain";
	final static Logger logger = LoggerFactory.getLogger(TextBox.class);
	private String content=null;
	@XStreamAsAttribute
	private String format= RDFXML_FORMAT;
	/**
	 * Default constructor
	 */
	public TextBox(){
	}
	/**
	 * Construct a content box to use RDFXML content.
	 * @param rdfxml
	 */
	public TextBox(String rdfxml){
		this(rdfxml,RDFXML_FORMAT);
	}

	public TextBox(String text, String format) {
		this.content = text;
		this.format = format;
	}
	
	
	public ExecBuffer execute(Context context) throws Exception{
		ExecBuffer execBuffer = newExecBuffer(format);
		// execBuffer.loadFromText(content); // would be nice?			
		if(execBuffer instanceof SesameMemoryBuffer){
			((SesameMemoryBuffer)execBuffer).loadFromText(content);
		}else if(execBuffer instanceof SesameTupleBuffer){
			((SesameTupleBuffer)execBuffer).loadFromText(content);
		}else if(execBuffer instanceof TextBuffer){
			((TextBuffer)execBuffer).setText(content);
		}else{
		
			throw new RuntimeException("Wrong buffer, expected SesameMemoryBuffer or SesameTupleBuffer not "+execBuffer.getClass());
		}
		return execBuffer;
	}
	
	static ExecBuffer newExecBuffer(String format){
		if(RDFXML_FORMAT.equals(format)){
			return new SesameMemoryBuffer();
		}
		if(SPARQL_FORMAT.equals(format)){
			return new SesameTupleBuffer();
		}
		if(TEXTPLAIN_FORMAT.equals(format)){
			return new TextBuffer();
		}
		logger.warn("unexpected format ["+format+"] using "+RDFXML_FORMAT);
		return new SesameMemoryBuffer();

	}
	public String getContent() {
		return content;
	}

	public void setContent(String text) {
		this.content = text;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
}
