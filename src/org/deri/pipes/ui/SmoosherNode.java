package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.Workspace;

public class SmoosherNode extends InOutNode {
	public SmoosherNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,100,25);
		wnd.setTitle("Smoosher");
		tagName="smoosher";
	}
	
	public String getCode(){
		return super.getCode();
	}
}
