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

/**
 * @author robful
 *
 */
public class OrConditionNode extends PipeNode {
	Port leftPort;
	Port rightPort;
	Port output;

	private Element config;


	/**
	 * @param inPType
	 * @param outPType
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public OrConditionNode(int x, int y) {
    	super(x,y,100,60);
        setToobar();
        wnd.setTitle("Or");
	}
	


	@Override
	protected void initialize() {
		leftPort = createPort(PipePortType.getPType(PipePortType.CONDITIONIN),40,32);
		rightPort = createPort(PipePortType.getPType(PipePortType.CONDITIONIN),40,42);
		output = createPort(PipePortType.getPType(PipePortType.CONDITIONOUT),"bottom");
		if(config != null){
			connectChildElement(config, "left", leftPort);  
			connectChildElement(config, "right", rightPort);  
		}
		
	}

	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element codeElm =doc.createElement(getTagName());
			if(config) setPosition(codeElm);
			insertInSrcCode(codeElm, leftPort, "left", config);
			insertInSrcCode(codeElm, rightPort, "right", config);
			return codeElm;
		}
		return null;
    }


	/**
	 * Creates a new AndConditionNode and adds it into the configuration.
	 * @param elm The element defining this http-get
	 * @param wsp The PipeEditor workspace
	 * @return
	 */
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		OrConditionNode node= new OrConditionNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		node.config = elm;
		wsp.addFigure(node);
		return node;
	}



	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
	}



	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "or";
	}

}
