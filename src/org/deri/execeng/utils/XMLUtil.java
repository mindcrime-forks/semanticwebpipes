package org.deri.execeng.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLUtil {
	final Logger logger = LoggerFactory.getLogger(XMLUtil.class);
	 public static ArrayList<Element> getSubElementByName(Element element,String tagName){
		   ArrayList<Element> result=new ArrayList<Element>();
		   NodeList childNodes=element.getChildNodes();
		   for(int i=0;i<childNodes.getLength();i++){
				switch (childNodes.item(i).getNodeType()){
					case Node.ELEMENT_NODE:
						if(((Element)childNodes.item(i)).getTagName().equalsIgnoreCase(tagName))
							result.add((Element)childNodes.item(i));	
						break;  
				}
		   }
		   return result;
	   }
	   
	   public static ArrayList<Element> getSubElement(Element element){
		   ArrayList<Element> result=new ArrayList<Element>();
		   NodeList childNodes=element.getChildNodes();
		   for(int i=0;i<childNodes.getLength();i++){
				switch (childNodes.item(i).getNodeType()){
					case Node.ELEMENT_NODE:
						result.add((Element)childNodes.item(i));	
						break;  
				}
		   }
		   return result;
	   }
	   
	   public static Element getFirstSubElement(Element element){
		   NodeList childNodes=element.getChildNodes();
		   for(int i=0;i<childNodes.getLength();i++){
				switch (childNodes.item(i).getNodeType()){
					case Node.ELEMENT_NODE:
						return (Element)childNodes.item(i);	
				}
		   }
		   return null;
	   }
	   
	   public static Element getFirstSubElementByName(Element element,String tagName){
		   NodeList childNodes=element.getChildNodes();
		   for(int i=0;i<childNodes.getLength();i++){
				switch (childNodes.item(i).getNodeType()){
					case Node.ELEMENT_NODE:
						if(((Element)childNodes.item(i)).getTagName().equalsIgnoreCase(tagName))
							return (Element)childNodes.item(i);	
				}
		   }
		   return null;
	   }
	   
	   public static Node getFirstChildByType(Element element,short nodeType){
		   NodeList childNodes=element.getChildNodes();
		   for(int i=0;i<childNodes.getLength();i++){
				if (childNodes.item(i).getNodeType()==nodeType){
					return childNodes.item(i);	
				}
		   }
		   return null;
	   }
	   
	   public static String getTextFromFirstSubEleByName(Element element,String tagName){
		   ArrayList<Element> childEle= getSubElementByName(element, tagName);
		   for(int i=0;i<childEle.size();i++){
			   String tmp=getTextData(childEle.get(i));
			   if(tmp!=null) return tmp;
		   }
		   return null;
	   }
	   
	   public static String getTextData(Element element){
		   NodeList childNodes=element.getChildNodes();
		   for(int i=0;i<childNodes.getLength();i++){
				if ((childNodes.item(i).getNodeType()==Node.TEXT_NODE)||(childNodes.item(i).getNodeType()==Node.CDATA_SECTION_NODE)){
					if(childNodes.item(i)!=null){
						String tmpStr=((Text)childNodes.item(i)).getWholeText().trim();
						if(tmpStr.length()>0){
							return tmpStr;
						}
					}
				}
		   }
		   return null;   
	   }

	   public static Element createElmWithText(Document doc,String tagName,String text){
		   Element elm =doc.createElement(tagName);
		   elm.appendChild(doc.createCDATASection(text!=null?text.trim():""));
		   return elm;
	   }
}
