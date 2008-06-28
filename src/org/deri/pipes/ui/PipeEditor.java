package org.deri.pipes.ui;

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
import org.zkoss.zul.Tabpanel;
import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.ui.*;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.endpoints.*;
import org.deri.execeng.model.Stream;
import org.deri.execeng.rdf.BoxParserImplRDF;
import org.deri.execeng.rdf.RDFBox;
import org.zkoss.zk.ui.Component;
import org.deri.execeng.endpoints.PipeManager;
import org.deri.execeng.endpoints.Pipe;
public class PipeEditor extends Workspace {
	private Textbox textDebugPanel,pipeid,pipename,password;
	private Tabpanel tabularDebugPanel=null;
	private OutPipeNode outputNode;
	public PipeEditor(String w,String h){
		super();
		setWidth(w);
		setHeight(h);
	}
	public void setTextDebugPanel(Textbox txtBox){
		textDebugPanel=txtBox;
	}
	public void addFigure(Shape shape){
		if((outputNode==null)&&(!(shape instanceof OutPipeNode))){
			outputNode = new OutPipeNode(500,400);
			outputNode.setWorkspace(this);
			addFigure(outputNode);
		}
		super.addFigure(shape);
	}
	
	public void addOutput(OutPipeNode outputNode){
		this.outputNode=outputNode;
		super.addFigure(outputNode);
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
	
	public void setConfigComps(Textbox pipeid,Textbox pipename,Textbox password){
		this.pipeid=pipeid;
		this.pipename=pipename;
		this.password=password;
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
	public void createFigure(int x,int y,String figureType){
	     x-=180;
	     y-=30;             
	     if(outputNode==null){
	          outputNode=new OutPipeNode(500,400);
	          outputNode.setWorkspace(this);
	          addFigure(outputNode);
	          
	     }    
	     if(figureType.equalsIgnoreCase("fetchop")){
	     	 addFigure(new RDFFetchNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("simplemixop")){
	     	 addFigure(new SimpleMixNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("deleteop")){
	     	 addFigure(new RDFDeleteNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("constructop")){
	     	 addFigure(new ConstructNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("fetch")){
	     	 addFigure(new RDFFetchNode(x,y));
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
	     else if(figureType.equalsIgnoreCase("urlbuilder")){
	     	addFigure(new URLBuilderNode(x,y));
	     }
	     else if(figureType.equalsIgnoreCase("parameter")){
	     	addFigure(new ParameterNode(x,y));
	     }
	}
	
	public static Listbox createListbox(TupleQueryResult tuple){
		   Listbox listbox =new Listbox();
		   listbox.setWidth("700px");
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
					       item.appendChild(new Listcell(bindingSet.getValue(bindingNames.get(i)).toString()));
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
				   textResult=((org.deri.execeng.rdf.SesameTupleBuffer)buff).toString();
			   }
		   }
		   reloadTextDebug(textResult);
		   reloadTabularDebug(tuple);
	}
	
		
	public void reload(String config){
		System.out.println(config);
		Object[] children=getChildren().toArray();
		for(int i=0;i<children.length;i++){
			((Component)children[i]).detach();
		}		
		InputSource input=new InputSource(new java.io.StringReader(config));
		try {
            DOMParser parser = new DOMParser();
            parser.parse(input);  
            PipeNode.loadConfig(parser.getDocument().getDocumentElement(),this);
        } catch (Exception e) {
        	System.out.print(e.toString()+"\n");
        	Stream.log.append(e.toString()+"\n");
        }
	}
	
	public void clone(String pid){
		reload(PipeManager.getPipeConfig(pid));
		pipeid.setValue("");
		pipename.setValue("");
	}
	
	public void edit(String pid){
		Pipe pipe=PipeManager.getPipe(pid);
		reload(pipe.config);
		pipeid.setValue(pipe.pipeid);
		pipename.setValue(pipe.pipename);
	}
}
