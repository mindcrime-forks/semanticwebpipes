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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.utils.UrlLoader;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SesameMemoryBuffer implements ExecBuffer {
	private transient Logger logger = LoggerFactory.getLogger(SesameMemoryBuffer.class);
	Repository buffRepository=null;
	public static final int NONE=0;
	public static final int RDFS=1;
	public static final int OWL=2;
	private int reasoningType=NONE;
	private Sail sail = new MemoryStore();

	public SesameMemoryBuffer(int reasoningType){
		this.reasoningType=reasoningType;
	}

	public SesameMemoryBuffer(){
		this(NONE);
	}

	public  RepositoryConnection getConnection(){
		try{
			if(buffRepository==null){
				initialiseRepository();
			}
			return buffRepository.getConnection();
		}
		catch(RepositoryException e){
			logger.warn("could not initialise repository",e);
		}
		return null;
	}

	private void initialiseRepository() throws RepositoryException {
		if(sail == null){
			sail=new MemoryStore();
		}
		switch (reasoningType){
		case RDFS:
			logger.debug("using ForwardChainingRDFSInferencer" );
			buffRepository =new SailRepository(
					new ForwardChainingRDFSInferencer(sail));
			break;
		case NONE:
		default:
			logger.debug("using no inference" );
			buffRepository = new SailRepository(sail);
		break;
		}
		buffRepository.initialize();
	}

	public Sail getSail(){
		return sail;
	}

	public void loadFromURL(String url,RDFFormat format){
		try{
			InputStream in = UrlLoader.openConnection(url, format);
			load(in,url,format);
		}catch(Exception e){
			logger.warn("error loading location ["+url+"]",e);
		}
	}

	public void load(InputStream in, String url, RDFFormat format) throws RDFParseException, RepositoryException, IOException {
		load(new InputStreamReader(in),url,format);
	}

	private void load(Reader in, String url,
			RDFFormat format) throws RDFParseException, RepositoryException, IOException {
		RepositoryConnection conn=this.getConnection() ;
		try{
			logger.debug("loading from "+url);
			conn.add(in, url, format);
		}finally{
			in.close();
		}		
	}

	public void loadFromText(String text, String baseURL){
		try{
			String url = ((null!=baseURL)&&(baseURL.trim().length()>0))?baseURL.trim():"http://pipes.deri.org/";
			load(new StringReader(text),url, RDFFormat.RDFXML);
		}
		catch (Exception e) {
			logger.warn("problem loading from text",e);
		}
	}

	public void loadFromText(String text){
		loadFromText(text,null);
	}

	public void stream(ExecBuffer outputBuffer){
		stream(outputBuffer,null);
	}
	
	public SesameTupleBuffer toTupleBuffer() throws QueryEvaluationException, RepositoryException, MalformedQueryException{
		String query ="SELECT * WHERE {?subject ?predicate ?object.}";
		TupleQueryResult result = getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		return new SesameTupleBuffer(result);
	}

	public void stream(ExecBuffer outputBuffer,String uri){
		URIImpl uriImpl=((null!=uri)&&(uri.trim().length()>0))?(new URIImpl(uri.trim())):null;
		try{
			if(outputBuffer instanceof SesameMemoryBuffer){
				
				RepositoryConnection repositoryConnection = ((SesameMemoryBuffer)outputBuffer).getConnection();
				try{
					RepositoryResult<Statement> repositoryResult = getConnection().getStatements(null, null, null, true);
					List<Statement> statements = repositoryResult.asList();
					if(uriImpl!=null) {
						repositoryConnection.add(statements,uriImpl);
					} else{
						repositoryConnection.add(statements);
					}
				}finally{
					repositoryConnection.close();
				}
			}else if(outputBuffer instanceof XMLStreamBuffer){
				((XMLStreamBuffer)outputBuffer).setStreamSource(toXMLStringBuffer());
			}else{
				logger.warn("the outputBuffer was not a SesameMemoryBuffor or XMLStreamBuffer - cannot stream uri=["+uri+"]");
			}
		}
		catch(RepositoryException e){
			logger.warn("problem streaming to ExecBuffer",e);
		}
	}

	public String toString(){
		return toXMLStringBuffer().toString(); 	
	}

	public StringBuffer toXMLStringBuffer(){
		StringWriter stBuff=new StringWriter();
		try{
			RepositoryConnection connection = getConnection();
			try{
			connection.export(new RDFXMLPrettyWriter(stBuff));
			return stBuff.getBuffer();
			}finally{
				connection.close();
			}
		}
		catch(Exception e){
			logger.warn("could not export to StringBuffer",e);
		}
		return null; 	
	}

	public void stream(OutputStream output){
		stream(output, RDFFormat.RDFXML);
	}

	public void stream(OutputStream output,RDFFormat format){
		try{
			RepositoryConnection connection = getConnection();
			try{
				connection.export(Rio.createWriter(format, output));
			}finally{
				connection.close();
			}
		}
		catch(Exception e){
			logger.warn("problem exportin",e);
		}
	}
}
