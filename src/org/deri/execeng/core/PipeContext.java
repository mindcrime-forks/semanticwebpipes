package org.deri.execeng.core;

import java.util.HashMap;
import java.util.Map;

import org.deri.execeng.model.Operator;
/**
 * Operators belonging to a Pipe.
 * @author rfuller
 *
 */
public class PipeContext {
	Map <String,Operator> operators = new HashMap<String,Operator>();
	PipeParser parser;
	/**
	 * Get the operator having this id.
	 * @param id
	 * @return The operator having the given id, or null if there is no such operator.
	 */
	public Operator getOperator(String id){
		return operators.get(id);
	}
	/**
	 * Add this operator into the context.
	 * @param id
	 * @param operator
	 */
	void addOperator(String id,Operator operator){
		operators.put(id, operator);
	}
	/**
	 * Whether the context contains an operator having this id.
	 * @param id
	 * @return true if there is an operator having this id, false otherwise.
	 */
	public boolean contains(String id) {
		return operators.containsKey(id);
	}
	/**
	 * Set the PipeParser.
	 * @param parser
	 */
	void setPipeParser(PipeParser parser){
		this.parser = parser;
	}
	/**
	 * Get the PipeParser.
	 * @return
	 */
	public PipeParser getPipeParser(){
		return parser;
	}
	/**
	 * Get the named operator having first tested that
	 * the execute() method was called, or executing.
	 * @param id
	 * @return the operator having been executed.
	 */
	public Operator getOperatorExecuted(String id) {
		Operator operator = getOperator(id);
		if (operator != null && !operator.isExecuted()){
    		operator.execute();
    	}
		return operator;
	}
}
