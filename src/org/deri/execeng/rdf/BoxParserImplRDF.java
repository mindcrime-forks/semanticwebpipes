package org.deri.execeng.rdf;
import org.apache.xerces.parsers.DOMParser;
import org.deri.execeng.core.BoxParser;
import org.deri.execeng.model.Stream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class BoxParserImplRDF extends BoxParser{
	
	public BoxParserImplRDF(){	
	}
    public Stream parse(String syntax){
    	return parse(new InputSource(new java.io.StringReader(syntax)));
    }
    public Stream parse(InputSource inputStream){
    	try {
            DOMParser parser = new DOMParser();
            parser.parse(inputStream);  
            
            // Search for the child node of the <code> element, parse and run pipe execution engine
            NodeList pipeCodeXMLElements = parser.getDocument().getDocumentElement().getElementsByTagName("code").item(0).getChildNodes();
            
            for(int i=0;i<pipeCodeXMLElements.getLength();i++){
    			switch (pipeCodeXMLElements.item(i).getNodeType()){
    				case Node.ELEMENT_NODE:
    					return loadStream((Element)pipeCodeXMLElements.item(i));
    			}
    		} 

        } catch (Exception e) {
        	System.out.print(e.toString()+"\n");
        	Stream.log.append(e.toString()+"\n");
        }
    	return null;
    }
    
    public Stream parseCode(String code){
    	try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new java.io.StringReader(code)));  
            return loadStream(parser.getDocument().getDocumentElement());
        } catch (Exception e) {
        	System.out.print(e.toString()+"\n");
        	Stream.log.append(e.toString()+"\n");
        }
    	return null;
    }
    
    //Danh Le :new Syntax for pipes (5th,Dec,2007)
    public static Stream loadStream(Element element){
    	System.out.println("element"+element.toString());
    	//patch generator box
    	if(element.getTagName().equalsIgnoreCase("smoosher")){    
    		return SameAsBox.loadStream(element);
    	}
    	
    	//patch generator box
    	if(element.getTagName().equalsIgnoreCase("patch-generator")){    
    		return PatchGeneratorBox.loadStream(element);
    	}
    	
    	//apply patch box
    	if(element.getTagName().equalsIgnoreCase("patch-executor")){    
    		return PatchExecutorBox.loadStream(element);
    	}
    	
    	//simple mix box
    	if(element.getTagName().equalsIgnoreCase("simplemix")){    
    		return SimpleMixBox.loadStream(element);
    	}
    	
    	//RDFS reasoner
    	if(element.getTagName().equalsIgnoreCase("rdfs")){    
    		return RDFSMixBox.loadStream(element);
    	}
    	
    	//RDF construct
    	if(element.getTagName().equalsIgnoreCase("construct")){    
    		return ConstructBox.loadStream(element);
		}
    	
    	//RDF construct
    	if(element.getTagName().equalsIgnoreCase("select")){    
    		return SelectBox.loadStream(element);
		}
    	
    	//RDF for loop
    	if(element.getTagName().equalsIgnoreCase("for")){    
    		return ForLoopBox.loadStream(element);
		}
    	 
    	//RDFFetch 
    	if(element.getTagName().equalsIgnoreCase("rdffetch")){    
    		return RDFFetchBox.loadStream(element);
		}
    	
    	//HTMLFetch 
    	if(element.getTagName().equalsIgnoreCase("htmlfetch")){    
    		return HTMLFetchBox.loadStream(element);
		}
    	
    	//XSLT 
    	if(element.getTagName().equalsIgnoreCase("xslt")){    
    		return XSLTBox.loadStream(element);
		}
    	
    	//XMLFetch 
    	if(element.getTagName().equalsIgnoreCase("xmlfetch")){    
    		return XMLFetchBox.loadStream(element);
		}
    	
    	
    	//XMLFetch 
    	if(element.getTagName().equalsIgnoreCase("xslfetch")){    
    		return XMLFetchBox.loadStream(element);
		}
    	
    	
    	//Tuple Fetch 
    	if(element.getTagName().equalsIgnoreCase("sparqlresultfetch")){    
    		return TupleQueryResultFetchBox.loadStream(element);
		}
    	
    	Stream tmp=StoredPipe.loadStream(element);
    	if(tmp!=null) return tmp;
    	Stream.log.append("Unreconigzed tag :");
	    Stream.log.append(element.getTagName());
	    Stream.log.append(element.toString());
	    Stream.log.append("\n");
	    //System.out.println("element"+element.toString());
    	return null;
    }
}
