package org.deri.pipes.core;

import java.util.List;
import java.util.Map;

import org.deri.pipes.model.Operator;

public class ProcessingPipe implements Operator{
	List<Map<String,String>> parameters;
	List<Operator> code;
	public ExecBuffer execute(PipeContext context) {
		ExecBuffer result = null;
		for(Operator operator : code){
			result = operator.execute(context);
		}
		return result;
	}

}
