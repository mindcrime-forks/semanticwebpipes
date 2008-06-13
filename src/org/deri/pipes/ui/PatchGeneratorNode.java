package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
public class PatchGeneratorNode extends InOutNode {

	public PatchGeneratorNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x, y, 150,25);
		// TODO Auto-generated constructor stub
		wnd.setTitle("Patch generator");
		tagName="patch-generator";
	}
    
	public String getCode(){
		return super.getCode();
	}
}
