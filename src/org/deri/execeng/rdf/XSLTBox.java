package org.deri.execeng.rdf;

import javax.xml.transform.stream.StreamSource;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.deri.execeng.utils.XSLTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
public class XSLTBox implements Operator {
	final Logger logger = LoggerFactory.getLogger(XSLTBox.class);
	String xmlStrID,xslStrID;
	private boolean isExecuted=false;
	PipeParser parser;
	XMLStreamBuffer buffer;
	public XSLTBox(PipeParser parser,Element element){
		this.parser=parser;
		initialize(element);
	}
	
	@Override
	public void execute() {
		if((null!=xmlStrID)&&(null!=xslStrID)){			
			StreamSource xmlSrc=executeXMLOp(xmlStrID);
			StreamSource xslSrc=executeXMLOp(xslStrID);
			if((xmlSrc!=null)&&(xslSrc!=null)){
				buffer=new XMLStreamBuffer(parser);	
			    buffer.setStreamSource(XSLTUtil.transform(xmlSrc,xslSrc));				
			}
	    }
		isExecuted=true;
	}
	
    private StreamSource executeXMLOp(String strID){
    	
    	Operator xmlOp=parser.getOpByID(strID);
		if(!xmlOp.isExecuted())   xmlOp.execute();
		ExecBuffer xmlBuff=xmlOp.getExecBuffer();
		
		StreamSource xmlSrc=null;
		if(xmlBuff instanceof XMLStreamBuffer) 
			xmlSrc=((XMLStreamBuffer)xmlBuff).getStreamSource();
		if((xmlBuff instanceof SesameTupleBuffer)||(xmlBuff instanceof SesameTupleBuffer)){ 
			XMLStreamBuffer tmpBuff= new XMLStreamBuffer(parser);
			xmlBuff.stream(tmpBuff);
			xmlSrc=tmpBuff.getStreamSource();
		}
		
		return xmlSrc;
    }
    
	public void initialize(Element element){
		xmlStrID=parser.getSource(XMLUtil.getFirstSubElement(
						XMLUtil.getFirstSubElementByName(element, "xmlsource")));
    	xslStrID=parser.getSource(XMLUtil.getFirstSubElement(
    					XMLUtil.getFirstSubElementByName(element, "xslsource")));
    	if (null==xmlStrID){
      		parser.log("<sourcelist> element must be set !!!");
      		//TODO : Handling error of lacking xml source for XSLT transformation 	
      	}
    	if (null==xslStrID){
      		parser.log("<sourcelist> element must be set !!!");
      		//TODO : Handling error of lacking xml source for XSLT transformation 	
      	}
    }

	@Override
	public ExecBuffer getExecBuffer() {
		// TODO Auto-generated method stub
		return buffer;
	}



	@Override
	public boolean isExecuted() {
		return isExecuted;
	}

	@Override
	public void stream(ExecBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stream(ExecBuffer buffer, String context) {
		// TODO Auto-generated method stub
		
	}	
}
