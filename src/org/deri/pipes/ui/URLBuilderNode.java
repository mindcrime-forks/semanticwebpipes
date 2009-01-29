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
import java.util.Hashtable;
import java.util.List;

import org.deri.execeng.utils.XMLUtil;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class URLBuilderNode extends InPipeNode implements ConnectingInputNode,ConnectingOutputNode{
	final Logger logger = LoggerFactory.getLogger(URLBuilderNode.class);
	Hashtable<String,Port> pathPorts= new Hashtable<String,Port>();
	Hashtable<String,Port> paraPorts= new Hashtable<String,Port>();
	
	Textbox baseURL=null;
	Port basePort=null;
	Vbox vbox,pathVbox,paraVbox;
	static int _rs=23;
	public static final String ADD_ICON="img/edit_add-48x48.png";
	public static final String REMOVE_ICON="img/edit_remove-48x48.png";
	Element content=null;
	
	class AddRemoveListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {	
				if(((Image)event.getTarget()).getSrc().equals(ADD_ICON)){
					if (event.getTarget().getParent().getParent()==pathVbox) addPath();
					else 
						if (event.getTarget().getParent().getParent()==paraVbox) addParameter();
				}
				if(((Image)event.getTarget()).getSrc().equals(REMOVE_ICON)){
					if(event.getTarget().getParent().getParent()==pathVbox){
						if(pathPorts.get(event.getTarget().getParent().getUuid())!=null)
							pathPorts.get(event.getTarget().getParent().getUuid()).detach();
						pathPorts.remove(event.getTarget().getParent().getUuid());
						event.getTarget().getParent().detach();
						relayoutPathPorts(1);
						relayoutParaPorts(1);
					}
					else
						if(event.getTarget().getParent().getParent()==paraVbox){
							paraPorts.get(event.getTarget().getParent().getUuid()).detach();
							paraPorts.remove(event.getTarget().getParent().getUuid());
							event.getTarget().getParent().detach();
							relayoutParaPorts(1);
						}
				}
				relayout();
		   }    
	}
	
	public URLBuilderNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,220,138);
		wnd.setTitle("URL builder");
		tagName="urlbuilder";
		
		vbox=new Vbox();
		wnd.appendChild(vbox);
		paraVbox =new Vbox();
		pathVbox =new Vbox();
			    
	    Hbox hbox= new Hbox();
	    hbox.appendChild(new Label("Base:"));
	    hbox.appendChild(baseURL=createParaBox(160,16));
	    vbox.appendChild(hbox);
	    
	    vbox.appendChild(pathVbox);
	    vbox.appendChild(paraVbox);
	    addLabel("Path elements",pathVbox);	    
	    addLabel("Query parameters",paraVbox);
		
	}
	
	protected void initialize(){
		super.initialize();
		basePort=createPort(PipePortType.TEXTIN,205,35);
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
		tagName="urlbuilder";
		
		vbox=new Vbox();
		wnd.appendChild(vbox);
		paraVbox =new Vbox();
		pathVbox =new Vbox();
		
		Hbox hbox= new Hbox();
	    hbox.appendChild(new Label("Base:"));
	    hbox.appendChild(baseURL=createParaBox(160,16));
	    vbox.appendChild(hbox);
        
		vbox.appendChild(pathVbox);
		addLabel("Path elements",pathVbox);
	    vbox.appendChild(paraVbox);
	    addLabel("Query parameters",paraVbox);
	    content=elm;
	}
	
	public void onConnected(Port port){
		if(basePort==port){
			baseURL.setValue("text [wired]");
			baseURL.setReadonly(true);
			return;
		}
		for(int i=1;i<pathVbox.getChildren().size();i++){
			if(pathPorts.get(((Hbox)pathVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setValue("text [wired]");
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setReadonly(true);
				return;
			}
		}
		for(int i=1;i<paraVbox.getChildren().size();i++){
			if(paraPorts.get(((Hbox)paraVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setValue("text [wired]");
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setReadonly(true);
				return;
			}
		}
	}
	
	public void onDisconnected(Port port){
		if(basePort==port){
			baseURL.setValue("");
			baseURL.setReadonly(false);
			return;
		}
		for(int i=1;i<pathVbox.getChildren().size();i++){
			if(pathPorts.get(((Vbox)pathVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setValue("");
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setReadonly(false);
				return;
			}
		}
		for(int i=1;i<paraVbox.getChildren().size();i++){
			if(paraPorts.get(((Vbox)paraVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setValue("false");
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setReadonly(false);
				return;
			}
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
	    return 94+(XMLUtil.getSubElementByName(elm, "path").size()+XMLUtil.getSubElementByName(elm, "para").size())*_rs;
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
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		hbox.appendChild(createParaBox(150,16));		
		Port nPort=createPort(PipePortType.TEXTIN,175,57+(pathVbox.getChildren().size())*_rs);
		pathPorts.put(hbox.getUuid(), nPort);
		pathVbox.appendChild(hbox);
		relayoutParaPorts(1);
	}
	
	public void addPath(Element pathElm){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		Port nPort=createPort(PipePortType.TEXTIN,175,57+(pathVbox.getChildren().size())*_rs);
		pathPorts.put(hbox.getUuid(), nPort);		
		
		Textbox txtBox=createParaBox(150,16);
		hbox.appendChild(txtBox);		
		pathVbox.appendChild(hbox);
		loadConnectedConfig(pathElm, nPort, txtBox);
	}
	
	public void addParameter(){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		
		hbox.appendChild(createParaBox(80,16));
		hbox.appendChild(new Label(" = "));
		hbox.appendChild(createParaBox(80,16));
		Port nPort=createPort(PipePortType.TEXTIN,203,57+(pathVbox.getChildren().size()+paraVbox.getChildren().size())*_rs);
		paraPorts.put(hbox.getUuid(), nPort);		
		paraVbox.appendChild(hbox);		
	}
	
	public void addParameter(Element paraElm){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));		
		
		hbox.appendChild(createParaBox(80,16,paraElm.getAttribute("name")));
		hbox.appendChild(new Label(" = "));
		Textbox txtBox=createParaBox(80,16);
		hbox.appendChild(txtBox);
		
		Port nPort=createPort(PipePortType.TEXTIN,203,57+(pathVbox.getChildren().size()+paraVbox.getChildren().size())*_rs);
		paraPorts.put(hbox.getUuid(), nPort);
		paraVbox.appendChild(hbox);
		loadConnectedConfig(paraElm, nPort, txtBox);	
	}
	
	public void relayout(){
		setDimension(220, 94+(pathVbox.getChildren().size()+paraVbox.getChildren().size()-2)*_rs);
	}
	
	public void relayoutParaPorts(int from){
		for(int i=from;i<paraVbox.getChildren().size();i++){
			paraPorts.get(((Hbox)paraVbox.getChildren().get(i)).getUuid())
			           .setPosition(203,57+(pathVbox.getChildren().size()+i)*_rs);
		}
	}
	
	public void relayoutPathPorts(int from){
		for(int i=from;i<pathVbox.getChildren().size();i++){
			pathPorts.get(((Hbox)pathVbox.getChildren().get(i)).getUuid())
			           .setPosition(175,57+i*_rs);
		}
	}
	
	public Textbox createParaBox(int w,int h,String value){
		Textbox box=new Textbox(value);
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	
	public Textbox createParaBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
		
	public String getCode(){
		if(getWorkspace()!=null){
			String code="";
			code+=getConnectedCode(baseURL, basePort);
						
			String tmp=null;
			for(int i=1;i<pathVbox.getChildren().size();i++){
				
				Hbox hbox=(Hbox)pathVbox.getChildren().get(i);
				tmp=getConnectedCode(((Textbox)hbox.getLastChild()), pathPorts.get(hbox.getUuid()));
				code+=tmp.trim();
				/*if(null!=tmp&&tmp.trim()!=""){
					if((code.charAt(code.length()-1)=='/')||(tmp.charAt(0)=='/'))
						code+=tmp;
					else
						code+="/"+tmp;
				}*/
				
			}
			String and="";
		
			for(int i=1;i<paraVbox.getChildren().size();i++){
				try{
					Hbox hbox=(Hbox)paraVbox.getChildren().get(i);
					if(((Textbox)hbox.getFirstChild().getNextSibling()).getValue().trim()!=""){
						tmp=getConnectedCode(((Textbox)hbox.getLastChild()), paraPorts.get(hbox.getUuid()));											
						//TODO : encoding url fragments here?
						
						if(tmp.indexOf('}')>=0)
							tmp=URLEncoder.encode(tmp,"UTF-8");
						code+=and+URLEncoder.encode(((Textbox)hbox.getFirstChild().getNextSibling()).getValue(),"UTF-8")+"="+tmp;
						and="&";
					}
				}
				catch(java.io.UnsupportedEncodingException e){
					logger.info("UTF-8 support is required by the JVM specification",e);
				}
			}
			
			return code;
		}
		return null;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if(srcCode!=null) return srcCode;
			srcCode =doc.createElement(tagName);
			if(config) setPosition((Element)srcCode);
			
			Element baseElm = doc.createElement("base");
			baseElm.appendChild(getConnectedCode(doc, baseURL, basePort, config));
			srcCode.appendChild(baseElm);
			
			for(int i=1;i<pathVbox.getChildren().size();i++){
				Hbox hbox=(Hbox)paraVbox.getChildren().get(i);
				Element pathElm=doc.createElement("path");
				pathElm.appendChild(getConnectedCode(doc, (Textbox)hbox.getLastChild(), pathPorts.get(hbox.getUuid()), config));	
				srcCode.appendChild(pathElm);
			}
			String tmp=null;
			for(int i=1;i<paraVbox.getChildren().size();i++){
				Hbox hbox=(Hbox)paraVbox.getChildren().get(i);
				Element paraElm =doc.createElement("para");
				paraElm.appendChild(getConnectedCode(doc,(Textbox)hbox.getLastChild(), paraPorts.get(hbox.getUuid()),config));
				srcCode.appendChild(paraElm);	
			}
			return srcCode;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		URLBuilderNode node= new URLBuilderNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")),elm);
		wsp.addFigure(node);
		return node;
	}
	
	public void debug(){
		((PipeEditor)getWorkspace()).reloadTextDebug(getCode()) ;
		((PipeEditor)getWorkspace()).reloadTabularDebug(null);
	}
}
