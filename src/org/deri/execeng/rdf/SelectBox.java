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
