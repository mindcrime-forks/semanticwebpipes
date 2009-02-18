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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.deri.pipes.core.Context;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.core.internals.StringOrSource;
import org.deri.pipes.model.TextBuffer;
import org.w3c.dom.Node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.ConverterMatcher;

/**
 * @author robful
 *
 */
@XStreamAlias("urlbuilder")
public class URLBuilderBox implements Operator{
	
	String base;
	@XStreamImplicit(itemFieldName="path")
	List<StringOrSource> paths;
	@XStreamImplicit(itemFieldName="para")
	List<Para> paras;
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.Operator#execute(org.deri.pipes.core.Context)
	 */
	@Override
	public ExecBuffer execute(Context context) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(base);
		if(paths != null){
			for(StringOrSource o : paths){
				sb.append(o.expand(context));
			}
		}
		if(paras != null && paras.size()>0){
			boolean first = true;
			for(Para para : paras){
				sb.append(first?"?":"&");
				sb.append(para.name).append("=");
				StringOrSource value = para.source;
				sb.append(URLEncoder.encode(value.expand(context),"UTF-8"));
				first=false;
			}
		}
		
		return new TextBuffer(sb.toString());
	}
	public String getBaseUrl() {
		return base;
	}
	public void setBaseUrl(String baseUrl) {
		this.base = baseUrl;
	}

	public void addPath(String path) {
		ensurePathsNotNull();
		paths.add(new StringOrSource(path));
	}
	
	public void addPath(Source path) {
		ensurePathsNotNull();
		paths.add(new StringOrSource(path));
	}
	
	private void ensurePathsNotNull() {
		if(paths == null){
			paths = new ArrayList<StringOrSource>();
		}
	}
	
	@XStreamConverter(ParaConverter.class)
	static class Para{
		String name;
		StringOrSource source;
	}
	/**
	 * @param string
	 * @param source
	 */
	public void addParameter(String name, String value) {
		ensureParasNotNull();
		Para para = new Para();
		para.name = name;
		para.source = new StringOrSource(value);
		paras.add(para);
	}
	public void addParameter(String name, Source value) {
		ensureParasNotNull();
		Para para = new Para();
		para.name = name;
		para.source = new StringOrSource(value);
		paras.add(para);
	}
	private void ensureParasNotNull() {
		if(paras == null){
			paras = new ArrayList<Para>();
		}
	}
	/**
	 * @return
	 */
	public boolean usesSource() {
		if(paths != null){
			for(StringOrSource x : paths){
				if(x.source != null){
					return true;
				}
			}
		}
		if(paras != null){
			for(Para x : paras){
				if(x.source.source != null){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @return
	 */
	public String getUrl(Context context) throws Exception{
		TextBuffer tb = (TextBuffer)execute(context);
		return tb.toString();
	}

}
