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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.xerces.dom.DocumentImpl;
import org.deri.pipes.ui.events.DebugListener;
import org.deri.pipes.ui.events.DeleteListener;
import org.deri.pipes.utils.IDTool;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Shape;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.integratedmodelling.zk.diagram.components.ZKNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public abstract class PipeNode extends ZKNode{  
	final Logger logger = LoggerFactory.getLogger(PipeNode.class);

	private static final long serialVersionUID = -1520720934219234911L;
	protected String tagName=null;	
	protected Window wnd=null;

	public PipeNode(int x,int y,int width,int height){
		super(x,y,width,height);
		this.canDelete=false;
		wnd =new Window();
		appendChild(wnd);
	}

	abstract void initialize();
	public abstract Node getSrcCode(Document doc,boolean config);
	public abstract void connectTo(Port port);

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

	protected Node getConnectedCode(Document doc,Textbox txtBox,Port port,boolean config){
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(port.getUuid());
		for(Port p:incomingConnections){
			if(p.getParent() instanceof ConnectingOutputNode){			
				if(config || ((port instanceof SourceOrStringPort)&&!(p.getParent() instanceof ParameterNode))){
					Node node = ((PipeNode)p.getParent()).getSrcCode(doc,config);
					if((!config)&&node.getNodeType()==Document.ELEMENT_NODE){
						if(!"source".equals(((Element)node).getTagName())){
							Element source = doc.createElement("source");
							source.appendChild(node);
							node = source;
						}
					}
					return node;
				}
				else {
					String srcCode = ((PipeNode)p.getParent()).getSrcCode(config);
					return asTextOrCDataNode(doc, srcCode);
				}
			}
		}
		return asTextOrCDataNode(doc,txtBox.getValue().trim());
	}

	private Node asTextOrCDataNode(Document doc, String srcCode) {
		if(srcCode.indexOf('<')>=0|| srcCode.indexOf('\n')>=0){
			return doc.createCDATASection(srcCode);
		}else{
			return doc.createTextNode(srcCode);
		}
	}

	protected String getConnectedCode(Textbox txtBox,Port port){
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(port.getUuid());
		for(Port p:incomingConnections)
			if(p.getParent() instanceof ConnectingOutputNode)			
				return (((PipeNode)p.getParent()).getSrcCode(false));
		return (txtBox.getValue().trim());
	}

	protected Node getConnectedCode(Document doc,String tagName, Port port,boolean config){
		Element elm=doc.createElement(tagName);    	
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(port.getUuid());
		for(Port p:incomingConnections){
			elm.appendChild(((PipeNode)p.getParent()).getSrcCode(doc,config));
			break;
		}
		return elm;	
	}

	public String generateID(){
		return IDTool.generateRandomID("");
	}

	public void insertInSrcCode(Element parentElm,Port incommingPort,String tagName,boolean config){
		Collection<Port> incomingConnections = getWorkspace().getIncomingConnections(incommingPort.getUuid());
		for(Port port:incomingConnections){
			Element outElm=parentElm.getOwnerDocument().createElement(tagName);
			Element node=(Element)((PipeNode)port.getParent()).getSrcCode(parentElm.getOwnerDocument(),config);
			if(node.getParentNode()!=null){
				String refID=generateID();
				node.setAttribute("ID", refID); //TODO: check first does the id attribute already exist?
				outElm.setAttribute("REFID", refID);
			}
			else
				outElm.appendChild(node);
			parentElm.appendChild(outElm);
		}
	}


	@Override
	public void detach() {
		Workspace ws = getWorkspace();
		if(ws == null){
			return;
		}
		Collection<Port> ports = new ArrayList<Port>();
		ports.addAll(getPorts());
		for(Port port : ports){
			List<Port> connectedIn = new ArrayList<Port>();
			connectedIn.addAll(ws.getIncomingConnections(port.getId()));
			for(Port p : connectedIn){
				ws.notifyConnection(port, p, "", true);
				ws.notifyConnection(p, port, "", true);
			}
			ws.getIncomingConnections(port.getId()).clear();
			List<Port> connectedOut = new ArrayList<Port>();
			connectedOut.addAll(ws.getOutgoingConnections(port.getId()));
			for(Port p : connectedOut){
				ws.notifyConnection(port, p, "", true);
				ws.notifyConnection(p, port, "", true);
			}
			ws.getOutgoingConnections(port.getId()).clear();
		}
		super.detach();
	}

	public void setPosition(Element elm){
		elm.setAttribute("x", ""+getX());
		elm.setAttribute("y", ""+getY());
	}

	protected void loadConnectedConfig(Element elm,Port port,Textbox txtbox){	  
		Element linkedElm=XMLUtil.getFirstSubElement(elm);
		String txt;
		if(linkedElm!=null){
			Shape shape = PipeNode.loadConfig(linkedElm,(PipeEditor)getWorkspace());
			if(shape !=null && shape instanceof PipeNode){
				((PipeNode)shape).connectTo(port);
			}else{
				logger.warn("cannot connect shape ["+shape+"] to port "+port+" using "+linkedElm.getNodeName());
			}
		}else if((txt=XMLUtil.getTextData(elm))!=null){
			if(txt.indexOf("${")>=0){				
				ParameterNode paraNode=((PipeEditor)getWorkspace()).getParameter(txt);
				if(paraNode != null){
					paraNode.connectTo(port);
				}else{
					logger.info("Cannot directly connect parameter referenced variable in "+txt);
					txtbox.setValue(txt);
				}
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


	public String getSrcCode(boolean config){
		DocumentImpl doc =new DocumentImpl();
		Node srcCode = getSrcCode(doc,config);
		return serializeNode(doc, srcCode);
	}

	/**
	 * Serialize this node into a String.
	 * @param doc
	 * @param srcCode
	 * @return
	 */
	protected String serializeNode(Document doc, Node srcCode) {
		if(srcCode.getNodeType() != Node.ELEMENT_NODE){
			return srcCode.getTextContent();
		}
		java.io.StringWriter  strWriter =new java.io.StringWriter(); 
		try{
			Properties props = 
				org.apache.xml.serializer.OutputPropertiesFactory.getDefaultMethodProperties(org.apache.xml.serializer.Method.XML);
			props.setProperty("omit-xml-declaration", "true");
			org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory.getSerializer(props);
			ser.setWriter(strWriter);
			ser.asDOMSerializer().serialize(srcCode!=null?(Element)srcCode:doc.getDocumentElement());
			String src = strWriter.getBuffer().toString();
			return src;
		}	 
		catch(java.io.IOException e){
			logger.info("problem rendering source",e);
		}
		return null;
	}

	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		//logger.debug(elm.getTagName());
		String elementName = elm.getTagName();
		if(elementName.equalsIgnoreCase("pipe")){    
			List<Element>  paraElms=XMLUtil.getSubElementByName(
					XMLUtil.getFirstSubElementByName(elm, "parameters"),"parameter");
			for(int i=0;i<paraElms.size();i++){
				wsp.addParameter((ParameterNode)PipeNode.loadConfig(paraElms.get(i),wsp));
			}
			return PipeNode.loadConfig(XMLUtil.getFirstSubElementByName(elm, "code"),wsp);
		}

		return wsp.createNodeForElement(elm);
	}


	public byte getTypeIdx(){
		return PipePortType.NONE;
	}

	public void debug(){	   
		((PipeEditor)getWorkspace()).hotDebug(getSrcCode(false));
	}


}
