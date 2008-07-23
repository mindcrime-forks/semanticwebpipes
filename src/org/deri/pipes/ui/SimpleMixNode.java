package org.deri.pipes.ui;
import org.deri.execeng.rdf.BoxParserImplRDF;
import org.deri.execeng.rdf.TextBox;
import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.*;
import org.w3c.dom.Element;
public class SimpleMixNode extends InOutNode {    
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
