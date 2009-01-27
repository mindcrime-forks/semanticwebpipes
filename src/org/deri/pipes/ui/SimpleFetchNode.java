package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Textbox;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SimpleFetchNode extends InPipeNode implements ConnectingInputNode{
	final Logger logger = LoggerFactory.getLogger(SimpleFetchNode.class);
	protected Textbox urlTextbox=null;
	protected Port urlPort=null;

	public SimpleFetchNode(byte portType,int x,int y,String title,String tagName){
		super(PipePortType.getPType(portType),x,y,200,50);
		this.tagName=tagName;
		wnd.setTitle(title);
		org.zkoss.zul.Label label=new org.zkoss.zul.Label(" URL: ");
        wnd.appendChild(label);
        urlTextbox =new Textbox();
		wnd.appendChild(urlTextbox);
	}
	
	protected void initialize(){
		super.initialize();
		urlPort =createPort(PipePortType.TEXTIN,35,36);
    
	}
	public void onConnected(Port port){
		urlTextbox.setValue("text [wired]");
		urlTextbox.setReadonly(true);
	}
	
	public void onDisconnected(Port port){
		urlTextbox.setValue("");
		urlTextbox.setReadonly(false);
	}
	
	public void setURL(String url){
		urlTextbox.setValue(url);
	}
	
	public Port getURLPort(){
		return urlPort;
	}
	
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			//return if srcCode was created
			if (srcCode!=null) return srcCode;
			
			srcCode = doc.createElement(tagName);
			if (config) setPosition((Element)srcCode);
			
			Element locElm=doc.createElement("location");
			locElm.appendChild(getConnectedCode(doc,urlTextbox,urlPort,config));
			srcCode.appendChild(locElm);
			return srcCode;
		}
		return null;
	}
	
	@Override
	public void reset(boolean recursive){
		srcCode=null;
		if(recursive)
			reset(urlPort,recursive);
	}
	
	public void _loadConfig(Element elm){		
		Element locElm=XMLUtil.getFirstSubElementByName(elm, "location");
		loadConnectedConfig(locElm, urlPort, urlTextbox);
	}
}
