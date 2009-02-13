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
import org.integratedmodelling.zk.diagram.components.PortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
public class XQueryNode extends InOutNode{
	final Logger logger = LoggerFactory.getLogger(XQueryNode.class);
	QueryBox queryBox=null;	
	Textbox contentTypeBox = null;
	public XQueryNode(int x,int y){
		super(PipePortType.getPType(PipePortType.ANYIN),PipePortType.getPType(PipePortType.ANYOUT),x,y,250,120);
		this.tagName = "xquery";
		wnd.setTitle("XQuery");
		Vbox vbox = new Vbox();
		Hbox hbox1 = new Hbox();
		vbox.appendChild(hbox1);
		hbox1.appendChild(new Label("Content-Type"));
		contentTypeBox = new Textbox();
		contentTypeBox.setWidth("100px");
		contentTypeBox.setHeight("20px");
		contentTypeBox.setText("text/xml");
		hbox1.appendChild(contentTypeBox);
		Hbox hbox2 = new Hbox();
		hbox2.appendChild(new Label("XQuery"));        
		queryBox=new QueryBox();
		queryBox.setQuery("xquery version \"1.0\";\n<html>\n <head>\n  <title>xquery</title>" +
				"\n </head>" +
				"\n <body>" +
				"\n{for $link //a return\n"
				+"\n<p>{$link}</p>" +
				"</body>" +
		"\n</html>");
		hbox2.appendChild(queryBox);
		vbox.appendChild(hbox2);
		wnd.appendChild(vbox);
	}
	protected void initialize(){
		super.initialize();
	}


	public void setTitle(String title){
		wnd.setTitle(title);
	}

	public void setQuery(String query){
		queryBox.setQuery(query);
	}

	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Node srcCode =super.getSrcCode(doc, config);
			Element contentTypeElm = doc.createElement("contentType");
			contentTypeElm.appendChild(doc.createTextNode(contentTypeBox.getValue()));
			srcCode.appendChild(contentTypeElm);
	    	Element queryElm=doc.createElement("query");
			srcCode.appendChild(queryElm);
			queryElm.appendChild(doc.createCDATASection(queryBox.getQuery()));
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
		XQueryNode node= new XQueryNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		String contentType = XMLUtil.getTextFromFirstSubEleByName(elm, "contentType");
		String query =XMLUtil.getTextFromFirstSubEleByName(elm, "query");
		if(query != null){
			node.queryBox.setQuery(query);
		}
		if(contentType != null){
			node.contentTypeBox.setValue(contentType);
		}
		node.connectSource(elm);
		return node;
	}

}
