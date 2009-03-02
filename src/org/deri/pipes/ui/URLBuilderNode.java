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

import java.net.URLEncoder;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.deri.pipes.core.Engine;
import org.deri.pipes.rdf.URLBuilderBox;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class URLBuilderNode extends InPipeNode implements ConnectingInputNode,ConnectingOutputNode{
	/**
	 * 
	 */
	private static final int PORT_BASE_Y_POSITION = 49;
	private static final int HBOX_HEIGHT=20;
	private static final int BASE_HEIGHT=120;

	/**
	 * 
	 */
	private static final int PORT_X_POSITION = 205;

	final Logger logger = LoggerFactory.getLogger(URLBuilderNode.class);
	
	ParameterizableTextbox baseURL=null;
	TextboxPort basePort=null;
	Vbox vbox,pathVbox,paraVbox;
	public static final String ADD_ICON="img/edit_add-48x48.png";
	public static final String REMOVE_ICON="img/edit_remove-48x48.png";
	Element content=null;
	
	class AddRemoveListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {	
				Component component = event.getTarget().getParent();
				if(((Image)event.getTarget()).getSrc().equals(ADD_ICON)){
					if (component.getParent()==pathVbox) addPath();
					else 
						if (component.getParent()==paraVbox) addParameter();
				}
				if(((Image)event.getTarget()).getSrc().equals(REMOVE_ICON)){
					if(component.getParent()==pathVbox){
						ParameterizableTextbox ptb = (ParameterizableTextbox)component.getLastChild();
						Port port = ptb.getPort();
						port.detach();
						component.detach();
						relayoutPathPorts(1);
						relayoutParaPorts(1);
					}
					else
						if(component.getParent()==paraVbox){
							ParameterizableTextbox ptb = (ParameterizableTextbox)component.getLastChild();
							Port port = ptb.getPort();
							port.detach();
							component.detach();
							relayoutParaPorts(1);
						}
				}
				relayout();
		   }    
	}
	
	public URLBuilderNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,220,BASE_HEIGHT);
		wnd.setTitle("URL builder");		
		vbox=new Vbox();
		wnd.appendChild(vbox);
		paraVbox =new Vbox();
		pathVbox =new Vbox();
			    
	    Hbox hbox= new Hbox();
	    hbox.appendChild(new Label("Base:"));
	    baseURL=createParaBox(160,16);
	    hbox.appendChild(baseURL);
	    vbox.appendChild(hbox);
	    
	    vbox.appendChild(pathVbox);
	    vbox.appendChild(paraVbox);
	    addLabel("Path elements",pathVbox);	    
	    addLabel("Query parameters",paraVbox);
		
	}
	
	protected void initialize(){
		super.initialize();
		basePort=new TextboxPort(getWorkspace(),baseURL);
		addPort(basePort,PORT_X_POSITION,35);
		if(content==null){
			addPath();
			addParameter();
		}
		else
			loadContent(content);
	}
	
	public URLBuilderNode(int x,int y,Element elm){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,220,getHeight(elm));
		wnd.setTitle("URL builder");
		
		vbox=new Vbox();
		wnd.appendChild(vbox);
		paraVbox =new Vbox();
		pathVbox =new Vbox();
		
		Hbox hbox= new Hbox();
	    hbox.appendChild(new Label("Base:"));
	    baseURL=createParaBox(160,16);
	    hbox.appendChild(baseURL);
	    vbox.appendChild(hbox);
        
		vbox.appendChild(pathVbox);
		addLabel("Path elements",pathVbox);
	    vbox.appendChild(paraVbox);
	    addLabel("Query parameters",paraVbox);
	    content=elm;
	}
	
	public void onConnected(Port port){
		if(port instanceof TextboxPort){
			Textbox textbox = ((TextboxPort)port).getTextbox();
			if(!textbox.isReadonly()){
				textbox.setValue("text [wired]");
				textbox.setReadonly(true);
				logger.info("Attached textbox port");
			}
			return;			
		}
	}
	
	public void onDisconnected(Port port){
		if(port instanceof TextboxPort){
			Textbox textbox = ((TextboxPort)port).getTextbox();
			if(textbox.isReadonly()){
				textbox.setValue("");
				textbox.setReadonly(false);
				logger.info("Detached textbox port");
			}
			return;			
		}else{
			logger.info("cannot detach:"+port);
		}
	}
	
	public void loadContent(Element elm){   
	    Element baseElm=XMLUtil.getFirstSubElementByName(elm,"base");
	    loadConnectedConfig(baseElm, basePort, baseURL);		
	    List<Element> pathElms=XMLUtil.getSubElementByName(elm, "path");
	    for(int i=0;i<pathElms.size();i++)
	    	addPath(pathElms.get(i));		    
	    List<Element> paraElms=XMLUtil.getSubElementByName(elm, "para");
	    for(int i=0;i<paraElms.size();i++)
	    	addParameter(paraElms.get(i));
	}
	
	public static int getHeight(Element elm){
	    return 94+(XMLUtil.getSubElementByName(elm, "path").size()+XMLUtil.getSubElementByName(elm, "para").size())*HBOX_HEIGHT;
	}
	
	public Image addImage(String src){
		Image img= new Image(src);
		img.setWidth("14px");
		img.setHeight("14px");
		img.addEventListener("onClick", new AddRemoveListener());
		return img;
	}
	
	public void addLabel(String label,Vbox box){
		Hbox hbox= new Hbox();
	    hbox.appendChild(addImage("img/edit_add-48x48.png"));
	    hbox.appendChild(new Label(label));
	    box.appendChild(hbox);
	}
	
	public void addPath(){
		addPathTextbox();
		relayoutParaPorts(1);
	}

	private ParameterizableTextbox addPathTextbox() {
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		ParameterizableTextbox pathBox = createParaBox(180,16);
		hbox.appendChild(pathBox);		
		TextboxPort nPort = new SourceOrStringPort(getWorkspace(),pathBox);
		pathBox.setPort(nPort);
		int yPosition = PORT_BASE_Y_POSITION+(pathVbox.getChildren().size())*HBOX_HEIGHT;
		addPort(nPort,PORT_X_POSITION,yPosition);
		pathVbox.appendChild(hbox);
		return pathBox;
	}
	
	public void addPath(Element pathElm){
		ParameterizableTextbox txtBox = addPathTextbox();
		loadConnectedConfig(pathElm, txtBox.getPort(), txtBox);
	}
	
	public void addParameter(){
		addParameterTextbox("");		
	}

	private ParameterizableTextbox addParameterTextbox(String name) {
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));		
		ParameterizableTextbox nameBox = createParaBox(80,16);
		nameBox.setText(name);
		hbox.appendChild(nameBox);
		hbox.appendChild(new Label(" = "));
		ParameterizableTextbox valueBox = createParaBox(80,16);
		hbox.appendChild(valueBox);
		TextboxPort nPort = new SourceOrStringPort(getWorkspace(),valueBox);
		int yPosition = PORT_BASE_Y_POSITION+(pathVbox.getChildren().size()+paraVbox.getChildren().size())*HBOX_HEIGHT;
		addPort(nPort,PORT_X_POSITION,yPosition);
		valueBox.setPort(nPort);
		paraVbox.appendChild(hbox);
		return valueBox;
	}
	
	public void addParameter(Element paraElm){
		ParameterizableTextbox txtBox = addParameterTextbox(paraElm.getAttribute("name"));
		loadConnectedConfig(paraElm, txtBox.getPort(), txtBox);	
	}
	
	public void relayout(){
		setDimension(220, BASE_HEIGHT+(pathVbox.getChildren().size()+paraVbox.getChildren().size()-2)*HBOX_HEIGHT);
	}
	
	public void relayoutParaPorts(int from){
		for(int i=from;i<paraVbox.getChildren().size();i++){
			Hbox hbox = (Hbox) paraVbox.getChildren().get(i);
			ParameterizableTextbox ptb = (ParameterizableTextbox)hbox.getLastChild();
			TextboxPort port = ptb.getPort();
			port.setPosition(PORT_X_POSITION,PORT_BASE_Y_POSITION+(pathVbox.getChildren().size()+i)*HBOX_HEIGHT);
		}
	}
	
	public void relayoutPathPorts(int from){
		for(int i=from;i<pathVbox.getChildren().size();i++){
			Hbox hbox = (Hbox)pathVbox.getChildren().get(i);
			ParameterizableTextbox ptb = (ParameterizableTextbox)hbox.getLastChild();
			TextboxPort port = ptb.getPort();
			port.setPosition(PORT_X_POSITION,PORT_BASE_Y_POSITION+i*HBOX_HEIGHT);
		}
	}

	public ParameterizableTextbox createParaBox(int w,int h,String value){
		ParameterizableTextbox box=new ParameterizableTextbox(value);
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	
	public ParameterizableTextbox createParaBox(int w,int h){
		ParameterizableTextbox box=new ParameterizableTextbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	

	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()==null){
			return null;
		}
		Element srcCode =doc.createElement(getTagName());
		if(config) setPosition((Element)srcCode);

		Element baseElm = doc.createElement("base");
		baseElm.appendChild(getConnectedCode(doc, baseURL, basePort, config));
		srcCode.appendChild(baseElm);

		List children = pathVbox.getChildren();
		for(int i=1;i<children.size();i++){
			Hbox hbox=(Hbox)pathVbox.getChildren().get(i);
			ParameterizableTextbox ptb = (ParameterizableTextbox)hbox.getLastChild();
			Port port = ptb.getPort();
			Element pathElm=doc.createElement("path");
			pathElm.appendChild(getConnectedCode(doc, ptb, port, config));	
			srcCode.appendChild(pathElm);
		}
		children = paraVbox.getChildren();
		for(int i=1;i<children.size();i++){
			Hbox hbox=(Hbox)children.get(i);
			ParameterizableTextbox ptb = (ParameterizableTextbox)hbox.getLastChild();
			Port port = ptb.getPort();
			Element paraElm =doc.createElement("para");
			paraElm.setAttribute("name", ((Textbox)hbox.getChildren().get(1)).getValue());
			paraElm.appendChild(getConnectedCode(doc,ptb,port,config));
			srcCode.appendChild(paraElm);	
		}
		if(!config){
			//Does this urlbuilder use a source? If not just return the string.
			String code = super.serializeNode(doc, srcCode);
			URLBuilderBox box = (URLBuilderBox)Engine.defaultEngine().parse(code);
			if(!box.usesSource()){
				try {
					return doc.createTextNode(box.getUrl(Engine.defaultEngine().newContext()));
				} catch (Exception e) {
					logger.warn("Couldn't get location",e);
				}
			}
		}
		return srcCode;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		URLBuilderNode node= new URLBuilderNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")),elm);
		wsp.addFigure(node);
		return node;
	}
	
	public void debug(){
		String srcCode = getSrcCode(false);
		if(srcCode.indexOf('<')>=0){
			((PipeEditor)getWorkspace()).hotDebug(srcCode);
		}else{
			((PipeEditor)getWorkspace()).reloadTextDebug(srcCode) ;
			((PipeEditor)getWorkspace()).reloadTabularDebug(null);
		}
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "urlbuilder";
	}


}
