package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class VariableNode extends InPipeNode implements ConnectingOutputNode{
	Textbox nameBox;
	public VariableNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,200,50);
		wnd.setTitle("Variable");
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
        hbox= new Hbox();
		hbox.appendChild(new Label("Name:"));
		hbox.appendChild(nameBox=createBox(120,16));
		vbox.appendChild(hbox);
		wnd.appendChild(vbox);
		tagName="variable";
	}
	
	public void setName(String name){
		nameBox.setValue(name);
	}
	
	public String getCode(){
		return "${{"+nameBox.getValue()+"}}";
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
			code+=nameBox.getValue();			
			code+="</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		VariableNode node= new VariableNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.setName(XMLUtil.getTextData(elm));
		return node;
	}
	public void debug(){
		
	}
}

