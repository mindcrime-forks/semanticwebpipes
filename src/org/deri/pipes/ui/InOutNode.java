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
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
import java.util.List;

import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
public abstract class InOutNode extends PipeNode{
	final Logger logger = LoggerFactory.getLogger(InOutNode.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 2684001403256691428L;
	protected Port input;
	protected Port output;
	PortType inPType;
	PortType outPType;
	public InOutNode(PortType inPType,PortType outPType,int x,int y,int width,int height){
		super(x,y,width,height);
		this.inPType=inPType;
		this.outPType=outPType;
        setToobar();
	}
	
	public Port getInputPort(){
		return input;
	}
	
	public Port getOutputPort(){
		return output;
	}
	
	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
	}
	
	protected void initialize(){
		input =createPort(inPType,"top");
        output =createPort(outPType,"bottom");
	}
	
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			Element codeElm =doc.createElement(getTagName());
			if(config) setPosition(codeElm);
			insertInSrcCode(codeElm, input, "source", config);
			return codeElm;
		}
		return null;
    }
		
	public void connectSource(Element elm){
		String childTagName = "source";
		connectChildElement(elm, childTagName,getInputPort());  
	}

}
