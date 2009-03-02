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

import java.util.Collection;
import java.util.Hashtable;

import org.deri.pipes.rdf.HTMLFetchBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;
public class HTMLFetchNode extends FetchNode{
	final Logger logger = LoggerFactory.getLogger(HTMLFetchNode.class);
	Hashtable<String,Checkbox> checkboxes=new Hashtable<String,Checkbox>();
	public HTMLFetchNode(int x,int y){
		super(PipePortType.RDFOUT,x,y,200,110,"HTML Fetch");
		Vbox vbox=new Vbox();
		Hbox hbox=new Hbox();
		int count=0;
		Collection<String> keys=HTMLFetchBox.getFormats();
		for(String key : keys) {
	    	Checkbox checkbox=new Checkbox(key);
	    	checkboxes.put(key,checkbox);
	    	hbox.appendChild(checkbox);
	    	count++;
	    	if(count%3==0){
	    		vbox.appendChild(hbox);
	    		hbox=new Hbox();
	    	}
		}
		wnd.appendChild(vbox);
	}
	
	public String getFormat(){
		String format="";
		Collection<String> keys=HTMLFetchBox.getFormats();
		for(String key : keys) {
	    	if(checkboxes.get(key).isChecked()) format+=key+"|";
		}
		return format;
	}
	
	public void setFormat(String format){
		Collection<String> keys=HTMLFetchBox.getFormats();
		for(String key : keys) {
	    	if(format.indexOf(key)>=0) checkboxes.get(key).setChecked(true);
		}
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		HTMLFetchNode node= new HTMLFetchNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
		return node;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "htmlfetch";
	}
}
