package org.deri.pipes.ui;

import java.util.ArrayList;

import org.integratedmodelling.zk.diagram.components.PortTypeManager;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.integratedmodelling.zk.diagram.components.Shape;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Tabpanel;
import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.ui.*;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.endpoints.*;
import org.deri.execeng.model.Stream;
import org.deri.execeng.rdf.BoxParserImplRDF;
import org.deri.execeng.rdf.RDFBox;
import org.deri.execeng.rdf.SelectBox;
import org.zkoss.zk.ui.Component;
import org.deri.execeng.endpoints.PipeManager;
import org.deri.execeng.endpoints.Pipe;
public class PipeEditor extends Workspace {
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
	
	public String getCode(){
		if(outputNode==null) return "";
		return outputNode.getCode();
	}
	
	public String getConfig(){
		if(outputNode==null) return "";
		return outputNode.getConfig();
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
			 outputNode=new  OutPipeNode(500,400);
			 addFigure(outputNode);
		 }
	     x-=180;
	     y-=30;             
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
		debug(getCode());
	}
	
	public void debug(String syntax){
		   BoxParserImplRDF parser= new BoxParserImplRDF();	   
		   Stream    stream= parser.parse(syntax);
		   TupleQueryResult tuple=null;
		   String textResult=null;
		   if(stream instanceof RDFBox){
			   ((RDFBox) stream).execute();
			   org.deri.execeng.core.ExecBuffer buff=((RDFBox)stream).getExecBuffer();
			   textResult=buff.toString();
			   if(buff instanceof org.deri.execeng.rdf.SesameMemoryBuffer){
				   try{
					   String query ="SELECT * WHERE {?predicate ?subject ?object.}";
			    		tuple=((((org.deri.execeng.rdf.SesameMemoryBuffer)buff).
			    				     getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query)).evaluate());
			    	}
			        catch(MalformedQueryException e){ 
			      	  
			        }
			        catch(QueryEvaluationException e){
			      	  
			        }
			        catch(RepositoryException e){
			      	  
			        }
			   }
			   else if(buff instanceof org.deri.execeng.rdf.SesameTupleBuffer){
				   tuple=((org.deri.execeng.rdf.SesameTupleBuffer)buff).getTupleQueryResult();
			   }
		   }
		   reloadTextDebug(textResult);
		   reloadTabularDebug(tuple);
	}
	
	public void hotDebug(String syntax){
		InputSource input=new InputSource(new java.io.StringReader(syntax));
		try {
           DOMParser parser = new DOMParser();
           parser.parse(input);
		   Stream    stream= BoxParserImplRDF.loadStream(parser.getDocument().getDocumentElement());
		   TupleQueryResult tuple=null;
		   String textResult=null;
		   if(stream instanceof RDFBox){
			   ((RDFBox) stream).execute();
			   org.deri.execeng.core.ExecBuffer buff=((RDFBox)stream).getExecBuffer();
			   textResult=buff.toString();
			   if(buff instanceof org.deri.execeng.rdf.SesameMemoryBuffer){
				   try{
					   String query ="SELECT * WHERE {?predicate ?subject ?object.}";
			    		tuple=((((org.deri.execeng.rdf.SesameMemoryBuffer)buff).
			    				     getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query)).evaluate());
			    	}
			        catch(MalformedQueryException e){ 
			      	  
			        }
			        catch(QueryEvaluationException e){
			      	  
			        }
			        catch(RepositoryException e){
			      	  
			        }
			   }   
		   }else if(stream instanceof SelectBox){
			   ((SelectBox) stream).execute();
			   org.deri.execeng.core.ExecBuffer buff=((SelectBox)stream).getExecBuffer();
			   //textResult=buff.toString();
			   
			   if(buff instanceof org.deri.execeng.rdf.SesameTupleBuffer){
				   tuple=((org.deri.execeng.rdf.SesameTupleBuffer)buff).getTupleQueryResult();
				   textResult=((org.deri.execeng.rdf.SesameTupleBuffer)buff).toString();
			   }
			   
		   }
		   reloadTextDebug(textResult);
		   reloadTabularDebug(tuple);		
		} catch (Exception e) {
	    	System.out.print(e.toString()+"\n");
	    }
	}
	
	public void reload(String config){
		Object[] children=getChildren().toArray();
		for(int i=0;i<children.length;i++){
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
		reload(pipe.config);
		pipeid.setValue(pipe.pipeid);
		bdid.setValue(pipe.pipeid);
		pipename.setValue(pipe.pipename);
	}
}
