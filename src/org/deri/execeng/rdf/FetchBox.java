package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Stream;
import org.deri.execeng.core.BoxParser;
import org.w3c.dom.Element;
import java.net.URLEncoder;
public class FetchBox extends RDFBox {
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String url=null;
	private int acceptHeader=0;
	public static final int RDFXML=0;
	public static final int SPARQLXML=1;
	
	public FetchBox(String url){
		this.acceptHeader=RDFXML;
		this.url=url;
	}
	
	public FetchBox(String url,String acceptHeader){
		if(acceptHeader!=null){
			if(acceptHeader.equalsIgnoreCase("rdfxml")){
				this.acceptHeader=RDFXML;
			}
			else if(acceptHeader.equalsIgnoreCase("sparqlxml")){
				this.acceptHeader=SPARQLXML;
			}
			else{
				this.acceptHeader=RDFXML;
			}
		}
		else{
			this.acceptHeader=RDFXML;
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
		switch (acceptHeader){
		case RDFXML:
			SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer();
			rdfBuffer.loadFromURL(url);			
			buffer=rdfBuffer;
			break;
		case SPARQLXML:
			buffer=new SesameTupleBuffer(url);
			break;
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
    	String tmpStr=BoxParser.getTextFromFirstSubEleByName(element, "location");
    	//System.out.println("fetchbox");
    	
    	if(tmpStr!=null)
    		return new FetchBox(tmpStr,element.getAttribute("accept"));
    	else{
    		Element tmpEle=BoxParser.getFirstSubElementByName(element, "sparqlendpoint");
    		//System.out.println("sparqlendpoint");
    		if(tmpEle!=null){
    			try{
    				//System.out.println("try");
		    		String endpoint=BoxParser.getTextFromFirstSubEleByName(tmpEle,"endpoint");
		    		String defaultgraph="default-graph-uri="+URLEncoder.encode(BoxParser.getTextFromFirstSubEleByName(tmpEle,"defaultgraph"),"UTF-8");
		    		String query="&query="+URLEncoder.encode(BoxParser.getTextFromFirstSubEleByName(tmpEle,"query"),"UTF-8");
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
