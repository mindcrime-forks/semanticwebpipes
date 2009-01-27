package org.deri.execeng.rdf;

import java.net.URLEncoder;
import java.util.List;

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

public class ForLoopBox extends RDFBox{
	final Logger logger = LoggerFactory.getLogger(ForLoopBox.class);
   
    private String srcListID;
    private String pipeCode=null;
	private PipeParser parser;
    public ForLoopBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
    }
        
    public org.deri.execeng.core.ExecBuffer getExecBuffer(){
   	    return buffer;
    }
    
    public void execute(){
    	if (!parser.getOpByID(srcListID).isExecuted()) parser.getOpByID(srcListID).execute();
    	if (!(parser.getOpByID(srcListID).getExecBuffer() instanceof SesameTupleBuffer)){
    		parser.log("sourcelist must contain Tuple set result, the FOR LOOP can't not be executed");    	
    		return;
    	}
    	
    	buffer=new SesameMemoryBuffer(parser); 
    	try{
    		TupleQueryResult tupleBuff=((SesameTupleBuffer)parser.getOpByID(srcListID).getExecBuffer()).getTupleQueryResult();
    		List<String> bindingNames=tupleBuff.getBindingNames();
	    	while (tupleBuff.hasNext()) {
	    	   PipeParser parser = new PipeParser();
	    	   String tmp=pipeCode;	    	    
			   BindingSet bindingSet = tupleBuff.next();		   
			   for(int i=0;i<bindingNames.size();i++){				   
			       tmp=tmp.replace("${{"+bindingNames.get(i)+"}}",
			    		          bindingSet.getValue(bindingNames.get(i)).toString());
			       try{
						tmp=tmp.replace(URLEncoder.encode("${{" + bindingNames.get(i) + "}}","UTF-8"),
												URLEncoder.encode(bindingSet.getValue(bindingNames.get(i)).stringValue(),"UTF-8"));						
				   }
				   catch(java.io.UnsupportedEncodingException e){
						parser.log(e);
				   }
			   }
			   Operator op = parser.parseCode(tmp); 
			   if(op instanceof RDFBox){
				   if(!(op.isExecuted())) op.execute();					
				   if(op.getExecBuffer()!=null)
					   op.stream(buffer);
			   }
			}
    	}catch(QueryEvaluationException e){
    		
    	}
   	   isExecuted=true;
    }
    
        
    public void initialize(Element element){    
    	java.io.StringWriter  strWriter =new java.io.StringWriter(); 
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
			parser.log(e);
		}
		pipeCode=strWriter.toString();
    	
    	Element srcListEle=XMLUtil.getFirstSubElementByName(element,"sourcelist");
    	srcListID=parser.getSource(srcListEle);
      	if (null==srcListID){
      		parser.log("<sourcelist> element must be set !!!");
      		//TODO : Handling error of lacking data set for FOR LOOP 	
      	}  
     }
}
