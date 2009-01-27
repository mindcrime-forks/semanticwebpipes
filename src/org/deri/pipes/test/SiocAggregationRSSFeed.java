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
										"SELECT ?url WHERE {?p ss:link ?url.?p fields:format \"RDF\"}";
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
				Utils.loadRDF(urlResults.next().getValue("url").stringValue(),rdfBuff);
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