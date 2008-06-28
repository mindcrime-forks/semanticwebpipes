package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Stream;
import org.deri.execeng.core.BoxParser;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import java.net.URLEncoder;
import info.aduna.lang.FileFormat;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;

public class FetchBox extends RDFBox {
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String url=null;
	private FileFormat format=null;
	public static final int RDFXML=0;
	public static final int SPARQLXML=1;
	
	public FetchBox(String url){
		this.format=RDFFormat.RDFXML;
		this.url=url;
	}
	
	public FetchBox(String url,String format){
		this.format=RDFFormat.valueOf(format);
		if(this.format==null){
			if (TupleQueryResultFormat.SPARQL.getName().equalsIgnoreCase(format)) 
				this.format=TupleQueryResultFormat.SPARQL;
			else if(TupleQueryResultFormat.JSON.getName().equalsIgnoreCase(format))
				this.format=TupleQueryResultFormat.JSON;
			else if(TupleQueryResultFormat.BINARY.getName().equalsIgnoreCase(format))
				this.format=TupleQueryResultFormat.BINARY;
		}
		this.url=url;
	}	
	
	public ExecBuffer getExecBuffer(){
		return buffer;
	}
	
	public void streamming(ExecBuffer outputBuffer){
   	   buffer.streamming(outputBuffer);
    }
	
	public void streamming(ExecBuffer outputBuffer,String context){
	   	   buffer.streamming(outputBuffer,context);
	}
	
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
	public void execute(){
		if(format instanceof RDFFormat){			
			SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer();
			rdfBuffer.loadFromURL(url,(RDFFormat)format);			
			buffer=rdfBuffer;
		}
		else{		
			buffer=new SesameTupleBuffer(url,(TupleQueryResultFormat)format);			
		}
		isExecuted=true;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    public static String endPointFormat(String format){
    	if(format!=null){
			if(format.equalsIgnoreCase("rdfxml")){
				return "application/rdf+xml";
			}
			else if(format.equalsIgnoreCase("sparqlxml")){
				return "application/sparql-results+xml";
			}
			else{
				return "application/rdf+xml";
			}
		}
		else{
			return "application/rdf+xml";
		}
    }
    public static Stream loadStream(Element element){
    	String tmpStr=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	//System.out.println("fetchbox");
    	
    	if(tmpStr!=null)
    		return new FetchBox(tmpStr,element.getAttribute("format"));
    	else{
    		Element tmpEle=XMLUtil.getFirstSubElementByName(element, "sparqlendpoint");
    		//System.out.println("sparqlendpoint");
    		if(tmpEle!=null){
    			try{
    				//System.out.println("try");
		    		String endpoint=XMLUtil.getTextFromFirstSubEleByName(tmpEle,"endpoint");
		    		String defaultgraph="default-graph-uri="+URLEncoder.encode(XMLUtil.getTextFromFirstSubEleByName(tmpEle,"defaultgraph"),"UTF-8");
		    		String query="&query="+URLEncoder.encode(XMLUtil.getTextFromFirstSubEleByName(tmpEle,"query"),"UTF-8");
		    		System.out.println(endpoint+"?"+defaultgraph+query+"&format="+URLEncoder.encode(endPointFormat(element.getAttribute("accept")),"UTF-8"));
		    		return new FetchBox(endpoint+"?"+defaultgraph+query+"&format="+URLEncoder.encode(endPointFormat(element.getAttribute("accept")),"UTF-8"));
    			}
	    		catch(java.io.UnsupportedEncodingException e){	    			
	    		}
    		}
    	}
    	Stream.log.append("Error in fetchbox\n");
    	Stream.log.append(element.toString()+"\n");
    	return null;
    }
}
