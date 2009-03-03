/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.deri.pipes.condition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.deri.pipes.core.Context;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.model.MultiExecBuffer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author robful
 *
 */
@XStreamAlias("compare")
public class ComparisonCondition implements Condition {
	public static final String CONDITION_EQ = "==";
	public static final String CONDITION_NE = "!=";
	public static final String CONDITION_GT = ">";
	public static final String CONDITION_LT = "<";
	public static final String CONDITION_LTE = "<=";
	public static final String CONDITION_GTE = ">=";
	
	Source leftSource;
	Source rightSource;
	@XStreamAsAttribute
	String comparator;
	/* (non-Javadoc)
	 * @see org.deri.pipes.condition.Condition#isTrue(org.deri.pipes.core.Context)
	 */
	@Override
	public boolean isTrue(Context context) throws Exception {
		ExecBuffer leftResult = leftSource.execute(context);
		ExecBuffer rightResult = rightSource.execute(context);
		String leftResultString = leftResult.toString();
		String rightResultString = rightResult.toString();
		String condition = comparator;
		
		return compare(condition, leftResultString, rightResultString);
	}

	static boolean compare(String condition, String left, String right) {
		if(isNumeric(left) && isNumeric(right)){
			BigDecimal leftDecimal = new BigDecimal(left);
			BigDecimal rightDecimal = new BigDecimal(right);
			return isConditionMet(leftDecimal.compareTo(rightDecimal),condition);
		}
		return isConditionMet(left.compareTo(right),condition);
	}

	/**
	 * @param compareTo
	 * @return
	 */
	static boolean isConditionMet(int result,String condition) {
		if(result < 0){
			return CONDITION_LT.equals(condition)
			|| CONDITION_LTE.equals(condition)
			|| CONDITION_NE.equals(condition);
		}else if(result == 0){
			return CONDITION_EQ.equals(condition) ||
			CONDITION_LTE.equals(condition) ||
			CONDITION_GTE.equals(condition);		
		}else{
			return CONDITION_GT.equals(condition)
			|| CONDITION_GTE.equals(condition)
			|| CONDITION_NE.equals(condition);
			
		}
	}

	/**
	 * @param leftResultString
	 * @return
	 */
	static boolean isNumeric(String string) {
		try{
			new BigDecimal(string);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

}
