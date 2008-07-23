package org.deri.pipes.ui;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Box;
import org.deri.execeng.model.Stream;
import org.deri.execeng.rdf.BoxParserImplRDF;
import org.deri.execeng.rdf.ConstructBox;
import org.deri.execeng.rdf.ForLoopBox;
import org.deri.execeng.rdf.PatchExecutorBox;
import org.deri.execeng.rdf.PatchGeneratorBox;
import org.deri.execeng.rdf.RDFBox;
import org.deri.execeng.rdf.RDFFetchBox;
import org.deri.execeng.rdf.RDFSMixBox;
import org.deri.execeng.rdf.SameAsBox;
import org.deri.execeng.rdf.SimpleMixBox;
import org.deri.execeng.rdf.TupleQueryResultFetchBox;
import org.integratedmodelling.zk.diagram.components.*;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.w3c.dom.Element;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.query.BindingSet;
import org.deri.execeng.utils.*;

import java.util.ArrayList;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class PipeNode extends ZKNode{  
	
	private static final long serialVersionUID = -1520720934219234911L;
	protected String tagName=null;	
	public class DeleteListener implements org.zkoss.zk.ui.event.EventListener {
		   PipeNode node;
		   public DeleteListener(PipeNode node){
			   this.node=node;
		   }
		   public void onEvent(Event event) throws UiException {
			     if(!(node instanceof OutPipeNode))
			    	 this.node.detach();
		   }
   }
   
	public class DebugListener implements org.zkoss.zk.ui.event.EventListener {
		   PipeNode node;
		   public DebugListener(PipeNode node){
			   this.node=node;
		   }
		   public void onEvent(Event event) throws UiException {
			     node.debug();
		   }
    }
   protected Window wnd=null;
   
   public PipeNode(int x,int y,int width,int height){
	   super(x,y,width,height);
	   this.canDelete=false;
	   wnd =new Window();
	   appendChild(wnd); 	   
   }
   
   protected void initialize(){
	   
   }
   
   public CustomPort createPort(PortType pType,String position){
	   CustomPort port=new CustomPort(((PipeEditor)getWorkspace()).getPTManager(),pType);
	   port.setPosition(position);
	   port.setPortType("custom");
       addPort(port,0,0);
	   return port;
   }
   
   public CustomPort createPort(PortType pType,int x,int y){
	   CustomPort port=new CustomPort(((PipeEditor)getWorkspace()).getPTManager(),pType);
	   port.setPosition("none");
	   port.setPortType("custom");
       addPort(port,x,y);
	   return port;
   }
   
   public CustomPort createPort(byte pType,String position){
	   return createPort(PipePortType.getPType(pType),position);
   }
   
   public CustomPort createPort(byte pType,int x,int y){
	   return createPort(PipePortType.getPType(pType),x,y);
   }
   
   protected Textbox createBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
   }
   
   protected String getConnectedCode(Textbox txtBox,Port port){
	    String code="";
	    boolean isConnected=false;
		for(Port p:getWorkspace().getIncomingConnections(port.getUuid())){
			if(p.getParent() instanceof ConnectingOutputNode){
				code=((PipeNode)p.getParent()).getCode();
				isConnected=true;
				break;
			}
		}
		if(!isConnected){
			code=txtBox.getValue().trim();
		}
		return code;
   }
   
   protected String getConnectedConfig(Textbox txtBox,Port port){
	    String code="";
		boolean isConnected=false;
		for(Port p:getWorkspace().getIncomingConnections(port.getUuid())){
			if(p.getParent() instanceof ConnectingOutputNode){
				code=((PipeNode)p.getParent()).getConfig();
				isConnected=true;
				break;
			}			
		}
		if(!isConnected){
			code+="<![CDATA["+txtBox.getValue().trim()+"]]>";;
		}
		return code;
  }
   
  protected void loadConnectedConfig(Element elm,Port port,Textbox txtbox){	  
		Element linkedElm=XMLUtil.getFirstSubElement(elm);
		String txt;
		if(linkedElm!=null){
			PipeNode linkedNode=PipeNode.loadConfig(linkedElm,(PipeEditor)getWorkspace());
			linkedNode.connectTo(port);
		}else if((txt=XMLUtil.getTextData(elm))!=null){
			if(txt.indexOf("${")>=0){				
				ParameterNode paraNode=((PipeEditor)getWorkspace()).getParameter(txt);
				paraNode.connectTo(port);
			}
			else{
				txtbox.setValue(txt);
			}
		}
  }
  
  public void setToobar(){
	   Caption caption =new Caption();
 	   Toolbarbutton delButton= new Toolbarbutton("","img/del-16x16.png");
 	   delButton.setClass("drag");
 	   delButton.addEventListener("onClick", new DeleteListener(this));
 	   Toolbarbutton debugButton= new Toolbarbutton("","img/debug.jpg");
	   debugButton.setClass("drag");
	   debugButton.addEventListener("onClick", new DebugListener(this));
 	   wnd.appendChild(caption);
 	   caption.appendChild(debugButton);
 	   caption.appendChild(delButton);
   }
   
   public String getCode(){
	   return null;
   }
   
   public String getConfig(){
	   return null;
   }
   
   public static PipeNode loadConfig(Element elm,PipeEditor wsp){
	   //System.out.println(elm.getTagName());
	   if(elm.getTagName().equalsIgnoreCase("pipe")){    
		   ArrayList<Element>  paraElms=XMLUtil.getSubElementByName(
				   								XMLUtil.getFirstSubElementByName(elm, "parameters"),"parameter");
		   for(int i=0;i<paraElms.size();i++){
			   wsp.addParameter((ParameterNode)PipeNode.loadConfig(paraElms.get(i),wsp));
		   }
   		   return PipeNode.loadConfig(XMLUtil.getFirstSubElementByName(elm, "code"),wsp);
	   }
	   
	   if(elm.getTagName().equalsIgnoreCase("code"))
		   return OutPipeNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("rdffetch"))    
   		   return RDFFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("simplemix"))    
	   		return SimpleMixNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("construct"))    
	   		return ConstructNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("for"))    
	   		return ForNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("xslt"))    
	   		return XSLTNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("htmlfetch"))    
	   		return HTMLFetchNode.loadConfig(elm,wsp);
	  
	   if(elm.getTagName().equalsIgnoreCase("xmlfetch"))    
	   		return XMLFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("xslfetch"))    
	   		return XSLFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("parameter"))    
	   		return ParameterNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("patch-executor"))    
	   		return PatchExecutorNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("patch-generator"))    
	   		return PatchGeneratorNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("smoosher"))    
	   		return SmoosherNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("rdfs"))    
	   		return RDFSMixNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("select"))    
	   		return SelectNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("tuplefetch"))    
	   		return TupleQueryResultFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("urlbuilder"))    
	   		return URLBuilderNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("variable"))    
	   		return VariableNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("sparqlendpoint"))    
	   		return SPARQLEndpointNode.loadConfig(elm,wsp);
	   
	   return null;
   }
   
   public void connectTo(Port port){
	   
   }
   public byte getTypeIdx(){
	   return PipePortType.NONE;
   }
   
   public void debug(){	   
	   ((PipeEditor)getWorkspace()).hotDebug(getCode());
   }
}
