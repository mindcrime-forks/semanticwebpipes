package org.deri.pipes.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Xml {
	final Logger logger = LoggerFactory.getLogger(Xml.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DocumentImpl doc =new DocumentImpl();
		Element root=doc.createElement("pipe");
		doc.appendChild(root);
		Element code=doc.createElement("code");
		root.appendChild(code);
		code.setAttribute("type", "uri");
		code.appendChild(doc.createTextNode("text"));
		code.appendChild(doc.createTextNode("text 2"));
		
		java.io.StringWriter  strWriter =new java.io.StringWriter(); 
		try{
			java.util.Properties props = 
			org.apache.xml.serializer.OutputPropertiesFactory.getDefaultMethodProperties(org.apache.xml.serializer.Method.XML);
			org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory.getSerializer(props);
			ser.setWriter(strWriter);
			ser.asDOMSerializer().serialize(doc.getDocumentElement());
			System.out.println(strWriter.getBuffer().toString());
		}
		catch(java.io.IOException e){
			
		}
		
	}

}
