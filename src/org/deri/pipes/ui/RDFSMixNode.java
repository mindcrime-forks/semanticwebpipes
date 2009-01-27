package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class RDFSMixNode extends InOutNode {
	final Logger logger = LoggerFactory.getLogger(RDFSMixNode.class);
	public RDFSMixNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,130,25);
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
