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
