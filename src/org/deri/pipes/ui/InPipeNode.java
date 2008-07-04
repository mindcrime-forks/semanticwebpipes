package org.deri.pipes.ui;

import org.deri.pipes.ui.PipeNode.DeleteListener;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Connection;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class InPipeNode extends PipeNode{
	
    protected Port output =null;
	
	public InPipeNode(PortType pType,int x,int y,int width,int height){
		super(x,y,width,height);
		        
        output =new CustomPort(OutPipeNode.getPTypeMag(),pType);
     	output.setPosition("bottom");
        output.setPortType("custom");
        addPort(output,0,0);
        
        setToobar();
	}
	
	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
	}
}
