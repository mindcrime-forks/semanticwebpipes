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
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.PipeContext;
import org.deri.pipes.model.Operator;
import org.deri.pipes.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SplitBox implements Operator{ 
	private transient Logger logger = LoggerFactory.getLogger(SplitBox.class);
	private transient ExecBuffer buffer;
	 String inputOpID;
	 protected boolean isExecuted=false;
	private PipeContext context;


	@Override
	public void execute(PipeContext context) {
		buffer=context.getOperatorExecuted(inputOpID).getExecBuffer();
		isExecuted = true;
	}

	@Override
	public ExecBuffer getExecBuffer() {
		return buffer;
	}

	@Override
	public boolean isExecuted() {
		return isExecuted;
	}

	@Override
	public void stream(ExecBuffer outputBuffer) {
		if (buffer!=null)
			buffer.stream(outputBuffer);
	}

	@Override
	public void stream(ExecBuffer outputBuffer, String context) {
		if (buffer!=null)
			buffer.stream(outputBuffer,context);
	}

	public void initialize(PipeContext context,Element element){
		this.context = context;
        Element inputSrc =XMLUtil.getFirstSubElement(element);
      	inputOpID=context.getPipeParser().getSourceOperatorId(inputSrc);
      	if (null==inputOpID){
      		logger.warn("<split> element must contain a sub element or have REFID attribute !!!");
      		//TODO : Handling error of lacking OWL data source 	
      	}
	}
          
}
