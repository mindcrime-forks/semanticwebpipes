package org.deri.execeng.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Pipe {
	final Logger logger = LoggerFactory.getLogger(Pipe.class);
	private String pipeid = null;
	private String pipename = null;
	private String syntax = null;
	private String config=null;

	public String getPipeid() {
		return pipeid;
	}

	public void setPipeid(String pipeid) {
		this.pipeid = pipeid;
	}

	public String getPipename() {
		return pipename;
	}

	public void setPipename(String pipename) {
		this.pipename = pipename;
	}

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}
