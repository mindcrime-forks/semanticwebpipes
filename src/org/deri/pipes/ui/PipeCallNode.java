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

import java.util.ArrayList;
import java.util.List;

import org.deri.pipes.core.Engine;
import org.deri.pipes.core.Pipe;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

/**
 * Operator to call one pipe from another.
 * @author robful
 *
 */
public class 
PipeCallNode extends InPipeNode implements ConnectingOutputNode{
	static Logger logger = LoggerFactory.getLogger(InPipeNode.class);
	Listbox pipelist;
	Vbox parameters;
	private static final int BASE_HEIGHT=50;
	private static final int PORT_BASE_Y_POSITION=61;
	private static final int PARAM_HEIGHT=24;
	private static final int TEXTBOX_HEIGHT=20;
	private static final int WIDTH=280;
	private static final int LABEL_WIDTH=80;
	private static final int TEXTBOX_WIDTH=160;
	private static final int PORT_X_POSITION=90;
	private Element content;
	private String preSelectedPipe;
	private PipeCallNode(int x, int y, Element elm){
		this(x,y,XMLUtil.getSubElementByName(elm, "para").size());
		this.content = elm;
	}

	public PipeCallNode(int x, int y){
    	this(x,y,0);
	}
	public PipeCallNode(int x, int y, String pipeName){
		this(x,y,getParamCount(pipeName));
		this.preSelectedPipe = pipeName;
	}
	/**
	 * @param pipeName
	 * @return
	 */
	private static int getParamCount(String pipeName) {
		try {
			return Engine.defaultEngine().getStoredPipe(pipeName).listParameters().size();
		} catch (Exception e) {
			logger.warn("couldn't load pipe ["+pipeName+"]",e);
		}
		return 0;
	}

	private PipeCallNode(int x, int y, int nParams){
		super(PipePortType.getPType(PipePortType.ANYOUT),x,y,WIDTH,BASE_HEIGHT+(nParams*PARAM_HEIGHT));
    	wnd.setTitle("Pipe Call");
    	pipelist = new Listbox();
    	pipelist.setWidth("200px");
    	pipelist.setMold("select");
    	pipelist.appendItem("Choose...", "");
    	List<PipeConfig> pipes = Engine.defaultEngine().getPipeStore().getPipeList();
    	for(PipeConfig pipe : pipes){
    		pipelist.appendItem(pipe.getId(), pipe.getId());
    	}
    	pipelist.addEventListener("onSelect", new EventListener(){
			@Override
			public void onEvent(Event arg0) throws Exception {
				resetParameters(pipelist.getSelectedItem().getValue().toString());
				
			}});
    	Hbox hbox = new Hbox();
    	hbox.appendChild(new Label("Pipe :"));
    	hbox.appendChild(pipelist);
    	Vbox vbox = new Vbox();
    	vbox.appendChild(hbox);
    	parameters = new Vbox();
    	vbox.appendChild(parameters);
    	wnd.appendChild(vbox);

	}
	
	protected void initialize(){
		super.initialize();
		if(content==null){
			if(preSelectedPipe != null){
				pipelist.getItems().clear();
				pipelist.appendItem(preSelectedPipe, preSelectedPipe);
				pipelist.setSelectedIndex(0);
				resetParameters(preSelectedPipe);
			}
		}
		else{
			loadContent(content);
		}
		resetHeight();
	}

	
	/**
	 * @param content2
	 */
	private void loadContent(Element el) {
		String pipeid = el.getAttribute("pipeid");
		pipelist.getItems().clear();
//		List list = new ArrayList();
//		list.add(new Listitem(pipeid,pipeid));
//		pipelist.setModel(new SimpleListModel(list));
		pipelist.appendItem(pipeid, pipeid);
		pipelist.setSelectedIndex(0);
	    List<Element> paraElms=XMLUtil.getSubElementByName(el, "para");
	    for(Element paraElm : paraElms){
	    	String name = paraElm.getAttribute("name");
	    	ParameterizableTextbox textbox = addParameter(name, "");
			loadConnectedConfig(paraElm, textbox.getPort(), textbox);	
	    }
		String x = "";
	}

	public void setSelectedPipe(String pipeid){
		preSelectedPipe=pipeid;
	}

	
	void resetParameters(String pipeId){
		try{
		List list = new ArrayList();
		list.addAll(parameters.getChildren());
		for(Object child : list){
			parameters.removeChild((Component)child);
		}
		list.clear();
		list.addAll(this.getChildren());
		for(Object child : list){
			if(child instanceof SourceOrStringPort){
				removePort((SourceOrStringPort)child);
			}
		}
		if(pipeId == null || pipeId.trim().length()==0){
			return;
		}
		try {
			Pipe pipe = Engine.defaultEngine().getStoredPipe(pipeId);
			for(String key : pipe.listParameters()){
				String value = pipe.getParameter(key);
				addParameter(key, value);
			}
		} catch (Exception e) {
			logger.warn("Couldn't get pipe ["+pipeId+"]",e);
		}
		}finally{
			resetHeight();
		}
	}

	private void resetHeight() {
		int height = BASE_HEIGHT+(parameters.getChildren().size()*PARAM_HEIGHT);
		setDimension(WIDTH,height);
	}

	private ParameterizableTextbox addParameter(String key, String value) {
		Hbox hbox = new Hbox();
		Textbox label = new Textbox();
		label.setText(key);
		label.setReadonly(true);
		label.setWidth(LABEL_WIDTH+"px");
		hbox.appendChild(label);
		hbox.appendChild(new Label(" = "));
		ParameterizableTextbox textbox=new ParameterizableTextbox();
		textbox.setHeight(TEXTBOX_HEIGHT+"px");
		textbox.setWidth(TEXTBOX_WIDTH+"px");
		textbox.setValue(value);
		TextboxPort nPort = new SourceOrStringPort(getWorkspace(),textbox);
		textbox.port = nPort;
		int yPosition = PORT_BASE_Y_POSITION+(parameters.getChildren().size()*PARAM_HEIGHT);
		addPort(nPort,PORT_X_POSITION,yPosition);
		
		hbox.appendChild(textbox);
		parameters.appendChild(hbox);
		return textbox;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Node getSrcCode(Document doc, boolean config) {
		Element el = doc.createElement(getTagName());
		if(config){
			setPosition(el);
		}
		el.setAttribute("pipeid", pipelist.getSelectedItem().getValue().toString());
		List children = parameters.getChildren();
		for(Object child: children){
			Hbox hbox=(Hbox)child;
			ParameterizableTextbox ptb = (ParameterizableTextbox)hbox.getLastChild();
			Port port = ptb.getPort();
			Element paraElm =doc.createElement("para");
			paraElm.setAttribute("name", ((Textbox)hbox.getChildren().get(0)).getValue());
			paraElm.appendChild(getConnectedCode(doc,ptb,port,config));
			el.appendChild(paraElm);	
		}
		
		return el;
	}
	/**
	 * Creates a new HttpGetNode and adds it into the configuration.
	 * @param elm The element defining this http-get
	 * @param wsp The PipeEditor workspace
	 * @return
	 */
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		PipeCallNode node= new PipeCallNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")),elm);
		wsp.addFigure(node);
		return node;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "pipe-call";
	}


}
