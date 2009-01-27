package org.deri.pipes.ui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
public class SimpleMixNode extends InOutNode {    
	final Logger logger = LoggerFactory.getLogger(SimpleMixNode.class);
	public SimpleMixNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x, y, 140,25);
	
		wnd.setTitle("Simple Mix");
		tagName="simplemix";
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		
		SimpleMixNode node= new SimpleMixNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.connectSource(elm);
		return node;
	}
}
