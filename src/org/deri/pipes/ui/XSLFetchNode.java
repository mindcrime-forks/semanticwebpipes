package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.openrdf.rio.RDFFormat;
import org.w3c.dom.Element;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class XSLFetchNode extends SimpleFetchNode{

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
