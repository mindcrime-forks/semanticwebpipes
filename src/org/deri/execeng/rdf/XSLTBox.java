package org.deri.execeng.rdf;

import javax.xml.transform.stream.StreamSource;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
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
	XMLStreamBuffer buffer;
	private PipeContext context;
	
	@Override
	public void execute() {
		if((null!=xmlStrID)&&(null!=xslStrID)){			
			StreamSource xmlSrc=executeXMLOp(xmlStrID);
			StreamSource xslSrc=executeXMLOp(xslStrID);
			if((xmlSrc!=null)&&(xslSrc!=null)){
				buffer=new XMLStreamBuffer();	
			    buffer.setStreamSource(XSLTUtil.transform(xmlSrc,xslSrc));				
			}
	    }
		isExecuted=true;
	}
	
    private StreamSource executeXMLOp(String strID){
    	
    	Operator xmlOp=context.getOperatorExecuted(strID);
		ExecBuffer xmlBuff=xmlOp.getExecBuffer();
		
		StreamSource xmlSrc=null;
		if(xmlBuff instanceof XMLStreamBuffer) 
			xmlSrc=((XMLStreamBuffer)xmlBuff).getStreamSource();
		if((xmlBuff instanceof SesameTupleBuffer)||(xmlBuff instanceof SesameTupleBuffer)){ 
			XMLStreamBuffer tmpBuff= new XMLStreamBuffer();
			xmlBuff.stream(tmpBuff);
			xmlSrc=tmpBuff.getStreamSource();
		}
		
		return xmlSrc;
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
		logger.error("not implemented");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stream(ExecBuffer buffer, String context) {
		logger.error("not implemented");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(PipeContext context, Element element) {
		this.setContext(context);
		xmlStrID=context.getPipeParser().getSourceOperatorId(XMLUtil.getFirstSubElement(
				XMLUtil.getFirstSubElementByName(element, "xmlsource")));
		xslStrID=context.getPipeParser().getSourceOperatorId(XMLUtil.getFirstSubElement(
				XMLUtil.getFirstSubElementByName(element, "xslsource")));
		if (null==xmlStrID){
			logger.warn("<sourcelist> element must be set !!!");
			//TODO : Handling error of lacking xml source for XSLT transformation 	
		}
		if (null==xslStrID){
			logger.warn("<sourcelist> element must be set !!!");
			//TODO : Handling error of lacking xml source for XSLT transformation 	
		}
	}

	public void setContext(PipeContext context) {
		this.context = context;
	}	
}
