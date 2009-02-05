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
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Context;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The MIX operator is used to merge triples from multiple RDF sourceOperators. Each RDF Source can be either a constant (directly input as RDF/XML) or another PipeConfig operator which can output RDF/XML data. There is an optional attribute &quot;uri&quot;. If it is speficfied, then the sourcedata will be placed into an named graph with attribute's value as graph name.
<pre>
Note: Constant RDF/XML text has to be wrapped into a CDATA section

Syntax template:

&lt;simplemix&gt;
&lt;source&gt;Enter one or more sourceOperators&lt;/source&gt;
....
&lt;/simplemix&gt;

Example (see http://pipes.deri.org:8080/pipes/Pipes/?id=simplemix )

   &lt;simplemix&gt;
     &lt;source&gt;
     &lt;fetch&gt;
     &lt;location&gt;http://www.w3.org/People/Berners-Lee/card?&lt;/location&gt;
     &lt;/fetch&gt;
     &lt;/source&gt;
     &lt;source&gt;
     &lt;![CDATA[
    &lt;rdf:RDF
   xmlns:j.0=&quot;http://xmlns.com/foaf/0.1/&quot;&gt;http://xmlns.com/foaf/0.1/&quot;
    xmlns:wot=&quot;http://xmlns.com/wot/0.1/&quot;
    xmlns:rdf=&quot;http://www.w3.org/1999/02/22-rdf-syntax-ns#&quot;&gt;
  &lt;j.0:PersonalProfileDocument&gt;
    &lt;j.0:primaryTopic&gt;
      &lt;j.0:Person rdf:about=&quot;http://g1o.net/foaf.rdf#me&quot;
         j.0:givenname=&quot;Giovanni&quot;
         j.0:family_name=&quot;Tummarello&quot;
         j.0:nick=&quot;Jccq&quot;
         j.0:title=&quot;Ph.D&quot;
         j.0:icqChatID=&quot;68832951&quot;&gt;
        &lt;j.0:knows&gt;
         ...
&lt;/rdf:RDF&gt;
     ]]&gt;
     &lt;/source&gt;
   &lt;/simplemix&gt;
</pre>
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SimpleMixBox extends AbstractMerge{ 
	private transient Logger logger = LoggerFactory.getLogger(SimpleMixBox.class);
	
	 public ExecBuffer execute(Context context) throws Exception{
		 SesameMemoryBuffer buffer= new SesameMemoryBuffer();
    	 mergeInputs(buffer,context);
    	 return buffer;
     }     
          
}
