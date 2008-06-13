package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Box;
public class TextBox implements Box{
	private ExecBuffer buffer=null;
	private boolean isExecuted=false;
	private String text=null;
	private int format=0;
	public static final int RDFXML=0;
	public static final int SPARQLXML=1;
	
	public TextBox(String text){
		this.format=RDFXML;
		this.text=text;
	}
	
	public TextBox(String text,String format){
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
	
	public void streamming(ExecBuffer outputBuffer){
   	   buffer.streamming(outputBuffer);
    }
	
	public void streamming(ExecBuffer outputBuffer,String context){
	   	   buffer.streamming(outputBuffer,context);
	}
	
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	public void execute(){
		switch (format){
		case RDFXML:
			SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer();
			rdfBuffer.loadFromText(text);			
			buffer=rdfBuffer;
			break;
		case SPARQLXML:
			SesameTupleBuffer sparqlXML=new SesameTupleBuffer();
			sparqlXML.loadFromText(text);
			buffer=sparqlXML;
			break;
		}
		isExecuted=true;
	}
}
