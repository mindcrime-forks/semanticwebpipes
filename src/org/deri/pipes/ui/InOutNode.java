package org.deri.pipes.ui;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
import org.deri.execeng.utils.XMLUtil;
import org.deri.pipes.ui.PipeNode.DeleteListener;
import org.integratedmodelling.zk.diagram.components.Connection;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Toolbarbutton;
import org.w3c.dom.Element;
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
	
	public void connectTo(Port port){
		System.out.println(output.getUuid()+"-->"+port.getUuid());
		getWorkspace().connect(output,port,false);
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
	
	public String getConfig(){
		if(getWorkspace()!=null){
	    	String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<source>\n";
	    		code+=((PipeNode)port.getParent()).getConfig();
	    		code+="</source>\n";
	    	}
	    	code+="</"+tagName+">\n";
	    	return code;
		}
		return null;
	}
	
	public void connectSource(Element elm){
		java.util.ArrayList<Element> childNodes=XMLUtil.getSubElementByName(elm, "source");
 		for(int i=0;i<childNodes.size();i++){		
 			PipeNode nextNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(childNodes.get(i)),(PipeEditor)getWorkspace());
 			nextNode.connectTo(getInputPort());
 		}  
	}
}
