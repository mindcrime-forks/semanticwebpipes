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
import org.deri.pipes.model.SesameTupleBuffer;
import org.deri.pipes.utils.XMLUtil;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class TupleQueryResultFetchBox extends FetchBox implements Operator {
	private transient Logger logger = LoggerFactory.getLogger(TupleQueryResultFetchBox.class);
	private ExecBuffer buffer=null;
	private TupleQueryResultFormat format=TupleQueryResultFormat.SPARQL;
		
	public void execute(PipeContext context){				
		buffer=new SesameTupleBuffer();
		((SesameTupleBuffer)buffer).loadFromURL(location,format);			
		isExecuted=true;
	}
	public static TupleQueryResultFormat formatOf(String format){
		if(format == null){
			return TupleQueryResultFormat.SPARQL;
		}
		if (TupleQueryResultFormat.SPARQL.getName().equalsIgnoreCase(format)) 
			return TupleQueryResultFormat.SPARQL;
		else if(TupleQueryResultFormat.JSON.getName().equalsIgnoreCase(format))
			return TupleQueryResultFormat.JSON;
		else if(TupleQueryResultFormat.BINARY.getName().equalsIgnoreCase(format))
			return TupleQueryResultFormat.BINARY;
		return null;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    

	public void setFormat(String fmt) {
		setFormat(formatOf(fmt));
	}

	public TupleQueryResultFormat getFormat() {
		return format;
	}

	public void setFormat(TupleQueryResultFormat format) {
		this.format = format;
	}
}