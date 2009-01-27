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
public class SplitBox implements Operator{ 
	final Logger logger = LoggerFactory.getLogger(SplitBox.class);
	 ExecBuffer buffer;
	 String inputOpID;
	 protected boolean isExecuted=false;
	 protected PipeParser parser;
	 public  SplitBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
     }

	@Override
	public void execute() {
		if (!parser.getOpByID(inputOpID).isExecuted()) parser.getOpByID(inputOpID).execute();
		buffer=parser.getOpByID(inputOpID).getExecBuffer();
	}

	@Override
	public ExecBuffer getExecBuffer() {
		return buffer;
	}

	@Override
	public boolean isExecuted() {
		return isExecuted;
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		if (buffer!=null)
			buffer.stream(outputBuffer);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		if (buffer!=null)
			buffer.stream(outputBuffer,context);
	}

	private void initialize(Element element){
        Element inputSrc =XMLUtil.getFirstSubElement(element);
      	inputOpID=parser.getSource(inputSrc);
      	if (null==inputOpID){
      		parser.log("<split> element must contain a sub element or have REFID attribute !!!");
      		//TODO : Handling error of lacking OWL data source 	
      	}
	}
          
}
