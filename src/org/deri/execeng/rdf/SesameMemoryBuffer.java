package org.deri.execeng.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.deri.execeng.core.ExecBuffer;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SesameMemoryBuffer extends ExecBuffer {
	final Logger logger = LoggerFactory.getLogger(SesameMemoryBuffer.class);
	Repository buffRepository=null;
	public static final int NONE=0;
	public static final int RDFS=1;
	public static final int OWL=2;
	private int reasoningType=NONE;
	private Sail sail;
	
	public SesameMemoryBuffer(int reasoningType){
		this.reasoningType=reasoningType;
	}

	public SesameMemoryBuffer(){
		this(NONE);
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

		}
		try{
			buffRepository.initialize();
			return buffRepository.getConnection();
		}
		catch(RepositoryException e){
			logger.warn("could not initialise repository",e);
		}
		return null;
	}

	public Sail getSail(){
		return sail;
	}

	public void loadFromURL(String url,RDFFormat format){
		try{
			InputStream in = openConnection(url, format);
			load(in,url,format);
		}catch(Exception e){
			logger.warn("error loading url ["+url+"]",e);
		}
	}
	
	public void load(InputStream in, String url, RDFFormat format) throws RDFParseException, RepositoryException, IOException {
		load(new InputStreamReader(in),url,format);
	}

	private void load(Reader in, String url,
			RDFFormat format) throws RDFParseException, RepositoryException, IOException {
		RepositoryConnection conn=this.getConnection() ;
		try{
			conn.add(in, url, format);
		}finally{
			in.close();
		}		
	}

	public void loadFromText(String text, String baseURL){
		try{
			String url = ((null!=baseURL)&(baseURL.trim().length()>0))?baseURL.trim():"http://pipes.deri.org/";
			load(new StringReader(text),url, RDFFormat.RDFXML);
		}
		catch (Exception e) {
			logger.warn("problem loading from text",e);
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
				RepositoryConnection repositoryConnection = ((SesameMemoryBuffer)outputBuffer).getConnection();
				RepositoryResult<Statement> repositoryResult = getConnection().getStatements(null, null, null, true);
				List<Statement> statements = repositoryResult.asList();
				if(uriImpl!=null) {
					repositoryConnection.add(statements,uriImpl);
				} else{
					repositoryConnection.add(statements);
				}
			}else if(outputBuffer instanceof XMLStreamBuffer){
				((XMLStreamBuffer)outputBuffer).setStreamSource(toXMLStringBuffer());
			}else{
				logger.warn("the outputBuffer was not a SesameMemoryBuffor or XMLStreamBuffer - cannot stream uri=["+uri+"]");
			}
		}
		catch(RepositoryException e){
			logger.warn("problem streaming to ExecBuffer",e);
		}
	}

	public String toString(){
		return toXMLStringBuffer().toString(); 	
	}

	public StringBuffer toXMLStringBuffer(){
		StringWriter stBuff=new StringWriter();
		try{
			getConnection().export(new RDFXMLPrettyWriter(stBuff));
			return stBuff.getBuffer();
		}
		catch(Exception e){
			logger.warn("could not export to StringBuffer",e);
		}
		return null; 	
	}

	public void stream(OutputStream output){
		stream(output, RDFFormat.RDFXML);
	}

	public void stream(OutputStream output,RDFFormat format){
		try{
			getConnection().export(Rio.createWriter(format, output));
		}
		catch(Exception e){
			logger.warn("problem exportin",e);
		}
	}
}
