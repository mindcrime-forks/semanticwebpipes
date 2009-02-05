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

package org.deri.pipes.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author robful
 *
 */
public class Parameters {

	Map<String,Parameter> parameters = new HashMap<String,Parameter>();
	/**
	 * @return
	 */
	public Collection<String> list() {
		List<String> list = new ArrayList<String>();
		list.addAll(parameters.keySet());
		Collections.sort(list);
		return list;
	}
	public String get(String key){
		Parameter parameter = parameters.get(key);
		if(parameter == null) return null;
		return parameter.value == null? parameter.defaultValue:parameter.value;
	}
	public Parameter getParameter(String key){
		return parameters.get(key);
	}
	/**
	 * Set the value of the given parameter.
	 * @param key
	 * @param value
	 */
	public void set(String key, String value){
		Parameter parameter = parameters.get(key);
		if(parameter != null){
			parameter.value = value;
		}else{
			parameter = new Parameter(key,value);
			parameters.put(parameter.id, parameter);
		}
	}
	/**
	 * Add a new Parameter from the id, label, default contained
	 * in this Map.
	 * @param map
	 */
	public void add(Map<String,String> map){
		Parameter parameter = new Parameter(map);
		parameters.put(parameter.id, parameter);
	}
	
	public class Parameter {
		public Parameter(Map<String,String> map){
			this.id = map.get("id");
			this.defaultValue = map.get("default");
			this.label = map.get("label");
		}
		/**
		 * @param key
		 * @param value
		 */
		public Parameter(String key, String value) {
			this.id = key;
			this.defaultValue = value;
			this.label = key;
		}
		public Map<String,String> toMap(){
			Map<String,String> map = new HashMap<String,String>();
			map.put("id",id);
			map.put("default",defaultValue);
			map.put("label",label);
			return map;
		}
		String id;
		String defaultValue;
		String label;
		String value;
	}

	/**
	 * @return
	 */
	public int size() {
		return parameters.size();
	}

}
