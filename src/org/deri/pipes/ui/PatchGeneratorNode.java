package org.deri.pipes.ui;
import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.*;
import org.w3c.dom.Element;
public class PatchGeneratorNode extends InOutNode {

	public PatchGeneratorNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x, y, 150,25);
		// TODO Auto-generated constructor stub
		wnd.setTitle("Patch generator");
		tagName="patch-generator";
	}
    
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		PatchGeneratorNode node= new PatchGeneratorNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.connectSource(elm);  
		return node;
	}
}
