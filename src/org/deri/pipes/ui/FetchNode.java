package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.openrdf.rio.RDFFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class FetchNode extends InPipeNode implements ConnectingInputNode{
	protected Textbox urlTextbox=null;
	protected Port urlPort=null;

	public FetchNode(byte outType,int x,int y,int width,int height,String title,String tagName){
		super(PipePortType.getPType(outType),x,y,width,height);
		this.tagName=tagName;
		wnd.setTitle(title);
		org.zkoss.zul.Label label=new org.zkoss.zul.Label(" URL: ");
        wnd.appendChild(label);
        urlTextbox =new Textbox();
		label=new org.zkoss.zul.Label("Format :");
		wnd.appendChild(urlTextbox);
        wnd.appendChild(label);
        
	}
	
	protected void initialize(){
		super.initialize();
		urlPort =createPort(PipePortType.TEXTIN,35,36);
		((CustomPort)urlPort).setMaxFanIn(1);
	}
	
	public String getFormat(){
		return null;
	}
	
	public void setFormat(String format){
		
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
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if(srcCode!=null) return srcCode;
			srcCode = doc.createElement(tagName);
			if(config) setPosition((Element)srcCode);
			((Element)srcCode).setAttribute("format", getFormat());
			
			Element locElm =doc.createElement("location");
			locElm.appendChild(getConnectedCode(doc, urlTextbox, urlPort, config));
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
		setFormat(elm.getAttribute("format"));
		Element locElm=XMLUtil.getFirstSubElementByName(elm, "location");
		loadConnectedConfig(locElm, urlPort, urlTextbox);
	}
}
