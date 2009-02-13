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

import org.deri.pipes.core.Context;
import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.model.BinaryContentBuffer;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class Html2XmlBoxTest extends TestCase {
 public void test() throws Exception{
	 MemoryContextFetcher x = new MemoryContextFetcher();
	 x.setKey("html");
	 x.setContentType("text/html");
	 x.setDefaultValue("x");
	 Html2XmlBox box = new Html2XmlBox();
	 box.setSource(new Source(x));
	 Context context = Engine.defaultEngine().newContext();
	 context.put("html", getHtml());
	 BinaryContentBuffer result = (BinaryContentBuffer) box.execute(context);
	 result.stream(System.out);
	 assertEquals("wrong content type","text/xml",result.getContentType());
	 
 }

/**
 * @return
 */
private Object getHtml() {
	StringBuilder sb = new StringBuilder();
	sb.append("\n<html>");
	sb.append("\n<head><title>some html page</title></head>");
	sb.append("\n<body>");
	sb.append("\n<p>some text with " +
			"\n<br> line break (not well formed)</p>");
	sb.append("\n</body>");
	return sb.toString();
	
}
}
