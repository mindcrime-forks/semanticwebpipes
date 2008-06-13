package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.Workspace;

public class SelectNode extends QueryNode {

	public SelectNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.SPARQLRESULTOUT),x, y,"Select");
		// TODO Auto-generated constructor stub
		setTitle("Select");
		tagName="select";
	}
	
	public String getCode(){
		return super.getCode();
	}
}
