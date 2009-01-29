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
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TimBL {
	final Logger logger = LoggerFactory.getLogger(TimBL.class);
	static final String TIMFOAFURL="http://www.w3.org/People/Berners-Lee/card";
	static final String TIMDBPLURL="http://dblp.l3s.de/d2r/resource/authors/Tim_Berners-Lee";
	static final String TIMDBPEDIAURL="http://dbpedia.org/resource/Tim_Berners-Lee";
	static final String DBPLQUERY="CONSTRUCT {<http://www.w3.org/People/Berners-Lee/card#i> ?p ?o." +
								  "           ?s2 ?p2 <http://www.w3.org/People/Berners-Lee/card#i>}" +
								  "WHERE {{<http://dblp.l3s.de/d2r/resource/authors/Tim_Berners-Lee> ?p ?o} " +
								  "UNION {?s2 ?p2 <http://dblp.l3s.de/d2r/resource/authors/Tim_Berners-Lee>} }";
	static final String DBPEDIAQUERY="CONSTRUCT {<http://www.w3.org/People/Berners-Lee/card#i> ?p ?o." +
									 "           ?s2 ?p2 <http://www.w3.org/People/Berners-Lee/card#i>}" +
									 "WHERE {{<http://dbpedia.org/resource/Tim_Berners-Lee> ?p ?o}" +
									 " UNION {?s2 ?p2 <http://dbpedia.org/resource/Tim_Berners-Lee>}}";
	public static void main(String[] args) throws Exception{
		
		RepositoryConnection timFoafBuff =Utils.createTripleBufferFromURL(TIMFOAFURL);		
		RepositoryConnection timDBPLBuff =Utils.createTripleBufferFromURL(TIMDBPLURL);;
		RepositoryConnection timDBpediaBuff =Utils.createTripleBufferFromURL(TIMDBPEDIAURL);
		
		RepositoryConnection resultBuff =Utils.createTripleBuffer();
			resultBuff.add(timFoafBuff.getStatements(null, null, null, true));
			resultBuff.add(timDBPLBuff.prepareGraphQuery(QueryLanguage.SPARQL,DBPLQUERY).evaluate());
			resultBuff.add(timDBpediaBuff.prepareGraphQuery(QueryLanguage.SPARQL,DBPEDIAQUERY).evaluate());
			RDFHandler handler=null;
			if(args.length>0)			
					handler=Rio.createWriter(RDFFormat.RDFXML, new java.io.FileWriter(args[0]));			
			else
				handler=Rio.createWriter(RDFFormat.N3, System.out);
			resultBuff.export(handler);
	}
}
