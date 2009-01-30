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
package org.deri.pipes.rdf;
import java.util.ArrayList;
import java.util.List;

import org.deri.pipes.core.PipeContext;
import org.deri.pipes.utils.XMLUtil;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RegExBox extends AbstractMerge{
	final Logger logger = LoggerFactory.getLogger(RegExBox.class);
	 ArrayList<String> types,regexes,replacements;
     
     public ArrayList<String> getTypes(){
    	 return types;
     }
     
     public ArrayList<String> getRegexes(){
    	 return regexes;
     }
    
     public ArrayList<String> getReplacements(){
    	 return replacements;
     }
     
     public void execute(){
    	 //merge all input sources to Sesame buffer
    	 SesameMemoryBuffer tmp= new SesameMemoryBuffer();
    	 mergeInputs(tmp);
    	 
    	 buffer = new SesameMemoryBuffer();
    	 try{
			 tmp.getConnection().export(new ReplaceHandler(this));
		 }
		 catch(Exception e){
			 logger.warn("problem executing",e);
		 }
    	     	 
    	 isExecuted=true;
     }   
         
     @Override
     public void initialize(PipeContext context,Element element){
    	super.initialize(context,element); 
   		List<Element> ruleEles =XMLUtil.getSubElementByName(
   				                       XMLUtil.getFirstSubElementByName(element, "rules"),"rule");
   		types =new ArrayList<String>();
   		regexes= new ArrayList<String>();
   		replacements= new ArrayList<String>();
   		for(int i=0;i<ruleEles.size();i++){
   			String typeAttribute = ruleEles.get(i).getAttribute("type");
			if(typeAttribute.equalsIgnoreCase("uri")
   			   ||typeAttribute.equalsIgnoreCase("literal")){
   				
   				types.add(ruleEles.get(i).getAttribute("type").toLowerCase());
   				regexes.add(XMLUtil.getTextFromFirstSubEleByName(ruleEles.get(i),"regex"));
   				replacements.add(XMLUtil.getTextFromFirstSubEleByName(ruleEles.get(i),"replacement"));
   			}   			   
   			else{
   				logger.warn("'type' attribute of <rule> tag must be 'uri' or 'literal', not ["+typeAttribute+"]");
   			}
   			   
   		}
     } 
     
     
     public class ReplaceHandler extends RDFInserter{
  		public ReplaceHandler(RegExBox regexBox){
  			super(buffer.getConnection());
  		}
  		

  		public void handleStatement(Statement st)
  		throws RDFHandlerException
  		{
  			Resource sub =st.getSubject();
  			URI pred=st.getPredicate();
  			Value obj=st.getObject();
  			for(int i=0;i<getTypes().size();i++){	  			
	  			if(getTypes().get(i)=="uri"){
	  				if(sub instanceof URI){
	  					sub=replace((URI)sub,i);
	  				}
	  				pred=replace(pred,i); 
	  				if(obj instanceof URI){
	  					obj=replace((URI)obj,i);
	  				}
	  			}
	  			else
	  				if(obj instanceof Literal){
	  					obj=replace((Literal)obj,i);
	  				}
  			}	
  			super.handleStatement(new StatementImpl(sub,pred,obj));
  		}	
  		
  		public URI replace(URI uri,int i){
  			return new URIImpl(uri.toString().replaceAll(getRegexes().get(i), getReplacements().get(i)));
  		}
  		
  		public Literal replace(Literal literal,int i){
  			return new LiteralImpl(literal.stringValue().replaceAll(getRegexes().get(i), getReplacements().get(i)));
  		}
     }
}
