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

package org.deri.pipes.xquery;

import java.util.HashMap;
import java.util.Map;

import net.sf.saxon.dom.DocumentBuilderImpl;

import org.apache.commons.io.IOUtils;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.PipeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * pipes Functions for xquery. These functions can be invoked
 * in the xquery operator using pipes: prefix.
 * @see org.deri.pipes.rdf.SaxonXqueryBox
 * @author robful
 *
 */
public class Functions {
	static Logger logger = LoggerFactory.getLogger(Functions.class);
	public static final ThreadLocal<Context> context = new ThreadLocal<Context>();
	public static String foo(String name){
		return "foo="+name;
	}
	/**
	 * Calls the given pipe, using the parameters provided, and returns
	 * a document if one can be parsed from the result, otherwise a String.
	 * @param pipeId
	 * @param args An even number set of parameters to the pipe, name followed by value.
	 * @return a Document if one can be parsed, otherwise a String.
	 * @throws Exception
	 */
	public static Object callPipe(String pipeId, String ... args) throws Exception{
		if(args.length%2 != 0){
			throw new PipeException("USAGE: pipes:call('pipe-name' [,'param-name',param-value [, 'param2-name', param2-value [,...]]])");
		}
		Map<String,String> params = new HashMap<String,String>();
		for(int i=0;i<args.length;i+=2){
			String key = args[i];
			String value = args[i+1];
			params.put(key, value);
		}
		logger.info("calling pipe ["+pipeId+"] with params "+params);
		Context x = context.get();
		ExecBuffer result = x.getEngine().getStoredPipe(pipeId).execute(x.getEngine().newContext(),params);
		Document dom = getResultAsXmlDocumentIfPossible(result);
		return dom == null? IOUtils.toString(result.getInputStream(),"UTF-8"): dom;
	}
	public static Object call(String pipeId) throws Exception{
		return callPipe(pipeId);
	}
    public static Object call(String pipeId, String k0, String v0) throws Exception{
		return callPipe(pipeId, k0, v0);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12, String k13, String v13) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12, k13, v13);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12, String k13, String v13, String k14, String v14) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12, k13, v13, k14, v14);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12, String k13, String v13, String k14, String v14, String k15, String v15) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12, k13, v13, k14, v14, k15, v15);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12, String k13, String v13, String k14, String v14, String k15, String v15, String k16, String v16) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12, k13, v13, k14, v14, k15, v15, k16, v16);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12, String k13, String v13, String k14, String v14, String k15, String v15, String k16, String v16, String k17, String v17) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12, k13, v13, k14, v14, k15, v15, k16, v16, k17, v17);
    }

    public static Object call(String pipeId, String k0, String v0, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12, String k13, String v13, String k14, String v14, String k15, String v15, String k16, String v16, String k17, String v17, String k18, String v18) throws Exception{
		return callPipe(pipeId, k0, v0, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11, v11, k12, v12, k13, v13, k14, v14, k15, v15, k16, v16, k17, v17, k18, v18);
    }


	
	
	private static Document getResultAsXmlDocumentIfPossible(ExecBuffer result){
		try{
			DocumentBuilderImpl builder = new DocumentBuilderImpl();
			Document dom = builder.parse(result.getInputStream());
			return dom;
		}catch(Exception e){
			logger.info("Couldn't create an xml document from the result (caught "+e+")");
			return null;
		}
	}
}
