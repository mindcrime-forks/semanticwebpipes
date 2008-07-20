package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Box;
import org.deri.execeng.model.Stream;
import org.deri.execeng.utils.XMLUtil;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;

public class XMLFetchBox implements Box {

	String url;
	boolean isExecuted=false;
	public XMLFetchBox(String url){
		this.url=url;
	}
	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public ExecBuffer getExecBuffer() {
		// TODO Auto-generated method stub
		return new XMLStreamBuffer(url);
	}

	@Override
	public boolean isExecuted() {
		// TODO Auto-generated method stub
		return isExecuted;
	}

	@Override
	public void streamming(ExecBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void streamming(ExecBuffer buffer, String context) {
		// TODO Auto-generated method stub

	}
	
	public String getURL(){
		return url;
	}
	
	public static Stream loadStream(Element element){
	    	String tmpStr=XMLUtil.getTextFromFirstSubEleByName(element, "location");
	    	
	    	if(tmpStr!=null)
	    		return new XMLFetchBox(tmpStr);
	    	
	    	Stream.log.append("Error in xml fetchbox\n");
	    	Stream.log.append(element.toString()+"\n");
	    	return null;
	}
}
