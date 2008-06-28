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
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFFetchBox extends RDFBox {
	private boolean isExecuted=false;
	private String url=null;
	private RDFFormat format=null;	
	public RDFFetchBox(String url){
		this.format=RDFFormat.RDFXML;
		this.url=url;
	}
	
	public RDFFetchBox(String url,String format){
		this.format=RDFFormat.valueOf(format);
		this.url=url;
	}	
	
	public ExecBuffer getExecBuffer(){
		return buffer;
	}
		
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
	public void execute(){
		SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer();
		rdfBuffer.loadFromURL(url,(RDFFormat)format);			
		buffer=rdfBuffer;
		isExecuted=true;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    
    public static Stream loadStream(Element element){
    	String tmpStr=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	//System.out.println("fetchbox");
    	
    	if(tmpStr!=null)
    		return new RDFFetchBox(tmpStr,element.getAttribute("format"));
    	
    	Stream.log.append("Error in fetchbox\n");
    	Stream.log.append(element.toString()+"\n");
    	return null;
    }
}
