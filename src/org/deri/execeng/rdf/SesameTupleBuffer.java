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
package org.deri.execeng.rdf;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.deri.execeng.core.ExecBuffer;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.MutableTupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SesameTupleBuffer extends ExecBuffer{
	final Logger logger = LoggerFactory.getLogger(SesameTupleBuffer.class);
	private MutableTupleQueryResult buffer=null;

	public SesameTupleBuffer(){
	}

	public SesameTupleBuffer(String url,TupleQueryResultFormat format){
		loadFromURL(url,format);
	}

	public SesameTupleBuffer(TupleQueryResult buffer){
		copyBuffer(buffer);
	}

	public void copyBuffer(TupleQueryResult buffer){
		try{
			this.buffer=new MutableTupleQueryResult(buffer);
		}
		catch(org.openrdf.query.QueryEvaluationException  e){		
			logger.warn("could not copy buffer",e);
		}
	}

	public TupleQueryResult getTupleQueryResult(){		
		try{
			return buffer.clone();
		}catch(CloneNotSupportedException e){
			logger.warn("Clone was not supported for "+buffer.getClass(),e);
			return null;
		}
	}


	public void loadFromText(String text){
		if (text==null) {
			buffer=null;
		}
		try{
			copyBuffer(QueryResultIO.parse(new ByteArrayInputStream(text.trim().getBytes()), TupleQueryResultFormat.SPARQL));
		}
		catch(Exception e){
			logger.warn("problem loading from text",e);
		}
	}

	public void loadFromURL(String url,TupleQueryResultFormat format){
		if (url==null) buffer=null;
		try{
			InputStream in = openConnection(url, format);
			try{
				copyBuffer(QueryResultIO.parse(in, format));
			}finally{
				in.close();
			}
		}
		catch(Exception e){
			logger.warn("problem loading from url ["+url+"]",e);
		}
	}


	public String toString(){
		java.io.ByteArrayOutputStream strOut=new java.io.ByteArrayOutputStream();
		stream(strOut);		
		return strOut.toString();		
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		stream(outputBuffer,(String)null);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		//TODO: why is context ignored.
		if(outputBuffer instanceof XMLStreamBuffer){
			((XMLStreamBuffer)outputBuffer).setStreamSource(new StringBuffer(toString()));
		}else{
			logger.warn("Cannot stream outputBuffer which is not an XMLStreamBuffer");
		}
	}

	@Override
	public void stream(OutputStream output) {
		try{
			QueryResultIO.write(buffer.clone(), TupleQueryResultFormat.SPARQL, output);
		}catch(Exception e){
			logger.warn("sreaming error",e);
		}
	}
}
