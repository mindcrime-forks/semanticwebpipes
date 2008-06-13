package org.deri.execeng.rdf;

import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Box;
import org.deri.execeng.core.BoxParser;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

public class ForLoopBox extends RDFBox{
   
    private TupleQueryResult sourcelist=null;
    private String pipeCode=null;
	
    public ForLoopBox(){  
    	buffer= new SesameMemoryBuffer();
    }
    
    public void setSourceList(TupleQueryResult sourcelist){
   	    this.sourcelist=sourcelist;
    }
    
    public void setPipeCode(String pipeCode){
   	    this.pipeCode=pipeCode;
    }
    
    public org.deri.execeng.core.ExecBuffer getExecBuffer(){
   	    return buffer;
    }
    
    public void execute(){
    	if ((sourcelist==null)||(pipeCode==null)) return;
    	java.util.List<String> bindingNames = sourcelist.getBindingNames();
    	Stream stream;
    	BoxParserImplRDF parser = new BoxParserImplRDF();
    	try{
	    	while (sourcelist.hasNext()) {
	    	   String tmp=pipeCode;	
			   BindingSet bindingSet = sourcelist.next();		   
			   for(int i=0;i<bindingNames.size();i++){				   
			       tmp=tmp.replace("$"+bindingNames.get(i)+"$",
			    		          bindingSet.getValue(bindingNames.get(i)).toString());
			   }
			   stream = parser.parseCode(tmp);
			   if (stream instanceof Box) 
				   if(!((Box)stream).isExecuted()) ((Box)stream).execute();					
			   if(stream!=null)
		    		    stream.streamming(buffer);    
			}
    	}catch(QueryEvaluationException e){
    		
    	}
   	   isExecuted=true;
    }
    
        
    public static Stream loadStream(Element element){    
    	ForLoopBox forLoopBox= new ForLoopBox();
    	java.io.StringWriter  strWriter =new java.io.StringWriter(); 
		try{
			(new XMLSerializer(strWriter,
					new OutputFormat())).serialize(
							(Element)(BoxParser.getFirstChildByType(
							    BoxParser.getFirstSubElementByName(element,"forloop"),
							       Node.ELEMENT_NODE)));
		}
		catch(java.io.IOException e){ }
		forLoopBox.setPipeCode(strWriter.toString());
    	
    	Element sourcelistEle=BoxParser.getFirstSubElementByName(element,"sourcelist");
    	String tmpStr=BoxParser.getTextData(sourcelistEle);
    	SesameTupleBuffer tmpSourceList=null;
    	if(tmpStr!=null){
    		tmpSourceList=new SesameTupleBuffer();
    		tmpSourceList.loadFromText(tmpStr);
    	}
    	else{
    		Element tmpSubEle=BoxParser.getFirstSubElementByName(sourcelistEle, "fetch");
    		if(tmpSubEle!=null){
    			FetchBox fetch=(FetchBox)FetchBox.loadStream(tmpSubEle);
    			fetch.execute();
    			tmpSourceList=(SesameTupleBuffer)fetch.getExecBuffer();
    		}
    		else{
    			tmpSubEle=BoxParser.getFirstSubElementByName(sourcelistEle, "select");
    			if(tmpSubEle!=null){
    				tmpSourceList=new SesameTupleBuffer();
    	    		tmpSourceList.loadFromSelect(tmpSubEle);
        		}
    			else{
    				//syntax error
    				return null;
    			}
    		}
    	}
    	forLoopBox.setSourceList(tmpSourceList.getTupleQueryResult());    	
		return forLoopBox;
     }
}
