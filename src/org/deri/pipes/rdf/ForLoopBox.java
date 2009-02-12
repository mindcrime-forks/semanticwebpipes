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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.deri.pipes.model.SesameTupleBuffer;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private transient Logger logger = LoggerFactory.getLogger(ForLoopBox.class);
    private Source sourcelist;
    private Source forloop;
        
    
    public ExecBuffer execute(Context context) throws Exception{
    	if(sourcelist == null){
    		logger.warn("sourcelist is null, cannot execute for loop");
    		return new SesameMemoryBuffer();
    	}
    	if(forloop == null){
    		logger.warn("forloop is null, cannot execute for loop");
    		return new SesameMemoryBuffer();    		
    	}
 		ExecBuffer operatorResult = sourcelist.execute(context);
		if (!(operatorResult instanceof Iterable)){
    		logger.warn("sourcelist must contain an iterable result the input buffer is "+operatorResult);   
    		return new SesameMemoryBuffer();    		
    	}   	
 		return executeForLoop(context, (Iterable<Map<String,String>>)operatorResult);
    }

    private ExecBuffer executeForLoop(Context context,Iterable<Map<String, String>> result) throws Exception {
    	SesameMemoryBuffer buffer = new SesameMemoryBuffer();
    	int maxConcurrent = 10;
    	List<Operator> execJobs = new ArrayList<Operator>();
    	try{
    		for(Map<String,String> map : result){

    			String operatorXml=context.getEngine().serialize(forloop);    	    
    			operatorXml = bindVariables(operatorXml, map);
    			logger.debug("parsing:"+operatorXml);
    			Operator op = context.getEngine().parse(operatorXml);
    			execJobs.add(op);
    			if(execJobs.size() == maxConcurrent){
    				executeJobsConcurrentlyAndClearList(context, buffer,execJobs);
    			}
    			executeJobsConcurrentlyAndClearList(context, buffer,execJobs);

    		}
    	}catch(Exception e){
    		logger.warn("error in for loop",e);
    	}
    	return buffer;
	}

	private void executeJobsConcurrentlyAndClearList(Context context,
			SesameMemoryBuffer buffer, List<Operator> execJobs)
			throws InterruptedException,IOException {
		if(execJobs.size()==0){
			return;
		}
		try{
			ExecBuffer execBuffer = context.getEngine().execute(execJobs, context);
			execBuffer.stream(buffer);
		}finally{
			execJobs.clear();
		}
	}

	private String bindVariables(String operatorXml, Map<String,String> bindings) {
		for(String key : bindings.keySet()){				   
				String value = bindings.get(key);
				operatorXml=operatorXml.replace("${{"+key+"}}",value);
		       try{
					operatorXml=operatorXml.replace(URLEncoder.encode("${{" + key + "}}","UTF-8"),
											URLEncoder.encode(value,"UTF-8"));						
			   }
			   catch(UnsupportedEncodingException e){
					logger.warn("UTF-8 support is required by the JVM specification");
			   }
		   }
		return operatorXml;
	}
    

}
