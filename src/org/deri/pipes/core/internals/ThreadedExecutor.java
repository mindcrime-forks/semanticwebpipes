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

package org.deri.pipes.core.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.Operator;
import org.deri.pipes.model.MultiExecBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes operators in parallel using an ExecutorService.
 * @author robful
 *
 */
public class ThreadedExecutor {
	Logger logger = LoggerFactory.getLogger(ThreadedExecutor.class);
	private TimeUnit defaultTimeoutUnits = TimeUnit.SECONDS;
	private long defaultTimeout = 60;
	private final ExecutorService pool;
	/**
	 * Create a ThreadedExecutor which will use a new cached ThreadPool.
	 */
	public ThreadedExecutor(){
		pool = Executors.newCachedThreadPool();
	}
	/**
	 * Create a ThreadedExecutor which will use this ExecutorService.
	 * @param pool
	 */
	public ThreadedExecutor(ExecutorService pool){
		this.pool = pool;
	}
	/**
	 * Execute the operators within the default timeout.
	 * @param operators
	 * @param context
	 * @return the ExecBuffers from executions.
	 * @throws InterruptedException 
	 */
	public MultiExecBuffer execute(List<Operator> operators, Context context) throws InterruptedException{
		return execute(operators,context,defaultTimeout ,defaultTimeoutUnits);
	}
	/**
	 * Execute the operators within the given timeout.
	 * @param operators
	 * @param context
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public MultiExecBuffer execute(List<Operator> operators, Context context, long timeout, TimeUnit unit) throws InterruptedException{
		if(pool.isShutdown()){
			throw new IllegalStateException("The executor has been shutdown");
		}
		List<ExecBuffer> buffers = new ArrayList<ExecBuffer>();
		List<Callable<ExecBuffer>> tasks = toCallable(operators,context);
		//TODO: add a timeout
		List<Future<ExecBuffer>> result = pool.invokeAll(tasks, timeout, unit);
		for(Future<ExecBuffer> future : result){
			try {
				buffers.add(future.get());
			} catch (Exception e) {
				logger.warn("An execution error occurred",e);
				buffers.add(null);
			}
		}
		return new MultiExecBuffer(buffers);
	}

	/**
	 * @param operators
	 * @return
	 */
	private List<Callable<ExecBuffer>> toCallable(List<Operator> operators, final Context context) {
		List<Callable<ExecBuffer>> callables = new ArrayList<Callable<ExecBuffer>>();
		for(final Operator operator : operators){
			callables.add(new Callable<ExecBuffer>(){
				@Override
				public ExecBuffer call() throws Exception {
					try{
						return operator.execute(context);
					}catch(Throwable t){
						logger.error("Problem encountered executing operator "+operator,t);
						return null;
					}
				}
			});
		}
		return callables;
	}
	/**
	 * Shutdown this executor releasing resources.
	 */
	public void shutdown(){
		logger.debug("shutdown invoked");
		pool.shutdown();
	}
}
