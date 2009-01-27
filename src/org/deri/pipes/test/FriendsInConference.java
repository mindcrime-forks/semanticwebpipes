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
