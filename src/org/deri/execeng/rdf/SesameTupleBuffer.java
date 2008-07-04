package org.deri.execeng.rdf;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.deri.execeng.core.ExecBuffer;
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
import org.deri.execeng.model.Box;
import org.deri.execeng.utils.XMLUtil;
public class SesameTupleBuffer extends org.deri.execeng.core.ExecBuffer{
	private MutableTupleQueryResult buffer=null;
	
	public SesameTupleBuffer(){		
	}
	
	public SesameTupleBuffer(String url,TupleQueryResultFormat format){
		loadFromURL(url,format);
	}
	
	public SesameTupleBuffer(TupleQueryResult buffer){
		copyBuffer(buffer);
		//System.out.println("SesameTupleBuffer \n"+toString());
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
		}
		catch(org.openrdf.query.TupleQueryResultHandlerException e){							
		}
		catch(org.openrdf.query.resultio.UnsupportedQueryResultFormatException e){							
		}
		catch (java.io.IOException e) {
			ExecBuffer.log.append(e.toString()+"\n");
		}
	}
	
	public void loadFromText(String text){
		if (text==null) buffer=null;
		try{
			copyBuffer(QueryResultIO.parse(new ByteArrayInputStream(text.trim().getBytes()), TupleQueryResultFormat.SPARQL));
		}
		catch(java.io.IOException e){	
		}
		catch(org.openrdf.query.resultio.QueryResultParseException e){							
		}
		catch(org.openrdf.query.TupleQueryResultHandlerException e){							
		}
		catch(org.openrdf.query.resultio.UnsupportedQueryResultFormatException e){							
		}
	}
	
	public void loadFromSelect(Element element){	
		SesameMemoryBuffer tmpBuffer=new SesameMemoryBuffer();
		java.util.ArrayList<Element> sources=XMLUtil.getSubElementByName(element, "source");
    	String query=XMLUtil.getTextFromFirstSubEleByName(element, "query");
    	for(int i=0;i<sources.size();i++){
    		Element tmpEle=XMLUtil.getFirstSubElement((Element)(sources.get(i)));
    		Stream tmpStream=null;
 			if(tmpEle==null)
 				tmpStream= new TextBox(XMLUtil.getTextData(sources.get(i)));
 			else
 				tmpStream=BoxParserImplRDF.loadStream(tmpEle);  	
     	   if(tmpStream instanceof Box) 
 			 if(!((Box)tmpStream).isExecuted()) ((Box)tmpStream).execute();
     	   if(sources.get(i).getAttribute("uri")==null)
     		   tmpStream.streamming(tmpBuffer);
     	   else{
     		   if(sources.get(i).getAttribute("uri").trim().length()>0)
     		   ((Box)tmpStream).streamming(tmpBuffer,sources.get(i).getAttribute("uri"));
     		   else tmpStream.streamming(tmpBuffer);
     	   }
        }
    	try{
    		copyBuffer((tmpBuffer.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query)).evaluate());
    	}
         catch(MalformedQueryException e){ 
      	   log.append(e.toString()+"\n");
         }
         catch(QueryEvaluationException e){
      	   log.append(e.toString()+"\n");
         }
         catch(RepositoryException e){
      	   log.append(e.toString()+"\n");
         }
	}
	
	public void streamming(org.deri.execeng.core.ExecBuffer outputBuffer){
		
	}
	
	public void streamming(org.deri.execeng.core.ExecBuffer outputBuffer,String context){
		
	}

	public void toOutputStream(java.io.OutputStream output){
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
	
	public String toString(){
		java.io.ByteArrayOutputStream strOut=new java.io.ByteArrayOutputStream();
		toOutputStream(strOut);		
		return strOut.toString();		
	}
}
