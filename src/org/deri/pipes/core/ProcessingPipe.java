package org.deri.pipes.core;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.deri.pipes.model.Operator;

public class ProcessingPipe {
	List<Map<String,String>> parameters;
	List<Operator> code;
	public ExecBuffer execute(PipeContext context) {
		ExecBuffer result = null;
		for(Operator operator : code){
			if(!operator.isExecuted()){
				operator.execute(context);
			}
			result = operator.getExecBuffer();
		}
		return result;
	}

}
