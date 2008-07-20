package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.openrdf.rio.RDFFormat;
import org.w3c.dom.Element;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SimpleFetchNode extends InPipeNode implements ConnectingInputNode{
	protected Textbox urlTextbox=null;
	protected Port urlPort=null;

	public SimpleFetchNode(byte portType,int x,int y,String title,String tagName){
		super(PipePortType.getPType(portType),x,y,200,80);
		this.tagName=tagName;
		
		urlPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		urlPort.setPosition("none");
		urlPort.setPortType("custom");
        addPort(urlPort,35,36);
        
		wnd.setTitle(title);
		org.zkoss.zul.Label label=new org.zkoss.zul.Label(" URL: ");
        wnd.appendChild(label);
        urlTextbox =new Textbox();
		wnd.appendChild(urlTextbox);
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
	
	public String getCode(){
		if(getWorkspace()!=null){
			String code="<"+tagName+">\n<location>";
			code+="<![CDATA["+getConnectedCode(urlTextbox, urlPort)+"]]>";
			code+="</location>\n</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n<location>";
			code+=getConnectedConfig(urlTextbox, urlPort);
			code+="</location>\n</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public void _loadConfig(Element elm){		
		Element locElm=XMLUtil.getFirstSubElementByName(elm, "location");
		Element linkedElm=XMLUtil.getFirstSubElement(locElm);
		String url;
		if(linkedElm!=null){
			PipeNode linkedNode=PipeNode.loadConfig(linkedElm,(PipeEditor)getWorkspace());
			linkedNode.connectTo(urlPort);
			onConnected(null);
		}else if((url=XMLUtil.getTextData(locElm))!=null){
			if(url.indexOf("${")>=0){
				ParameterNode paraNode=OutPipeNode.getParaNode(url);
				paraNode.connectTo(urlPort);
				onConnected(null);
			}
			else{
				setURL(url);
			}
		}

	}
}
