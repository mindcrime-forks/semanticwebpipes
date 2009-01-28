package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFFetchBox extends RDFBox {
	final Logger logger = LoggerFactory.getLogger(RDFFetchBox.class);
	private String url=null;
	private RDFFormat format=null;		
	
	public void execute(){
		SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer();
		rdfBuffer.loadFromURL(url,format);			
		buffer=rdfBuffer;
		isExecuted=true;
	}
    

	@Override
	public void initialize(PipeContext context, Element element) {
    	setUrl(XMLUtil.getTextFromFirstSubEleByName(element, "location"));
    	
    	if((null==url)&&(url.trim().length()==0)){
    		logger.warn("Missing location attribute for element "+element);
    	}
    	String attrFormat = element.getAttribute("format");
    	setFormat(attrFormat);
	}


	public void setFormat(String attrFormat) {
		if(null==attrFormat){
    		logger.info("No format given, assuming rdfxml");
			setFormat(RDFFormat.RDFXML);
		}else{	
			setFormat(RDFFormat.valueOf(attrFormat));
		}
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public RDFFormat getFormat() {
		return format;
	}


	public void setFormat(RDFFormat format) {
		this.format = format;
	}
}
