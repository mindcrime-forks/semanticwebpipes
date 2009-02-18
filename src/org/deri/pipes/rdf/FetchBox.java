package org.deri.pipes.rdf;

import org.deri.pipes.core.internals.StringOrSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FetchBox extends RDFBox {
	private transient Logger logger = LoggerFactory.getLogger(FetchBox.class);
	protected StringOrSource location = null;
	
	public void setLocation(String url) {
		this.location = new StringOrSource(url);
	}
	
	abstract void setFormat(String format);

}