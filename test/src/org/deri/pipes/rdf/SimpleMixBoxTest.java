package org.deri.pipes.rdf;

import junit.framework.TestCase;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.PipeParser;
import org.deri.pipes.core.internals.Source;

public class SimpleMixBoxTest extends TestCase {
	private SimpleMixBox fixture;
	private Context mockContext;
	public void test() throws Exception{
		fixture = new SimpleMixBox();
		String xml1 = "<?xml version='1.0'?>" +
				"\n<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'" +
				"\n         xmlns:dc='http://purl.org/dc/elements/1.1/' >" +
				"\n<rdf:Description rdf:about='http://www.w3.org/TR/rdf-syntax-grammar'> " +
				"\n  <dc:title>RDF/XML Syntax Specification (Revised)</dc:title> " +
				"\n</rdf:Description> " +
				"\n</rdf:RDF>";
		String xml2 = "<?xml version='1.0'?>" +
				"\n<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'" +
				"\n         xmlns:dc='http://purl.org/dc/elements/1.1/' >" +
				"\n<rdf:Description rdf:about='http://www.w3.org/TR/rdf-syntax-grammar'> " +
				"\n  <dc:abstract>RDF is good for everyone</dc:abstract> " +
				"\n</rdf:Description> " +
				"\n</rdf:RDF>";
		fixture.addInput(new Source(new TextBox(xml1)));
		fixture.addInput(new Source(new TextBox(xml2)));
		ExecBuffer buffer = fixture.execute(mockContext);
		String result = buffer.toString();
		System.out.println(result);
		assertTrue("Missing abstract in result :\n"+result,result.indexOf("abstract")>0);
		assertTrue("Missing title in result :\n"+result,result.indexOf("title")>0);
//		System.out.println(new PipeParser().serializeToXML(fixture));
	}
}
