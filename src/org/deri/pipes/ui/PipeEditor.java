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

import groovy.lang.GroovyClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.deri.pipes.model.SesameTupleBuffer;
import org.deri.pipes.model.TextBuffer;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortTypeManager;
import org.integratedmodelling.zk.diagram.components.PortTypeMask;
import org.integratedmodelling.zk.diagram.components.Shape;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
public class PipeEditor extends Workspace {
	//TODO: make engine a settable field
	static Engine engine = Engine.defaultEngine();
	private transient IPipeNodeFactory groovyPipeNodeFactory;
	final static Logger logger = LoggerFactory.getLogger(PipeEditor.class);
	private Textbox textDebugPanel,pipeid,pipename,password;
	private Bandbox bdid;
	private Tabpanel tabularDebugPanel=null;
	private OutPipeNode outputNode;
	private PortTypeManager pTypeMag;
	private ArrayList<ParameterNode> paraList =new ArrayList<ParameterNode>();
	public PipeEditor(String w,String h){
		super();
		setWidth(w);
		setHeight(h);
		pTypeMag=new PortTypeManager(this);
		PipePortType.generateAllPortTypes(this);
	}

	/**
	 * Overriding this method because the standard connect and delete events
	 * do not seem to be propogated as expected.
	 */
	@Override
	public void notifyConnection(Port src, Port target, String connId, boolean isDeleted) {
		super.notifyConnection(src, target, connId, isDeleted);
		if(isDeleted){
			if(target instanceof TextboxPort){
				((TextboxPort)target).onDisconnect();
			}
		}else{
			if(target instanceof TextboxPort){
				((TextboxPort)target).onConnect();
			}			
		}
		
	}

	public void setTextDebugPanel(Textbox txtBox){
		textDebugPanel=txtBox;
	}

	public void addFigure(Shape shape){
		super.addFigure(shape);
		if(shape instanceof PipeNode)
			((PipeNode)shape).initialize();
	}

	public PortTypeManager getPTManager(){
		return pTypeMag;
	}

	public OutPipeNode getOutput(){
		return outputNode;
	}

	public void setOutput(OutPipeNode outputNode){
		this.outputNode=outputNode;
	}

	public void setTabularDebugPanel(Tabpanel tabpanel){
		tabularDebugPanel=tabpanel;
	}

	public Textbox getTextDebugPanel(){
		return textDebugPanel;
	}

	public Tabpanel getTabularDebugPanel(){
		return tabularDebugPanel;
	}


	public void setConfigComps(Textbox pipeid,Bandbox bdid,Textbox pipename,Textbox password){
		this.pipeid=pipeid;
		this.pipename=pipename;
		this.password=password;
		this.bdid=bdid;
	}

	public Textbox getPipeIdTxtBox(){
		return pipeid;
	}

	public Textbox getPipeNameTxtBox(){
		return pipeid;
	}

	public Textbox getPasswordTxtBox(){
		return password;
	}

	public boolean savePipe(){
		return engine.getPipeStore().save(getPipeConfig()); 
	}

	public PipeConfig getPipeConfig() {
		PipeConfig pipeConfig = new PipeConfig();
		pipeConfig.setId(getPipeId());
		pipeConfig.setName(getPipeName());
		pipeConfig.setPassword(getPassword());
		pipeConfig.setSyntax(getSrcCode(false));
		pipeConfig.setConfig(getSrcCode(true));
		return pipeConfig;
	}


	public String getSrcCode(boolean config){
		if(outputNode==null) return "";
		return outputNode.getSrcCode(config);
	}

	public String getPipeId(){
		return pipeid.getValue();
	}

	public String getPipeName(){
		return pipename.getValue();
	}

	public String getPassword(){
		return password.getValue();
	}

