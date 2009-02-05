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

import org.deri.pipes.core.Operator;
import org.deri.pipes.model.Memoizer;

import net.sf.cglib.proxy.Enhancer;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author robful
 *
 */
public class BypassCGLibMapper extends MapperWrapper {
	private static final String CGLIB_NAMING = "$$EnhancerByCGLIB$$";

	/**
	 * @param wrapped
	 */
	public BypassCGLibMapper(Mapper wrapped) {
		super(wrapped);
	}
	
	 public String serializedClass(Class type) {
	        return isCGLibEnhanced(type) 
	            ? super.serializedClass(type.getSuperclass())
	            : super.serializedClass(type);
	    }

	public static boolean isCGLibEnhanced(Class type) {
		if(type == null){
			return false;
		}
		String name = type.getName();
		return name.indexOf(CGLIB_NAMING) > 0 && Enhancer.isEnhanced(type);
	}

	@Override
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		if(isCGLibEnhanced(definedIn)){
			if(fieldName.startsWith("CGLIB$")){
				return false;
			}
		}
		return super.shouldSerializeMember(definedIn, fieldName);
	}

}
