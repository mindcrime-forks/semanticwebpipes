package org.deri.execeng.rdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * The MIX operator is used to merge triples from multiple RDF sources. Each RDF Source can be either a constant (directly input as RDF/XML) or another Pipe operator which can output RDF/XML data. There is an optional attribute &quot;uri&quot;. If it is speficfied, then the sourcedata will be placed into an named graph with attribute's value as graph name.
<pre>
Note: Constant RDF/XML text has to be wrapped into a CDATA section

Syntax template:

&lt;simplemix&gt;
&lt;source&gt;Enter one or more sources&lt;/source&gt;
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
	final Logger logger = LoggerFactory.getLogger(SimpleMixBox.class);
	
	 public void execute(){
    	 buffer= new SesameMemoryBuffer();
    	 mergeInputs();
    	 isExecuted=true;
     }     
          
}
