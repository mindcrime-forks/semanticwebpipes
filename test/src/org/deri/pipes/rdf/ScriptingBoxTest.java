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
import org.deri.pipes.core.Pipe;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.text.TextBox;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class ScriptingBoxTest extends TestCase {
	public void test() throws Exception{
		ScriptingBox x = new ScriptingBox();
		String xml1 = "<?xml version='1.0'?>" +
		"\n<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'" +
		"\n         xmlns:dc='http://purl.org/dc/elements/1.1/' >" +
		"\n<rdf:Description rdf:about='http://www.w3.org/TR/rdf-syntax-grammar'> " +
		"\n  <dc:title>RDF/XML Syntax Specification (Revised)</dc:title> " +
		"\n</rdf:Description> " +
		"\n</rdf:RDF>";
		x.setSource(new Source(new TextBox(xml1)));
		x.setLanguage("groovy");
		x.setScript("'Hello, this is the answer, thanks to groovy scripting!'");
		ExecBuffer output = x.execute(new Context());
		System.out.println(output);
	}
	
	public void testGiovanni() throws Exception{
		MemoryContextFetcher mcf = new MemoryContextFetcher();
		mcf.setContentType("text/plain");
		mcf.setKey("people");
		String people="Giovanni Tummarello\nRobert Fuller\nDanh Le pouch";
		mcf.setDefaultValue(people);
		
		ScriptingBox x = new ScriptingBox();
		x.setLanguage("groovy");
		x.setSource(new Source(mcf));
		Context context = new Context();
		context.put("people", people);
		String script = "import groovy.xml.MarkupBuilder" +
				"\n def writer = new StringWriter()" +
				"\n def xml = new MarkupBuilder(writer)" +
				"\n xml.'rdf:RDF'('xmlns:rdf':'http://www.w3.org/1999/02/22-rdf-syntax-ns#','xmlns:foaf':'http://xmlns.com/foaf/0.1/'){" +
				"\n   input.inputStream.eachLine{ " +
				"\n    name -> parts=name.split(/\\s/,2);" +
				"\n     xml.'foaf:person'('rdf:about':\"http://example.com/${name.replaceAll(' ','_')}\"){" +
				"\n      'foaf:name'(name)" +
				"\n      'foaf:firstName'(parts[0])" +
				"\n      'foaf:surname'(parts[1])" +
				"\n     }" +
				"\n   }" +
				"\n }" +
				"" +
				"" +
				"" +
				"" +
				"" +
				"" +
				"\nwriter.toString();";
		x.setScript(script);
		ExecBuffer output = x.execute(context);
		System.out.println(output);
		Pipe pipe = new Pipe();
		pipe.addOperator(x);
		System.out.println(Engine.defaultEngine().serialize(pipe));
	}
}
