package org.deri.execeng.rdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;

import javax.xml.transform.stream.StreamSource;
public class XMLStreamBuffer extends ExecBuffer {
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
		}
		
	}

	@Override
	public void stream(OutputStream output) {
		// TODO Auto-generated method stub
		if(url!=null){
			
		}
		else{
			try {
				(new OutputStreamWriter(output)).write(strBuff.toString(),0,strBuff.length());
			} catch (IOException e) {
				parser.log(e);
			}
		}
	}
}
