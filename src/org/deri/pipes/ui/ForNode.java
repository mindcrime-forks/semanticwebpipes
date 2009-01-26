package org.deri.pipes.ui;
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
public class ForNode extends InOutNode{
	Port loopPort= null;
	public ForNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.SPARQLRESULTIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,130,25);
		wnd.setTitle("FOR loop");
        tagName="for";
	}
	
	protected void initialize(){
		super.initialize();
		loopPort =createPort(PipePortType.RDFIN,"left");
		((CustomPort)loopPort).setMaxFanIn(1);
        ((CustomPort)getInputPort()).setMaxFanIn(1);
	}
	
	public Port getLoopPort(){
		return loopPort;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
	    	if(srcCode!=null) return srcCode;
	    	
	    	srcCode=doc.createElement("for");
	    	if(config) setPosition((Element)srcCode);
	    	
	    	srcCode.appendChild(getConnectedCode(doc, "sourcelist", input, config));
	    	srcCode.appendChild(getConnectedCode(doc, "forloop", loopPort, config));		
	    	
	    	return srcCode;
		}
		return null;
    }
	
	@Override
	public void reset(boolean recursive){
		super.reset(recursive);
		if (!recursive) return;
		reset(loopPort,recursive);
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
