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
package org.deri.pipes.ui;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.core.PipeParser;
import org.deri.pipes.endpoints.Pipe;
import org.deri.pipes.endpoints.PipeManager;
import org.deri.pipes.model.Operator;
import org.deri.pipes.model.Stream;
import org.deri.pipes.rdf.RDFBox;
import org.deri.pipes.rdf.SelectBox;
import org.integratedmodelling.zk.diagram.components.PortTypeManager;
import org.integratedmodelling.zk.diagram.components.PortTypeMask;
import org.integratedmodelling.zk.diagram.components.Shape;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
public class PipeEditor extends Workspace {
	final Logger logger = LoggerFactory.getLogger(PipeEditor.class);
	private Textbox textDebugPanel,pipeid,pipename,password;
	private Bandbox bdid;
	private Tabpanel tabularDebugPanel=null;
	private OutPipeNode outputNode;
	private PortTypeManager pTypeMag;
	private ArrayList<ParameterNode> paraList =new ArrayList();
	public PipeEditor(String w,String h){
		super();
		setWidth(w);
		setHeight(h);
		pTypeMag=new PortTypeManager(this);
		PipePortType.generateAllPortTypes(this);
	}
	
	public void setTextDebugPanel(Textbox txtBox){
		textDebugPanel=txtBox;
	}
	
	public void addFigure(Shape shape){
		super.addFigure(shape);
		if(shape instanceof PipeNode)
			((PipeNode)shape).initialize();
	}
	
	public PortTypeManager getPTManager(){
		return pTypeMag;
	}
	
	public OutPipeNode getOutput(){
		return outputNode;
	}
	
	public void setOutput(OutPipeNode outputNode){
		this.outputNode=outputNode;
	}
	
	public void setTabularDebugPanel(Tabpanel tabpanel){
		tabularDebugPanel=tabpanel;
	}
	
	public Textbox getTextDebugPanel(){
		return textDebugPanel;
	}
	
	public Tabpanel getTabularDebugPanel(){
		return tabularDebugPanel;
	}
	
	public void setConfigComps(Textbox pipeid,Bandbox bdid,Textbox pipename,Textbox password){
		this.pipeid=pipeid;
		this.pipename=pipename;
		this.password=password;
		this.bdid=bdid;
	}

	public Textbox getPipeIdTxtBox(){
		return pipeid;
	}
	
	public Textbox getPipeNameTxtBox(){
		return pipeid;
	}
	
	public Textbox getPasswordTxtBox(){
		return password;
	}
	
	public boolean savePipe(){
		return PipeManager.savePipe(this); 
	}
	
	public String getSrcCode(boolean config){
		if(outputNode==null) return "";
		outputNode.reset(true);
		return outputNode.getSrcCode(config);
	}
	
	public String getPipeId(){
		return pipeid.getValue();
	}

	public String getPipeName(){
		return pipename.getValue();
	}
	
	public String getPassword(){
		return password.getValue();
	}
	
	public void addParameter(ParameterNode paraNode){
		if (paraList.indexOf(paraNode)<0)
			paraList.add(paraNode);
	}
	
	public ParameterNode getParameter(String nodeId){
		for(int i=0;i<paraList.size();i++){
			if((nodeId.equals("${"+paraList.get(i).getParaId()+"}"))&&(paraList.get(i).getWorkspace()!=null)){
			   return paraList.get(i);
			}
		}
		return null;
	}
	
	public ArrayList<ParameterNode> getParameters(){
		return paraList;
	}
	
	public void removeParameters(){
		paraList.removeAll(paraList);
	}
	
