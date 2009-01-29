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

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * The FOR operator will  invoke a parameterized pipe multiple times and merge the resulting outputs of each invocation. The &lt;sourcelist&gt; specifies an operator that ouputs a SPARQL result set. For each result in the set, the variable  values will be subtituted into the parametrized pipe specified in &lt;forloop&gt; and the pipe will be invoked.
<pre>
Syntax template:

&lt;for&gt;
   &lt;sourcelist&gt;
requires SPARQL XML FORMAT
 &lt;/sourcelist&gt;
&lt;forloop&gt;
    &lt;simplemix&gt;&lt;fetch&gt;&lt;location&gt;$uri$&lt;/location&gt;&lt;/fetch&gt;&lt;simplemix&gt;
&lt;/forloop&gt;
&lt;/for&gt;

Example (See also http://pipes.deri.org:8080/pipes/Pipes/?id=forloop  ): This pipe fech from the URIs of all five people known by the author of a FOAF file.

&lt;for&gt;
   &lt;sourcelist&gt;
   &lt;select&gt;
     &lt;source&gt;&lt;fetch&gt;&lt;location&gt;http://www.w3.org/People/Berners-Lee/card?&lt;/location&gt;&lt;/fetch&gt;&lt;/source&gt;    
&lt;query&gt;
       &lt;![CDATA[
         select ?uri where {?s &lt;http://xmlns.com/foaf/0.1/knows?&gt; ?uri} ORDER by desc(?uri) LIMIT 5
     ]]&gt;
   &lt;/query&gt;
 &lt;/select&gt;
   &lt;/sourcelist&gt; 
&lt;forloop&gt;
&lt;simplemix&gt;&lt;source&gt;&lt;fetch&gt;&lt;location&gt;$uri$&lt;/location&gt;&lt;/fetch&gt;&lt;/source&gt;&lt;/simplemix&gt;
&lt;/forloop&gt;
&lt;/for&gt;
</pre>
 *
 */
public class ForLoopBox extends RDFBox{
	final Logger logger = LoggerFactory.getLogger(ForLoopBox.class);
   
    private String srcListID=null;
    private String pipeCode=null;
        
    public ExecBuffer getExecBuffer(){
   	    return buffer;
    }
    
    public void execute(){
    	Operator operator = context.getOperatorExecuted(srcListID);
    	if (!(operator.getExecBuffer() instanceof SesameTupleBuffer)){
    		logger.warn("sourcelist must contain Tuple set result, the FOR LOOP cannot not be executed");    	
    		return;
    	}
    	
    	buffer=new SesameMemoryBuffer(); 
    	try{
    		TupleQueryResult tupleBuff=((SesameTupleBuffer)operator.getExecBuffer()).getTupleQueryResult();
    		List<String> bindingNames=tupleBuff.getBindingNames();
	    	while (tupleBuff.hasNext()) {
	    	   String tmp=pipeCode;	    	    
			   BindingSet bindingSet = tupleBuff.next();		   
			   for(int i=0;i<bindingNames.size();i++){				   
			       tmp=tmp.replace("${{"+bindingNames.get(i)+"}}",
			    		          bindingSet.getValue(bindingNames.get(i)).toString());
			       try{
						tmp=tmp.replace(URLEncoder.encode("${{" + bindingNames.get(i) + "}}","UTF-8"),
												URLEncoder.encode(bindingSet.getValue(bindingNames.get(i)).stringValue(),"UTF-8"));						
				   }
				   catch(UnsupportedEncodingException e){
						logger.warn("UTF-8 support is required by the JVM specification");
				   }
			   }
	    	   PipeParser parser = new PipeParser();
			   Operator op = parser.parseCode(tmp); 
			   if(op instanceof RDFBox){
				   if(!(op.isExecuted())) {
					   op.execute();					
				   }
				   if(op.getExecBuffer()!=null){
					   op.stream(buffer);					   
				   }
			   }
	    	}
    	}catch(QueryEvaluationException e){
    		logger.warn("error in for loop",e);
    	}
   	   isExecuted=true;
    }
    
        
    public void initialize(PipeContext context, Element element){
    	super.setContext(context);
    	StringWriter  strWriter =new StringWriter(); 
		try{
			java.util.Properties props = 
			org.apache.xml.serializer.OutputPropertiesFactory.getDefaultMethodProperties(org.apache.xml.serializer.Method.XML);
			org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory.getSerializer(props);
			ser.setWriter(strWriter);
			ser.asDOMSerializer().serialize((Element)(XMLUtil.getFirstChildByType(
							                   			XMLUtil.getFirstSubElementByName(element,"forloop"),
							                               Node.ELEMENT_NODE)));
		}
		catch(java.io.IOException e){
			logger.warn("problem during initialize",e);
		}
		setPipeCode(strWriter.toString());
    	
    	Element srcListEle=XMLUtil.getFirstSubElementByName(element,"sourcelist");
    	setSrcListID(context.getPipeParser().getSourceOperatorId(srcListEle));
      	if (null==srcListID){
      		logger.warn("<sourcelist> element must be set !!!");
      		//TODO : Handling error of lacking data set for FOR LOOP 	
      	}  
     }

	public String getSrcListID() {
		return srcListID;
	}

	public void setSrcListID(String srcListID) {
		this.srcListID = srcListID;
	}

	public String getPipeCode() {
		return pipeCode;
	}

	public void setPipeCode(String pipeCode) {
		this.pipeCode = pipeCode;
	}
}
