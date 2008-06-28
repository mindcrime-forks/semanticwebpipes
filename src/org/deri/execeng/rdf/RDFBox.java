package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;

public abstract class RDFBox implements org.deri.execeng.model.Box {
	protected SesameMemoryBuffer buffer;
	protected boolean isExecuted=false;
	
	public void streamming(ExecBuffer outputBuffer){
	   if((buffer!=null)&&(outputBuffer!=null))	
		   buffer.streamming(outputBuffer);
	   else{
		   System.out.println("check"+(buffer==null));
	   }
    }
	public void streamming(ExecBuffer outputBuffer,String uri){
	   	   buffer.streamming(outputBuffer,uri);
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
