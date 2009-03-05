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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class ParameterNode extends InPipeNode implements ConnectingOutputNode{
	final Logger logger = LoggerFactory.getLogger(ParameterNode.class);
	Textbox nameBox;
	Textbox labelBox;
	Textbox defaultBox;
	public ParameterNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,200,100);
		wnd.setTitle("Parameter");
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
		hbox.appendChild(new Label("Label:"));
		hbox.appendChild(labelBox=createBox(120,16));
		vbox.appendChild(hbox);
		
        hbox= new Hbox();
		hbox.appendChild(new Label("Name:"));
		hbox.appendChild(nameBox=createBox(120,16));
		vbox.appendChild(hbox);
		
		hbox= new Hbox();
		hbox.appendChild(new Label("Default:"));
		hbox.appendChild(defaultBox=createBox(120,16));
		vbox.appendChild(hbox);
		wnd.appendChild(vbox);
	}
	
	public void setName(String name){
		nameBox.setValue(name);
	}
	
	public String getName(){
		return nameBox.getValue();
	}
	
	public void setLabel(String label){
		labelBox.setValue(label);
	}
	
	public void setDefaultValue(String value){
		defaultBox.setValue(value);
	}
	
	public String getParaId(){
		return nameBox.getValue();
	}
	
	public String getDefaultVal(){
		return defaultBox.getValue();
	}
	
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element srcCode =doc.createElement(getTagName());
			if(config) setPosition((Element)srcCode);
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "id", nameBox.getValue()));
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "label", labelBox.getValue()));
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "default", defaultBox.getValue()));			
			return srcCode;
		}
		return null;
	}
	
	public String getSrcCode(boolean config){	
		if(getWorkspace()!=null){
			((PipeEditor)getWorkspace()).addParameter(this);
			return "${"+nameBox.getValue()+"}";
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		String nodeId = XMLUtil.getTextFromFirstSubEleByName(elm, "id");
		ParameterNode node = wsp.getParameter(nodeId);
		if(node != null){
			return node;
		}
		node= new ParameterNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);	
		node.setName(nodeId);
		node.setLabel(XMLUtil.getTextFromFirstSubEleByName(elm, "label"));
		node.setDefaultValue(XMLUtil.getTextFromFirstSubEleByName(elm, "default"));
		wsp.addParameter(node);
		return node;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "parameter";
	}
	
	public void debug(){	   
		PipeEditor pipeEditor = (PipeEditor)getWorkspace();
		pipeEditor.reloadTabularDebug(null);
		pipeEditor.reloadTextDebug("["+nameBox.getValue()+"]=["+defaultBox.getValue()+"]");
	}


}

