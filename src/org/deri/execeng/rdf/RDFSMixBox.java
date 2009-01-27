package org.deri.execeng.rdf;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFSMixBox extends AbstractMerge{
	final Logger logger = LoggerFactory.getLogger(RDFSMixBox.class);
	
	 public  RDFSMixBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
     }
     
     public ExecBuffer getExecBuffer(){
    	 return buffer;
     }
     
     public void execute(){
    	 buffer= new SesameMemoryBuffer(parser,SesameMemoryBuffer.RDFS);
    	 mergeInputs();    	 
    	 isExecuted=true;
     }     
}
