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

import org.deri.pipes.text.TextBox;
import org.deri.pipes.utils.XMLUtil;
import org.openrdf.rio.RDFFormat;
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

public class TextNode extends InPipeNode implements ConnectingOutputNode{
	final Logger logger = LoggerFactory.getLogger(TextNode.class);
	Listbox listbox;
	TextBandBox content;
	public TextNode(int x,int y){
		super(PipePortType.getPType(PipePortType.ANYOUT),x,y,180,70);
		wnd.setTitle("Text");
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
        hbox= new Hbox();
		hbox.appendChild(new Label("Format:"));
        listbox =new Listbox();
        listbox.setMold("select");
        listbox.appendItem(TextBox.RDFXML_FORMAT,TextBox.RDFXML_FORMAT);
        listbox.appendItem(TextBox.SPARQL_FORMAT,TextBox.SPARQL_FORMAT);
        listbox.appendItem(TextBox.TEXTPLAIN_FORMAT,TextBox.TEXTPLAIN_FORMAT);
        hbox.appendChild(listbox);
		vbox.appendChild(hbox);
		content = new TextBandBox();
		content.setTextboxText("<?xml version='1.0' encoding='UTF-8'?>\n"
					+"<rdf:RDF \n" +
							"xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'> \n"
					+"</rdf:RDF>");
		vbox.appendChild(content);
		wnd.appendChild(vbox);
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
	
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element srcCode = doc.createElement(getTagName());
			if(config){
				setPosition(srcCode);
			}
			srcCode.setAttribute("format", getFormat());
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "content", content.getTextboxText()));
			return srcCode;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		TextNode node= new TextNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		String content = XMLUtil.getTextFromFirstSubEleByName(elm, "content");
		String format = elm.getAttribute("format");
		if(format != null){
			node.setFormat(format);
		}
		if(content != null){
			node.content.setTextboxText(content);
		}
		return node;
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "text";
	}



}

