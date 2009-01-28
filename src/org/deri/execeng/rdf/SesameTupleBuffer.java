package org.deri.execeng.rdf;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.deri.execeng.core.ExecBuffer;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.MutableTupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SesameTupleBuffer extends ExecBuffer{
	final Logger logger = LoggerFactory.getLogger(SesameTupleBuffer.class);
	private MutableTupleQueryResult buffer=null;

	public SesameTupleBuffer(){
	}

	public SesameTupleBuffer(String url,TupleQueryResultFormat format){
		loadFromURL(url,format);
	}

	public SesameTupleBuffer(TupleQueryResult buffer){
		copyBuffer(buffer);
	}

	public void copyBuffer(TupleQueryResult buffer){
		try{
			this.buffer=new MutableTupleQueryResult(buffer);
		}
		catch(org.openrdf.query.QueryEvaluationException  e){		
			logger.warn("could not copy buffer",e);
		}
	}

	public TupleQueryResult getTupleQueryResult(){		
		try{
			return buffer.clone();
		}catch(CloneNotSupportedException e){
			logger.warn("Clone was not supported for "+buffer.getClass(),e);
			return null;
		}
	}


	public void loadFromText(String text){
		if (text==null) {
			buffer=null;
		}
		try{
			copyBuffer(QueryResultIO.parse(new ByteArrayInputStream(text.trim().getBytes()), TupleQueryResultFormat.SPARQL));
		}
		catch(Exception e){
			logger.warn("problem loading from text",e);
		}
	}

	public void loadFromURL(String url,TupleQueryResultFormat format){
		if (url==null) buffer=null;
		try{
			InputStream in = openConnection(url, format);
			try{
				copyBuffer(QueryResultIO.parse(in, format));
			}finally{
				in.close();
			}
		}
		catch(Exception e){
			logger.warn("problem loading from url ["+url+"]",e);
		}
	}


	public String toString(){
		java.io.ByteArrayOutputStream strOut=new java.io.ByteArrayOutputStream();
		stream(strOut);		
		return strOut.toString();		
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		stream(outputBuffer,(String)null);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		//TODO: why is context ignored.
		if(outputBuffer instanceof XMLStreamBuffer){
			((XMLStreamBuffer)outputBuffer).setStreamSource(new StringBuffer(toString()));
		}else{
			logger.warn("Cannot stream outputBuffer which is not an XMLStreamBuffer");
		}
	}

	@Override
	public void stream(OutputStream output) {
		try{
			QueryResultIO.write(buffer.clone(), TupleQueryResultFormat.SPARQL, output);
		}catch(Exception e){
			logger.warn("sreaming error",e);
		}
	}
}
