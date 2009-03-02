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
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class XSLTNode extends InOutNode{
	final Logger logger = LoggerFactory.getLogger(XSLTNode.class);
	Port xslPort= null;
	public XSLTNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.XMLIN),PipePortType.getPType(PipePortType.XMLOUT),x,y,220,25);
		wnd.setTitle("XSLT Transformation");
	}
	
	protected void initialize(){
		super.initialize();
		xslPort =createPort(PipePortType.XSLIN,"left");
	}
	
	public Port getXSLPort(){
		return xslPort;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element srcCode = doc.createElement(getTagName());
			if(config) setPosition((Element)srcCode);
	    	srcCode.appendChild(getConnectedCode(doc,"xmlsource",input,config));
	    	srcCode.appendChild(getConnectedCode(doc,"xslsource",xslPort,config));
	    	return srcCode;
		}
		return null;
    }
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		XSLTNode node= new XSLTNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		
		Element xmlElm=XMLUtil.getFirstSubElementByName(elm, "xmlsource");
		PipeNode xmlNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(xmlElm),wsp);
		xmlNode.connectTo(node.getInputPort());
		
		Element xslElm=XMLUtil.getFirstSubElementByName(elm, "xslsource");
		PipeNode xslNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(xslElm),wsp);
		xslNode.connectTo(node.getXSLPort());
		return node;
    }

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "xslt";
	}
}
