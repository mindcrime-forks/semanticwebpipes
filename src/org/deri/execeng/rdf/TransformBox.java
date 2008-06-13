package org.deri.execeng.rdf;

import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Box;
import org.deri.execeng.core.BoxParser;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.query.QueryLanguage;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Element;
import java.util.ArrayList;
public class TransformBox extends RDFBox{
   
    private ArrayList<Stream> baseStreams =new ArrayList<Stream>();
    private ArrayList<String> graphNames =new ArrayList<String>();
    private String constructQuery;
	
    public TransformBox(){  
    	buffer= new SesameMemoryBuffer();
    }
    
    public void addBaseStream(Stream stream,String uri){
   	    baseStreams.add(stream);
   	    graphNames.add(uri);
    }
    
    public void setConstructQuery(String query){
   	    constructQuery=query;
    }
    
    public org.deri.execeng.core.ExecBuffer getExecBuffer(){
   	    return buffer;
    }
    
    public void execute(){
       SesameMemoryBuffer tmp= new SesameMemoryBuffer();
       for(int i=0;i<baseStreams.size();i++){
    	   if(baseStreams.get(i) instanceof Box) 
			 if(!((Box)baseStreams.get(i)).isExecuted()) ((Box)baseStreams.get(i)).execute();
    	   if(graphNames.get(i)==null)
    		   baseStreams.get(i).streamming(tmp);
    	   else
    		   if(graphNames.get(i).trim().length()>0)
    		   ((Box)baseStreams.get(i)).streamming(tmp,graphNames.get(i));
    		   else baseStreams.get(i).streamming(tmp);
       }
       try{    	  
          tmp.getConnection().prepareGraphQuery(QueryLanguage.SPARQL,constructQuery.trim()).evaluate(new RDFInserter(buffer.getConnection()));
       }
       catch(RDFHandlerException e){    	   
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
   	   isExecuted=true;
    }
    
    public static Stream loadStream(Element element){    
    	TransformBox transformBox= new TransformBox();
    	java.util.ArrayList<Element> sources=BoxParser.getSubElementByName(element, "source");
    	String constructQuery=BoxParser.getTextFromFirstSubEleByName(element, "query");
    	if((sources.size()<=0)&&(constructQuery==null)){
			Stream.log.append("Construct operator syntax error at\n");
		    Stream.log.append(element.toString());
		    Stream.log.append("\n");
			return null;
		}
    	
    	for(int i=0;i<sources.size();i++){
    		Element tmp=BoxParser.getFirstSubElement((Element)(sources.get(i)));
    		Stream tmpStream=null;
 			if(tmp==null)
 				tmpStream= new TextBox(BoxParser.getTextData(sources.get(i)));
 			else
 				tmpStream=BoxParserImplRDF.loadStream(tmp);
    		transformBox.addBaseStream(tmpStream,sources.get(i).getAttribute("uri"));
    	}
    	
    	transformBox.setConstructQuery(constructQuery);
    	return transformBox;
     }
}
