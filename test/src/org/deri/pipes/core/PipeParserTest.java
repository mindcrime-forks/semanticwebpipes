package org.deri.pipes.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

public class PipeParserTest extends TestCase {
	public void testXStreamParser() throws Exception{
		XStream xstream = new PipeParser().getXStreamSerializer();
//		if(false){
		testSource(xstream, "pipe5.xml");
		if(false){
		testSource(xstream, "pipe2.xml");
		testSource(xstream, "pipe3.xml");
		testSource(xstream, "pipe4.xml");
//		}
		testSource(xstream, "pipe5.xml");
		}
//		XMLUnit.setIgnoreWhitespace(true);
//		XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream(controlXml)), new StringReader(xstream.toXML(o)));
	}



	private void testSource(XStream xstream, String controlXml) throws Exception{
		InputStream in = getClass().getResourceAsStream(controlXml);
		ProcessingPipe pipe = (ProcessingPipe) xstream.fromXML(in);
		PipeContext context = new PipeContext();
		context.setXstream(xstream);
		context.setHttpClient(new HttpClient());
		ExecBuffer result = pipe.execute(context);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		result.stream(bout);
		System.out.println("output: "+bout.toString("UTF-8"));
		System.out.println(xstream.toXML(pipe));
	}
}
