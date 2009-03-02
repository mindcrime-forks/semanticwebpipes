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

import org.deri.pipes.ui.InOutNode;
import org.deri.pipes.ui.PipeEditor;
import org.deri.pipes.ui.PipeNode;
import org.deri.pipes.ui.PipePortType;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author robful
 *
 */
public class NotConditionNode extends InOutNode {

	private Element config;


	/**
	 * @param inPType
	 * @param outPType
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public NotConditionNode(int x, int y) {
    	super(PipePortType.getPType(PipePortType.CONDITIONIN),PipePortType.getPType(PipePortType.CONDITIONOUT),x,y,180,25);
    	wnd.setTitle("Not");
	}
	


	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
		if(config != null){
			connectChildElement(config, "condition",getInputPort());  
		}
		
	}

	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element codeElm =doc.createElement(getTagName());
			if(config) setPosition(codeElm);
			insertInSrcCode(codeElm, input, "condition", config);
			return codeElm;
		}
		return null;
    }


	/**
	 * Creates a new NotConditionNode and adds it into the configuration.
	 * @param elm The element defining this http-get
	 * @param wsp The PipeEditor workspace
	 * @return
	 */
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		NotConditionNode node= new NotConditionNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		node.config = elm;
		wsp.addFigure(node);
		return node;
	}



	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "not";
	}

}
