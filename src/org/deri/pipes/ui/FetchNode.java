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

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Textbox;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public abstract class FetchNode extends InPipeNode implements ConnectingInputNode{
	final Logger logger = LoggerFactory.getLogger(FetchNode.class);
	protected Textbox urlTextbox=null;
	protected Port urlPort=null;

	public FetchNode(byte outType,int x,int y,int width,int height,String title){
		super(PipePortType.getPType(outType),x,y,width,height);
		wnd.setTitle(title);
		org.zkoss.zul.Label label=new org.zkoss.zul.Label(" URL: ");
        wnd.appendChild(label);
        urlTextbox =new Textbox();
		label=new org.zkoss.zul.Label("Format :");
		wnd.appendChild(urlTextbox);
        wnd.appendChild(label);
        
	}
	
	protected void initialize(){
		super.initialize();
		urlPort = new SourceOrStringPort(getWorkspace(), urlTextbox);
		addPort(urlPort,35,36);
		((CustomPort)urlPort).setMaxFanIn(1);
	}
	
	public String getFormat(){
		return null;
	}
	
	public void setFormat(String format){
		
	}
	
	public void onConnected(Port port){
		if(port == urlPort){
			urlTextbox.setValue("text [wired]");
			urlTextbox.setReadonly(true);
		}
	}

	public void onDisconnected(Port port){
		if(port==urlPort){
			urlTextbox.setValue("");
			urlTextbox.setReadonly(false);
		}
	}
	
	public void setURL(String url){
		urlTextbox.setValue(url);
	}
	
	public Port getURLPort(){
		return urlPort;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element srcCode = doc.createElement(getTagName());
			if(config) setPosition((Element)srcCode);
			((Element)srcCode).setAttribute("format", getFormat());
			
			Element locElm =doc.createElement("location");
			Node connectedCode = getConnectedCode(doc, urlTextbox, urlPort, config);
			locElm.appendChild(connectedCode);
			srcCode.appendChild(locElm);
			return srcCode;
		}
		return null;
	}
	
	
	public void _loadConfig(Element elm){	
		setFormat(elm.getAttribute("format"));
		Element locElm=XMLUtil.getFirstSubElementByName(elm, "location");
		loadConnectedConfig(locElm, urlPort, urlTextbox);
	}
}
