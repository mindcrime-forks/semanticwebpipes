package org.deri.pipes.core;

import java.util.List;
import java.util.Map;

import org.deri.pipes.model.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessingPipe implements Operator{
	Logger logger = LoggerFactory.getLogger(ProcessingPipe.class);
	List<Map<String,String>> parameters;
	List<Operator> code;
	public ExecBuffer execute(PipeContext context) throws Exception {
		long startTime = System.currentTimeMillis();
		ExecBuffer result = null;
		for(Operator operator : code){
			result = operator.execute(context);
		}
		long elapsed = System.currentTimeMillis() - startTime;
		logger.info("pipe execution time was "+elapsed+"ms");
		return result;
	}

}
