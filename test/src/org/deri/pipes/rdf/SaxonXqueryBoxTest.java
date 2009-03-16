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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.text.TextBox;
import org.openrdf.query.QueryEvaluationException;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class SaxonXqueryBoxTest extends TestCase {
	
	public void test() throws Exception{
		MemoryContextFetcher mcf = new MemoryContextFetcher();
		mcf.setContentType("text/xml");
		mcf.setDefaultValue("xxx");
		mcf.setKey("input");
		String input = createXmlInput();
		Context context = new Context();
		context.put("input", input);
		SaxonXqueryBox xquery = new SaxonXqueryBox();
		xquery.setSource(new Source(mcf));
		xquery.setContentType("text/html");
		xquery.setQuery(createXQuery());
		ExecBuffer result = xquery.execute(context);
		result.stream(System.out);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		result.stream(bout);
		String answer1 = new String(bout.toByteArray());
		
		String xml = Engine.defaultEngine().serialize(xquery);
		Operator xquery2 = Engine.defaultEngine().parse(xml);
		String xml2 = Engine.defaultEngine().serialize(xquery2);
		assertEquals("wrong xml regenerated",xml,xml2);
		
		ExecBuffer result2 = xquery2.execute(context);
		bout = new ByteArrayOutputStream();
		result2.stream(bout);
		String answer2 = new String(bout.toByteArray());
		
		assertEquals("answers differed",answer1,answer2);

	}
	/**
	 * Tests another pipe being called from xquery.
	 * @throws Exception
	 */
	public void testCallingPipesFunction() throws Exception{
		MemoryContextFetcher mcf = new MemoryContextFetcher();
		mcf.setContentType("text/xml");
		mcf.setDefaultValue("xxx");
		mcf.setKey("input");
		String input = createXmlInput();
		Engine engine = Engine.defaultEngine();
		PipeConfig helloConfig = new PipeConfig();
		String helloWorldExecutedIdentifier = "Tada! The Hello World Pipe Was Invoked From Xquery!!!";
		helloConfig.setId("hello-world");
		helloConfig.setSyntax("<pipe><code><text format='text/plain'><content>"+helloWorldExecutedIdentifier+" Hello ${person}</content></text></code></pipe>");
		engine.getPipeStore().save(helloConfig);
		
		Context context = engine.newContext();
		context.put("input", input);
		SaxonXqueryBox xquery = new SaxonXqueryBox();
		xquery.setSource(new Source(mcf));
		xquery.setContentType("text/html");
		xquery.setQuery(createXQueryCallingPipesFunction());
		ExecBuffer result = xquery.execute(context);
		String x = IOUtils.toString(result.getInputStream());
		assertTrue("Called pipe was not executed",x.indexOf(helloWorldExecutedIdentifier)>=0);
	}

	/**
	 * @return
	 */
	private String createXQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("xquery version \"1.0\";");
		sb.append("\n<html>");
		sb.append("\n<head>");
		sb.append("\n<title>A list of people</title>");
		sb.append("\n    </head>");
		sb.append("\n    <body>");
		sb.append("\n      <h1>A list of people</h1>");
		sb.append("\n      <p>Here are some interesting people:</p>");
		sb.append("\n      <ul> {");
		sb.append("\n        for $b in //persons/person");
		sb.append("\n        order by $b/name return");
		sb.append("\n          <li>{ string($b/name) }</li>");
		sb.append("\n      } </ul>");
		sb.append("\n    </body>");
		sb.append("\n  </html>");
		return sb.toString();
	}
	private String createXQueryCallingPipesFunction() {
		StringBuilder sb = new StringBuilder();
		sb.append("xquery version \"1.0\";");
		sb.append("\n<html>");
		sb.append("\n<head>");
		sb.append("\n<title>A list of people</title>");
		sb.append("\n    </head>");
		sb.append("\n    <body>");
		sb.append("\n      <h1>A list of people</h1>");
		sb.append("\n      <p>Here are some interesting people:</p>");
		sb.append("\n      <ul> {");
		sb.append("\n        for $b in //persons/person");
		sb.append("\n        order by $b/name return");
		sb.append("\n          <li>{ pipes:call('hello-world','person',$b/name) }</li>");
		sb.append("\n      } </ul>");
		sb.append("\n    </body>");
		sb.append("\n  </html>");
		return sb.toString();
	}

	/**
	 * @return
	 */
	private String createXmlInput() {
		String[] persons = {"Robert Fuller", "Giovanni Tumarello", "Danh Le Phouc"};
		StringBuilder sb = new StringBuilder();
		sb.append("<people><persons>");
		for(String person : persons){
			sb.append("\n\t<person>");
			sb.append("\\n<name>");
			sb.append(person);
			sb.append("</name>");
			sb.append("\n\t</person>");
		}
		sb.append("\n</persons></people>");
		return sb.toString();
	}
	
	public void testGenerateFunctionMethods(){
		StringBuilder x = new StringBuilder();
		for(int i=1;i<20;i++){
			StringBuilder sb = new StringBuilder();
			sb.append("    public static Object call(String pipeId");
			StringBuilder c = new StringBuilder();
			for(int j=0;j<i;j++){
				sb.append(", String k").append(j).append(", String v").append(j);
				c.append(", k").append(j).append(", v").append(j);
			}
			sb.append(") throws Exception{\n");
			sb.append("		return callPipe(pipeId");
			sb.append(c);
			sb.append(");\n");
			sb.append("    }\n\n");
			x.append(sb);
		}
		System.out.println(x);
	}
	
	public void testCanUseTupleBufferInput() throws Exception{
		SelectBox selectBox = new SelectBox();
		selectBox.source = new ArrayList<Source>();
		TextBox delegate = new TextBox();
		delegate.setFormat(TextBox.RDFXML_FORMAT);
		delegate.setContent("<?xml version='1.0' encoding='UTF-8'?><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'" +
				"\n xmlns:foaf='http://xmlns.com/foaf/0.1/'>" +
				"\n<foaf:Person rdf:about='http://data.semanticweb.org/person/giovanni-tummarello'>" +
				"\n<foaf:name>Giovanni Tummarello</foaf:name>" +
				"\n</foaf:Person>" +
				"</rdf:RDF>");
		selectBox.source.add(new Source(delegate));
		selectBox.setQuery(getFnConcatQuery());
		SaxonXqueryBox x = new SaxonXqueryBox();
		x.setQuery(createXQueryForTuples());
		x.setSource(new Source(selectBox));
		Context context = Engine.defaultEngine().newContext();
		ExecBuffer result = x.execute(context);
		String answer = result.toString();
		assertTrue("The tuple buffer didn't expand was:["+answer+"]",answer.indexOf("<literal>")>0);
		System.out.println(answer);
		
		
	}
	private String createXQueryForTuples() {
		StringBuilder sb = new StringBuilder();
		sb.append("xquery version \"1.0\";");
		sb.append("\n<x>");
		sb.append("\n{ for $x in //*:literal");
		sb.append("\n return");
		sb.append("\n<literal>{$x/text()}</literal>");
		sb.append("\n}");
		sb.append("\n </x>");
		return sb.toString();
	}


	/**
	 * @return
	 */
	private String getFnConcatQuery() {
		return "PREFIX fn: <http://www.w3.org/2005/xpath-functions#>\n"
			+"\nselect ?name where {?s ?p ?name ."
			+"\nFILTER ( ?name=fn:concat('Giovanni ','Tummarello') )"
			+"\n}";

	}
}
