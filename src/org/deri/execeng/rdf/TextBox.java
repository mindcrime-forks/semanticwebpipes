package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
public class TextBox implements Operator{
	private static final String SPARQL_FORMAT = "sparqlxml";
	private static final String RDFXML_FORMAT = "rdfxml";
	final static Logger logger = LoggerFactory.getLogger(TextBox.class);
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String text=null;
	private int format=0;
	public static final int RDFXML=0;
	public static final int SPARQLXML=1;
	
	public TextBox(String text){
		this(text,null);
	}
	
	public TextBox(String text,String format){
		this.text=text;
		this.format = parseFormat(format);
	}	
	
	private int parseFormat(String formatStr) {
		if(formatStr==null){
			logger.debug("format not specified, using default format "+RDFXML_FORMAT);
			return RDFXML;
		}
		if(formatStr.equalsIgnoreCase(RDFXML_FORMAT)){
			return RDFXML;
		}else if(formatStr.equalsIgnoreCase(SPARQL_FORMAT)){
			return SPARQLXML;
		}else{
			logger.warn("unknown format ["+formatStr+"], using"+RDFXML_FORMAT);
			return RDFXML;
		}
	}

	public ExecBuffer getExecBuffer(){
		return buffer;
	}
	
	public void stream(ExecBuffer outputBuffer){
   	   buffer.stream(outputBuffer);
    }
	
	public void stream(ExecBuffer outputBuffer,String context){
	   	   buffer.stream(outputBuffer,context);
	}
	
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
	public void execute(){
		ExecBuffer execBuffer = newExecBuffer(format);
		// execBuffer.loadFromText(text); // would be nice?			
		if(execBuffer instanceof SesameMemoryBuffer){
			((SesameMemoryBuffer)execBuffer).loadFromText(text);
		}else if(execBuffer instanceof SesameTupleBuffer){
			((SesameTupleBuffer)execBuffer).loadFromText(text);
		}else{
			throw new RuntimeException("Wrong buffer, expected SesameMemoryBuffer or SesameTupleBuffer not "+execBuffer.getClass());
		}
		buffer=execBuffer;
		isExecuted=true;
	}
	
	static ExecBuffer newExecBuffer(int format){
		switch (format){
		case RDFXML:
			return new SesameMemoryBuffer();
		case SPARQLXML:
			return new SesameTupleBuffer();
		default:
			logger.warn("unexpected format ["+format+"] using rdfxml");
			return new SesameMemoryBuffer();
		}

	}

	@Override
	public void initialize(PipeContext context, Element element) {
		// nothing to do here?
	}
}
