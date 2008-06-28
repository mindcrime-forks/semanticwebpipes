package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.w3c.dom.Element;

public class RDFSMixNode extends InOutNode {
	public RDFSMixNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,100,25);
		wnd.setTitle("RDFS Mix");
		tagName="rdfs";
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		RDFSMixNode node= new RDFSMixNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.connectSource(elm);
		return node;
	}
}
