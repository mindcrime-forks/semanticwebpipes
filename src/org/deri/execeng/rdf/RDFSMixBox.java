package org.deri.execeng.rdf;
import java.util.Vector;

import org.deri.execeng.core.BoxParser;
import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Box;
import org.w3c.dom.Element;
import org.deri.execeng.utils.XMLUtil;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFSMixBox extends RDFBox{ 
	
	 private Vector<Stream> inputStreams = new Vector<Stream>();
     private boolean isExecuted=false;
	 public RDFSMixBox(){
		 buffer= new SesameMemoryBuffer(SesameMemoryBuffer.RDFS);
     }
     
     public void addStream(Stream stream){
    	 inputStreams.add(stream);
     }
     
     public org.deri.execeng.core.ExecBuffer getExecBuffer(){
    	 return buffer;
     }
     
     public boolean isExecuted(){
    	 return isExecuted;
     }
     
     public void execute(){
    	 for(int i=0;i<inputStreams.size();i++){
    		 Stream stream=(Stream)(inputStreams.elementAt(i));
    		 if(stream instanceof Box){ 
    			 if(!((Box)stream).isExecuted()) ((Box)stream).execute();    		    
    		 }
    		 if(stream!=null)
    		    stream.streamming(buffer);
    	 }
    	 isExecuted=true;
     }
     
     public static Stream loadStream(Element element){    
    	RDFSMixBox rdfsMixBox= new RDFSMixBox();
    	java.util.ArrayList<Element> childNodes=XMLUtil.getSubElementByName(element, "source");
 		for(int i=0;i<childNodes.size();i++){
 			Element tmp=XMLUtil.getFirstSubElement((Element)(childNodes.get(i)));
 			if(tmp==null){
 				rdfsMixBox.addStream(new TextBox(XMLUtil.getTextData(childNodes.get(i))));
 			}
 			else
 				rdfsMixBox.addStream(BoxParserImplRDF.loadStream(tmp)); 			
 		}    		
 		return rdfsMixBox;
     }
     
}
