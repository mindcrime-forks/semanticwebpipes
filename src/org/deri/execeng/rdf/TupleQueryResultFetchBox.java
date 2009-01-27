package org.deri.execeng.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deri.execeng.model.Operator;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Stream;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import java.net.URLEncoder;
import org.openrdf.query.resultio.TupleQueryResultFormat;

public class TupleQueryResultFetchBox implements Operator {
	final Logger logger = LoggerFactory.getLogger(TupleQueryResultFetchBox.class);
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String url=null;
	private TupleQueryResultFormat format=null;
	private PipeParser parser;
	
	public TupleQueryResultFetchBox(PipeParser parser,Element element){
		this.parser=parser;
		initialize(element);
	}
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
	
	public void stream(ExecBuffer outputBuffer){
   	   buffer.stream(outputBuffer);
    }
	
	public void stream(ExecBuffer outputBuffer,String context){
	   	   buffer.stream(outputBuffer,context);
	}
	
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
	public void execute(){				
		buffer=new SesameTupleBuffer(parser,url,format);			
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
    
    private void initialize(Element element){    	
    	url=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	
    	if((null!=url)&&url.trim().length()>0){
    		if(element.getAttribute("format")!=null)
    			format=formatOf(element.getAttribute("format"));
    		else	
    			format=TupleQueryResultFormat.SPARQL;    		
    	}
    	parser.log("Error in Fetching SPARQL Result");
    	parser.log(element.toString());    	
    }
}
