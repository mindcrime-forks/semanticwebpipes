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
package org.deri.pipes.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.deri.pipes.model.Operator;

import com.thoughtworks.xstream.XStream;
/**
 * Operators belonging to a Pipe.
 * @author rfuller
 *
 */
public class PipeContext {
	Map <String,Operator> operators = new HashMap<String,Operator>();
	XStream xstream;
	PipeParser parser;
	private transient HttpClient httpClient;
	/**
	 * Get the operator having this id.
	 * @param id
	 * @return The operator having the given id, or null if there is no such operator.
	 */
	public Operator getOperator(String id){
		return operators.get(id);
	}
	/**
	 * Add this operator into the context.
	 * @param id
	 * @param operator
	 */
	void addOperator(String id,Operator operator){
		operators.put(id, operator);
	}
	/**
	 * Whether the context contains an operator having this id.
	 * @param id
	 * @return true if there is an operator having this id, false otherwise.
	 */
	public boolean contains(String id) {
		return operators.containsKey(id);
	}
	/**
	 * Set the PipeParser.
	 * @param parser
	 */
	void setPipeParser(PipeParser parser){
		this.parser = parser;
	}
	/**
	 * Get the PipeParser.
	 * @return
	 */
	public PipeParser getPipeParser(){
		return parser;
	}
	/**
	 * Get the named operator having first tested that
	 * the execute() method was called, or executing.
	 * @param id
	 * @return the operator having been executed.
	 */
	public Operator getOperatorExecuted(String id) {
		Operator operator = getOperator(id);
		if (operator != null && !operator.isExecuted()){
    		operator.execute(this);
    	}
		return operator;
	}
	public String serialize(Object o) {
		return xstream.toXML(o);
	}
	public Operator parse(String tmp) {
		return (Operator)xstream.fromXML(tmp);
	}
	public XStream getXstream() {
		return xstream;
	}
	public void setXstream(XStream xstream) {
		this.xstream = xstream;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public HttpClient getHttpClient(){
		return httpClient;
	}
}
