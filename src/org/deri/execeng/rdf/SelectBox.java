package org.deri.execeng.rdf;

import java.util.ArrayList;
import java.util.List;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class SelectBox extends AbstractMerge {
	final Logger logger = LoggerFactory.getLogger(SelectBox.class);

    private ArrayList<String> graphNames =new ArrayList<String>();
    private String selectQuery;    
	SesameTupleBuffer resultBuffer;
   
    public SelectBox(PipeParser parser,Element element){
		this.parser=parser;
		initialize(element);
    }
   
	
	public void stream(ExecBuffer outputBuffer){
	  /* if((buffer!=null)&&(outputBuffer!=null))	
		   buffer.streamming(outputBuffer);
	   else{
		   logger.debug("check"+(buffer==null));
	   }*/
    }
	
	public void stream(ExecBuffer outputBuffer,String uri){
	   	   //buffer.streamming(outputBuffer,uri);
	}
	
	
    public String toString(){
    	return resultBuffer.toString(); 
    }
    @Override
    public org.deri.execeng.core.ExecBuffer getExecBuffer(){
   	    return resultBuffer;
    }
    
    public void execute(){              
       SesameMemoryBuffer tmp= new SesameMemoryBuffer(parser);
       mergeInputs(tmp);
       
       try{   
    	   resultBuffer=new SesameTupleBuffer(parser,((tmp.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, selectQuery)).evaluate()));
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
    
    @Override
    protected void initialize(Element element){    
    	
    	List<Element> sources=XMLUtil.getSubElementByName(element, "source");
    	selectQuery=XMLUtil.getTextFromFirstSubEleByName(element, "query");
    	if((sources.size()<=0)&&(selectQuery==null)){
			parser.log("SELECT operator syntax error at");
		    parser.log(element.toString());			
		}
    	
    	for(int i=0;i<sources.size();i++){
    		String opID=parser.getSource(sources.get(i));
    		if (null!=opID){
    			addStream(opID);
    			graphNames.add(sources.get(i).getAttribute("uri"));
    		}
    	}
    	
     }
}
