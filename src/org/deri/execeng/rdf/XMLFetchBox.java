package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class XMLFetchBox implements Operator {
	final Logger logger = LoggerFactory.getLogger(XMLFetchBox.class);

	String url;
	boolean isExecuted=false;
	PipeParser parser;
	XMLStreamBuffer buffer;
	public XMLFetchBox(PipeParser parser,Element element){
		this.parser=parser;
		initialize(element);
	}
	@Override
	public void execute() {
		buffer = new XMLStreamBuffer(parser,url);
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
	
	public void initialize(Element element){
    	url=XMLUtil.getTextFromFirstSubEleByName(element, "location");	    	
    	if(null==url){
    		parser.log("Error in xml fetchbox");
    		parser.log(element.toString());
    	}
	}
}
