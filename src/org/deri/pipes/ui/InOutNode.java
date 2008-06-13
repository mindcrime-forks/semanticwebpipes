package org.deri.pipes.ui;

import org.deri.pipes.ui.PipeNode.DeleteListener;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Toolbarbutton;
public class InOutNode extends PipeNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2684001403256691428L;
	protected Port input =null,output=null;
	
	public InOutNode(PortType inPType,PortType outPType,int x,int y,int width,int height){
		super(x,y,width,height);
		input =new CustomPort(OutPipeNode.getPTypeMag(),inPType);
    	input.setPosition("top");
        input.setPortType("custom");
        addPort(input,0,0);
        
        output =new CustomPort(OutPipeNode.getPTypeMag(),outPType);
     	output.setPosition("bottom");
        output.setPortType("custom");
        addPort(output,0,0);
        
       Caption caption =new Caption();
 	   Toolbarbutton delButton= new Toolbarbutton("","img/del-16x16.png");
 	   delButton.setClass("drag");
 	   delButton.addEventListener("onClick", new DeleteListener(this));
 	   wnd.appendChild(caption);
 	   caption.appendChild(delButton);
	}
	
	public Port getInputPort(){
		return input;
	}
	
	public Port getOutputPort(){
		return output;
	}
    
	public String getCode(){
		if(getWorkspace()!=null){
	    	String code="<"+tagName+">\n";
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<source>\n";
	    		code+=((PipeNode)port.getParent()).getCode();
	    		code+="</source>\n";
	    	}
	    	code+="</"+tagName+">\n";
	    	return code;
		}
		return null;
    }
}
