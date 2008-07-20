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
		xslPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.XSLIN));
		xslPort.setPosition("left");
		xslPort.setPortType("custom");
        addPort(xslPort,0,0);	
        tagName="xslt";
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
		
		Element slElm=XMLUtil.getFirstSubElementByName(elm, "xmlsource");
		PipeNode slNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(slElm),wsp);
		slNode.connectTo(node.getInputPort());
		
		Element loopElm=XMLUtil.getFirstSubElementByName(elm, "xslsource");
		PipeNode loopNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(loopElm),wsp);
		loopNode.connectTo(node.getXSLPort());
		return node;
    }
}
