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

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.PipeContext;
import org.deri.pipes.model.Operator;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.deri.pipes.model.SesameTupleBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
public class TextBox implements Operator{
	private static final String SPARQL_FORMAT = "sparqlxml";
	private static final String RDFXML_FORMAT = "rdfxml";
	final static Logger logger = LoggerFactory.getLogger(TextBox.class);
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String text=null;

	private int format=0;
	public static final int RDFXML=0;
	public static final int SPARQLXML=1;	

	public ExecBuffer getExecBuffer(){
		return buffer;
	}
	
	public void stream(ExecBuffer outputBuffer){
   	   buffer.stream(outputBuffer);
    }
	
	public void stream(ExecBuffer outputBuffer,String context){
	   	   buffer.stream(outputBuffer,context);
	}
	
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
	
	public void execute(PipeContext context){
		ExecBuffer execBuffer = newExecBuffer(format);
		// execBuffer.loadFromText(text); // would be nice?			
		if(execBuffer instanceof SesameMemoryBuffer){
			((SesameMemoryBuffer)execBuffer).loadFromText(text);
		}else if(execBuffer instanceof SesameTupleBuffer){
			((SesameTupleBuffer)execBuffer).loadFromText(text);
		}else{
			throw new RuntimeException("Wrong buffer, expected SesameMemoryBuffer or SesameTupleBuffer not "+execBuffer.getClass());
		}
		buffer=execBuffer;
		isExecuted=true;
	}
	
	static ExecBuffer newExecBuffer(int format){
		switch (format){
		case RDFXML:
			return new SesameMemoryBuffer();
		case SPARQLXML:
			return new SesameTupleBuffer();
		default:
			logger.warn("unexpected format ["+format+"] using rdfxml");
			return new SesameMemoryBuffer();
		}

	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFormat() {
		switch(format){
		case RDFXML:
			return RDFXML_FORMAT;
		case SPARQLXML:
			return SPARQL_FORMAT;
		default:
			logger.warn("unknown format ["+format+"], using "+RDFXML_FORMAT);
			return RDFXML_FORMAT;
		}
	}

	public void setFormat(String format) {
		this.format = parseFormat(format);
	}
	
	private int parseFormat(String formatStr) {
		if(formatStr==null){
			logger.debug("format not specified, using default format "+RDFXML_FORMAT);
			return RDFXML;
		}
		if(formatStr.equalsIgnoreCase(RDFXML_FORMAT)){
			return RDFXML;
		}else if(formatStr.equalsIgnoreCase(SPARQL_FORMAT)){
			return SPARQLXML;
		}else{
			logger.warn("unknown format ["+formatStr+"], using "+RDFXML_FORMAT);
			return RDFXML;
		}
	}


	@Override
	public void initialize(PipeContext context, Element element) {
		// nothing to do here?
	}
}
