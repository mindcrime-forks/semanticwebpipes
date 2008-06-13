package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
public class SimpleMixNode extends InOutNode {    
	public SimpleMixNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x, y, 100,25);
		// TODO Auto-generated constructor stub
		wnd.setTitle("Simple Mix");
		tagName="simplemix";
	}
	public String getCode(){
		return super.getCode();
	}
}
