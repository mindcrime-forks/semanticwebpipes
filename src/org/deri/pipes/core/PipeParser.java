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
/**
 * 
 */
package org.deri.pipes.core;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.endpoints.PipeManager;
import org.deri.pipes.endpoints.Pipes;
import org.deri.pipes.model.Operator;
import org.deri.pipes.rdf.TextBox;
import org.deri.pipes.utils.IDTool;
import org.deri.pipes.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class PipeParser {
	final static Logger logger = LoggerFactory.getLogger(PipeParser.class);
	PipeContext pipeContext = new PipeContext();
	
	
	/**
	 * Parse the xml syntax contained in this String.
	 * @param syntax xml pipe syntax.
	 */
	public Operator parse(String syntax) {
		return parse(new InputSource(new java.io.StringReader(syntax)));
	}

	/**
	 * Parse the xml syntax contained in the first 'code' element
	 * @param inputStream an xml stream.
	 * @return an Operator, or null if the element could not be parsed.
	 */
    public Operator parse(InputSource inputStream){
    	try {
            DOMParser parser = new DOMParser();
            parser.parse(inputStream);  
            
            // Search for the child node of the <code> element, parse and run pipe execution engine
            NodeList pipeCodeXMLElements = parser.getDocument().getDocumentElement().getElementsByTagName("code").item(0).getChildNodes();
            
            for(int i=0;i<pipeCodeXMLElements.getLength();i++){
    			switch (pipeCodeXMLElements.item(i).getNodeType()){
    				case Node.ELEMENT_NODE:
    					return parseOperator((Element)pipeCodeXMLElements.item(i));
    			}
    		} 

        } catch (Exception e) {
        	logger.warn("could not parse input stream",e);
        }
    	return null;
    }
    
    public Operator parseCode(String code){
    	try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new java.io.StringReader(code)));  
            return parseOperator(parser.getDocument().getDocumentElement());
        } catch (Exception e) {
        	logger.debug("problem parsing code: ["+code+"]");
        	logger.warn("could not parse code",e);
        }
    	return null;
    }
    
	public static Operator loadStoredOperator(Element element){
		String tagName = element.getTagName();
		String syntax =PipeManager.getPipeSyntax(tagName);
		if (syntax==null){
			logger.warn("no syntax found for element "+element);
			return null;
		}
		List<Element> parameters =XMLUtil.getSubElement(element);
		for (int i=0;i<parameters.size();i++) {			
			syntax = syntax.replace("${" + parameters.get(i).getTagName() + "}", XMLUtil.getTextData(parameters.get(i)));
		}
		return (new PipeParser()).parse(syntax);
	}
	
	public Operator parseOperator(Element element){
		String lowerCaseTagName = element.getTagName().toLowerCase();
		String opClassName = Pipes.getOperatorProps().getProperty(lowerCaseTagName);
		logger.debug("mapped element ["+element.getTagName()+"] to operator ["+opClassName+"]");
		if(opClassName!=null){
			try {
				//find proper implemented class for an operator syntax 
				Class operatorClass = Class.forName(opClassName);
				
				//initialize operator (PipeParser,Element)
				Object obj= operatorClass.newInstance();
				logger.debug("output "+obj.toString());
				if((obj!=null)||(obj instanceof Operator)){
					Operator operator = (Operator)obj;
					operator.initialize(pipeContext,element);
					return operator;
				}
				logger.debug("cant create Operator was: "+obj);
			} catch (Exception e) {
				logger.info("Could not parse element "+element,e);
			}
		}
		
		Operator operator=loadStoredOperator(element);
		if(operator!=null){
			return operator;
		}
    	logger.warn("Unreconigzed tag :"+element.getTagName()+" "+element.toString());
		return null;
	}
	
	public String addOperator(String id,Operator operator){
		if((null!=id)&&(id.trim().length()>0)){
			id=id.trim().toLowerCase();
			if(pipeContext.contains(id)){
				logger.warn("Not adding operator with duplicated ID [" +id+"]");
				return null;
			}else{
				pipeContext.addOperator(id,operator);
				return id;
			}
		}else{
			return addOperator(operator);
		}
	}
	
	public String addOperator(Operator operator){
		if(operator==null){
			logger.debug("addOperator invoked with null operator");
			return null;
		}
		String id=generateID();
		pipeContext.addOperator(generateID(),operator);
		return id;
	}
	
	private String generateID(){
		return IDTool.generateRandomID("ID");
	}
	

	
	public String getSourceOperatorId(Element source){
		String refid = source.getAttribute("refid");
		if(refid!=null){
		    return refid;	
		}else{
			Element sourceElement=XMLUtil.getFirstSubElement(source);
			String id = null;
			final Operator operator;
			if(sourceElement==null){
				TextBox textbox = new TextBox();
				String format = source.getAttribute("format");
				textbox.setFormat(format);
				String textData = XMLUtil.getTextData(source);
				textbox.setText(textData);
				operator = textbox;
			}else{
				id = sourceElement.getAttribute("id");
				operator = parseOperator(sourceElement);
			}
			return addOperator(id,operator);
	    }
	}

}
