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
package org.deri.execeng.rdf;

import java.util.ArrayList;
import java.util.List;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.QueryLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 Similar to CONSTRUCT operator, the SELECT operator is used performs a SELECT query and outputs the result in the SPARQL-Result XML format. This  operator is  only used as an input for FOR operator.  Each RDF Source can be either a constant (directly input as RDF/XML) or another Pipe operator which can output RDF/XML data. There is an optional attribute &quot;uri&quot;. If it is speficfied, then the sourcedata will be placed into an named graph with attribute's value as graph name.
<pre>
Note: Constant RDF/XML text has to be wrapped into a CDATA section.

Syntax template:

&lt;select&gt;
&lt;source uri=&quot;uri&quot;&gt;Enter one source syntax here!&lt;/source&gt;
&lt;source &gt;Enter one source syntax here!&lt;/source&gt;
&lt;query&gt;
    Enter SPARQL SELECT query here!
&lt;/query&gt;
&lt;/select&gt;

Example:

&lt;select&gt;
     &lt;source&gt;&lt;fetch&gt;&lt;location&gt;http://www.w3.org/People/Berners-Lee/card?&lt;/location&gt;&lt;/fetch&gt;&lt;/source&gt;
     &lt;source&gt;&lt;fetch&gt;&lt;location&gt;http://g1o.net/foaf.rdf?&lt;/location&gt;&lt;/fetch&gt;&lt;/source&gt;
&lt;query&gt;
       &lt;![CDATA[
         select ?uri where {?s &lt;http://xmlns.com/foaf/0.1/knows?&gt; ?uri} ORDER by desc(?uri) LIMIT 2 offset 10
     ]]&gt;
   &lt;/query&gt;
 &lt;/select&gt;
 </pre>
 * @author robful
 *
 */
public class SelectBox extends AbstractMerge {
	final Logger logger = LoggerFactory.getLogger(SelectBox.class);

    private ArrayList<String> graphNames =new ArrayList<String>();
    private String selectQuery;    
	SesameTupleBuffer resultBuffer;   //TODO: this isn't written out.
	
	public void stream(ExecBuffer outputBuffer){
		logger.error("Method not implemented");
	  /* if((buffer!=null)&&(outputBuffer!=null))	
		   buffer.streamming(outputBuffer);
	   else{
		   logger.debug("check"+(buffer==null));
	   }*/
    }
	
	public void stream(ExecBuffer outputBuffer,String uri){
		logger.error("Method not implemented");
	   	   //buffer.streamming(outputBuffer,uri);
	}
	
	    
    public void execute(){              
       SesameMemoryBuffer tmp= new SesameMemoryBuffer();
       mergeInputs(tmp);
       
       try{   
    	   resultBuffer=new SesameTupleBuffer(((tmp.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, selectQuery)).evaluate()));
       }
       catch(Exception e){ 
    	   logger.warn("error during execution",e);
       }
   	   isExecuted=true;
    }
    
    @Override
    public void initialize(PipeContext context, Element element){    
    	
    	List<Element> sources=XMLUtil.getSubElementByName(element, "source");
    	selectQuery=XMLUtil.getTextFromFirstSubEleByName(element, "query");
    	if((sources.size()<=0)&&(selectQuery==null)){
			logger.warn("SELECT operator syntax error at "+element.toString());			
		}
    	
    	for(int i=0;i<sources.size();i++){
    		String opID=context.getPipeParser().getSourceOperatorId(sources.get(i));
    		if (null!=opID){
    			addStream(opID);
    			graphNames.add(sources.get(i).getAttribute("uri"));
    		}
    	}
    	
     }
}
