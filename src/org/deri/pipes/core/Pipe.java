package org.deri.pipes.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
/**
 * A Semantic Web Pipe!
 * @author robful
 *
 */
public class Pipe implements Operator{
	static Logger logger = LoggerFactory.getLogger(Pipe.class);
	private transient List<Operator> codeWithVariablesExpanded;
	@XStreamAsAttribute
	private String id;
	Parameters parameters;
	List<Operator> code;
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}
	/**
	 * List the parameters to this pipe.
	 * @return
	 */
	public Collection<String> listParameters(){
		return parameters == null?new ArrayList<String>():parameters.list();
	}
	/**
	 * Get the value of the given parameter.
	 * @param key
	 * @return
	 */
	public String getParameter(String key){
		return parameters == null?null:parameters.get(key);
	}
	public void setParameter(String key, String value){
		if(parameters == null){
			parameters = new Parameters();
		}
		parameters.set(key, value);
		this.codeWithVariablesExpanded = null;
	}
	/**
	 * Execute this pipe and return the result.
	 */
	public ExecBuffer execute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();
		if(codeWithVariablesExpanded == null){
			expandVariables(context);
		}
		if(codeWithVariablesExpanded.size()>1){
		logger.info("the pipe has ["+codeWithVariablesExpanded.size()+"] results at root level");
		}
		ExecBuffer result = codeWithVariablesExpanded.size()==1?
				codeWithVariablesExpanded.get(0).execute(context)
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
			return;
		}
		/*
		 * Temporarily hide the parameters and serialize the code.
		 */
		Parameters tmpParameters = this.parameters;
		this.parameters = null;
		String xml = null;
		try{
			xml = context.getEngine().serialize(this);
			
		}finally{
			this.parameters = tmpParameters;
		}
		xml = expandParameters(tmpParameters, xml);
		logger.debug("expanded pipe with variables to: {}",xml);
		Pipe pipe = (Pipe) context.getEngine().parse(xml);
		this.codeWithVariablesExpanded = pipe.code;
		
	}
	private String expandParameters(Parameters tmpParameters,
			String xml) {
		for(String bindingName : tmpParameters.list()){
			String bindingValue = tmpParameters.get(bindingName);
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
	public void addOperator(Operator operator){
		if(code == null){
			code = new ArrayList<Operator>();
		}
		code.add(operator);
		
	}
	/**
	 * TODO: use Context for setting/expanding parameters.
	 * @param newContext
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public ExecBuffer execute(Context context, Map<String, String> params) throws Exception {
		for(String key : params.keySet()){
			this.setParameter(key, params.get(key));
		}
		return this.execute(context);
	}
}
