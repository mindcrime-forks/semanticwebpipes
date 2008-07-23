package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class XSLTNode extends InOutNode{
	Port xslPort= null;
	public XSLTNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.XMLIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,200,25);
		wnd.setTitle("XSL Transformation");
        tagName="xslt";
	}
	
	protected void initialize(){
		super.initialize();
		xslPort =createPort(PipePortType.XSLIN,"left");
	}
	
	public Port getXSLPort(){
		return xslPort;
	}
	
	public String getCode(){
		if(getWorkspace()!=null){
	    	String code="<"+tagName+">\n";
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<xmlsource>\n";
	    		code+=((PipeNode)port.getParent()).getCode();
	    		code+="</xmlsource>\n";
	    		break;
	    	}
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(xslPort.getUuid())){
	    		code+="<xslsource>\n";
	    		code+=((PipeNode)port.getParent()).getCode();
	    		code+="</xslsource>\n";
	    		break;
	    	}
	    	code+="</"+tagName+">\n";
	    	return code;
		}
		return null;
    }
	
	public String getConfig(){
		if(getWorkspace()!=null){
	    	String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<xmlsource>\n";
	    		code+=((PipeNode)port.getParent()).getConfig();
	    		code+="</xmlsource>\n";
	    		break;
	    	}
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(xslPort.getUuid())){
	    		code+="<xslsource>\n";
	    		code+=((PipeNode)port.getParent()).getConfig();
	    		code+="</xslsource>\n";
	    		break;
	    	}
	    	code+="</"+tagName+">\n";
	    	return code;
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
