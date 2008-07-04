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
public class FetchNode extends InPipeNode {
	protected Textbox urlTextbox=null;
	protected Port urlPort=null;

	public FetchNode(byte outType,int x,int y,int width,int height,String title,String tagName){
		super(PipePortType.getPType(outType),x,y,width,height);
		this.tagName=tagName;
		
		urlPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		urlPort.setPosition("none");
		urlPort.setPortType("custom");
        addPort(urlPort,35,33);
        
		wnd.setTitle(title);
		org.zkoss.zul.Label label=new org.zkoss.zul.Label(" URL: ");
        wnd.appendChild(label);
        urlTextbox =new Textbox();
		label=new org.zkoss.zul.Label("Format :");
		wnd.appendChild(urlTextbox);
		//urlTextbox.setReadonly(true);
        wnd.appendChild(label);
        
	}
	
	public String getFormat(){
		return null;
	}
	
	public void setFormat(String format){
		
	}
	
	public void disableURLTextbox(){
		urlTextbox.setValue("text [wired]");
		urlTextbox.setReadonly(true);
	}
	
	public void enableURLTextbox(){
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
			String code="<"+tagName+" format=\""+getFormat()+"\">\n<location>";
			code+="<![CDATA["+getConnectedCode(urlTextbox, urlPort)+"]]>";
			code+="</location>\n</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" format=\""+getFormat()+"\" x=\""+getX()+"\" y=\""+getY()+"\">\n<location>";
			code+=getConnectedConfig(urlTextbox, urlPort);
			code+="</location>\n</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public void _loadConfig(Element elm){		
		setFormat(elm.getAttribute("format"));
		Element locElm=XMLUtil.getFirstSubElementByName(elm, "location");
		Element linkedElm=XMLUtil.getFirstSubElement(locElm);
		String url;
		if(linkedElm!=null){
			PipeNode linkedNode=PipeNode.loadConfig(linkedElm,(PipeEditor)getWorkspace());
			linkedNode.connectTo(urlPort);
			disableURLTextbox();
		}else if((url=XMLUtil.getTextData(locElm))!=null){
			if(url.indexOf("${")>=0){
				ParameterNode paraNode=OutPipeNode.getParaNode(url);
				paraNode.connectTo(urlPort);
				disableURLTextbox();
			}
			else{
				setURL(url);
			}
		}

	}
}
