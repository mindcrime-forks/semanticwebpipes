package org.deri.execeng.rdf;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.MutableTupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Element;
import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
public class SesameTupleBuffer extends org.deri.execeng.core.ExecBuffer{
	private MutableTupleQueryResult buffer=null;
	private PipeParser parser;
	
	public SesameTupleBuffer(PipeParser parser){
		this.parser=parser;
	}
	
	public SesameTupleBuffer(PipeParser parser,String url,TupleQueryResultFormat format){
		this.parser=parser;
		loadFromURL(url,format);
	}
	
	public SesameTupleBuffer(PipeParser parser,TupleQueryResult buffer){
		this.parser=parser;
		copyBuffer(buffer);
	}
	
	public void copyBuffer(TupleQueryResult buffer){
		try{
			this.buffer=new MutableTupleQueryResult(buffer);
		}
		catch(org.openrdf.query.QueryEvaluationException  e){							
		}
	}
	
	public TupleQueryResult getTupleQueryResult(){		
		try{
			return buffer.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadFromURL(String url,TupleQueryResultFormat format){
		if (url==null) buffer=null;
		try{
	    	HttpURLConnection urlConn=(HttpURLConnection)((new URL(url.trim())).openConnection());
			urlConn.setRequestProperty("Accept", format.getDefaultMIMEType());
			urlConn.connect();
		    copyBuffer(QueryResultIO.parse(urlConn.getInputStream(), format));
	    }
    	catch(org.openrdf.query.resultio.QueryResultParseException e){
    		parser.log(e);
		}
		catch(org.openrdf.query.TupleQueryResultHandlerException e){
			parser.log(e);
		}
		catch(org.openrdf.query.resultio.UnsupportedQueryResultFormatException e){
			parser.log(e);
		}
		catch (java.io.IOException e) {
			parser.log(e);
		}
	}
	
	public void loadFromText(String text){
		if (text==null) buffer=null;
		try{
			copyBuffer(QueryResultIO.parse(new ByteArrayInputStream(text.trim().getBytes()), TupleQueryResultFormat.SPARQL));
		}
		catch(org.openrdf.query.resultio.QueryResultParseException e){
    		parser.log(e);
		}
		catch(org.openrdf.query.TupleQueryResultHandlerException e){
			parser.log(e);
		}
		catch(org.openrdf.query.resultio.UnsupportedQueryResultFormatException e){
			parser.log(e);
		}
		catch (java.io.IOException e) {
			parser.log(e);
		}
	}
		
	public String toString(){
		java.io.ByteArrayOutputStream strOut=new java.io.ByteArrayOutputStream();
		stream(strOut);		
		return strOut.toString();		
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		// TODO Auto-generated method stub
		if(outputBuffer instanceof XMLStreamBuffer){
			((XMLStreamBuffer)outputBuffer).setStreamSource(new StringBuffer(toString()));
		}
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		stream(outputBuffer);
	}

	@Override
	public void stream(OutputStream output) {
		try{
			QueryResultIO.write(buffer.clone(), TupleQueryResultFormat.SPARQL, output);
		}
		catch(org.openrdf.query.QueryEvaluationException  e){							
		}
		catch(org.openrdf.query.TupleQueryResultHandlerException e){							
		}
		catch(org.openrdf.query.resultio.UnsupportedQueryResultFormatException e){							
		}
		catch (java.io.IOException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
		catch (CloneNotSupportedException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
	}
}
