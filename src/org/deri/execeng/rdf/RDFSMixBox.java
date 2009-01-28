package org.deri.execeng.rdf;
import org.deri.execeng.core.ExecBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFSMixBox extends AbstractMerge{
	final Logger logger = LoggerFactory.getLogger(RDFSMixBox.class);
          
     public void execute(){
    	 buffer= new SesameMemoryBuffer(SesameMemoryBuffer.RDFS);
    	 mergeInputs();    	 
    	 isExecuted=true;
     }     
}
