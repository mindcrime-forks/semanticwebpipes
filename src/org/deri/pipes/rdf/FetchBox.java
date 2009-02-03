package org.deri.pipes.rdf;

import org.deri.pipes.core.PipeContext;
import org.deri.pipes.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class FetchBox extends RDFBox {
	private transient Logger logger = LoggerFactory.getLogger(FetchBox.class);
	protected String location = null;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String url) {
		this.location = url;
	}
	
	abstract void setFormat(String format);

}