package org.deri.execeng.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class XMLStreamBuffer extends ExecBuffer {
	final Logger logger = LoggerFactory.getLogger(XMLStreamBuffer.class);
	String url=null;
	StringBuffer strBuff=null;
	PipeParser parser;
	
	public XMLStreamBuffer(PipeParser parser){
		this.parser=parser;
	}
	
	public XMLStreamBuffer(PipeParser parser,String url){
		this.parser=parser;
		this.url= url;
	}
	
	public XMLStreamBuffer(PipeParser parser,StringBuffer strBuff){
		this.parser=parser;
		this.strBuff=strBuff;
	}
	
	public StreamSource getStreamSource(){
		if(url!=null) 
			return new StreamSource(url);
		if (strBuff!=null) 
		  return new StreamSource(new java.io.StringReader(strBuff.toString()));
		return null;
	}
	
	public void setStreamSource(StringBuffer strBuff){
		this.strBuff=strBuff;
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		stream(outputBuffer,null);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		if(outputBuffer instanceof SesameMemoryBuffer){
			if(url!=null) 
				((SesameMemoryBuffer)outputBuffer).loadFromURL(url, RDFFormat.RDFXML);
			else	
				((SesameMemoryBuffer)outputBuffer).loadFromText(strBuff.toString(),null);
		}
		else if(outputBuffer instanceof SesameTupleBuffer){
			if(url!=null) 
				((SesameTupleBuffer)outputBuffer).loadFromURL(url, TupleQueryResultFormat.SPARQL);
			else	
				((SesameTupleBuffer)outputBuffer).loadFromText(strBuff.toString());
		}else{
			logger.warn("cannot stream outputBuffer which is not a SesameMemoryBuffer or SesameTupleBuffer");
		}
		
	}

	@Override
	public void stream(OutputStream output) {
		// TODO Auto-generated method stub
		if(url!=null){
			try {
				InputStream in = openConnection(url, RDFFormat.RDFXML);
				try{
					IOUtils.copy(in, output);
				}finally{
					in.close();
				}
			} catch (IOException e) {
				logger.warn("Couldn't read from url=["+url+"]",e);
			}
			
		}else{
			try {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);
				outputStreamWriter.write(strBuff.toString());
				outputStreamWriter.flush();
			} catch (IOException e) {
				parser.log(e);
			}
		}
	}
}
