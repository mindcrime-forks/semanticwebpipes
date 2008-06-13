package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Workspace;
public class ForNode extends InOutNode{
	Port loopPort= null;
	public ForNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.SPARQLRESULTIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,100,25);
		wnd.setTitle("FOR loop");
		loopPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.RDFIN));
		loopPort.setPosition("left");
		loopPort.setPortType("custom");
        addPort(loopPort,0,0);	
        tagName="for";
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
}