	public void createFigure(int x,int y,String figureType){
		 if(outputNode==null){
			 outputNode=new  OutPipeNode(400,400);
			 addFigure(outputNode);
		 }
	     x-=350;
	     y-=70;             
	     if(figureType.equalsIgnoreCase("rdffetchop")){
	     	 addFigure(new RDFFetchNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("htmlfetchop")){
	     	 addFigure(new HTMLFetchNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("sparqlresultfetchop")){
	     	 addFigure(new SPARQLResultFetchNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("simplemixop")){
	     	 addFigure(new SimpleMixNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("constructop")){
	     	 addFigure(new ConstructNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("selectop")){
	     	 addFigure(new SelectNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("patch-gen")){
	     	 addFigure(new PatchGeneratorNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("patch-exec")){
	     	 addFigure(new PatchExecutorNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("rdfsmixop")){
	     	 addFigure(new RDFSMixNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("smoosherop")){
	     	 addFigure(new SmoosherNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("forop")){
	     	addFigure(new ForNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("xsltop")){
		     	addFigure(new XSLTNode(x,y));
		 }
	     else if(figureType.equalsIgnoreCase("xmlfetchop")){
		     	addFigure(new XMLFetchNode(x,y));
		 }
	     else if(figureType.equalsIgnoreCase("xslfetchop")){
		     	addFigure(new XSLFetchNode(x,y));
		 }
	     else if(figureType.equalsIgnoreCase("urlbuilder")){
	     	addFigure(new URLBuilderNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("parameter")){
	     	addFigure(new ParameterNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("variable")){
		     	addFigure(new VariableNode(x,y));
		 }
	     else if(figureType.equalsIgnoreCase("sparqlendpoint")){
		     	addFigure(new SPARQLEndpointNode(x,y));
		 }
	}
	
	public static Listbox createListbox(TupleQueryResult tuple){
		   Listbox listbox =new Listbox();
		   listbox.setWidth("98%");
		   listbox.setRows(20);
		   java.util.List<String> bindingNames = tuple.getBindingNames();
		   Listhead listhead=new Listhead();
		   for(int i=0;i<bindingNames.size();i++)
			    listhead.appendChild(new Listheader(bindingNames.get(i)));
		   listbox.appendChild(listhead);
		   try{
			   while (tuple.hasNext()) {
		    	   Listitem item=new Listitem();			    	   
				   BindingSet bindingSet = tuple.next();		   
				   for(int i=0;i<bindingNames.size();i++){
					       Listcell cell=new Listcell(bindingSet.getValue(bindingNames.get(i)).toString());
					       cell.setStyle("font-size: 8px;");
					       item.appendChild(cell);
				   }
				   listbox.appendChild(item);	   
		       } 
		   }
		   catch(QueryEvaluationException e){
		      	  
	       }
		   return listbox;
	}
	
	public  void reloadTextDebug(String text){
		   textDebugPanel.setValue(text);
	}
	   
	public void reloadTabularDebug(TupleQueryResult tuple){
		   if(tabularDebugPanel.getFirstChild()!=null)
			   tabularDebugPanel.getFirstChild().detach();
		   if(tuple!=null)
			   tabularDebugPanel.appendChild(createListbox(tuple));
	}
	
	public void debug(){
		debug(getSrcCode(false));
	}
	public String populatePara(String syntax){
		for(int i=0;i<paraList.size();i++){
			syntax = syntax.replace("${" + paraList.get(i).getParaId() + "}", paraList.get(i).getDefaultVal());
			try{
				syntax=syntax.replace(URLEncoder.encode("${" + paraList.get(i).getParaId()  + "}","UTF-8"),
										URLEncoder.encode(paraList.get(i).getDefaultVal(),"UTF-8"));
			}
			catch(java.io.UnsupportedEncodingException e){
				logger.warn("UTF-8 support is required by the JVM specification",e);
			}
		}
		
		return syntax;
	}
	public void debug(String syntax){
		   syntax=populatePara(syntax);	
		   //logger.debug(syntax);
		   PipeParser parser= new PipeParser();	   
		   Stream    stream= parser.parse(syntax);
		   TupleQueryResult tuple=null;
		   String textResult=null;
		   if(stream instanceof RDFBox){
			   ((RDFBox) stream).execute(null);
			   org.deri.pipes.core.ExecBuffer buff=((RDFBox)stream).getExecBuffer();
			   textResult=buff.toString();
			   if(buff instanceof org.deri.pipes.model.SesameMemoryBuffer){
				   try{
					   String query ="SELECT * WHERE {?predicate ?subject ?object.}";
			    		tuple=((((org.deri.pipes.model.SesameMemoryBuffer)buff).
			    				     getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query)).evaluate());
			    	}
			        catch(MalformedQueryException e){ 
			      	  
			        }
			        catch(QueryEvaluationException e){
			      	  
			        }
			        catch(RepositoryException e){
			      	  
			        }
			   }
			   else if(buff instanceof org.deri.pipes.model.SesameTupleBuffer){
				   tuple=((org.deri.pipes.model.SesameTupleBuffer)buff).getTupleQueryResult();
			   }
		   }
		   reloadTextDebug(textResult);
		   reloadTabularDebug(tuple);
	}
	
	public void hotDebug(String syntax){
		syntax=populatePara(syntax);
		 //logger.debug(syntax);
		InputSource input=new InputSource(new java.io.StringReader(syntax));
		try {
           DOMParser parser = new DOMParser();
           parser.parse(input);
           PipeParser pipeParser= new PipeParser();
		   Operator    op= pipeParser.parseOperator(parser.getDocument().getDocumentElement());
		   TupleQueryResult tuple=null;
		   String textResult=null;
		   if(op instanceof RDFBox){
			   ((RDFBox) op).execute(null);
			   org.deri.pipes.core.ExecBuffer buff=((RDFBox)op).getExecBuffer();
			   textResult=buff.toString();
			   if(buff instanceof org.deri.pipes.model.SesameMemoryBuffer){
				   String query ="SELECT * WHERE {?predicate ?subject ?object.}";
				   try{
			    		tuple=((((org.deri.pipes.model.SesameMemoryBuffer)buff).
			    				     getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query)).evaluate());
			    	}
			        catch(Exception e){ 
			        	logger.warn("Problem executing sparql query ["+query+"]",e);
			        }
			   }   
		   }else if(op instanceof SelectBox){
			   ((SelectBox) op).execute(null);
			   org.deri.pipes.core.ExecBuffer buff=((SelectBox)op).getExecBuffer();
			   //textResult=buff.toString();
			   
			   if(buff instanceof org.deri.pipes.model.SesameTupleBuffer){
				   tuple=((org.deri.pipes.model.SesameTupleBuffer)buff).getTupleQueryResult();
				   textResult=((org.deri.pipes.model.SesameTupleBuffer)buff).toString();
			   }
			   
		   }
		   reloadTextDebug(textResult);
		   reloadTabularDebug(tuple);		
		} catch (Exception e) {
			logger.warn("could not hotDebug",e);
	    }
	}
	
	public void reload(String config){
		Object[] children=getChildren().toArray();
		for(int i=0;i<children.length;i++){
			if(!(children[i] instanceof PortTypeMask))
				((Component)children[i]).detach();
		}
		outputNode=null;
		if((null==config)||(config.trim()=="")) return;
		InputSource input=new InputSource(new java.io.StringReader(config));
		try {
            DOMParser parser = new DOMParser();
            parser.parse(input);  
            PipeNode.loadConfig(parser.getDocument().getDocumentElement(),this);
        } catch (Exception e) {
        	System.out.print(e.toString()+"\n");
        }
	}
	
	public void clone(String pid){
		reload(PipeManager.getPipeConfig(pid));
		pipeid.setValue("");
		bdid.setValue("");
		pipename.setValue("");
	}
	
	public void newPipe(){
		reload(null);
		pipeid.setValue("");
		bdid.setValue("");
		pipename.setValue("");
	}
	
	public void edit(String pid){
		Pipe pipe=PipeManager.getPipe(pid);
		reload(pipe.getConfig());
		pipeid.setValue(pipe.getId());
		bdid.setValue(pipe.getId());
		pipename.setValue(pipe.getName());
	}
}
