package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
public class RDFDeleteNode extends InOutNode{
	Port deletingPort= null;
	public RDFDeleteNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,100,25);
		wnd.setTitle("Delete");
		deletingPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.RDFIN));
		deletingPort.setPosition("left");
		deletingPort.setPortType("input");
        addPort(deletingPort,0,0);		
	}

}
