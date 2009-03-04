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

import java.util.ArrayList;

import org.deri.pipes.core.Context;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.text.TextBox;
import org.openrdf.query.QueryEvaluationException;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class SelectBoxTest extends TestCase {
	public void testSupportsFnConcat() throws Exception{
		SelectBox x = new SelectBox();
		x.source = new ArrayList<Source>();
		TextBox delegate = new TextBox();
		delegate.setFormat(TextBox.RDFXML_FORMAT);
		delegate.setContent("<?xml version='1.0' encoding='UTF-8'?><rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'" +
				"\n xmlns:foaf='http://xmlns.com/foaf/0.1/'>" +
				"\n<foaf:Person rdf:about='http://data.semanticweb.org/person/giovanni-tummarello'>" +
				"\n<foaf:name>Giovanni Tummarello</foaf:name>" +
				"\n</foaf:Person>" +
				"</rdf:RDF>");
		x.source.add(new Source(delegate));
		x.setQuery(getFnConcatQuery());
		try{
			System.out.println(x.execute(new Context()).toString());
		}catch(QueryEvaluationException e){
			if(e.getMessage().indexOf("concat")>=0){
				fail(e.getMessage());
			}
			throw e;
		}
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
