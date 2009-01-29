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
package org.deri.execeng.rdf;

import javax.xml.transform.stream.StreamSource;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.deri.execeng.model.Operator;
import org.deri.execeng.utils.XMLUtil;
import org.deri.execeng.utils.XSLTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
public class XSLTBox implements Operator {
	final Logger logger = LoggerFactory.getLogger(XSLTBox.class);
	String xmlStrID,xslStrID;
	private boolean isExecuted=false;
	XMLStreamBuffer buffer;
	private PipeContext context;
	
	@Override
	public void execute() {
		if((null!=xmlStrID)&&(null!=xslStrID)){			
			StreamSource xmlSrc=executeXMLOp(xmlStrID);
			StreamSource xslSrc=executeXMLOp(xslStrID);
			if((xmlSrc!=null)&&(xslSrc!=null)){
				buffer=new XMLStreamBuffer();	
			    buffer.setStreamSource(XSLTUtil.transform(xmlSrc,xslSrc));				
			}
	    }
		isExecuted=true;
	}
	
    private StreamSource executeXMLOp(String strID){
    	
    	Operator xmlOp=context.getOperatorExecuted(strID);
		ExecBuffer xmlBuff=xmlOp.getExecBuffer();
		
		StreamSource xmlSrc=null;
		if(xmlBuff instanceof XMLStreamBuffer) 
			xmlSrc=((XMLStreamBuffer)xmlBuff).getStreamSource();
		if((xmlBuff instanceof SesameTupleBuffer)||(xmlBuff instanceof SesameTupleBuffer)){ 
			XMLStreamBuffer tmpBuff= new XMLStreamBuffer();
			xmlBuff.stream(tmpBuff);
			xmlSrc=tmpBuff.getStreamSource();
		}
		
		return xmlSrc;
    }
    

	@Override
	public ExecBuffer getExecBuffer() {
		// TODO Auto-generated method stub
		return buffer;
	}



	@Override
	public boolean isExecuted() {
		return isExecuted;
	}

	@Override
	public void stream(ExecBuffer buffer) {
		logger.error("not implemented");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stream(ExecBuffer buffer, String context) {
		logger.error("not implemented");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(PipeContext context, Element element) {
		this.setContext(context);
		xmlStrID=context.getPipeParser().getSourceOperatorId(XMLUtil.getFirstSubElement(
				XMLUtil.getFirstSubElementByName(element, "xmlsource")));
		xslStrID=context.getPipeParser().getSourceOperatorId(XMLUtil.getFirstSubElement(
				XMLUtil.getFirstSubElementByName(element, "xslsource")));
		if (null==xmlStrID){
			logger.warn("<sourcelist> element must be set !!!");
			//TODO : Handling error of lacking xml source for XSLT transformation 	
		}
		if (null==xslStrID){
			logger.warn("<sourcelist> element must be set !!!");
			//TODO : Handling error of lacking xml source for XSLT transformation 	
		}
	}

	public void setContext(PipeContext context) {
		this.context = context;
	}	
}
