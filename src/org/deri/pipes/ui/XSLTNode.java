package org.deri.pipes.ui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class XSLTNode extends InOutNode{
	final Logger logger = LoggerFactory.getLogger(XSLTNode.class);
	Port xslPort= null;
	public XSLTNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.XMLIN),PipePortType.getPType(PipePortType.XMLOUT),x,y,200,25);
		wnd.setTitle("XSLT Transformation");
        tagName="xslt";
	}
	
	protected void initialize(){
		super.initialize();
		xslPort =createPort(PipePortType.XSLIN,"left");
	}
	
	public Port getXSLPort(){
		return xslPort;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if (srcCode!=null) return srcCode;
			if(config) setPosition((Element)srcCode);
	    	srcCode.appendChild(getConnectedCode(doc,"xmlsource",input,config));
	    	srcCode.appendChild(getConnectedCode(doc,"xslsource",xslPort,config));
	    	return srcCode;
		}
		return null;
    }
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		XSLTNode node= new XSLTNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		
		Element xmlElm=XMLUtil.getFirstSubElementByName(elm, "xmlsource");
		PipeNode xmlNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(xmlElm),wsp);
		xmlNode.connectTo(node.getInputPort());
		
		Element xslElm=XMLUtil.getFirstSubElementByName(elm, "xslsource");
		PipeNode xslNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(xslElm),wsp);
		xslNode.connectTo(node.getXSLPort());
		return node;
    }
}
