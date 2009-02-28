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
package org.deri.pipes.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLUtil {
	final Logger logger = LoggerFactory.getLogger(XMLUtil.class);
	 public static List<Element> getSubElementByName(Element element,String tagName){
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
	   
	   public static List<Element> getSubElement(Element element){
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
		   List<Element> childEle= getSubElementByName(element, tagName);
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
		   text = text!=null?text.trim():"";
		   Node node = (text.indexOf('<')>=0 || text.indexOf('\n')>=0)?doc.createCDATASection(text):doc.createTextNode(text);
		   elm.appendChild(node);
		   return elm;
	   }
}
