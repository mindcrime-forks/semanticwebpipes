package org.deri.execeng.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.HttpURLConnection;
import java.net.URL;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.openrdf.OpenRDFException;
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
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.model.impl.URIImpl;
public class SesameMemoryBuffer extends ExecBuffer {
	final Logger logger = LoggerFactory.getLogger(SesameMemoryBuffer.class);
	Repository buffRepository=null;
	public static final int NONE=0;
	public static final int RDFS=1;
	public static final int OWL=2;
	private int reasoningType=0;
	private Sail sail;
	PipeParser parser;
	
	public SesameMemoryBuffer(PipeParser parser,int reasoningType){
		this.parser=parser;
		this.reasoningType=reasoningType;
	}
	
	public SesameMemoryBuffer(PipeParser pipeParser){
		this.parser=pipeParser;
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
				parser.log(e);
			}
		}
		else{
			try{
				return buffRepository.getConnection();
			}
			catch(RepositoryException e){
				parser.log(e);
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
			parser.log(e);
		}
		catch (java.io.IOException e) {
			parser.log(e);
		}
	}
	
	public void loadFromText(String text, String baseURL){
		RepositoryConnection conn=this.getConnection() ;
    	try{
	    	conn.add(new java.io.StringReader(text), 
	    	    ((null!=baseURL)&(baseURL.trim().length()>0))?baseURL.trim():"http://pipes.deri.org/",
	    			 RDFFormat.RDFXML);
	    }
		catch (OpenRDFException e) {
			parser.log(e);
		}
		catch (java.io.IOException e) {
			parser.log(e);
		}
	}
	
	public void loadFromText(String text){
		loadFromText(text,null);
	}
	
	public void stream(ExecBuffer outputBuffer){
		stream(outputBuffer,null);
    }
	
	public void stream(ExecBuffer outputBuffer,String uri){
		URIImpl uriImpl=((null!=uri)&&(uri.trim().length()>0))?(new URIImpl(uri.trim())):null;
		try{
			if(outputBuffer instanceof SesameMemoryBuffer){
				if(uriImpl!=null)
					((SesameMemoryBuffer)outputBuffer).getConnection().add(
							(getConnection().getStatements(null, null, null, true)).asList(),uriImpl);
				else
					((SesameMemoryBuffer)outputBuffer).getConnection().add(
							(getConnection().getStatements(null, null, null, true)).asList());
				
	    	}
			else if(outputBuffer instanceof XMLStreamBuffer){
				((XMLStreamBuffer)outputBuffer).setStreamSource(toXMLStringBuffer());
			}
	    }
		catch(RepositoryException e){
			parser.log(e);
		}
    }
	
	public String toString(){
		return toXMLStringBuffer().toString(); 	
	}
	
	public StringBuffer toXMLStringBuffer(){
		java.io.StringWriter stBuff=new java.io.StringWriter();
		try{
		  getConnection().export(new RDFXMLPrettyWriter(stBuff));
		  return stBuff.getBuffer();
		}
		catch(RepositoryException e){
			parser.log(e);
		}catch(RDFHandlerException e){
			parser.log(e);
		}
	   return null; 	
	}
	
	public void stream(java.io.OutputStream output){
		stream(output, RDFFormat.RDFXML);
	}
	
	public void stream(java.io.OutputStream output,RDFFormat format){
		try{
		  getConnection().export(Rio.createWriter(format, output));
		}
		catch(RepositoryException e){
			parser.log(e);
		}catch(RDFHandlerException e){
			parser.log(e);
		}
	}
}
