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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Textbox;

public class TextBandBox extends Bandbox {
	final Logger logger = LoggerFactory.getLogger(TextBandBox.class);
	protected class TextChangeListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {
						 if(event.getTarget()==textbox){
							 setValue(textbox.getRawText());
						 }
						 else{
							 textbox.setText(getValue());
						 }
				
		   }    
	}
	Textbox textbox;
	Bandpopup group;
	public TextBandBox(){
		this("");
	}
	
	public TextBandBox(String q){
		super(q);
		group= new Bandpopup();
		textbox = new Textbox();
		textbox.setMultiline(true);
		textbox.setRows(8);
		textbox.setCols(60);
		textbox.addEventListener("onChange", new TextChangeListener());
		group.appendChild(textbox);
		appendChild(group);
		addEventListener("onChange", new TextChangeListener());
		setTextboxText(q);
	}
	
	public String getTextboxText(){
		return textbox.getRawText();
	}
	
	public void setTextboxText(String q){
		textbox.setText(q);
		setValue(q);
	}
}
