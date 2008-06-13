package org.deri.pipes.ui;

import org.deri.pipes.ui.PipeNode.DeleteListener;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Toolbarbutton;

public class InPipeNode extends PipeNode{
	
    protected Port output =null;
	
	public InPipeNode(PortType pType,int x,int y,int width,int height){
		super(x,y,width,height);
		        
        output =new CustomPort(OutPipeNode.getPTypeMag(),pType);
     	output.setPosition("bottom");
        output.setPortType("custom");
        addPort(output,0,0);
        
       Caption caption =new Caption();
 	   Toolbarbutton delButton= new Toolbarbutton("","img/del-16x16.png");
 	   delButton.setClass("drag");
 	   delButton.addEventListener("onClick", new DeleteListener(this));
 	   wnd.appendChild(caption);
 	   caption.appendChild(delButton);
	}

}
