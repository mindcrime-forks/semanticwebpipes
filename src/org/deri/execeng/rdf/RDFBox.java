package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;

public abstract class RDFBox implements org.deri.execeng.model.Operator {
	protected SesameMemoryBuffer buffer;
	protected boolean isExecuted=false;
	
	public void stream(ExecBuffer outputBuffer){
	   if((buffer!=null)&&(outputBuffer!=null))	
		   buffer.stream(outputBuffer);
	   else{
		   //System.out.println("check"+(buffer==null));
	   }
    }
	public void stream(ExecBuffer outputBuffer,String uri){
		   if((null!=uri)&&(uri.trim().length()>0))
	   	    buffer.stream(outputBuffer,uri.trim());
		   else
			   buffer.stream(outputBuffer);
	}
	
	public org.deri.execeng.core.ExecBuffer getExecBuffer(){
   	 	return buffer;
    }
	
	public boolean isExecuted(){
	   	return isExecuted;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
}
