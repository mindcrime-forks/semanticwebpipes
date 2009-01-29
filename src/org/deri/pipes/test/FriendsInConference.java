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

import java.net.URLEncoder;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendsInConference { 
	final Logger logger = LoggerFactory.getLogger(FriendsInConference.class);

	static String CONFQUERY=    "PREFIX swc: <http://data.semanticweb.org/ns/swc/ontology#>"+
								"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
								"CONSTRUCT {?person ?ss ?oo.?person swc:attend ?conf}" +
								"	WHERE {" +
								"	  GRAPH ?graph{" +
								"	       ?person a foaf:Person." +
								"	       ?person ?ss ?oo." +
								"          ?conf ?sub \"$CONFNAME\"." +
								"          ?conf swc:completeGraph ?graph." +
								"      }"+
								"}";
	static String FRIENDSQUERY=
		"PREFIX swc: <http://data.semanticweb.org/ns/swc/ontology#>"+
		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
		"SELECT ?name" +
		"	WHERE {" +
		"			<$authorURI> foaf:knows ?person."+	
		"	       ?person a foaf:Person. " +
		"	       ?person foaf:name ?name." +
		"      	   ?person swc:attend ?conf." +
		"      }";
	static final String SPARQLENDPOINT="http://data.semanticweb.org/sparql";
	public static void main(String[] args) throws Exception {
		StringBuilder endPointQuery =new StringBuilder(SPARQLENDPOINT);
			endPointQuery.append("?query=")
							.append(URLEncoder.encode(CONFQUERY.replace("$CONFNAME", args[1]),"UTF-8"));			
			RepositoryConnection buff =Utils.createTripleBufferFromURL(endPointQuery.toString());
			Utils.loadRDF(args[0],buff);			
			TupleQueryResult results=buff.prepareTupleQuery(QueryLanguage.SPARQL,FRIENDSQUERY.replace("$authorURI",args[0]+"#me")).evaluate();
			if(args.length>2)
				QueryResultIO.write(results, TupleQueryResultFormat.SPARQL, new java.io.FileOutputStream(args[2]));	
		    else
		    	QueryResultIO.write(results, TupleQueryResultFormat.SPARQL, System.out);
			
	}
} 
