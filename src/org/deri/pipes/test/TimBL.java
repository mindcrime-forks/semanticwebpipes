package org.deri.pipes.test;
import java.io.IOException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
public class TimBL {
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
	public static void main(String[] args){
		
		RepositoryConnection timFoafBuff =Utils.createTripleBufferFromURL(TIMFOAFURL);		
		RepositoryConnection timDBPLBuff =Utils.createTripleBufferFromURL(TIMDBPLURL);;
		RepositoryConnection timDBpediaBuff =Utils.createTripleBufferFromURL(TIMDBPEDIAURL);
		
		RepositoryConnection resultBuff =Utils.createTripleBuffer();
		try{
			resultBuff.add(timFoafBuff.getStatements(null, null, null, true));
			resultBuff.add(timDBPLBuff.prepareGraphQuery(QueryLanguage.SPARQL,DBPLQUERY).evaluate());
			resultBuff.add(timDBpediaBuff.prepareGraphQuery(QueryLanguage.SPARQL,DBPEDIAQUERY).evaluate());
			RDFHandler handler=null;
			if(args.length>0)			
					handler=Rio.createWriter(RDFFormat.RDFXML, new java.io.FileWriter(args[0]));			
			else
				handler=Rio.createWriter(RDFFormat.N3, System.out);
			resultBuff.export(handler);
		}catch(RepositoryException e){
			e.printStackTrace();
		} catch (UnsupportedRDFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
	}
}
