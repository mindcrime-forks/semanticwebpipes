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
public class ForNode extends InOutNode{
	Port loopPort= null;
	public ForNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.SPARQLRESULTIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,130,25);
		wnd.setTitle("FOR loop");
		loopPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.RDFIN));
		loopPort.setPosition("left");
		loopPort.setPortType("custom");
        addPort(loopPort,0,0);	
        tagName="for";
	}
	
	public Port getLoopPort(){
		return loopPort;
	}
	
	public String getCode(){
		if(getWorkspace()!=null){
	    	String code="<"+tagName+">\n";
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<sourcelist>\n";
	    		code+=((PipeNode)port.getParent()).getCode();
	    		code+="</sourcelist>\n";
	    		break;
	    	}
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(loopPort.getUuid())){
	    		code+="<forloop>\n";
	    		code+=((PipeNode)port.getParent()).getCode();
	    		code+="</forloop>\n";
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
	    		code+="<sourcelist>\n";
	    		code+=((PipeNode)port.getParent()).getConfig();
	    		code+="</sourcelist>\n";
	    		break;
	    	}
	    	
	    	for(Port port:getWorkspace().getIncomingConnections(loopPort.getUuid())){
	    		code+="<forloop>\n";
	    		code+=((PipeNode)port.getParent()).getConfig();
	    		code+="</forloop>\n";
	    		break;
	    	}
	    	code+="</"+tagName+">\n";
	    	return code;
		}
		return null;
    }
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		ForNode node= new ForNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		
		Element slElm=XMLUtil.getFirstSubElementByName(elm, "sourcelist");
		PipeNode slNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(slElm),wsp);
		slNode.connectTo(node.getInputPort());
		
		Element loopElm=XMLUtil.getFirstSubElementByName(elm, "forloop");
		PipeNode loopNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(loopElm),wsp);
		loopNode.connectTo(node.getLoopPort());
		return node;
    }
}
