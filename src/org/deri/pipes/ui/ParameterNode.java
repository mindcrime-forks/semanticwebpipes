package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class ParameterNode extends InPipeNode implements ConnectingOutputNode{
	Textbox nameBox,labelBox,defaultBox;
	public ParameterNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,200,100);
		wnd.setTitle("Text input");
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
		hbox.appendChild(new Label("Label:"));
		hbox.appendChild(labelBox=createBox(120,16));
		vbox.appendChild(hbox);
		
        hbox= new Hbox();
		hbox.appendChild(new Label("Name:"));
		hbox.appendChild(nameBox=createBox(120,16));
		vbox.appendChild(hbox);
		
		hbox= new Hbox();
		hbox.appendChild(new Label("Default:"));
		hbox.appendChild(defaultBox=createBox(120,16));
		vbox.appendChild(hbox);
		wnd.appendChild(vbox);
		tagName="parameter";
	}
	
	public void setName(String name){
		nameBox.setValue(name);
	}
	
	public void setLabel(String label){
		labelBox.setValue(label);
	}
	
	public void setDefaultValue(String value){
		defaultBox.setValue(value);
	}
	
	/*public String getParameter(){
		return "${"+nameBox.getValue()+"}";
	}*/
	
	
	public String getParaId(){
		return nameBox.getValue();
	}
	
	public String getParaCode(){
		if(getWorkspace()!=null){
			String code="<"+tagName+">\n";
			code+="<id>"+nameBox.getValue()+"</id>\n";
			code+="<label>"+labelBox.getValue()+"</label>\n";
			code+="<default>"+defaultBox.getValue()+"</default>\n";
			code+="</"+tagName+">\n";
			return code;
		}
		return null;
	}
	public String getCode(){
		if (OutPipeNode.paraList.indexOf(this)<0){
			OutPipeNode.paraList.add(this);
		}
		return "${"+nameBox.getValue()+"}";
	}
	
	public String getParaConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
			code+="<id>"+nameBox.getValue()+"</id>\n";
			code+="<label>"+labelBox.getValue()+"</label>\n";
			code+="<default>"+defaultBox.getValue()+"</default>\n";
			code+="</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			if (OutPipeNode.paraList.indexOf(this)<0){
				OutPipeNode.paraList.add(this);
			}
			return "${"+nameBox.getValue()+"}";
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		ParameterNode node= new ParameterNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.setName(XMLUtil.getTextFromFirstSubEleByName(elm, "id"));
		node.setLabel(XMLUtil.getTextFromFirstSubEleByName(elm, "label"));
		node.setLabel(XMLUtil.getTextFromFirstSubEleByName(elm, "default"));
		return node;
	}
	
	public void debug(){
		
	}
}

