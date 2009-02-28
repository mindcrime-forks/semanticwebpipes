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

import org.deri.pipes.condition.Condition;
import org.deri.pipes.condition.ConditionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 * Converts a condition wrapper.
 * @author robful
 *
 */
public class ConditionConverter implements Converter {
	Logger logger = LoggerFactory.getLogger(ConditionConverter.class);
	private final SourceConverter sourceConverter;
	public ConditionConverter(SourceConverter sourceConverter){
		this.sourceConverter = sourceConverter;
	}
	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		ConditionWrapper wrapper = (ConditionWrapper)arg0;
		Condition delegate = wrapper.getDelegate();
		sourceConverter.convertDelegate(writer, context, delegate);
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		ConditionWrapper wrapper = new ConditionWrapper();
		if(reader.hasMoreChildren()){
			reader.moveDown();
			String nodeName = reader.getNodeName();
			Object delegate = context.convertAnother(wrapper, sourceConverter.getClassForNode(nodeName));
			if(delegate == null){
				delegate = sourceConverter.unmarshal(reader);
			}
			if(delegate instanceof Condition){
				Condition condition = (Condition)delegate;
				wrapper.setDelegate(condition);
			}else{
				logger.warn("Ignoring unexpected object in xml tree (was expecting Condition of some kind):"+nodeName);
			}
			reader.moveUp();
		}
		return wrapper;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return ConditionWrapper.class.equals(clazz);
	}


}
