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

import org.deri.pipes.condition.ComparisonCondition;
import org.deri.pipes.text.TextBox;
import org.deri.pipes.ui.PipeEditor;
import org.deri.pipes.ui.PipeNode;
import org.deri.pipes.ui.PipePortType;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zhtml.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class CompareConditionNode extends PipeNode{
	Listbox comparator;
	Port leftSourcePort;
	Port rightSourcePort;
	Port output;
	Element config;
	public CompareConditionNode(int x,int y){
		super(x,y,150,60);
		super.setToobar();
		wnd.setTitle("Compare");
         comparator =new Listbox();
        comparator.setMold("select");
        comparator.appendItem(ComparisonCondition.CONDITION_EQ,ComparisonCondition.CONDITION_EQ);
        comparator.appendItem(ComparisonCondition.CONDITION_NE,ComparisonCondition.CONDITION_NE);
        comparator.appendItem(ComparisonCondition.CONDITION_LT,ComparisonCondition.CONDITION_LT);
        comparator.appendItem(ComparisonCondition.CONDITION_GT,ComparisonCondition.CONDITION_GT);
        comparator.appendItem(ComparisonCondition.CONDITION_LTE,ComparisonCondition.CONDITION_LTE);
        comparator.appendItem(ComparisonCondition.CONDITION_GTE,ComparisonCondition.CONDITION_GTE);
        Div div = new Div();
        div.setStyle("width: 40px; margin-left: auto; margin-right: auto");
        div.appendChild(comparator);
		wnd.appendChild(div);
	}
	public String getComparatorValue(){
		if(comparator.getSelectedItem()!=null)
			return comparator.getSelectedItem().getValue().toString();
		return comparator.getItemAtIndex(0).getValue().toString();
	}
	
	public void setComparatorValue(String value){
		for(int i=0;i<comparator.getItemCount();i++)
		 if(comparator.getItemAtIndex(i).getValue().toString().equalsIgnoreCase(value))
				 comparator.setSelectedIndex(i);
	}
	
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element srcCode = doc.createElement(getTagName());
			if(config){
				setPosition(srcCode);
			}
			srcCode.setAttribute("comparator", getComparatorValue());
			Node left = super.getConnectedCode(doc, "leftSource", leftSourcePort, config);
			if(left != null){
				srcCode.appendChild(left);
			}
			Node right = super.getConnectedCode(doc, "rightSource", rightSourcePort, config);
			if(right != null){
				srcCode.appendChild(right);
			}
			return srcCode;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		CompareConditionNode node= new CompareConditionNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		node.config = elm;
		wsp.addFigure(node);
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "compare";
	}
	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#initialize()
	 */
	@Override
	protected void initialize() {
		leftSourcePort = createPort(PipePortType.getPType(PipePortType.ANYIN),5,35);
		rightSourcePort = createPort(PipePortType.getPType(PipePortType.ANYIN),145,35);		
        output =createPort(PipePortType.getPType(PipePortType.CONDITIONOUT),"bottom");
        if(config != null){
        	String comparator = config.getAttribute("comparator");
        	if(comparator != null){
        		setComparatorValue(comparator);
        	}
        	connectChildElement(config, "leftSource", leftSourcePort);
        	connectChildElement(config, "rightSource", rightSourcePort);
        }
	}



}

