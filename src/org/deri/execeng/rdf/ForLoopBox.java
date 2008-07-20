package org.deri.execeng.rdf;

import java.net.URLEncoder;

import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Box;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
//import org.apache.xml.serialize.XMLSerializer;
//import org.apache.xml.serialize.OutputFormat;
import org.deri.execeng.utils.XMLUtil;

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
    	//System.out.println("For execute\n"+pipeCode);
    	if ((sourcelist==null)||(pipeCode==null)) return;
    	java.util.List<String> bindingNames = sourcelist.getBindingNames();
    	Stream stream;
    	BoxParserImplRDF parser = new BoxParserImplRDF();
    	try{
	    	while (sourcelist.hasNext()) {
	    	   String tmp=pipeCode;
	    	   
			   BindingSet bindingSet = sourcelist.next();		   
			   for(int i=0;i<bindingNames.size();i++){				   
			       tmp=tmp.replace("${{"+bindingNames.get(i)+"}}",
			    		          bindingSet.getValue(bindingNames.get(i)).toString());
			       try{
						tmp=tmp.replace(URLEncoder.encode("${" + bindingNames.get(i) + "}","UTF-8"),
												URLEncoder.encode(bindingSet.getValue(bindingNames.get(i)).toString(),"UTF-8"));
					}
					catch(java.io.UnsupportedEncodingException e){
						e.printStackTrace();
					}
			   }
			   //System.out.println("For loop\n"+bindingNames.get(0)+"\n"+tmp);
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
			java.util.Properties props = 
			org.apache.xml.serializer.OutputPropertiesFactory.getDefaultMethodProperties(org.apache.xml.serializer.Method.XML);
			org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory.getSerializer(props);
			ser.setWriter(strWriter);
			ser.asDOMSerializer().serialize((Element)(XMLUtil.getFirstChildByType(
							                   			XMLUtil.getFirstSubElementByName(element,"forloop"),
							                               Node.ELEMENT_NODE)));
			/*(new XMLSerializer(strWriter,
					new OutputFormat())).serialize(
							(Element)(XMLUtil.getFirstChildByType(
							    XMLUtil.getFirstSubElementByName(element,"forloop"),
							       Node.ELEMENT_NODE)));*/
		}
		catch(java.io.IOException e){ }
		forLoopBox.setPipeCode(strWriter.toString());
    	
    	Element sourcelistEle=XMLUtil.getFirstSubElementByName(element,"sourcelist");
    	String tmpStr=XMLUtil.getTextData(sourcelistEle);
    	SesameTupleBuffer tmpSourceList=null;
    	if(tmpStr!=null){
    		tmpSourceList=new SesameTupleBuffer();
    		tmpSourceList.loadFromText(tmpStr);
    	}
    	else{
    		Element tmpSubEle=XMLUtil.getFirstSubElementByName(sourcelistEle, "sparqlresultfetch");
    		if(tmpSubEle!=null){
    			TupleQueryResultFetchBox fetch=(TupleQueryResultFetchBox)TupleQueryResultFetchBox.loadStream(tmpSubEle);
    			fetch.execute();
    			tmpSourceList=(SesameTupleBuffer)fetch.getExecBuffer();
    		}
    		else{
    			tmpSubEle=XMLUtil.getFirstSubElementByName(sourcelistEle, "select");
    			if(tmpSubEle!=null){
    				SelectBox select=(SelectBox)SelectBox.loadStream(tmpSubEle);
        			select.execute();
        			tmpSourceList=(SesameTupleBuffer)select.getExecBuffer();
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
