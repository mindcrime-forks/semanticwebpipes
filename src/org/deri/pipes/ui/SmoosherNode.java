package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class SmoosherNode extends InOutNode {
	final Logger logger = LoggerFactory.getLogger(SmoosherNode.class);
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
