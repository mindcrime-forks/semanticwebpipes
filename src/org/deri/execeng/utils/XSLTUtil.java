package org.deri.execeng.utils;
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

import org.deri.execeng.endpoints.Pipes;
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
		if(Pipes.REQ!=null)
			return "http://"+Pipes.REQ.getServerName()+":"+Pipes.REQ.getServerPort()+Pipes.REQ.getContextPath();
		return "";	
	}
}

