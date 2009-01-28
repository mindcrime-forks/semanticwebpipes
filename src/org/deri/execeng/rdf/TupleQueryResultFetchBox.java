package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class TupleQueryResultFetchBox implements Operator {
	final Logger logger = LoggerFactory.getLogger(TupleQueryResultFetchBox.class);
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String url=null;
	private TupleQueryResultFormat format=TupleQueryResultFormat.SPARQL;
	
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
    
	@Override
	public void initialize(PipeContext context, Element element) {
		setUrl(XMLUtil.getTextFromFirstSubEleByName(element, "location"));
    	
    	if((null!=url)&&url.trim().length()>0){
     	}else{
    		logger.warn("location attibute not set:"+element.toString());
    	}
   		if(element.getAttribute("format")!=null){
			setFormat(element.getAttribute("format"));
		}
		else{	
			setFormat(TupleQueryResultFormat.SPARQL);
		}
	}

	public void setFormat(String fmt) {
		setFormat(formatOf(fmt));
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TupleQueryResultFormat getFormat() {
		return format;
	}

	public void setFormat(TupleQueryResultFormat format) {
		this.format = format;
	}
}
