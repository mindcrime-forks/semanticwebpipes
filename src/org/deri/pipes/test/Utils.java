package org.deri.pipes.test;

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
	
	public static   RepositoryConnection createTripleBuffer(){
		Repository buffRepository = new SailRepository(new MemoryStore());
		try{
			buffRepository.initialize();
			return buffRepository.getConnection();
		}
		catch(RepositoryException e){
			e.printStackTrace();
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
		catch (OpenRDFException e) {
		     e.printStackTrace();
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static RepositoryConnection createTripleBufferFromURL(String url){
		RepositoryConnection buff =Utils.createTripleBuffer();
		Utils.loadRDF(url,buff);
		return buff;
	}
}
