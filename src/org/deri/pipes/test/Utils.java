package org.deri.pipes.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class Utils {	
	static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static RepositoryConnection createTripleBuffer(){
		Repository buffRepository = new SailRepository(new MemoryStore());
		try{
			buffRepository.initialize();
			return buffRepository.getConnection();
		}
		catch(RepositoryException e){
			logger.info("problem creating triple buffer",e);
		}		
		return null;
	}
	
	public static RepositoryConnection loadRDF(String url, RepositoryConnection conn){
    	try{
	    	HttpURLConnection urlConn=(HttpURLConnection)((new URL(url)).openConnection());
			urlConn.setRequestProperty("Accept", RDFFormat.RDFXML.getDefaultMIMEType());
			urlConn.connect();
			conn.add(urlConn.getInputStream(), url, RDFFormat.RDFXML);			
	    }
		catch (Exception e) {
		     logger.info("problim loading rdf from ["+url+"]",e);
		}
		return conn;
	}
	
	public static RepositoryConnection createTripleBufferFromURL(String url){
		RepositoryConnection buff =Utils.createTripleBuffer();
		Utils.loadRDF(url,buff);
		return buff;
	}
}
