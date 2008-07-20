package org.deri.execeng.utils;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.deri.execeng.endpoints.*;
public class XSLTUtil {
	
	public static String transform(String xmlURL,String xsltURL){
		StringWriter sw=new StringWriter();
		StreamResult output = new StreamResult(sw);
		transform(xmlURL,xsltURL,output);
		return sw.getBuffer().toString();
	}
	
	public static String transform(Source xmlStream,Source xsltStream){
		StringWriter sw=new StringWriter();
		StreamResult output = new StreamResult(sw);
		transform(xmlStream,xsltStream,output);
		return sw.getBuffer().toString();
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
		if(Pipes.REQ!=null)
			return "http://"+Pipes.REQ.getServerName()+":"+Pipes.REQ.getServerPort()+Pipes.REQ.getContextPath();
		return "";	
	}
}

