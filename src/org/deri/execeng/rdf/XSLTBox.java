package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Stream;
import org.w3c.dom.Element;
import java.util.Hashtable;
import java.util.Enumeration;

import java.io.File;
import org.deri.execeng.utils.*;
public class XSLTBox extends RDFBox {
	XMLFetchBox xmlStream,xslStream;
	String baseURI;
	private boolean isExecuted=false;
	public XSLTBox(XMLFetchBox xmlStream,XMLFetchBox xslStream){
		System.out.println("new XSLT");
		this.xmlStream=xmlStream;
		baseURI=xmlStream.getURL();
		this.xslStream=xslStream;
	}
	
	
	@Override
	public void execute() {
		buffer=new SesameMemoryBuffer();
		if(!isExecuted){
			System.out.println("execute XSLT");
			String text=XSLTUtil.transform(((XMLStreamBuffer)xmlStream.getExecBuffer()).getStreamSource(), 
												((XMLStreamBuffer)xslStream.getExecBuffer()).getStreamSource());
			buffer.loadFromText(text, baseURI);
			isExecuted=true;
		}
	}
	
	public static Stream loadStream(Element element){
    	XMLFetchBox xmlStr=(XMLFetchBox)BoxParserImplRDF.loadStream(XMLUtil.getFirstSubElement(
    												XMLUtil.getFirstSubElementByName(element, "xmlsource")));
    	XMLFetchBox xslStr=(XMLFetchBox)BoxParserImplRDF.loadStream(XMLUtil.getFirstSubElement(
    												XMLUtil.getFirstSubElementByName(element, "xslsource")));
    	
    	if((xmlStr!=null)&&(xslStr!=null))
    		return new XSLTBox(xmlStr,xslStr);
    	
    	Stream.log.append("Error in fetchbox\n");
    	Stream.log.append(element.toString()+"\n");
    	return null;
    }	
}
