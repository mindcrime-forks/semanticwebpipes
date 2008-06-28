package org.deri.execeng.rdf;
import java.util.Vector;
import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Box;
import org.deri.execeng.core.BoxParser;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SimpleMixBox extends RDFBox{ 
	
	 private Vector<Stream> inputStreams = new Vector<Stream>();
	 public SimpleMixBox(){ 
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
    		 if((stream!=null)&&(buffer!=null))
    		    stream.streamming(buffer);
    	 }
    	 isExecuted=true;
     }
     
     public static Stream loadStream(Element element){    	 
    	SimpleMixBox simpleMixBox= new SimpleMixBox();
 		java.util.ArrayList<Element> childNodes=XMLUtil.getSubElementByName(element, "source");
 		for(int i=0;i<childNodes.size();i++){
 			Element tmp=XMLUtil.getFirstSubElement((Element)(childNodes.get(i)));
 			if(tmp==null){
 				simpleMixBox.addStream(new TextBox(XMLUtil.getTextData(childNodes.get(i))));
 			}
 			else
 				simpleMixBox.addStream(BoxParserImplRDF.loadStream(tmp)); 			
 		}    		
 		return simpleMixBox;
     }     
}
