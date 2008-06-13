package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.Workspace;

public class RDFSMixNode extends InOutNode {
	public RDFSMixNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,100,25);
		wnd.setTitle("RDFS Mix");
		tagName="rdfs";
	}
	
	public String getCode(){
		return super.getCode();
	}
}
