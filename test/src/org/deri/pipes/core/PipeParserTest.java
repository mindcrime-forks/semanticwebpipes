package org.deri.pipes.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.deri.pipes.model.Operator;
import org.deri.pipes.rdf.ForLoopBox;
import org.deri.pipes.rdf.PatchExecutorBox;
import org.deri.pipes.rdf.RDFBox;
import org.deri.pipes.rdf.RDFFetchBox;
import org.deri.pipes.rdf.SameAsBox;
import org.deri.pipes.rdf.SelectBox;
import org.deri.pipes.rdf.SimpleMixBox;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import junit.framework.TestCase;

public class PipeParserTest extends TestCase {
	public void testXStreamParser() throws Exception{
		XStream xstream = new XStream(new PureJavaReflectionProvider(),
			    new DomDriver() {
			        public HierarchicalStreamWriter createWriter(Writer out) {
			            return new PrettyPrintWriter(out) {
			                protected void writeText(QuickWriter writer, String text) {
			                	if(text==null || (text.indexOf('&')<0 && text.indexOf('<')<0)){
			                		writer.write(text);
			                	}else{
			                    writer.write("<![CDATA[");
			                    writer.write(text);
			                    writer.write("]]>");
			                	}
			                }
			            };
			        }
			    }
			);
		xstream.alias("pipe",ProcessingPipe.class);
		xstream.registerLocalConverter(ProcessingPipe.class, "parameters", new ParameterConverter());
		xstream.registerConverter(new SourceConverter());
		//xstream normally uses 'reference' for references, we want refid
		xstream.aliasSystemAttribute("refid", "reference");
		SourceConverter.registerAliases(xstream);
		xstream.autodetectAnnotations(true);
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
