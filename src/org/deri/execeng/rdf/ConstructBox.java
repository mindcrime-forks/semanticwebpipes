package org.deri.execeng.rdf;

import java.util.Vector;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.w3c.dom.Element;

public class ConstructBox extends AbstractMerge {
	
    private Vector<String> graphNames =new Vector<String>();
    private String constructQuery;
	
    public ConstructBox(PipeParser parser, Element element){  
    	this.parser=parser;
    	initialize(element);    	
    }
   
    public void execute(){
       buffer= new SesameMemoryBuffer(parser);
       SesameMemoryBuffer tmp=new SesameMemoryBuffer(parser);
       mergeInputs(tmp);
       
       try{    	  
          tmp.getConnection().prepareGraphQuery(QueryLanguage.SPARQL,constructQuery).evaluate(new RDFInserter(buffer.getConnection()));
       }
       catch(RDFHandlerException e){
    	   parser.log(e.toString());
       }
       catch(MalformedQueryException e){ 
    	   parser.log(e.toString());
       }
       catch(QueryEvaluationException e){
    	   parser.log(e.toString());
       }
       catch(RepositoryException e){
    	   parser.log(e.toString());
       }
   	   isExecuted=true;
    }
    
    protected void initialize(Element element){   
    	
    	java.util.ArrayList<Element> sources=XMLUtil.getSubElementByName(element, "source");
    	constructQuery=XMLUtil.getTextFromFirstSubEleByName(element, "query");
    	if((sources.size()<=0)&&(constructQuery==null)){
			parser.log("Construct operator syntax error at");
		    parser.log(element.toString());
		    return;
		}
    	constructQuery=constructQuery.trim();
    	for(int i=0;i<sources.size();i++){
    		String opID=parser.getSource(sources.get(i));
    		if (null!=opID){
    			addStream(opID);
    	   	    graphNames.add(sources.get(i).getAttribute("uri"));
    		}
    	}    	
     }
}
