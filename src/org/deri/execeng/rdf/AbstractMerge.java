package org.deri.execeng.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;

public abstract class AbstractMerge extends RDFBox {
	protected List<String> inputStreams = new ArrayList<String>();
    protected boolean isExecuted=false;
    protected PipeParser parser;
    
	public void addStream(String str){
   	    inputStreams.add(str);
    }
	
	public void mergeInputs(){
		mergeInputs(buffer);
	}
	
	public void mergeInputs(ExecBuffer buffer){
		for(int i=0;i<inputStreams.size();i++){
	   		 Operator str=parser.getOpByID(inputStreams.get(i));   
	       	 if(!str.isExecuted()) str.execute();
	       	 if (str.getExecBuffer() instanceof SesameMemoryBuffer){
	       		str.stream(buffer);
	       	 }else{
	       		parser.log("Inappropriate input format, RDF is required!!!");
	       	 }
	   	}
	}
	
	protected void initialize(Element element){
  		List<Element> sources=XMLUtil.getSubElementByName(element, "source");
  		for(int i=0;i<sources.size();i++){
     		String opID=parser.getSource(sources.get(i));
     		if (null!=opID){
     			addStream(opID);
     		}
     	}  		
    } 
}
