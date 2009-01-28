package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class XMLFetchBox implements Operator {
	final Logger logger = LoggerFactory.getLogger(XMLFetchBox.class);

	String url;
	boolean isExecuted=false;
	XMLStreamBuffer buffer;
	
	@Override
	public void execute() {
		buffer = new XMLStreamBuffer(url);
		isExecuted=true;
	}

	@Override
	public ExecBuffer getExecBuffer() {
		return buffer;
	}

	@Override
	public boolean isExecuted() {
		// TODO Auto-generated method stub
		return isExecuted;
	}

	@Override
	public void stream(ExecBuffer buffer) {
		buffer.stream(buffer);
	}

	@Override
	public void stream(ExecBuffer buffer, String context) {
		if(buffer!=null)
			buffer.stream(buffer);
	}
	
	@Override
	public void initialize(PipeContext context, Element element) {
	   	setUrl(XMLUtil.getTextFromFirstSubEleByName(element, "location"));	    	
    	if(null==url){
    		logger.warn("Error in xml fetchbox, missing location: "+element.toString());
    	}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
