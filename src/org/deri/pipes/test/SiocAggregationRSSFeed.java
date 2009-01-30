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
package org.deri.pipes.test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.net.URLEncoder;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiocAggregationRSSFeed {
	final Logger logger = LoggerFactory.getLogger(SiocAggregationRSSFeed.class);
	static final String SindiceURLQuery="PREFIX ss: <http://sindice.com/vocab/search#> " +
										" PREFIX fields: <http://sindice.com/vocab/fields#> " +
										"SELECT ?location WHERE {?p ss:link ?location.?p fields:format \"RDF\"}";
	static final String MessageQuery="PREFIX sioc: <http://rdfs.org/sioc/ns#> " +
									 "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
									 "PREFIX dcterms: <http://purl.org/dc/terms/> " + 
									 "SELECT ?title ?description " +
									 "WHERE { {{?person sioc:email_sha1 \"$SHA1EMAIL\"} UNION {?person sioc:email_sha1 \"$SHA1EMAIL\"}}. " +
									 "        ?post sioc:has_creator ?person. " +
									 "		  ?post dcterms:title ?title.	" +
									 "		  ?post sioc:content ?desctription. }";
	static final String SPARQL2RSS ="http://pipes.deri.org/evaluation/sparql-rss.xsl";
	public static void main(String[] args) throws Exception{		
		StringBuilder sindiceQuery =new StringBuilder("http://sindice.com/search?q=");
			sindiceQuery.append(URLEncoder.encode("((* <http://xmlns.com/foaf/0.1/mbox_sha1sum> \""+args[0]+"\") OR ","UTF-8"))
							.append(URLEncoder.encode("(* <http://rdfs.org/sioc/ns#email_sha1> \""+args[0]+"\")) AND","UTF-8"))
								.append(URLEncoder.encode("(* <http://rdfs.org/sioc/ns#content> *)","UTF-8"))
									.append("&qt=advanced");
			RepositoryConnection sindiceBuff =Utils.createTripleBufferFromURL(sindiceQuery.toString());
			TupleQueryResult urlResults=sindiceBuff.prepareTupleQuery(QueryLanguage.SPARQL,SindiceURLQuery).evaluate();
			RepositoryConnection rdfBuff=Utils.createTripleBuffer();
			while (urlResults.hasNext()) {
				Utils.loadRDF(urlResults.next().getValue("location").stringValue(),rdfBuff);
			}	
			TupleQueryResult results=rdfBuff.prepareTupleQuery(QueryLanguage.SPARQL,MessageQuery.replace("SHA1EMAIL", args[0])).evaluate();
			ByteArrayOutputStream resultBuff =new ByteArrayOutputStream();;
			QueryResultIO.write(results, TupleQueryResultFormat.SPARQL, resultBuff);
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(SPARQL2RSS));
			if(args.length>1)			
				transformer.transform(new StreamSource(new StringReader(resultBuff.toString("UTF-8"))),new StreamResult(new File(args[1])));			
		    else
		    	transformer.transform(new StreamSource(new StringReader(resultBuff.toString("UTF-8"))),new StreamResult(System.out));
	}
}