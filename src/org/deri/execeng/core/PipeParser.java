/**
 * 
 */
package org.deri.execeng.core;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.List;

import org.apache.xerces.parsers.DOMParser;
import org.deri.execeng.endpoints.PipeManager;
import org.deri.execeng.endpoints.Pipes;
import org.deri.execeng.model.Operator;
import org.deri.execeng.rdf.TextBox;
import org.deri.execeng.utils.IDTool;
import org.deri.execeng.utils.XMLUtil;
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
	final Logger logger = LoggerFactory.getLogger(PipeParser.class);
	private Hashtable<String,Operator> operators=null;
	private StringBuffer log= new StringBuffer();
	
	public PipeParser(){
		operators= new Hashtable<String,Operator>();
	}
	
	/* (non-Javadoc)
	 * @see org.deri.execeng.core.BoxParser#parse(java.lang.String)
	 */
	public Operator parse(String syntax) {
		// TODO Auto-generated method stub
		return parse(new InputSource(new java.io.StringReader(syntax)));
	}
	
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
        	log(e);
        }
    	return null;
    }
    
    public Operator parseCode(String code){
    	try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new java.io.StringReader(code)));  
            return parseOperator(parser.getDocument().getDocumentElement());
        } catch (Exception e) {
        	log(e);
        }
    	return null;
    }
    
	public static Operator loadStoredOperator(Element element){
		String syntax =PipeManager.getPipeSyntax(element.getTagName());
		if (syntax==null) return null;
		List<Element> parameters =XMLUtil.getSubElement(element);
		for (int i=0;i<parameters.size();i++) {			
			syntax = syntax.replace("${" + parameters.get(i).getTagName() + "}", XMLUtil.getTextData(parameters.get(i)));
		}
		if (syntax != null) {
			return (new PipeParser()).parse(syntax);
		}
		return null;
	}
	
	public Operator parseOperator(Element element){
		String opClassName=Pipes.getOperatorProps().getProperty(element.getTagName().toLowerCase());
		logger.debug(element.getTagName()+"---"+opClassName);
		if(opClassName!=null){
			try {
				//find proper implemented class for an operator syntax 
				Class operatorClass = Class.forName(opClassName);
				Class parameterTypes[] ={PipeParser.class,org.w3c.dom.Element.class};
				
				//initialize operator (PipeParser,Element)
				Constructor operatorConstructor=operatorClass.getConstructor(parameterTypes);
				Object obj= operatorConstructor.newInstance(this,element);
				logger.debug("output "+obj.toString());
				if((obj!=null)||(obj instanceof Operator)) return (Operator)obj;
				logger.debug("cant create operator");
			} catch (Exception e) {
				logger.info("Could not parse element "+element,e);
			}
		}
		
		Operator tmp=loadStoredOperator(element);
		if(tmp!=null) return tmp;
    	log("Unreconigzed tag :");
	    log(element.getTagName());
	    log(element.toString());
		return null;
	}
	
	public String addOperator(String id,Operator operator){
		if((null!=id)&&(id.trim().length()>0)){
			id=id.trim().toLowerCase();
			if(operators.containsKey(id)){
				log("Duplicated ID" +id);
				return null;
			}				
			else
				operators.put(id,operator);
		}
		else
			return addOperator(operator);
		return id;
	}
	
	public String addOperator(Operator operator){
		if(operator==null){
			logger.debug("addOperator invoked with null operator");
			return null;
		}
		String id=generateID();
		operators.put(generateID(),operator);
		return id;
	}
	
	private String generateID(){
		return IDTool.generateRandomID("ID");
	}
	
	public Operator getOpByID(String id){
		return operators.get(id);
	}
	
	public void log(String mess){
		log.append(mess+"\n");
	}
	
	public void log(Exception e){
		log(e.toString()+"\n");
	}
	
	public String getSource(Element source){
		if(source.getAttribute("refid")!=null){
		    return source.getAttribute("refid");	
		}else{
			Element tmp=XMLUtil.getFirstSubElement(source);	    		
			if(tmp==null){
				if(source.getAttribute("format")!=null){
					return addOperator(new TextBox(this,XMLUtil.getTextData(source),source.getAttribute("format")));
				}else{
					return addOperator(new TextBox(this,XMLUtil.getTextData(source)));
				}
			}else{
				return addOperator(tmp.getAttribute("id"),parseOperator(tmp));
			}
	    }
	}

}
