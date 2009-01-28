package org.deri.execeng.rdf;

import java.util.ArrayList;
import java.util.List;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;

public abstract class AbstractMerge extends RDFBox {
	protected List<String> inputStreams = new ArrayList<String>();
    
	public void addStream(String str){
   	    inputStreams.add(str);
    }
	
	protected void mergeInputs(){
		mergeInputs(buffer);
	}
	
	protected void mergeInputs(ExecBuffer buffer){
		for(int i=0;i<inputStreams.size();i++){
	   		 Operator str=context.getOperatorExecuted(inputStreams.get(i));   
	       	 if (str.getExecBuffer() instanceof SesameMemoryBuffer){
	       		str.stream(buffer);
	       	 }else{
	       		logger.warn("Inappropriate input format, RDF is required!!!");
	       	 }
	   	}
	}
	
	public void initialize(PipeContext context, Element element){
		setContext(context);
  		List<Element> sources=XMLUtil.getSubElementByName(element, "source");
  		for(int i=0;i<sources.size();i++){
     		String opID=context.getPipeParser().getSourceOperatorId(sources.get(i));
     		if (null!=opID){
     			addStream(opID);
     		}
     	}  		
    }
}
