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
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */

import java.util.Hashtable;
import java.util.List;

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class RegExNode extends InOutNode implements ConnectingInputNode{
	final Logger logger = LoggerFactory.getLogger(RegExNode.class);
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
	    List<Element> rules=XMLUtil.getSubElementByName(XMLUtil.getFirstSubElementByName(elm,"rules"),"rule");
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
			Node srcCode =super.getSrcCode(doc, config);
			
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

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "regex";
	}
}
