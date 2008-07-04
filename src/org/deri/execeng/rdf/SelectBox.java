package org.deri.execeng.rdf;

import java.util.ArrayList;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Box;
import org.deri.execeng.model.Stream;
import org.deri.execeng.utils.XMLUtil;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.w3c.dom.Element;

public class SelectBox implements Box {

	private ArrayList<Stream> baseStreams =new ArrayList<Stream>();
    private ArrayList<String> graphNames =new ArrayList<String>();
    private String selectQuery;
    protected boolean isExecuted=false;
	SesameTupleBuffer buffer;
    public SelectBox(){  
    }
   
	
	public void streamming(ExecBuffer outputBuffer){
	  /* if((buffer!=null)&&(outputBuffer!=null))	
		   buffer.streamming(outputBuffer);
	   else{
		   System.out.println("check"+(buffer==null));
	   }*/
    }
	
	public void streamming(ExecBuffer outputBuffer,String uri){
	   	   //buffer.streamming(outputBuffer,uri);
	}
	
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    public void addBaseStream(Stream stream,String uri){
   	    baseStreams.add(stream);
   	    graphNames.add(uri);
    }
    
    public void setSelectQuery(String query){
   	    selectQuery=query;
    }
    
    public org.deri.execeng.core.ExecBuffer getExecBuffer(){
   	    return buffer;
    }
    
    public void execute(){
       System.out.println("before Select exectute");	
       SesameMemoryBuffer tmp= new SesameMemoryBuffer();
       for(int i=0;i<baseStreams.size();i++){
    	   if(baseStreams.get(i) instanceof Box) 
			 if(!((Box)baseStreams.get(i)).isExecuted()) ((Box)baseStreams.get(i)).execute();
    	  // System.out.println("stream result"+((Box)baseStreams.get(i)).getExecBuffer().toString());
    	   if(graphNames.get(i)==null)
    		   baseStreams.get(i).streamming(tmp);
    	   else
    		   if(graphNames.get(i).trim().length()>0)
    		   ((Box)baseStreams.get(i)).streamming(tmp,graphNames.get(i));
    		   else baseStreams.get(i).streamming(tmp);
       }
       try{   
    	   System.out.println("query :\n" +selectQuery);
    	   buffer=new SesameTupleBuffer(((tmp.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, selectQuery)).evaluate()));
    	 //  System.out.println("query result:" +buffer.toString());
    	   System.out.println("query successful!");
       }
       catch(MalformedQueryException e){ 
      	   log.append(e.toString()+"\n");
         }
         catch(QueryEvaluationException e){
      	   log.append(e.toString()+"\n");
         }
         catch(RepositoryException e){
      	   log.append(e.toString()+"\n");
       }
         System.out.println("query log:" +log.toString());
   	   isExecuted=true;
    }
    
    public static Stream loadStream(Element element){    
    	SelectBox transformBox= new SelectBox();
    	java.util.ArrayList<Element> sources=XMLUtil.getSubElementByName(element, "source");
    	String constructQuery=XMLUtil.getTextFromFirstSubEleByName(element, "query");
    	if((sources.size()<=0)&&(constructQuery==null)){
			Stream.log.append("Construct operator syntax error at\n");
		    Stream.log.append(element.toString());
		    Stream.log.append("\n");
			return null;
		}
    	
    	for(int i=0;i<sources.size();i++){
    		Element tmp=XMLUtil.getFirstSubElement((Element)(sources.get(i)));
    		Stream tmpStream=null;
 			if(tmp==null)
 				tmpStream= new TextBox(XMLUtil.getTextData(sources.get(i)));
 			else
 				tmpStream=BoxParserImplRDF.loadStream(tmp);
    		transformBox.addBaseStream(tmpStream,sources.get(i).getAttribute("uri"));
    	}
    	
    	transformBox.setSelectQuery(constructQuery);
    	System.out.println("select load log "+Stream.log.toString());
    	return transformBox;
     }
}
