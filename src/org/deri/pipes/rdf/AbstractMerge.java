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
package org.deri.pipes.rdf;

import java.util.ArrayList;
import java.util.List;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.core.internals.ThreadedExecutor;
import org.deri.pipes.model.MultiExecBuffer;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
/**
 * Abstract class for merging multiple RDF results into
 * a single result before further processing.
 * @author robful
 *
 */
public abstract class AbstractMerge extends RDFBox {
	private static Logger logger = LoggerFactory.getLogger(AbstractMerge.class);
	@XStreamImplicit
	protected List<Source> source = new ArrayList<Source>();
	/**
	 * Add a new input source.
	 * @param source
	 */
	void addInput(Operator source){
		if(!(source instanceof Source)){
			source = new Source(source);
		}
		this.source.add((Source)source);
	}
	/**
	 * Execute a set of operators, merging the results together.
	 * @param buffer
	 * @param context
	 * @throws Exception
	 */
	protected void mergeInputs(ExecBuffer buffer, Context context) throws Exception{
		List<Operator> operators = new ArrayList<Operator>();
		operators.addAll(source);
		MultiExecBuffer result = context.getEngine().execute(operators, context);
		result.stream(buffer);
	}
	
}
