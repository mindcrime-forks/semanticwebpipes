package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.w3c.dom.Element;

public class SmoosherNode extends InOutNode {
	public SmoosherNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,130,25);
		wnd.setTitle("Smoosher");
		tagName="smoosher";
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SmoosherNode node= new SmoosherNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.connectSource(elm);
		return node;
	}
}
