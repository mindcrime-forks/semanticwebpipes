package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.Port;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
public class TextInNode extends InPipeNode {
	Textbox nameBox,labelBox,defaultBox;
	public TextInNode(int x,int y){
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
	
	public Textbox createBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	
	public String getParameter(){
		return "$"+nameBox.getValue()+"$";
	}
	
	public String getCode(){
		if(getWorkspace()!=null){
			String code="<"+tagName+">\n";
			code+="<id>"+nameBox.getValue()+"</id>\n";
			code+="<label>"+labelBox.getValue()+"</label>\n";
			code+="<default>"+nameBox.getValue()+"</default>\n";
			code+="</"+tagName+">\n";
			return code;
		}
		return null;
	}
}
