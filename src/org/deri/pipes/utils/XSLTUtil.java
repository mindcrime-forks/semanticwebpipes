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
package org.deri.pipes.utils;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.deri.pipes.endpoints.Pipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
public class XSLTUtil {
	final Logger logger = LoggerFactory.getLogger(XSLTUtil.class);
	
	public static String transform(String xmlURL,String xsltURL){
		StringWriter sw=new StringWriter();
		StreamResult output = new StreamResult(sw);
		transform(xmlURL,xsltURL,output);
		return sw.getBuffer().toString();
	}
	
	public static StringBuffer transform(Source xmlStream,Source xsltStream){
		StringWriter sw=new StringWriter();
		StreamResult output = new StreamResult(sw);
		transform(xmlStream,xsltStream,output);
		return sw.getBuffer();
	}
	
	public static void transform(String xmlURL,String xsltURL,Result result){
		transform(new StreamSource(xmlURL),new StreamSource(xsltURL),result);
	}
	
	public static void transform(Source xmlStream,Source xsltStream,Result result){
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltStream);
			transformer.transform(xmlStream, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}

	}
	public static String getBaseURL(){
		Execution exec=Executions.getCurrent();
		if(exec!=null)
			return "http://"+exec.getServerName()+":"+exec.getServerPort()+exec.getContextPath();
		if(Pipes.REQ!=null)//fuller says this is wrong and dangerous.
			return "http://"+Pipes.REQ.getServerName()+":"+Pipes.REQ.getServerPort()+Pipes.REQ.getContextPath();
		return "";	
	}
}

