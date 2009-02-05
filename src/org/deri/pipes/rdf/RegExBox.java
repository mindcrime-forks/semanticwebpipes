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
import java.util.Collections;
import java.util.List;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RegExBox extends AbstractMerge{
	private transient Logger logger = LoggerFactory.getLogger(RegExBox.class);
	private List<Rule> rules = new ArrayList<Rule>();
     
     public ExecBuffer execute(Context context) throws Exception{
    	 //merge all input sourceOperators to Sesame buffer
    	 SesameMemoryBuffer tmp= new SesameMemoryBuffer();
    	 mergeInputs(tmp,context);
    	 
    	 SesameMemoryBuffer buffer = new SesameMemoryBuffer();
    	 try{
			 tmp.getConnection().export(new ReplaceHandler(this,buffer));
		 }
		 catch(Exception e){
			 logger.warn("problem executing",e);
		 }
		 return buffer;
     }   
          
     
     
     public class ReplaceHandler extends RDFInserter{
  		public ReplaceHandler(RegExBox regexBox, SesameMemoryBuffer buffer){
  			super(buffer.getConnection());
  		}
  		

  		public void handleStatement(Statement st)
  		throws RDFHandlerException
  		{
  			Resource sub =st.getSubject();
  			URI pred=st.getPredicate();
  			Value obj=st.getObject();
  			for(Rule rule : rules){	  			
	  			if("uri".equals(rule.type)){
	  				if(sub instanceof URI){
	  					sub=replace((URI)sub,rule);
	  				}
	  				pred=replace(pred,rule); 
	  				if(obj instanceof URI){
	  					obj=replace((URI)obj,rule);
	  				}
	  			}
	  			else
	  				if(obj instanceof Literal){
	  					obj=replace((Literal)obj,rule);
	  				}
  			}	
  			super.handleStatement(new StatementImpl(sub,pred,obj));
  		}	
  		
  		public URI replace(URI uri,Rule rule){
  			return new URIImpl(uri.toString().replaceAll(rule.regex, rule.replacement));
  		}
  		
  		public Literal replace(Literal literal,Rule rule){
  			return new LiteralImpl(literal.stringValue().replaceAll(rule.regex, rule.replacement));
  		}
     }
     public void addRule(Rule rule){
    	 rules.add(rule);
     }
     public List<Rule> getRules(){
    	 return Collections.unmodifiableList(rules);
     }
     
     @XStreamAlias("rule")
	public
     static class Rule{
    	 @XStreamAsAttribute
    	 String type;
    	 @XStreamAsAttribute
    	 String regex;
    	 @XStreamAsAttribute
    	 String replacement;
     }
}
