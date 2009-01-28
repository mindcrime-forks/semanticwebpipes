package org.deri.execeng.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Pipe {
	final Logger logger = LoggerFactory.getLogger(Pipe.class);
	private String id = null;
	private String name = null;
	private String syntax = null;
	private String config=null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
