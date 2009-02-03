package org.deri.pipes.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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