	public void addParameter(ParameterNode paraNode){
		if (paraList.indexOf(paraNode)<0){
			if(paraNode.getName()!= null){
				for(ParameterNode p : paraList){
					if(paraNode.getName().equals(p.getName())){
						return;
					}
				}
			}
			paraList.add(paraNode);
		}
	}

	public ParameterNode getParameter(String nodeId){
		for(int i=0;i<paraList.size();i++){
			ParameterNode parameterNode = paraList.get(i);
			if((nodeId.equals(parameterNode.getParaId()) || nodeId.equals("${"+parameterNode.getParaId()+"}"))&&(parameterNode.getWorkspace()!=null)){
				return parameterNode;
			}
		}
		logger.debug("No parameter set for ["+nodeId+"]");
		return null;
	}

	public ArrayList<ParameterNode> getParameters(){
		return paraList;
	}

	public void removeParameters(){
		paraList.removeAll(paraList);
	}

	public void createFigure(int x,int y,String figureType){
		if(outputNode==null){
			outputNode=new  OutPipeNode(400,400);
			addFigure(outputNode);
		}
		x-=350;
		y-=70;
		try{
			if(groovyPipeNodeFactory == null){
				initialiseGrovePipeNodeFactory();
			}
			Shape shape =  groovyPipeNodeFactory.createShape(figureType,x,y);
			if(shape != null){
				addFigure(shape);
			}else{
				logger.warn("Not configured to add shape having tag name=["+figureType+"]");

			}
		}catch(Throwable t){
			logger.error("Cannot parse pipe xml",t);
		}
	}


	public static Listbox createListbox(TupleQueryResult tuple){
		Listbox listbox =new Listbox();
		listbox.setWidth("98%");
		listbox.setRows(20);
		java.util.List<String> bindingNames = tuple.getBindingNames();
		Listhead listhead=new Listhead();
		for(int i=0;i<bindingNames.size();i++)
			listhead.appendChild(new Listheader(bindingNames.get(i)));
		listbox.appendChild(listhead);
		try{
			while (tuple.hasNext()) {
				Listitem item=new Listitem();			    	   
				BindingSet bindingSet = tuple.next();		   
				for(int i=0;i<bindingNames.size();i++){
					Listcell cell=new Listcell(bindingSet.getValue(bindingNames.get(i)).toString());
					cell.setStyle("font-size: 8px;");
					item.appendChild(cell);
				}
				listbox.appendChild(item);	   
			} 
		}
		catch(QueryEvaluationException e){
			logger.warn("Problem encountered appending to listbox",e);
		}
		return listbox;
	}

	public  void reloadTextDebug(String text){
		textDebugPanel.setValue(text);
	}

	public void reloadTabularDebug(TupleQueryResult tuple){
		if(tabularDebugPanel.getFirstChild()!=null)
			tabularDebugPanel.getFirstChild().detach();
		if(tuple!=null)
			tabularDebugPanel.appendChild(createListbox(tuple));
	}

	public void debug(){
		debug(getSrcCode(false));
	}
	
	public String populatePara(String syntax){
		for(int i=0;i<paraList.size();i++){
			syntax = syntax.replace("${" + paraList.get(i).getParaId() + "}", paraList.get(i).getDefaultVal());
			try{
				syntax=syntax.replace(URLEncoder.encode("${" + paraList.get(i).getParaId()  + "}","UTF-8"),
						URLEncoder.encode(paraList.get(i).getDefaultVal(),"UTF-8"));
			}
			catch(java.io.UnsupportedEncodingException e){
				logger.warn("UTF-8 support is required by the JVM specification",e);
			}
		}

		return syntax;
	}
	public void debug(String syntax){
		hotDebug(syntax);
	}

