package org.deri.execeng.rdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.StringReader;
import java.io.StringWriter;
import org.deri.execeng.core.PipeParser;
import org.w3c.dom.Element;
import org.deri.execeng.utils.XMLUtil;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class OWLBox extends AbstractMerge{
	final Logger logger = LoggerFactory.getLogger(OWLBox.class);
	 String owlOpID;
	 public  OWLBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
     }
     
     public org.deri.execeng.core.ExecBuffer getExecBuffer(){
    	 return buffer;
     }
     
     public void execute(){
    	 //merge all input sources to Sesame buffer
    	 buffer= new SesameMemoryBuffer(parser);
    	 mergeInputs();
    	 
    	 //create a Jena Model containing input RDF data for reasoning from merged Sesame buffer
    	 Model data =createJenaModel(buffer);
    	 
    	 //create a Jena Model containing OWL schema from <owlsource> tag parsed into operator with ID owlOpID
    	 if (!parser.getOpByID(owlOpID).isExecuted()) parser.getOpByID(owlOpID).execute();
    	 Model schema =createJenaModel((SesameMemoryBuffer)parser.getOpByID(owlOpID).getExecBuffer());
    	 
    	 //create default Jena reasoner and infer implicit RDF triples
    	 Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
    	 reasoner = reasoner.bindSchema(schema);
    	 InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
    	 
    	 //write inferred triples back to opearator's buffer
    	 buffer = new SesameMemoryBuffer(parser);
    	 writeJenaModel(infmodel, buffer);
    	 
    	 isExecuted=true;
     }   
     
     private Model createJenaModel(SesameMemoryBuffer buffer){
    	 Model model = ModelFactory.createDefaultModel();
    	 model.read(new StringReader(buffer.toString()),null);
    	 return model;
     }
     
     private void writeJenaModel(Model model,SesameMemoryBuffer buffer){
    	 StringWriter writer =new StringWriter();
    	 model.write(writer);
    	 buffer.loadFromText(writer.getBuffer().toString());
     }
     
     @Override
     protected void initialize(Element element){
    	super.initialize(element); 
   		Element owlSrc =XMLUtil.getFirstSubElementByName(element, "owlsource");
    	
      	owlOpID=parser.getSource(owlSrc);
      	if (null==owlOpID){
      		parser.log("<owlsource> element must be set !!!");
      		//TODO : Handling error of lacking OWL data source 	
      	}  		
     } 
}
