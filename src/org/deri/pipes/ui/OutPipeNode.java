package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
public class OutPipeNode extends PipeNode {
		
	    protected Port input =null;
		public OutPipeNode(int x,int y){
			super(x,y,200,25);
			wnd.setTitle("Output");		
		}
		
		protected void initialize(){
			input =createPort(PipePortType.RDFIN,"top");
		}
		
		public Port getInputPort(){
			return input;
		}
		
		public Node getSrcCode(Document doc,boolean config){
			((PipeEditor)getWorkspace()).removeParameters();
			if (srcCode!=null) return srcCode;
			srcCode=doc.createElement("pipe");
			Element codeElm=doc.createElement("code");
			if (config) setPosition(codeElm);
			
			for(Port p:getWorkspace().getIncomingConnections(input.getUuid())){
				if(p.getParent() instanceof PipeNode){
					codeElm.appendChild(((PipeNode)p.getParent()).getSrcCode(doc,config));					
					break;
				}
			}
			
			Element paraElm =doc.createElement("parameters");
			ArrayList<ParameterNode> paraList=((PipeEditor)getWorkspace()).getParameters();
			for(int i=0;i<paraList.size();i++)
				paraElm.appendChild(paraList.get(i).getSrcCode(doc,config));
			
			srcCode.appendChild(paraElm);
			srcCode.appendChild(codeElm);
			return srcCode;
		}
		
		public static PipeNode loadConfig(Element elm,PipeEditor wsp){
			OutPipeNode node=new OutPipeNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
			wsp.addFigure(node);
			wsp.setOutput(node);
		    PipeNode nextNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(elm),wsp);
		    nextNode.connectTo(node.getInputPort());
			return node;
		}
		
		@Override
		public void reset(boolean recursive){
			super.reset(recursive);
			if (!recursive) return;
			for(Port p:getWorkspace().getIncomingConnections(input.getUuid()))
				if(p.getParent() instanceof PipeNode)
					((PipeNode)p.getParent()).reset(recursive);				
		}
			
		public void debug(){	   
			   ((PipeEditor)getWorkspace()).debug(getSrcCode(false));
		}
	}