package org.deri.execeng.rdf;
import java.io.StringReader;
import java.io.StringWriter;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

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
	 String owlOpID = null;
     
     public ExecBuffer getExecBuffer(){
    	 return buffer;
     }
     
     public void execute(){
    	 //merge all input sources to Sesame buffer
    	 buffer= new SesameMemoryBuffer();
    	 mergeInputs();
    	 
    	 //create a Jena Model containing input RDF data for reasoning from merged Sesame buffer
    	 Model data =createJenaModel(buffer);
    	 
    	 //create a Jena Model containing OWL schema from <owlsource> tag parsed into operator with ID owlOpID
    	 Operator operator = context.getOperatorExecuted(owlOpID);
		Model schema =createJenaModel((SesameMemoryBuffer)operator.getExecBuffer());
    	 
    	 //create default Jena reasoner and infer implicit RDF triples
    	 Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
    	 reasoner = reasoner.bindSchema(schema);
    	 InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
    	 
    	 //write inferred triples back to opearator's buffer
    	 buffer = new SesameMemoryBuffer();
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
     public void initialize(PipeContext context, Element element){
    	super.initialize(context,element); 
   		Element owlSrc =XMLUtil.getFirstSubElementByName(element, "owlsource");
    	
      	setOwlOpID(context.getPipeParser().getSourceOperatorId(owlSrc));
      	if (null==owlOpID){
      		logger.warn("no owlsource found for element "+owlSrc);
      		//TODO : Handling error of lacking OWL data source 	
      	}  		
     }

	public String getOwlOpID() {
		return owlOpID;
	}

	public void setOwlOpID(String owlOpID) {
		this.owlOpID = owlOpID;
	} 
}
