package org.deri.execeng.rdf;
import org.deri.execeng.core.ExecBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * 
Similar to MIX operator,this RDFS operator will merge specified RDF sources and then infer implicit triples from the merged triples.The output of this operator is also RDFXML format.  Each RDF Source can be either a constant (directly input as RDF/XML) or another Pipe operator which can output RDF/XML data. There is an optional attribute &quot;uri&quot;. If it is speficfied, then the sourcedata will be placed into an named graph with attribute's value as graph name.
<pre>
Note: Constant RDF/XML text has to be wrapped into a CDATA section.

Syntax template:

&lt;rdfs&gt;
&lt;source&gt;Enter one or more sources&lt;/source&gt;

&lt;/rdfs&gt;
 

Example (See also http://pipes.deri.org:8080/pipes/Pipes/?id=RDFSReasoner ? )

&lt;construct&gt;
&lt;source&gt;
&lt;rdfs&gt;
     &lt;source&gt;&lt;fetch&gt;&lt;location&gt;http://www.w3.org/People/Berners-Lee/card?&lt;/location&gt;&lt;/fetch&gt;&lt;/source&gt;
     &lt;source&gt;&lt;fetch&gt;&lt;location&gt;http://xmlns.com/foaf/spec/index.rdf?&lt;/location&gt;&lt;/fetch&gt;&lt;/source&gt;
 &lt;/rdfs&gt;
&lt;/source&gt;
&lt;query&gt;
&lt;![CDATA[
PREFIX foaf:   &lt;http://xmlns.com/foaf/0.1/?&gt;
PREFIX rdf:   &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#?&gt;
CONSTRUCT

{ ?x foaf:name ?name }
WHERE

{ ?x foaf:name ?name . ?x rdf:type foaf:Agent}
]]&gt;
&lt;/query&gt;
&lt;/construct&gt;
</pre>
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFSMixBox extends AbstractMerge{
	final Logger logger = LoggerFactory.getLogger(RDFSMixBox.class);
          
     public void execute(){
    	 buffer= new SesameMemoryBuffer(SesameMemoryBuffer.RDFS);
    	 mergeInputs();    	 
    	 isExecuted=true;
     }     
}
