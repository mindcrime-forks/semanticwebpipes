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
import org.zkoss.zul.Vbox;

public class ReplaceTextNode extends InOutNode{
	TextBandBox pattern;
	TextBandBox replacement;
	public ReplaceTextNode(int x,int y){
		super(PipePortType.getPType(PipePortType.ANYIN),PipePortType.getPType(PipePortType.TEXTOUT),x,y,250,70);
		wnd.setTitle("Replace Text");
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
        hbox= new Hbox();
		hbox.appendChild(new Label("Pattern:"));
		pattern = new TextBandBox();
        hbox.appendChild(pattern);
		vbox.appendChild(hbox);
		hbox = new Hbox();
		hbox.appendChild(new Label("Replacement:"));
		replacement = new TextBandBox();
		hbox.appendChild(replacement);
		vbox.appendChild(hbox);
		wnd.appendChild(vbox);
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		Element srcCode = (Element)super.getSrcCode(doc, config);
		if(srcCode != null){
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "pattern", pattern.getTextboxText()));
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "replacement", replacement.getTextboxText()));
		}
		return srcCode;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		ReplaceTextNode node= new ReplaceTextNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.connectSource(elm);

		String pattern = XMLUtil.getTextFromFirstSubEleByName(elm, "pattern");
		if(pattern != null){
			node.pattern.setTextboxText(pattern);
		}
		String replacement = XMLUtil.getTextFromFirstSubEleByName(elm, "replacement");
		if(replacement != null){
			node.replacement.setTextboxText(replacement);
		}
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "replace-text";
	}



}

