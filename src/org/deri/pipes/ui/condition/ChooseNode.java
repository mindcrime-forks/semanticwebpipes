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

package org.deri.pipes.ui.condition;

import org.deri.pipes.ui.PipeEditor;
import org.deri.pipes.ui.PipeNode;
import org.deri.pipes.ui.PipePortType;
import org.integratedmodelling.zk.diagram.components.Port;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;

/**
 * @author robful
 *
 */
public class ChooseNode extends PipeNode{

	Port ifPort;
	Port thenPort;
	Port elsePort;
	Port output;
	Element config;
	
	public ChooseNode(int x, int y){
		super(x,y,180,50);
        setToobar();
        Hbox hbox = new Hbox();
        hbox.setWidth("180px");
		Label label = new Label("If True");
		label.setStyle("margin-left: 10px; float: left;");
		hbox.appendChild(label);
		label = new Label("If False");
		label.setStyle("margin-right: 10px; float: right;");
		hbox.appendChild(label);
        wnd.appendChild(hbox);
        wnd.setTitle("Choose");
	}
	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
	}
	
	protected void initialize(){
		ifPort = createPort(PipePortType.getPType(PipePortType.CONDITIONIN),"top");
		ifPort.setTooltiptext("Condition to be evaluated");
		thenPort = createPort(PipePortType.getPType(PipePortType.ANYIN),5,35);
		thenPort.setTooltiptext("Source to use when condition is true");
		elsePort = createPort(PipePortType.getPType(PipePortType.ANYIN),175,35);	
		elsePort.setTooltiptext("Source to use when condition is false");
        output =createPort(PipePortType.getPType(PipePortType.ANYOUT),"bottom");
        if(config != null){
    		connectChildElement(config, "if", ifPort);
    		connectChildElement(config, "then", thenPort);
    		connectChildElement(config, "else", elsePort);
        }
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Node getSrcCode(Document doc, boolean config) {
		if(getWorkspace()!=null){
			Element elm = doc.createElement(getTagName());
			if(config){
				setPosition(elm);
			}
			insertInSrcCode(elm, ifPort, "if", config);
			insertInSrcCode(elm, thenPort, "then", config);
			insertInSrcCode(elm, elsePort, "else", config);
			return elm;
		}
		return null;
	}

	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		ChooseNode node= new ChooseNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		node.config = elm;
		wsp.addFigure(node);
	
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "choose";
	}

}
