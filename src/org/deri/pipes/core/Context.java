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
import org.deri.pipes.core.internals.ThreadedExecutor;

import com.thoughtworks.xstream.XStream;
/**
 * Operators belonging to a PipeConfig.
 * @author rfuller
 *
 */
public class Context {
	Map <String,Operator> operators = new HashMap<String,Operator>();
	Map map = new HashMap();
	private transient HttpClient httpClient;
	private Engine engine;
	/**
	 * Default constructor.
	 */
	public Context(){
		
	}
	/**
	 * Create a Context for this engine.
	 * @param engine
	 */
	public Context(Engine engine){
		this.engine = engine;
	}
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

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public HttpClient getHttpClient(){
		return httpClient;
	}
	/**
	 * @param obj
	 * @return
	 */
	public Object get(Object obj) {
		return map.get(obj);
	}
	/**
	 * @param obj
	 * @param invoke
	 */
	public void put(Object key, Object value) {
		map.put(key, value);
	}

	/**
	 * @return
	 */
	public Engine getEngine() {
		if(engine == null){
			engine = new Engine();
		}
		return engine;
	}
}
