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

import java.util.HashMap;
import java.util.Map;

import org.deri.pipes.core.Operator;
import org.deri.pipes.rdf.ConstructBox;
import org.deri.pipes.rdf.ForLoopBox;
import org.deri.pipes.rdf.HTMLFetchBox;
import org.deri.pipes.rdf.PatchExecutorBox;
import org.deri.pipes.rdf.PatchGeneratorBox;
import org.deri.pipes.rdf.RDFFetchBox;
import org.deri.pipes.rdf.RegExBox;
import org.deri.pipes.rdf.SameAsBox;
import org.deri.pipes.rdf.SelectBox;
import org.deri.pipes.rdf.SimpleMixBox;
import org.deri.pipes.rdf.TextBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SourceConverter implements Converter {
	Logger logger = LoggerFactory.getLogger(SourceConverter.class);
	public static Map<String,Class> ALIAS_MAPPINGS = new HashMap<String,Class>();
	static{ //TODO: move these back to properties file
		ALIAS_MAPPINGS.put("simplemix",SimpleMixBox.class);
		ALIAS_MAPPINGS.put("source",Source.class);
		ALIAS_MAPPINGS.put("code",Source.class);
		ALIAS_MAPPINGS.put("sourcelist",Source.class);
		ALIAS_MAPPINGS.put("rdffetch",RDFFetchBox.class);
		ALIAS_MAPPINGS.put("for",ForLoopBox.class);
		ALIAS_MAPPINGS.put("select",SelectBox.class);
		ALIAS_MAPPINGS.put("smoosher",SameAsBox.class);
		ALIAS_MAPPINGS.put("smoosher",SameAsBox.class);
		ALIAS_MAPPINGS.put("patch-executor",PatchExecutorBox.class);
		ALIAS_MAPPINGS.put("patch-generator",PatchGeneratorBox.class);
		ALIAS_MAPPINGS.put("construct",ConstructBox.class);
		ALIAS_MAPPINGS.put("htmlfetch", HTMLFetchBox.class);
		ALIAS_MAPPINGS.put("regex", RegExBox.class);
		ALIAS_MAPPINGS.put("rule",RegExBox.Rule.class);
		ALIAS_MAPPINGS.put("text",TextBox.class);
	}
	
	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Source source = (Source)arg0;
		Operator delegate = source.getDelegate();
		if(delegate != null){
			Class delegateRealClass = BypassCGLibMapper.isCGLibEnhanced(delegate.getClass())?delegate.getClass().getSuperclass():delegate.getClass();
			writer.startNode(getNodeForClass(delegateRealClass));
			context.convertAnother(delegate);
			writer.endNode();
		}
		
	}

	private String getNodeForClass(Class clazz) {
		for(String key : ALIAS_MAPPINGS.keySet()){
			if(clazz.equals(ALIAS_MAPPINGS.get(key))){
				return key;
			}
		}
		return(clazz.getName());
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Source source = new Source();
		if(reader.hasMoreChildren()){
			reader.moveDown();
			String nodeName = reader.getNodeName();
			Object delegate = context.convertAnother(source, ALIAS_MAPPINGS.get(nodeName));
			if(delegate instanceof Operator){
				Operator operator = (Operator)delegate;
				source.setDelegate(operator);
			}else{
				logger.warn("Ignoring unexpected object in xml tree (was expecting Operator of some kind):"+nodeName);
			}
			reader.moveUp();
		}
		return source;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return Source.class.equals(clazz);
	}

	public static void registerAliases(XStream xstream) {
		for(String key : ALIAS_MAPPINGS.keySet()){
			xstream.alias(key, ALIAS_MAPPINGS.get(key));
		}
	}

}
