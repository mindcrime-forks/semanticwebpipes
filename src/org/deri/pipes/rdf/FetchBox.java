package org.deri.pipes.rdf;

import org.deri.pipes.core.PipeContext;
import org.deri.pipes.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public abstract class FetchBox extends RDFBox {
	Logger logger = LoggerFactory.getLogger(FetchBox.class);
	protected String location = null;

	public FetchBox() {
		super();
	}

	public void initialize(PipeContext context, Element element) {
		setContext(context);
		setLocation(XMLUtil.getTextFromFirstSubEleByName(element, "location"));
		if((null==location)||(location.trim().length()==0)){
			logger.warn("location missing for FetchBox "+element);
		}
		setFormat(element.getAttribute("format"));
		
		
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String url) {
		this.location = url;
	}
	
	abstract void setFormat(String format);

}