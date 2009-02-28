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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.model.TextBuffer;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class URLBuilderBoxTest extends TestCase {
	
	public void test() throws Exception{
		URLBuilderBox x = new URLBuilderBox();
		String baseUrl="http://api.opencalais.com/";
		x.setBaseUrl(baseUrl);
		x.addPath("enlighten/rest");
		HttpGetBox y = new HttpGetBox();
		y.setLocation("http://www.aplpi.com/");
		x.addParameter("content", new Source(y));
		x.addParameter("licenseId","8zqqt6d7f7akn5vcnjhbd2qu");
		Engine.defaultEngine().parse("<http-get/>");
		String xSerialized = Engine.defaultEngine().serialize(x);
		System.out.println(xSerialized);
		callOpenCalais(x);
		
		String code = "<urlbuilder><base>"+baseUrl+"</base><path>foo</path></urlbuilder>";
		x=(URLBuilderBox)Engine.defaultEngine().parse(xSerialized);
		String ySerialized = Engine.defaultEngine().serialize(x);
		assertEquals("wrong xml",xSerialized,ySerialized);
		
	}

	private void callOpenCalais(URLBuilderBox x) throws Exception, IOException {
		ExecBuffer execBuffer = x.execute(Engine.defaultEngine().newContext());
		assertTrue("Result should be a TextBuffer",execBuffer instanceof TextBuffer);
		TextBuffer text = (TextBuffer)execBuffer;
		String calaisUrl = text.toString();
		System.out.println(calaisUrl);
		HttpGetBox getbox = new HttpGetBox();
		getbox.setLocation(calaisUrl);
		execBuffer = getbox.execute(Engine.defaultEngine().newContext());
		execBuffer.stream(System.out);
	}

}
