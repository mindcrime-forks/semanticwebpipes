package org.deri.pipes.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Pipe implements Operator{
	private transient Logger logger = LoggerFactory.getLogger(Pipe.class);
	private transient List<Operator> codeWithVariablesExpanded;
	@XStreamAsAttribute
	private String id;
	List<Map<String,String>> parameters;
	List<Operator> code;
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}
	/**
	 * Execute this pipe and return the result.
	 */
	public ExecBuffer execute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();
		if(codeWithVariablesExpanded == null){
			expandVariables(context);
		}
		ExecBuffer result = codeWithVariablesExpanded.size()>1?
				codeWithVariablesExpanded.get(1).execute(context)
				:context.getEngine().execute(codeWithVariablesExpanded,context);
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info("pipe execution time was "+elapsed+"ms");
		return result;
	}
	/**
	 * 
	 */
	private synchronized void expandVariables(Context context) {
		if(parameters == null || parameters.size() == 0){
			codeWithVariablesExpanded = code;
		}
		/*
		 * Temporarily hide the parameters and serialize the code.
		 */
		List<Map<String,String>> tmpParameters = this.parameters;
		this.parameters = null;
		String xml = null;
		try{
			xml = context.getEngine().serialize(this);
			
		}finally{
			this.parameters = tmpParameters;
		}
		xml = expandParameters(tmpParameters, xml);
		Pipe pipe = (Pipe) context.getEngine().parse(xml);
		this.codeWithVariablesExpanded = pipe.code;
		
	}
	private String expandParameters(List<Map<String, String>> tmpParameters,
			String xml) {
		for(Map<String,String> parameterMap : tmpParameters){
			String bindingName = parameterMap.get("id");
			String bindingValue = parameterMap.get("default");
			xml=xml.replace("${"+bindingName+"}",bindingValue);
		    	   try {
					xml=xml.replace(URLEncoder.encode("${" + bindingName + "}","UTF-8"),
												URLEncoder.encode(bindingValue,"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.warn("UTF-8 support is missing, though required of the jvm specification");
				}						
		}
		return xml;
	}
}
