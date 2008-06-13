package org.deri.execeng.rdf;
import java.util.Vector;

import org.deri.execeng.core.BoxParser;
import org.deri.execeng.model.Box;
import org.deri.execeng.model.Stream;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SameAsBox extends RDFBox{ 
	
	 private Vector<Stream> inputStreams = new Vector<Stream>();
	 public SameAsBox(){ 
		 buffer= new SesameMemoryBuffer();
     }
     
     public void addStream(Stream stream){
    	 inputStreams.add(stream);
     }
     
     
     public void execute(){
    	 for(int i=0;i<inputStreams.size();i++){
    		 Stream stream=(Stream)(inputStreams.elementAt(i));
    		 if(stream instanceof Box) 
        	      if(!((Box)stream).isExecuted()) ((Box)stream).execute();    		    
    		 if(stream!=null)
    		    stream.streamming(buffer);
    	 }
    	 
    	 RepositoryConnection conn = buffer.getConnection();
    	 Repository rep = conn.getRepository();
    	 
    	 Smoosher smusher = new Smoosher();
    	 try {
			smusher.smoosh(rep);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    	 
    	 isExecuted=true;
     }
     
     public static Stream loadStream(Element element){    	 
    	SameAsBox sameAsBox= new SameAsBox();
 		java.util.ArrayList<Element> childNodes=BoxParser.getSubElementByName(element, "source");
 		for(int i=0;i<childNodes.size();i++){
 			Element tmp=BoxParser.getFirstSubElement((Element)(childNodes.get(i)));
 			if(tmp==null){
 				sameAsBox.addStream(new TextBox(BoxParser.getTextData(childNodes.get(i))));
 			}
 			else
 				sameAsBox.addStream(BoxParserImplRDF.loadStream(tmp)); 			
 		}    		
 		return sameAsBox;
     }     
}