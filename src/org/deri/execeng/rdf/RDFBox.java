package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public abstract class RDFBox implements Operator {
	Logger logger = LoggerFactory.getLogger(RDFBox.class);
	protected SesameMemoryBuffer buffer;
	protected boolean isExecuted=false;
	protected PipeContext context;
	
	public void stream(ExecBuffer outputBuffer){
	   if((buffer!=null)&&(outputBuffer!=null))	
		   buffer.stream(outputBuffer);
	   else{
		   logger.debug("check "+(buffer==null));
	   }
    }
	public void stream(ExecBuffer outputBuffer,String uri){
		if((null!=uri)&&(uri.trim().length()>0)){
			buffer.stream(outputBuffer,uri.trim());
		}else{
			buffer.stream(outputBuffer);
		}
	}
	
	public ExecBuffer getExecBuffer(){
   	 	return buffer;
    }
	
	public boolean isExecuted(){
	   	return isExecuted;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
	/**
	 * Set the pipe context.
	 * @param context
	 */
	void setContext(PipeContext context){
		this.context = context;
	}


}
