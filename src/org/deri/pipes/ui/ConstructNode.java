package org.deri.pipes.ui;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */

import org.integratedmodelling.zk.diagram.components.Workspace;
import org.w3c.dom.Element;
import org.deri.execeng.utils.XMLUtil;
public class ConstructNode extends QueryNode {

	public ConstructNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.RDFOUT),x, y,"Construct");
		setTitle("Construct");
		tagName="construct";
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		ConstructNode node= new ConstructNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.setQuery(XMLUtil.getTextFromFirstSubEleByName(elm, "query"));
		node.connectSource(elm);
		return node;
	}

}
