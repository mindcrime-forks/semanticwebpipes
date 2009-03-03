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

import org.deri.pipes.core.Context;
import org.deri.pipes.core.Engine;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.text.TextBox;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class ChooseBoxTest extends TestCase {
	public void test(ConditionInterfaceForThisTest xxx) throws Exception{
		ChooseBox x = new ChooseBox();
		x.then = new Source();
		TextBox thenDelegate = new TextBox("it is true");
		thenDelegate.setFormat(TextBox.TEXTPLAIN_FORMAT);
		x.then.setDelegate(thenDelegate);
		x.otherwise = new Source();
		TextBox otherwiseDelegate = new TextBox("it is false");
		otherwiseDelegate.setFormat(TextBox.TEXTPLAIN_FORMAT);
		x.otherwise.setDelegate(otherwiseDelegate);
		TestCondition rightCondition = new TestCondition();
		TestCondition leftCondition = new TestCondition();
		ConditionWrapper wrapper = new ConditionWrapper();
		Condition condition = xxx.setConditions(rightCondition, leftCondition);
		wrapper.delegate = condition;
		x.condition = wrapper;
		leftCondition.answer = true;
		rightCondition.answer = true;
		assertEquals("Wrong result","it is true",x.execute(new Context()).toString());
		leftCondition.answer = false;
		rightCondition.answer = false;
		assertEquals("Wrong result","it is false",x.execute(new Context()).toString());
		String xml = Engine.defaultEngine().serialize(x);
		System.out.println(xml);
		Operator operator = Engine.defaultEngine().parse(xml);
		assertEquals(xml,Engine.defaultEngine().serialize(operator));
	}
	interface ConditionInterfaceForThisTest{
		Condition setConditions(Condition left, Condition right);
	}
	public void testOrCondition() throws Exception{
		test(new ConditionInterfaceForThisTest(){

			@Override
			public Condition setConditions(Condition left, Condition right) {
				OrCondition orx = new OrCondition();
				orx.left = new ConditionWrapper(left);
				orx.right = new ConditionWrapper(right);
				return orx;
			}});
	}
	public void testAndCondition() throws Exception{
		test(new ConditionInterfaceForThisTest(){

			@Override
			public Condition setConditions(Condition left, Condition right) {
				AndCondition orx = new AndCondition();
				orx.left = new ConditionWrapper(left);
				orx.right = new ConditionWrapper(right);
				return orx;
			}});
	}
	public void testNotCondition() throws Exception{
		test(new ConditionInterfaceForThisTest(){

			@Override
			public Condition setConditions(Condition left, Condition right) {
				AndCondition orx = new AndCondition();
				orx.left = new ConditionWrapper(new NotCondition(new NotCondition(left)));
				orx.right = new ConditionWrapper(right);
				return orx;
			}});
	}
	static class TestCondition implements Condition{
		boolean answer = false;
		public boolean isTrue(Context context) throws Exception {
			return answer;
		}
	}
}
