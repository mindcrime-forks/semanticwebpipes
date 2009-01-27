package org.deri.execeng.rdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Vector;
import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Operator;
import org.deri.execeng.core.BoxParser;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SimpleMixBox extends AbstractMerge{ 
	final Logger logger = LoggerFactory.getLogger(SimpleMixBox.class);
	
	 public  SimpleMixBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
     }

	 public void execute(){
    	 buffer= new SesameMemoryBuffer(parser);
    	 mergeInputs();
    	 isExecuted=true;
     }     
          
}
