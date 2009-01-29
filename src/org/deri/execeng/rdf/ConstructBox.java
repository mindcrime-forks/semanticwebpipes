package org.deri.execeng.rdf;

import java.util.ArrayList;
import java.util.List;

import org.deri.execeng.core.PipeContext;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.util.RDFInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * The Construct query is used to create expected triples by using SPARQL-CONSTRUCT query on specified RDF sources. The output of this operator is the result of a SPARQL-CONSTRUCT query executed on the one or more sources. Each RDF Source can be either a constant (directly input as RDF/XML) or another Pipe operator which can output RDF/XML data. There is an optional attribute &quot;uri&quot;. If it is speficfied, then the source data will be placed into an named graph with attribute's value as graph name.
<pre>
Note: Constant RDF/XML text has to be wrapped into a CDATA section.

Syntax template:

&lt;construct&gt;

&lt;source uri=uri&gt;Enter one source syntax here!&lt;/source&gt;

...
&lt;query&gt;
    Enter SPARQL construct query here!
&lt;/query&gt;
&lt;/construct&gt;
</pre>
 *
 */
public class ConstructBox extends AbstractMerge {
	final Logger logger = LoggerFactory.getLogger(ConstructBox.class);
	
    private List<String> graphNames =new ArrayList<String>();
    private String constructQuery;

   
    public void execute(){
    	buffer= new SesameMemoryBuffer();
    	SesameMemoryBuffer tmp=new SesameMemoryBuffer();
    	mergeInputs(tmp);
    	try{    	  
    		tmp.getConnection().prepareGraphQuery(QueryLanguage.SPARQL,constructQuery).evaluate(new RDFInserter(buffer.getConnection()));
    	}
    	catch(Exception e){
    		logger.warn("problem executing construct box",e);
    	}
    	isExecuted=true;
    }

    public void initialize(PipeContext context,Element element){   
    	super.setContext(context);
    	List<Element> sources=XMLUtil.getSubElementByName(element, "source");
    	constructQuery=XMLUtil.getTextFromFirstSubEleByName(element, "query");
    	if((sources.size()<=0)&&(constructQuery==null)){
			logger.warn("Construct operator syntax error at "+element);
		    return;
		}
    	this.setConstructQuery(constructQuery.trim());
    	for(int i=0;i<sources.size();i++){
    		String opID=context.getPipeParser().getSourceOperatorId(sources.get(i));
    		if (null!=opID){
    			addStream(opID);
    	   	    graphNames.add(sources.get(i).getAttribute("uri"));
    		}
    	}    	
     }

	public String getConstructQuery() {
		return constructQuery;
	}

	public void setConstructQuery(String constructQuery) {
		this.constructQuery = constructQuery;
	}
}
