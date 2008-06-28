package org.deri.execeng.rdf;

import org.deri.execeng.model.Box;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Stream;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import java.net.URLEncoder;
import org.openrdf.query.resultio.TupleQueryResultFormat;

public class TupleQueryResultFetchBox implements Box {
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String url=null;
	private TupleQueryResultFormat format=null;
	
	
	public TupleQueryResultFetchBox(String url){
		this.format=TupleQueryResultFormat.SPARQL;
		this.url=url;
	}
	
	public TupleQueryResultFetchBox(String url,TupleQueryResultFormat format){
		
		this.format=format;
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
		buffer=new SesameTupleBuffer(url,format);			
		isExecuted=true;
	}
	public static TupleQueryResultFormat formatOf(String format){
		if (TupleQueryResultFormat.SPARQL.getName().equalsIgnoreCase(format)) 
			return TupleQueryResultFormat.SPARQL;
		else if(TupleQueryResultFormat.JSON.getName().equalsIgnoreCase(format))
			return TupleQueryResultFormat.JSON;
		else if(TupleQueryResultFormat.BINARY.getName().equalsIgnoreCase(format))
			return TupleQueryResultFormat.BINARY;
		return null;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    
    public static Stream loadStream(Element element){    	
    	String tmpStr=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	//System.out.println("fetchbox");
    	
    	if(tmpStr!=null)
    		return new TupleQueryResultFetchBox(tmpStr,formatOf(element.getAttribute("format")));
    	/*Element tmpEle=XMLUtil.getFirstSubElementByName(element, "sparqlendpoint");
    		//System.out.println("sparqlendpoint");
    	if(tmpEle!=null){
    		try{
    				//System.out.println("try");
		    		String endpoint=XMLUtil.getTextFromFirstSubEleByName(tmpEle,"endpoint");
		    		String defaultgraph="default-graph-uri="+URLEncoder.encode(XMLUtil.getTextFromFirstSubEleByName(tmpEle,"defaultgraph"),"UTF-8");
		    		String query="&query="+URLEncoder.encode(XMLUtil.getTextFromFirstSubEleByName(tmpEle,"query"),"UTF-8");
		    		TupleQueryResultFormat format=formatOf(element.getAttribute("format"));
		    		return new TupleQueryResultFetchBox(endpoint+"?"+defaultgraph+query+"&format="+
		    				 		URLEncoder.encode(format.getDefaultMIMEType(),"UTF-8"),format);
    		}
	    	catch(java.io.UnsupportedEncodingException e){	    			
	    	
    		}
    	}*/
    	Stream.log.append("Error in fetchbox\n");
    	Stream.log.append(element.toString()+"\n");
    	return null;
    }
}
