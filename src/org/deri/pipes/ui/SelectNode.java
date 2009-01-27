package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.w3c.dom.Element;
import org.deri.execeng.utils.*;
public class SelectNode extends QueryNode {
	final Logger logger = LoggerFactory.getLogger(SelectNode.class);

	public SelectNode(int x, int y) {
		super(PipePortType.getPType(PipePortType.SPARQLRESULTOUT),x, y,"Select");
		// TODO Auto-generated constructor stub
		setTitle("Select");
		tagName="select";
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SelectNode node= new SelectNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);	
		node.setQuery(XMLUtil.getTextFromFirstSubEleByName(elm, "query"));
		node.connectSource(elm);		
		return node;
	}
}