	public void hotDebug(String syntax){
		syntax=populatePara(syntax);
		//	logger.debug(syntax);
		TupleQueryResult tuple=null;
		String textResult=null;
		try {
			Operator op= engine.parse(syntax);;
			if(op == null){
				textResult = "An error occurred parsing the pipe";
				return;
			}
			ExecBuffer buff=op.execute(engine.newContext());
			if(buff == null){
				textResult = "An error occurred executing the pipe";
				return;
			}
			try{
				if(buff instanceof SesameMemoryBuffer){
					buff = ((SesameMemoryBuffer)buff).toTupleBuffer();
					textResult= buff.toString();
				}
				if(buff instanceof SesameTupleBuffer){
					tuple=((SesameTupleBuffer)buff).getTupleQueryResult();
					textResult= buff.toString();
				}if (buff instanceof TextBuffer){
					textResult= buff.toString();
				}else{
					logger.info("Using InputStream to get string for buffer"+buff.getClass());
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					buff.stream(bout);
					bout.close();
					textResult = bout.toString("UTF-8");
				}
			}catch(Exception e){
				textResult = getStack(e);
				logger.warn("Problem encountered getting tuples from pipe result",e);
			}
		} catch (Exception e) {
			textResult=getStack(e);
			logger.warn("could not hotDebug",e);
		}finally{
			reloadTextDebug(textResult);
			reloadTabularDebug(tuple);
		}
	}

	private String getStack(Exception e) {
		String textResult;
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		textResult = stringWriter.toString();
		return textResult;
	}

	public void reload(String config){
		Object[] children=getChildren().toArray();
		for(int i=0;i<children.length;i++){
			if(!(children[i] instanceof PortTypeMask))
				((Component)children[i]).detach();
		}
		outputNode=null;
		if((null==config)||(config.trim()=="")) return;
		InputSource input=new InputSource(new StringReader(config));
		try {
			DOMParser parser = new DOMParser();
			parser.parse(input);  
			PipeNode.loadConfig(parser.getDocument().getDocumentElement(),this);
		} catch (Exception e) {
			logger.warn("problem parsing config",e);
		}
	}

	public void clone(String pid){
		PipeConfig config = engine.getPipeStore().getPipe(pid);
		reload(config == null?"":config.getConfig());
		pipeid.setValue("Copy of "+config.getId());
		bdid.setValue(pipeid.getValue());
		pipename.setValue(config.getName().length()>0?"(Copy of) "+config.getName():"");
	}

	public void newPipe(){
		reload(null);
		pipeid.setValue("");
		bdid.setValue("");
		pipename.setValue("");
	}

	public void edit(String pid){
		PipeConfig pipeConfig=engine.getPipeStore().getPipe(pid);
		if(pipeConfig == null){
			pipeConfig = new PipeConfig();
		}
		reload(pipeConfig.getConfig());
		pipeid.setValue(pipeConfig.getId());
		bdid.setValue(pipeConfig.getId());
		pipename.setValue(pipeConfig.getName());
	}

	/**
	 * @param elm
	 * @return
	 */
	public PipeNode createNodeForElement(Element element) {
		try{
			if(groovyPipeNodeFactory == null){
				initialiseGrovePipeNodeFactory();
			}
			return groovyPipeNodeFactory.createPipeNode(element, this);
		}catch(Throwable t){
			logger.error("Cannot parse pipe xml",t);
		}
		return null;
	}

	@Override
	public void onConnectionCreated(Port arg0, Port arg1) {
		// TODO Auto-generated method stub
		super.onConnectionCreated(arg0, arg1);
		System.out.println("connectionCreated");
	}

	@Override
	public void onConnectionDeleted(Port arg0, Port arg1) {
		// TODO Auto-generated method stub
		super.onConnectionDeleted(arg0, arg1);
		System.out.println("connectionDeleted");
	}

	private void initialiseGrovePipeNodeFactory()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		GroovyClassLoader loader = new GroovyClassLoader(PipeNode.class.getClassLoader());
		Class groovyClass = loader.loadClass("PipeNodeFactory",true,true);
		groovyPipeNodeFactory = (IPipeNodeFactory) groovyClass.newInstance();
	}
}
