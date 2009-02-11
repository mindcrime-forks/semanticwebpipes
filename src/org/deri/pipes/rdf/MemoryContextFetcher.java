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

import org.apache.log4j.Logger;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.model.BinaryContentBuffer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Fetches content from the processing context. This is
 * used where an application wishes to use pipes to
 * process objects held by the application rather than
 * on a server.
 * @author robful
 *
 */
@XStreamAlias("memory-context")
public class MemoryContextFetcher implements Operator {
	transient Logger logger = Logger.getLogger(MemoryContextFetcher.class);
	String key;
	String contentType;
	String defaultValue;
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.Operator#execute(org.deri.pipes.core.Context)
	 */
	@Override
	public ExecBuffer execute(Context context) throws Exception {
		BinaryContentBuffer result = new BinaryContentBuffer();
		if(contentType != null){
			result.setContentType(contentType);
		}
		logger.debug("retrieving value from context using key=["+key+"]");
		Object obj = context.get(key);
		if(obj == null){
			logger.warn("No value was retrieved from context using key=["+key+"], will use default value");
			result.setContent(defaultValue);
		}else{
			if(obj instanceof CharSequence){
				result.setContent(obj.toString());
			}else if(obj instanceof byte[]){
				result.setContent((byte[])obj);
			}else{
				logger.warn("Not able to convert object to String or byte array:"+obj.getClass());
			}

		}
		return result;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	

}
