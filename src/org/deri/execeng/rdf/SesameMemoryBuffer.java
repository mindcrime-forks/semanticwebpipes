package org.deri.execeng.rdf;

import java.net.HttpURLConnection;
import java.net.URL;

import org.deri.execeng.core.ExecBuffer;
import org.openrdf.OpenRDFException;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.model.impl.URIImpl;
public class SesameMemoryBuffer extends ExecBuffer {
	Repository buffRepository=null;
	public static final int NONE=0;
	public static final int RDFS=1;
	public static final int OWL=2;
	private int reasoningType=0;
	private Sail sail;
	public SesameMemoryBuffer(int reasoningType){
		this.reasoningType=reasoningType;
	}
	public SesameMemoryBuffer(){
		reasoningType=NONE;
	}
	public  RepositoryConnection getConnection(){
		if(buffRepository==null){
			sail=new MemoryStore();
			switch (reasoningType){
			   case RDFS:
				   buffRepository =new SailRepository(
	                          new ForwardChainingRDFSInferencer(sail));
				   break;
			   case NONE:
			   default:
				   buffRepository = new SailRepository(sail);
			       break;
			}
			
			try{
				buffRepository.initialize();
				return buffRepository.getConnection();
			}
			catch(RepositoryException e){
				log.append(e.toString()+"\n");
			}
		}
		else{
			try{
				return buffRepository.getConnection();
			}
			catch(RepositoryException e){
				log.append(e.toString()+"\n");
			}
		}
		return null;
	}
	
	public Sail getSail(){
		return sail;
	}
	public void loadFromURL(String url,RDFFormat format){
		RepositoryConnection conn=this.getConnection() ;
    	try{
	    	HttpURLConnection urlConn=(HttpURLConnection)((new URL(url)).openConnection());
			urlConn.setRequestProperty("Accept", format.getDefaultMIMEType());
			urlConn.connect();
			conn.add(urlConn.getInputStream(), url, format);
	    }
		catch (OpenRDFException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
		catch (java.io.IOException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
	}
	
	public void loadFromText(String text){
		RepositoryConnection conn=this.getConnection() ;
    	try{
	    	conn.add(new java.io.StringReader(text), "http://pipes.deri.org/", RDFFormat.RDFXML);
	    }
		catch (OpenRDFException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
		catch (java.io.IOException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
	}
	
	public void loadFromText(String text,String baseURL){
		RepositoryConnection conn=this.getConnection() ;
    	try{
	    	conn.add(new java.io.StringReader(text), baseURL, RDFFormat.RDFXML);
	    }
		catch (OpenRDFException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
		catch (java.io.IOException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
	}
	
	public void streamming(ExecBuffer outputBuffer){
		if(outputBuffer instanceof SesameMemoryBuffer){
			try{
				((SesameMemoryBuffer)outputBuffer).getConnection().add((getConnection().getStatements(null, null, null, true)).asList());
			}
			catch(RepositoryException e){
				log.append(e.toString()+"\n");
			}
    	}
    }
	
	public void streamming(ExecBuffer outputBuffer,String uri){
		if(outputBuffer instanceof SesameMemoryBuffer){
			try{
				((SesameMemoryBuffer)outputBuffer).getConnection().add((getConnection().getStatements(null, null, null, true)).asList(),new URIImpl(uri));
			}
			catch(RepositoryException e){
				log.append(e.toString()+"\n");
			}
    	}
    }
	
	public String toString(){
		java.io.StringWriter stBuff=new java.io.StringWriter();
		try{
		  getConnection().export(new RDFXMLPrettyWriter(stBuff));
		  return stBuff.toString();
		}
		catch(RepositoryException e){
			log.append(e.toString()+"\n");
		}catch(RDFHandlerException e){
			log.append(e.toString()+"\n");
		}
	   return null; 	
	}
	
	public void toOutputStream(java.io.OutputStream output){
		toOutputStream(output, RDFFormat.RDFXML);
	}
	
	public void toOutputStream(java.io.OutputStream output,RDFFormat format){
		try{
		  getConnection().export(Rio.createWriter(format, output));
		}
		catch(RepositoryException e){
			log.append(e.toString()+"\n");
		}catch(RDFHandlerException e){
			log.append(e.toString()+"\n");
		}
	}
	
}
