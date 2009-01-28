package org.deri.execeng.rdf;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private PipeContext context;


	@Override
	public void execute() {
		buffer=context.getOperatorExecuted(inputOpID).getExecBuffer();
		isExecuted = true;
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

	public void initialize(PipeContext context,Element element){
		this.context = context;
        Element inputSrc =XMLUtil.getFirstSubElement(element);
      	inputOpID=context.getPipeParser().getSourceOperatorId(inputSrc);
      	if (null==inputOpID){
      		logger.warn("<split> element must contain a sub element or have REFID attribute !!!");
      		//TODO : Handling error of lacking OWL data source 	
      	}
	}
          
}
