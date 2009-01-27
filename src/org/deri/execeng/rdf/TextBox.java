package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TextBox implements Operator{
	final Logger logger = LoggerFactory.getLogger(TextBox.class);
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String text=null;
	private int format=0;
	public static final int RDFXML=0;
	public static final int SPARQLXML=1;
	PipeParser parser;
	
	public TextBox(PipeParser parser,String text){
		this.parser=parser;
		this.format=RDFXML;
		this.text=text;
	}
	
	public TextBox(PipeParser parser,String text,String format){
		this.parser=parser;
		if(format!=null){
			if(format.equalsIgnoreCase("rdfxml")){
				this.format=RDFXML;
			}
			else if(format.equalsIgnoreCase("sparqlxml")){
				this.format=SPARQLXML;
			}
			else{
				this.format=RDFXML;
			}
		}
		else{
			this.format=RDFXML;
		}
		this.text=text;
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
		switch (format){
		case RDFXML:
			SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer(parser);
			rdfBuffer.loadFromText(text);			
			buffer=rdfBuffer;
			break;
		case SPARQLXML:
			SesameTupleBuffer sparqlXML=new SesameTupleBuffer(parser);
			sparqlXML.loadFromText(text);
			buffer=sparqlXML;
			break;
		}
		isExecuted=true;
	}
}
