package org.deri.pipes.ui;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deri.execeng.utils.XMLUtil;
import org.deri.pipes.ui.PipeNode.DeleteListener;
import org.integratedmodelling.zk.diagram.components.Connection;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Toolbarbutton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
public class InOutNode extends PipeNode{
	final Logger logger = LoggerFactory.getLogger(InOutNode.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 2684001403256691428L;
	protected Port input =null,output=null;
	PortType inPType,outPType;
	public InOutNode(PortType inPType,PortType outPType,int x,int y,int width,int height){
		super(x,y,width,height);
		this.inPType=inPType;
		this.outPType=outPType;
        setToobar();
	}
	
	public Port getInputPort(){
		return input;
	}
	
	public Port getOutputPort(){
		return output;
	}
	
	public void connectTo(Port port){
		getWorkspace().connect(output,port,false);
	}
	
	protected void initialize(){
		input =createPort(inPType,"top");
        output =createPort(outPType,"bottom");
	}
	
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if (srcCode!=null) return srcCode;
			Element codeElm =doc.createElement(tagName);
			if(config) setPosition(codeElm);
			insertInSrcCode(codeElm, input, "source", config);
			return codeElm;
		}
		return null;
    }
	
	public void reset(boolean recursive){
		super.reset(recursive);
		if (!recursive) return;
		reset(input,recursive);
	}
	
	public void connectSource(Element elm){
		java.util.ArrayList<Element> childNodes=XMLUtil.getSubElementByName(elm, "source");
 		for(int i=0;i<childNodes.size();i++){		
 			PipeNode nextNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(childNodes.get(i)),(PipeEditor)getWorkspace());
 			nextNode.connectTo(getInputPort());
 		}  
	}
}
