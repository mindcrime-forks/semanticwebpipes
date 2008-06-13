package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.Workspace;

public class ConstructNode extends QueryNode {

	public ConstructNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFOUT),x, y,"Construct");
		setTitle("Construct");
		tagName="construct";
	}
	
	public String getCode(){
		return super.getCode();
	}

}
