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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.utils.UrlLoader;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class XMLStreamBuffer implements ExecBuffer,InputStreamProvider{
	private transient Logger logger = LoggerFactory.getLogger(XMLStreamBuffer.class);
	String url=null;
	StringBuffer strBuff=null;
	private byte[] byteData = null;
	
	public XMLStreamBuffer(){
	}
	
	public XMLStreamBuffer(String url){
		this.url= url;
	}
	
	public XMLStreamBuffer(StringBuffer strBuff){
		this.strBuff=strBuff;
	}
	
	public StreamSource getStreamSource() throws IOException{
		return new StreamSource(getInputStream());
	}
	
	public void setStreamSource(StringBuffer strBuff){
		this.strBuff=strBuff;
	}

	@Override
	public void stream(ExecBuffer outputBuffer) throws IOException{
		stream(outputBuffer,null);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) throws IOException {
		try{
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
		}catch(Exception e){
			throw new IOException("Cannot stream to the output buffer",e);
		}
		
	}

	@Override
	public void stream(OutputStream output) throws IOException{

		InputStream in = getInputStream();
		try{
			IOUtils.copy(in, output);
		}finally{
			in.close();
		}
	}

	/**
	 * @param byteArrayInputStream
	 */
	public void setStreamSource(byte[] data) {
		this.byteData  = data;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.model.InputStreamProvider#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		if(byteData != null){
			return new ByteArrayInputStream(byteData);
		}
		if(url!=null) 
			return UrlLoader.openConnection(url, RDFFormat.RDFXML);
		if (strBuff!=null) 
		  return new ByteArrayInputStream(strBuff.toString().getBytes("UTF-8"));
		return null;
	}
}
