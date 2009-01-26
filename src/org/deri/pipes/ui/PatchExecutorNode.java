package org.deri.pipes.ui;
import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.*;
import org.w3c.dom.Element;
public class PatchExecutorNode extends InOutNode {

	public PatchExecutorNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x, y, 180,25);
		// TODO Auto-generated constructor stub
		wnd.setTitle("Patch executor");
		tagName="patch-executor";
	}
    
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		PatchExecutorNode node= new PatchExecutorNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.connectSource(elm);  
		return node;
	}
}
