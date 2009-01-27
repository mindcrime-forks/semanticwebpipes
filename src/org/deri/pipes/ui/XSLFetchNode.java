package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class XSLFetchNode extends SimpleFetchNode{
	final Logger logger = LoggerFactory.getLogger(XSLFetchNode.class);

	public XSLFetchNode(int x,int y){
		super(PipePortType.XSLOUT,x,y,"XSL Fetch","xslfetch");
	}
		
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		XSLFetchNode node= new XSLFetchNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
		return node;
	}
}
