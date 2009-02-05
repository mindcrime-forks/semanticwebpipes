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

package org.deri.pipes.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author robful
 *
 */
public class Memoizer {
	static Logger logger = LoggerFactory.getLogger(Memoizer.class);
	static Map<Class,Class> proxyClassCache = new HashMap<Class,Class>();
	private static Method operatorExecuteMethod = null;
	static{
		try {
			operatorExecuteMethod = Operator.class.getMethod("execute", new Class[]{Context.class});
			// This is here to remind developers when changing operator interface.
			new Operator(){
				@Override
				public ExecBuffer execute(Context context) {
					return null;
				}
				
			};
		} catch (Throwable t) {
			logger.error("Unable to apply memoization to Operator.execute method",t);
		}
	}
	public static Object getMemoizedInstance(Class clazz){
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallbackFilter(new ExecuteCallbackFilter());
		Callback[] callbacks = new Callback[]{NoOp.INSTANCE,new OperatorExecuteCallback()};
		enhancer.setCallbacks(callbacks);
		return enhancer.create();
	}
	static class OperatorExecuteCallback implements MethodInterceptor{

        /**
         * Intercept the proxy method invocations to inject memoization.
         * On first invocation, the original method is invoked through MethodProxy.
         * @param object the proxy object
         * @param method intercepted Method
         * @param args arguments of the method
         * @param proxy the proxy used to invoke the original method
         * @throws Throwable any exception may be thrown; if so, super method will not be invoked
         * @return any value compatible with the signature of the proxied method.
         */
		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy proxy) throws Throwable {
			Context context = (Context)args[0];
			Object memoizedResult = context.get(obj);
			if(memoizedResult == null){
				context.put(obj,proxy.invokeSuper(obj, args));
			}
			return context.get(obj);
		}

		
	}
	/**
	 * Filter to accept only Operator.execute method.
	 */
	static class ExecuteCallbackFilter implements CallbackFilter{

		/* (non-Javadoc)
		 * @see net.sf.cglib.proxy.CallbackFilter#accept(java.lang.reflect.Method)
		 */
		@Override
		public int accept(Method method) {
			return isOperatorExecuteMethod(method)?1:0;
		}

		/**
		 * @param method
		 * @return
		 */
		private boolean isOperatorExecuteMethod(Method method) {
			if(operatorExecuteMethod == null || !method.getName().equals(operatorExecuteMethod.getName())){
				return false;
			}
			if(!method.getReturnType().equals(operatorExecuteMethod.getReturnType())){
				return false;
			}
			Class[] types1 = method.getParameterTypes();
			Class[] types2 = operatorExecuteMethod.getParameterTypes();
			if(types1.length != types2.length){
				return false;
			}
			for(int i=0;i<types1.length;i++){
				if(!types1[i].equals(types2[i])){
					return false;
				}
			}
			return true;
		}
		
	}

}
