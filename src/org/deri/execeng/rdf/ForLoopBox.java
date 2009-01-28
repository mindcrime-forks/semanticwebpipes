package org.deri.execeng.rdf;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
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
   
    private String srcListID=null;
    private String pipeCode=null;
        
    public ExecBuffer getExecBuffer(){
   	    return buffer;
    }
    
    public void execute(){
    	Operator operator = context.getOperatorExecuted(srcListID);
    	if (!(operator.getExecBuffer() instanceof SesameTupleBuffer)){
    		logger.warn("sourcelist must contain Tuple set result, the FOR LOOP cannot not be executed");    	
    		return;
    	}
    	
    	buffer=new SesameMemoryBuffer(); 
    	try{
    		TupleQueryResult tupleBuff=((SesameTupleBuffer)operator.getExecBuffer()).getTupleQueryResult();
    		List<String> bindingNames=tupleBuff.getBindingNames();
	    	while (tupleBuff.hasNext()) {
	    	   String tmp=pipeCode;	    	    
			   BindingSet bindingSet = tupleBuff.next();		   
			   for(int i=0;i<bindingNames.size();i++){				   
			       tmp=tmp.replace("${{"+bindingNames.get(i)+"}}",
			    		          bindingSet.getValue(bindingNames.get(i)).toString());
			       try{
						tmp=tmp.replace(URLEncoder.encode("${{" + bindingNames.get(i) + "}}","UTF-8"),
												URLEncoder.encode(bindingSet.getValue(bindingNames.get(i)).stringValue(),"UTF-8"));						
				   }
				   catch(UnsupportedEncodingException e){
						logger.warn("UTF-8 support is required by the JVM specification");
				   }
			   }
	    	   PipeParser parser = new PipeParser();
			   Operator op = parser.parseCode(tmp); 
			   if(op instanceof RDFBox){
				   if(!(op.isExecuted())) {
					   op.execute();					
				   }
				   if(op.getExecBuffer()!=null){
					   op.stream(buffer);					   
				   }
			   }
	    	}
    	}catch(QueryEvaluationException e){
    		logger.warn("error in for loop",e);
    	}
   	   isExecuted=true;
    }
    
        
    public void initialize(PipeContext context, Element element){
    	super.setContext(context);
    	StringWriter  strWriter =new StringWriter(); 
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
			logger.warn("problem during initialize",e);
		}
		setPipeCode(strWriter.toString());
    	
    	Element srcListEle=XMLUtil.getFirstSubElementByName(element,"sourcelist");
    	setSrcListID(context.getPipeParser().getSourceOperatorId(srcListEle));
      	if (null==srcListID){
      		logger.warn("<sourcelist> element must be set !!!");
      		//TODO : Handling error of lacking data set for FOR LOOP 	
      	}  
     }

	public String getSrcListID() {
		return srcListID;
	}

	public void setSrcListID(String srcListID) {
		this.srcListID = srcListID;
	}

	public String getPipeCode() {
		return pipeCode;
	}

	public void setPipeCode(String pipeCode) {
		this.pipeCode = pipeCode;
	}
}
