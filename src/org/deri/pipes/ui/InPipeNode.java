package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deri.pipes.ui.PipeNode.DeleteListener;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Connection;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class InPipeNode extends PipeNode{
	final Logger logger = LoggerFactory.getLogger(InPipeNode.class);
	
    protected Port output =null;
	PortType pType;
	
	public InPipeNode(PortType pType,int x,int y,int width,int height){
		super(x,y,width,height);
		this.pType=pType;   
        setToobar();
	}
	
	protected void initialize(){
		output =createPort(pType,"bottom");
	}
	
	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
		if(port.getParent() instanceof ConnectingInputNode)
			((ConnectingInputNode)port.getParent()).onConnected(port);
	}
}
