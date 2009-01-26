package org.deri.pipes.ui;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */

import java.util.Hashtable;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.openrdf.rio.RDFFormat;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;

import java.net.URLEncoder;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class RegExNode extends InOutNode implements ConnectingInputNode{
	Hashtable<String,Port> regPorts= new Hashtable<String,Port>();
	Hashtable<String,Port> replPorts= new Hashtable<String,Port>();	
	Vbox vbox;
	static int _rs=23;
	public static final String ADD_ICON="img/edit_add-48x48.png";
	public static final String REMOVE_ICON="img/edit_remove-48x48.png";
	Element content=null;
	
	class AddRemoveListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {	
				if(((Image)event.getTarget()).getSrc().equals(ADD_ICON)){
					addRule();
				}
				if(((Image)event.getTarget()).getSrc().equals(REMOVE_ICON)){
					if(regPorts.get(event.getTarget().getParent().getUuid())!=null){
						regPorts.get(event.getTarget().getParent().getUuid()).detach();
						regPorts.remove(event.getTarget().getParent().getUuid());
						replPorts.get(event.getTarget().getParent().getUuid()).detach();
						replPorts.remove(event.getTarget().getParent().getUuid());
					}
					event.getTarget().getParent().detach();
					relayoutRulePorts(1);
				}
				relayout();
		   }    
	}
	
	public RegExNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,220,138);
		wnd.setTitle("RDF Regex");
		tagName="regex";
		vbox=new Vbox();
		wnd.appendChild(vbox);
		addRule();
	}
	
	protected void initialize(){
		super.initialize();
		if(content==null){
			addRule();
		}
		else
			loadContent(content);
	}
	
	public RegExNode(int x,int y,Element elm){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,220,getHeight(elm));
		wnd.setTitle("URL builder");
		vbox=new Vbox();
		wnd.appendChild(vbox);
	    content=elm;
	}
	
	public void onConnected(Port port){		
		for(int i=1;i<vbox.getChildren().size();i++){
			if(regPorts.get(((Hbox)vbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)vbox.getChildren().get(i)).getFirstChild().getNextSibling()).setValue("text [wired]");
				((Textbox)((Hbox)vbox.getChildren().get(i)).getFirstChild().getNextSibling()).setReadonly(true);
				return;
			}
			if(replPorts.get(((Hbox)vbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)vbox.getChildren().get(i)).getLastChild()).setValue("text [wired]");
				((Textbox)((Hbox)vbox.getChildren().get(i)).getLastChild()).setReadonly(true);
				return;
			}
		}
		
	}
	
	public void onDisconnected(Port port){
		for(int i=1;i<vbox.getChildren().size();i++){
			if(regPorts.get(((Hbox)vbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)vbox.getChildren().get(i)).getFirstChild().getNextSibling()).setValue("");
				((Textbox)((Hbox)vbox.getChildren().get(i)).getFirstChild().getNextSibling()).setReadonly(false);
				return;
			}
			if(replPorts.get(((Hbox)vbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)vbox.getChildren().get(i)).getLastChild()).setValue("");
				((Textbox)((Hbox)vbox.getChildren().get(i)).getLastChild()).setReadonly(false);
				return;
			}
		}
	}
	
	public void loadContent(Element elm){   
	    ArrayList<Element> rules=XMLUtil.getSubElementByName(XMLUtil.getFirstSubElementByName(elm,"rules"),"rule");
	    for(int i=0;i<rules.size();i++) addRule(rules.get(i));
	}
	
	public static int getHeight(Element elm){
	    return 94+(XMLUtil.getSubElementByName(XMLUtil.getFirstSubElementByName(elm,"rules"),"rule").size())*_rs;
	}
	
	public Image addImage(String src){
		Image img= new Image(src);
		img.setWidth("14px");
		img.setHeight("14px");
		img.addEventListener("onClick", new AddRemoveListener());
		return img;
	}
	
	public void addLabel(String label){
		Hbox hbox= new Hbox();
	    hbox.appendChild(addImage("img/edit_add-48x48.png"));
	    hbox.appendChild(new Label(label));
	    vbox.appendChild(hbox);
	}
	
	public void addRule(){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		hbox.appendChild(new Label("Replace"));
		Listbox listbox =new Listbox();
        listbox.setMold("select");
        listbox.appendItem("uri", "URI");
        listbox.appendItem("literal", "Literal");
        hbox.appendChild(listbox);
        hbox.appendChild(new Label("regex"));
		hbox.appendChild(createBox(80,16));		
		Port nPort=createPort(PipePortType.TEXTIN,80,12+(vbox.getChildren().size())*_rs);
		regPorts.put(hbox.getUuid(), nPort);
		hbox.appendChild(new Label("with"));
		hbox.appendChild(createBox(80,16));
		nPort=createPort(PipePortType.TEXTIN,175,12+(vbox.getChildren().size())*_rs);
		vbox.appendChild(hbox);
	}
	
	public void addRule(Element ruleElm){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		hbox.appendChild(new Label("Replace"));
		
		Listbox listbox =new Listbox();
        listbox.setMold("select");
        listbox.appendItem("uri", "URI");
        listbox.appendItem("literal", "Literal");
        if("uri".equalsIgnoreCase(ruleElm.getAttribute("type")))        	listbox.setSelectedIndex(1);
        else listbox.setSelectedIndex(0);
        hbox.appendChild(listbox);
        
        hbox.appendChild(new Label("regex"));
		Textbox txtbox= createBox(80,16);
        hbox.appendChild(txtbox);		
		Port nPort=createPort(PipePortType.TEXTIN,80,12+(vbox.getChildren().size())*_rs);
		regPorts.put(hbox.getUuid(), nPort);
		loadConnectedConfig(XMLUtil.getFirstSubElementByName(ruleElm,"regex"), nPort, txtbox);
		hbox.appendChild(new Label("regex"));
		
		txtbox= createBox(80,16);
        hbox.appendChild(txtbox);		
		nPort=createPort(PipePortType.TEXTIN,175,12+(vbox.getChildren().size())*_rs);
		regPorts.put(hbox.getUuid(), nPort);
		loadConnectedConfig(XMLUtil.getFirstSubElementByName(ruleElm,"replacement"), nPort, txtbox);
		
		vbox.appendChild(hbox);		
	}
	
	public void relayout(){
		setDimension(220, 24+(vbox.getChildren().size())*_rs);
	}
	
	public void relayoutRulePorts(int from){
		for(int i=from;i<vbox.getChildren().size();i++){
			regPorts.get(((Hbox)vbox.getChildren().get(i)).getUuid())
			           .setPosition(80,27+i*_rs);
			regPorts.get(((Hbox)vbox.getChildren().get(i)).getUuid())
	           .setPosition(175,27+i*_rs);
		}
	}
	
	public Textbox createBox(int w,int h,String value){
		Textbox box=new Textbox(value);
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	
	public Textbox createBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
		
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if (srcCode!=null) return srcCode;
			srcCode =super.getSrcCode(doc, config);
			
			Element rulesElm=doc.createElement("rules");
			for(int i=1;i<vbox.getChildren().size();i++){	
				Element ruleElm=doc.createElement("rule");
				Hbox hbox=(Hbox)vbox.getChildren().get(i);
				
				Listbox listbox=(Listbox)hbox.getFirstChild().getNextSibling();
				if(listbox.getSelectedItem()!=null)
					ruleElm.setAttribute("type", listbox.getSelectedItem().getValue().toString());
				else
					ruleElm.setAttribute("type", "uri");
				
				Element regElm =doc.createElement("regex");
				regElm.appendChild(getConnectedCode(doc, (Textbox)(listbox.getNextSibling().getNextSibling()), 
						                                                   regPorts.get(hbox.getUuid()), config) );
				ruleElm.appendChild(regElm);
				
				Element replElm =doc.createElement("replacement");
				regElm.appendChild(getConnectedCode(doc, (Textbox)hbox.getLastChild(), 
						                                                   replPorts.get(hbox.getUuid()), config) );
				ruleElm.appendChild(replElm);
				
				rulesElm.appendChild(ruleElm);
			}
			
	    	srcCode.appendChild(rulesElm);
	    	return srcCode;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		RegExNode node= new RegExNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")),elm);
		wsp.addFigure(node);
		node.connectSource(elm);
		return node;
	}
	
	public void debug(){
		((PipeEditor)getWorkspace()).reloadTextDebug(getSrcCode(false)) ;
		((PipeEditor)getWorkspace()).reloadTabularDebug(null);
	}
}
