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
import org.deri.pipes.core.PipeContext;
import org.deri.pipes.core.Source;
import org.deri.pipes.model.Operator;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.deri.pipes.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public abstract class AbstractMerge extends RDFBox {
	private transient Logger logger = LoggerFactory.getLogger(AbstractMerge.class);
	protected void mergeInputs(PipeContext context){
		mergeInputs(buffer,context);
	}
	void addSource(String src){
		throw new RuntimeException("remove this method");
	}
	
	protected void mergeInputs(ExecBuffer buffer, PipeContext context){
		for(Source src : source){
			if(!src.isExecuted()){
				src.execute(context);
			}
	       	 if (src.getExecBuffer() instanceof SesameMemoryBuffer){
	       		src.stream(buffer);
	       	 }else{
	       		logger.warn("Inappropriate input format, RDF is required!!!");
	       	 }
	   	}
	}
	
	public void initialize(PipeContext context, Element element){
		setContext(context);
  		List<Element> sources=XMLUtil.getSubElementByName(element, "source");
  		for(int i=0;i<sources.size();i++){
     		String opID=context.getPipeParser().getSourceOperatorId(sources.get(i));
     		if (null!=opID){
     	//		addSource(opID);
     		}
     	}  		
    }
}
