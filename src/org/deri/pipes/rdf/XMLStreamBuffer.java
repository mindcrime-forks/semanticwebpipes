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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.deri.pipes.core.ExecBuffer;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class XMLStreamBuffer extends ExecBuffer {
	final Logger logger = LoggerFactory.getLogger(XMLStreamBuffer.class);
	String url=null;
	StringBuffer strBuff=null;
	
	public XMLStreamBuffer(){
	}
	
	public XMLStreamBuffer(String url){
		this.url= url;
	}
	
	public XMLStreamBuffer(StringBuffer strBuff){
		this.strBuff=strBuff;
	}
	
	public StreamSource getStreamSource(){
		if(url!=null) 
			return new StreamSource(url);
		if (strBuff!=null) 
		  return new StreamSource(new java.io.StringReader(strBuff.toString()));
		return null;
	}
	
	public void setStreamSource(StringBuffer strBuff){
		this.strBuff=strBuff;
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		stream(outputBuffer,null);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		if(outputBuffer instanceof SesameMemoryBuffer){
			if(url!=null) 
				((SesameMemoryBuffer)outputBuffer).loadFromURL(url, RDFFormat.RDFXML);
			else	
				((SesameMemoryBuffer)outputBuffer).loadFromText(strBuff.toString(),null);
		}
		else if(outputBuffer instanceof SesameTupleBuffer){
			if(url!=null) 
				((SesameTupleBuffer)outputBuffer).loadFromURL(url, TupleQueryResultFormat.SPARQL);
			else	
				((SesameTupleBuffer)outputBuffer).loadFromText(strBuff.toString());
		}else{
			logger.warn("cannot stream outputBuffer which is not a SesameMemoryBuffer or SesameTupleBuffer");
		}
		
	}

	@Override
	public void stream(OutputStream output) {
		// TODO Auto-generated method stub
		if(url!=null){
			try {
				InputStream in = openConnection(url, RDFFormat.RDFXML);
				try{
					IOUtils.copy(in, output);
				}finally{
					in.close();
				}
			} catch (IOException e) {
				logger.warn("Couldn't read from location=["+url+"]",e);
			}
			
		}else{
			try {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);
				outputStreamWriter.write(strBuff.toString());
				outputStreamWriter.flush();
			} catch (IOException e) {
				logger.warn("problem streaming output",e);
			}
		}
	}

	@Override
	public RepositoryConnection getConnection() {
		if(true){
			throw new RuntimeException("getConnection not implemented for XMLStreamBuffer");
		}
		return null;
	}
}
