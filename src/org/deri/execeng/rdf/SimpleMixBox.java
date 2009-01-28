package org.deri.execeng.rdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SimpleMixBox extends AbstractMerge{ 
	final Logger logger = LoggerFactory.getLogger(SimpleMixBox.class);
	
	 public void execute(){
    	 buffer= new SesameMemoryBuffer();
    	 mergeInputs();
    	 isExecuted=true;
     }     
          
}
