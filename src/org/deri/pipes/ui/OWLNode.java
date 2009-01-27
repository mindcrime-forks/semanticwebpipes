package org.deri.pipes.ui;

import java.util.List;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class OWLNode extends InOutNode{
	final Logger logger = LoggerFactory.getLogger(OWLNode.class);
	Port owlPort= null;
	public OWLNode(int x,int y){		
		super(PipePortType.getPType(PipePortType.RDFIN),PipePortType.getPType(PipePortType.RDFOUT),x,y,200,25);
		wnd.setTitle("OWL Reasoner");
        tagName="OWL";
	}
	
	protected void initialize(){
		super.initialize();
		owlPort =createPort(PipePortType.RDFIN,"left");
	}
	
	public Port getOWLPort(){
		return owlPort;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if (srcCode!=null) return srcCode;
			srcCode =super.getSrcCode(doc, config);
	    	srcCode.appendChild(getConnectedCode(doc,"owlsource",owlPort,config));
	    	return srcCode;
		}
		return null;
    }
	
	@Override
	public void reset(boolean recursive){
		super.reset(recursive);
		if(recursive) reset(owlPort,recursive);
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		OWLNode node= new OWLNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		
		List<Element> srcEles=XMLUtil.getSubElementByName(elm, "source");
		for(int i=0;i<srcEles.size();i++){
			PipeNode xmlNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(srcEles.get(i)),wsp);
			xmlNode.connectTo(node.getInputPort());
		}
		
		Element xslElm=XMLUtil.getFirstSubElementByName(elm, "owlsource");
		PipeNode xslNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(xslElm),wsp);
		xslNode.connectTo(node.getOWLPort());
		
		return node;
    }
}
