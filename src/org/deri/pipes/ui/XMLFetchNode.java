package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class XMLFetchNode extends SimpleFetchNode{
	final Logger logger = LoggerFactory.getLogger(XMLFetchNode.class);

	public XMLFetchNode(int x,int y){
		super(PipePortType.XMLOUT,x,y,"XML Fetch","xmlfetch");
	}
		
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		XMLFetchNode node= new XMLFetchNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
		return node;
	}
}
