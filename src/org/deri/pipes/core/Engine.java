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

import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.core.internals.ThreadedExecutor;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.model.MultiExecBuffer;
import org.deri.pipes.store.FilePipeStore;
import org.deri.pipes.store.PipeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SemanticWebPipes engine, for parsing and executing pipes.
 * @author robful
 *
 */
public class Engine {
	Logger logger = LoggerFactory.getLogger(Engine.class);
	private static Engine defaultEngine;
	PipeParser parser;
	ThreadedExecutor executor;
	private PipeStore pipeStore;
	private HttpClient httpClient;

	public PipeStore getPipeStore(){
		if(pipeStore == null){
			logger.warn("Pipe store not set... use a FilePipeStore in temporary folder");
			pipeStore = new FilePipeStore();
		}
		return pipeStore;
	}
	public void setPipeStore(PipeStore pipeStore){
		this.pipeStore = pipeStore;
		logger.info("Set pipe store to "+pipeStore);
	}
	/**
	 * @return
	 */
	public PipeParser getPipeParser() {
		if(parser == null){
			parser = new PipeParser();
		}
		return parser;
	}

	/**
	 * @return
	 */
	public ThreadedExecutor getExecutor() {
		if(executor == null){
			executor = new ThreadedExecutor();
		}
		return executor;
	}
	/**
	 * Set the PipeParser.
	 * @param parser
	 */
	void setPipeParser(PipeParser parser){
		this.parser = parser;
	}
	
	/**
	 * Get the HttpClient.
	 * @return
	 */
	public HttpClient getHttpClient(){
		if(httpClient == null){
			httpClient = new HttpClient();
			httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

		}
		return httpClient;
	}
	/**
	 * Set the http client;
	 * @param httpClient
	 */
	public void setHttpClient(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	/**
	 * Parse the XML returning the defined operator.
	 * @param xml
	 * @return
	 */
	public Operator parse(String xml) {
		return (Operator)getPipeParser().parse(xml);
	}
	/**
	 * Execute the pipe contained in this xml.
	 * @param xml
	 * @return
	 * @throws Exception
	 * @deprecated
	 */
	public ExecBuffer execute(String xml) throws Exception{
		Operator parsedObject = getPipeParser().parse(xml);
		return executeOperator(parsedObject);
	}
	/**
	 * @deprecated
	 * @param parsedObject
	 * @return
	 * @throws Exception
	 */
	private ExecBuffer executeOperator(Object parsedObject) throws Exception {
		Operator operator = (Operator)parsedObject;
		return operator.execute(newContext());
	}
	/**
	 * Execute the pipe contained in this xml input stream.
	 * @param xml
	 * @return
	 * @throws Exception
	 * @deprecated
	 */
	public ExecBuffer execute(InputStream in) throws Exception{
		Object parsedObject = getPipeParser().parse(in);
		return executeOperator(parsedObject);
	}

	/**
	 * @return
	 */
	public Context newContext() {
		return new Context(this);
	}

	/**
	 * @return
	 */
	public static Engine defaultEngine() {
		if(defaultEngine == null){
			defaultEngine = new Engine();
		}
		return defaultEngine;
	}

	/**
	 * @param in
	 * @return
	 */
	public Operator parse(InputStream in) {
		return (Operator) getPipeParser().parse(in);
	}

	/**
	 * @param inputs
	 * @param context
	 * @return
	 * @throws InterruptedException 
	 */
	public MultiExecBuffer execute(List<Operator> inputs, Context context) throws InterruptedException {
		return getExecutor().execute(inputs,context);
	}

	/**
	 * Serialize this operator to XML.
	 * @param operator
	 * @return
	 */
	public String serialize(Operator operator) {
		return getPipeParser().serializeToXML(operator);
	}

	/**
	 * Release any resources associated with this Engine.
	 * If the defaultEngine is shutdown, a new defaultEngine
	 * is created next time getDefaultEngine() is invoked.
	 */
	public void shutdown(){
		logger.debug("Shutdown invoked");
		if(this == defaultEngine){
			defaultEngine = null;
		}
		if(this.executor != null){
			this.executor.shutdown();
		}
	}
	/**
	 * @param pipeId
	 * @return
	 */
	public Pipe getStoredPipe(String pipeId) throws Exception{
		//TODO: cache compiled pipes
		PipeConfig config = getPipeStore().getPipe(pipeId);
		if(config == null){
			throw new NullPointerException("No pipe found having id="+pipeId);
		}
		return (Pipe)parse(config.getSyntax());
	}


}
