/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.deri.pipes.rdf;
import java.io.StringReader;
import java.io.StringWriter;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.PipeContext;
import org.deri.pipes.model.Operator;
import org.deri.pipes.utils.XMLUtil;
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
	 String owlsource = null;
     
     public ExecBuffer getExecBuffer(){
    	 return buffer;
     }
     
     public void execute(){
    	 //merge all input sources to Sesame buffer
    	 buffer= new SesameMemoryBuffer();
    	 mergeInputs();
    	 
    	 //create a Jena Model containing input RDF data for reasoning from merged Sesame buffer
    	 Model data =createJenaModel((SesameMemoryBuffer)buffer);
    	 
    	 //create a Jena Model containing OWL schema from <owlsource> tag parsed into operator with ID owlsource
    	 Operator operator = context.getOperatorExecuted(owlsource);
		Model schema =createJenaModel((SesameMemoryBuffer)operator.getExecBuffer());
    	 
    	 //create default Jena reasoner and infer implicit RDF triples
    	 Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
    	 reasoner = reasoner.bindSchema(schema);
    	 InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
    	 
    	 //write inferred triples back to opearator's buffer
    	 buffer = new SesameMemoryBuffer();
    	 writeJenaModel(infmodel, (SesameMemoryBuffer)buffer);
    	 
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
    	
      	setOwlsource(context.getPipeParser().getSourceOperatorId(owlSrc));
      	if (null==owlsource){
      		logger.warn("no owlsource found for element "+owlSrc);
      		//TODO : Handling error of lacking OWL data source 	
      	}  		
     }

	public String setOwlsource() {
		return owlsource;
	}

	public void setOwlsource(String owlOpID) {
		this.owlsource = owlOpID;
	} 
}
