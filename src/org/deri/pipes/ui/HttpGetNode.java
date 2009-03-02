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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Label;
/**
 * @author robful
 *
 */
public class HttpGetNode extends InPipeNode implements ConnectingInputNode{
	/**
	 * 
	 */
	private static final String ATTR_ACCEPT_CONTENT_TYPE = "acceptContentType";
	/**
	 * 
	 */
	private static final String ATTR_RESOLVE_HTML_LINKS = "resolveHtmlLinks";
	final transient Logger logger = LoggerFactory.getLogger(FetchNode.class);
	protected Textbox urlTextbox;
	protected Textbox acceptContentTypeTextbox;
	protected Checkbox resolveLinksCheckbox;
	protected Port urlPort=null;

	public HttpGetNode(int x,int y){
		super(PipePortType.getPType(PipePortType.ANYOUT),x,y,300,100);
		wnd.setTitle("HTTP Get");
		Vbox vbox = new Vbox();
		wnd.appendChild(vbox);
        Hbox hbox = new Hbox();
        Label urlLabel = new Label(" URL: ");
        urlLabel.setWidth("60px");
		hbox.appendChild(urlLabel);
		urlTextbox = new Textbox();
        urlTextbox.setWidth("200px");
        hbox.appendChild(urlTextbox);
        vbox.appendChild(hbox);
        
        hbox = new Hbox();
        Label acceptLabel = new Label(" Accept: ");
        acceptLabel.setWidth("60px");
		hbox.appendChild(acceptLabel);
		acceptContentTypeTextbox = new Textbox();
        acceptContentTypeTextbox.setWidth("200px");
        acceptContentTypeTextbox.setTooltiptext("(Optional) value for the Accept HTTP Header");
        hbox.appendChild(acceptContentTypeTextbox);
        vbox.appendChild(hbox);
        resolveLinksCheckbox =  new Checkbox("Make Links Absolute");
        resolveLinksCheckbox.setTooltip("Change relative href links to absolute URLs");
        vbox.appendChild(resolveLinksCheckbox);
	}


	protected void initialize(){
		super.initialize();
		urlPort = new SourceOrStringPort(getWorkspace(), urlTextbox);
		addPort(urlPort,280,36);
		//urlPort = createPort(PipePortType.SOURCEORSTRINGIN,280,36);
		((CustomPort)urlPort).setMaxFanIn(1);
	}
	
	public void onConnected(Port port){
	}

	public void onDisconnected(Port port){
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
			if(config){
				super.setPosition(srcCode);
			}
			if(acceptContentTypeTextbox.getText().trim().length()>0){
				srcCode.setAttribute(ATTR_ACCEPT_CONTENT_TYPE,acceptContentTypeTextbox.getText().trim());
			}
			if(resolveLinksCheckbox.isChecked()){
				srcCode.setAttribute(ATTR_RESOLVE_HTML_LINKS, "true");
			}
			Element locElm =doc.createElement("location");
			locElm.appendChild(getConnectedCode(doc, urlTextbox, urlPort, config));
			srcCode.appendChild(locElm);
			return srcCode;
		}
		return null;
	}
	/**
	 * Creates a new HttpGetNode and adds it into the configuration.
	 * @param elm The element defining this http-get
	 * @param wsp The PipeEditor workspace
	 * @return
	 */
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		HttpGetNode node= new HttpGetNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		if(elm.hasAttribute(ATTR_RESOLVE_HTML_LINKS)){
			node.resolveLinksCheckbox.setChecked("true".equals(elm.getAttribute(ATTR_RESOLVE_HTML_LINKS)));
		}
		if(elm.hasAttribute(ATTR_ACCEPT_CONTENT_TYPE)){
			node.acceptContentTypeTextbox.setText(elm.getAttribute(ATTR_ACCEPT_CONTENT_TYPE));
		}
		wsp.addFigure(node);
		Element locElm=XMLUtil.getFirstSubElementByName(elm, "location");
		node.loadConnectedConfig(locElm, node.urlPort, node.urlTextbox);
		return node;
	}


	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "http-get";
	}

}
