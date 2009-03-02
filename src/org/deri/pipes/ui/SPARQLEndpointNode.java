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

import java.net.URLEncoder;

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class SPARQLEndpointNode extends InPipeNode implements ConnectingInputNode,ConnectingOutputNode {
	final Logger logger = LoggerFactory.getLogger(SPARQLEndpointNode.class);
	Textbox endpoint,defaulturi;
	TextBandBox textBandBox;
	Port endpointPort,defaulturiPort=null;
	
	protected Listbox listbox;
	
	public SPARQLEndpointNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,260,100);
		wnd.setTitle("SPARQL Endpoint builder");
		
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
		hbox.appendChild(new Label("Endpoint:"));
		hbox.appendChild(endpoint=createBox(120,16));
		vbox.appendChild(hbox);
		
		
		
	    hbox= new Hbox();
		hbox.appendChild(new Label("Default-graph-URI:"));
		hbox.appendChild(defaulturi=createBox(120,16));
		vbox.appendChild(hbox);
		
		
	    
	    hbox= new Hbox();
		hbox.appendChild(new Label("Query:"));
		hbox.appendChild(textBandBox=new TextBandBox());
		vbox.appendChild(hbox);
		        
        wnd.appendChild(vbox);
	}
	
	protected void initialize(){
		super.initialize();
		endpointPort=createPort(PipePortType.TEXTIN,190,35);
	    defaulturiPort=createPort(PipePortType.TEXTIN,250,59);
	}
	
	public void onConnected(Port port){
		if(endpointPort==port){
			endpoint.setValue("text [wired]");
			endpoint.setReadonly(true);
			return;
		}
		if(defaulturiPort==port){
			defaulturi.setValue("text [wired]");
			defaulturi.setReadonly(true);
			return;
		}
	}
	
	public void onDisconnected(Port port){
		if(endpointPort==port){
			endpoint.setValue("");
			endpoint.setReadonly(false);
			return;
		}
		if(defaulturiPort==port){
			defaulturi.setValue("");
			defaulturi.setReadonly(false);
			return;
		}
	}
	
	public String getFormat(){
		if(listbox.getSelectedItem()!=null)
			return listbox.getSelectedItem().getValue().toString();
		return listbox.getItemAtIndex(0).getValue().toString();
	}
	
	public void setFormat(String format){
		for(int i=0;i<listbox.getItemCount();i++)
		 if(listbox.getItemAtIndex(i).getValue().toString().equalsIgnoreCase(format))
				 listbox.setSelectedIndex(i);
	}
	
	public void setEndpoint(String url){
		endpoint.setValue(url);
	}
			
	public void setDefaultURI(String uri){
		defaulturi.setValue(uri);
	}
	
	public void setQuery(String query){
		textBandBox.setTextboxText(query);
	}
	
	public String getSrcCode(boolean config){
		if(getWorkspace()!=null){
			if (config) return "";
			String code="";
			code+=getConnectedCode(endpoint, endpointPort);
			String uri=getConnectedCode(defaulturi, defaulturiPort);
			try{
				code+="?query="+URLEncoder.encode(textBandBox.getValue(),"UTF-8");
				if((null!=uri)&&(uri.indexOf("://")>0))					
						code+="&default-graph-uri="+URLEncoder.encode(uri.trim(),"UTF-8");
			}
			catch(java.io.UnsupportedEncodingException e){
				logger.info("UTF-8 support is required by the JVM specification",e);
			}
			return code;
		}
		return null;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element srcCode =doc.createElement(getTagName());
			if(config) setPosition((Element)srcCode);
			
			Element endpointElm=doc.createElement("endpoint");
			endpointElm.appendChild(getConnectedCode(doc, endpoint, endpointPort, config));
			srcCode.appendChild(endpointElm);
			
			Element graphElm=doc.createElement("default-graph-uri");
			graphElm.appendChild(getConnectedCode(doc, defaulturi, defaulturiPort, config));
			srcCode.appendChild(endpointElm);
			
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "query", textBandBox.getText()));
			return srcCode;
		}
		return null;
	}
	
	public void _loadConfig(Element elm){	
		loadConnectedConfig(XMLUtil.getFirstSubElementByName(elm, "endpoint"), endpointPort, endpoint);
		loadConnectedConfig(XMLUtil.getFirstSubElementByName(elm, "default-graph-uri"), defaulturiPort, defaulturi);
		setQuery(XMLUtil.getTextFromFirstSubEleByName(elm, "query"));
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SPARQLEndpointNode node= new SPARQLEndpointNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
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
		return "sparqlendpoint";
	}

}

