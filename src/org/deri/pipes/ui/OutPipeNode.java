package org.deri.pipes.ui;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.integratedmodelling.zk.diagram.components.PortTypeManager;
import java.util.ArrayList;
import org.w3c.dom.Element;
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
		
		public String getCode(){
			((PipeEditor)getWorkspace()).removeParameters();
			String pipe="<pipe>\n";
			String code="";
			for(Port p:getWorkspace().getIncomingConnections(input.getUuid())){
				if(p.getParent() instanceof PipeNode){
					code+="<code>\n"+((PipeNode)p.getParent()).getCode()+"</code>\n";					
					break;
				}
			}
			pipe+="<parameters>\n";
			ArrayList<ParameterNode> paraList=((PipeEditor)getWorkspace()).getParameters();
			for(int i=0;i<paraList.size();i++){
				pipe+=paraList.get(i).getParaCode();
			}
			pipe+="</parameters>\n";
			pipe+=code;
			pipe+="</pipe>\n";
			//System.out.println(pipe);
			return pipe;
		}
		
		public String getConfig(){
			((PipeEditor)getWorkspace()).removeParameters();
			String pipe="<pipe>\n";
			String code="";
			for(Port p:getWorkspace().getIncomingConnections(input.getUuid())){
				if(p.getParent() instanceof PipeNode){
					code+="<code x=\""+getX()+"\" y=\""+getY()+"\">\n"+((PipeNode)p.getParent()).getConfig()+"</code>\n";					
					break;
				}
			}
			
			pipe+="<parameters>\n";
			ArrayList<ParameterNode> paraList=((PipeEditor)getWorkspace()).getParameters();
			for(int i=0;i<paraList.size();i++){
				pipe+=paraList.get(i).getParaConfig();
			}
			pipe+="</parameters>\n";
			pipe+=code;
			
			pipe+="</pipe>\n";
			return pipe;
		}
		
		public static PipeNode loadConfig(Element elm,PipeEditor wsp){
			OutPipeNode node=new OutPipeNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
			wsp.addFigure(node);
			wsp.setOutput(node);
		    PipeNode nextNode=PipeNode.loadConfig(XMLUtil.getFirstSubElement(elm),wsp);
		    nextNode.connectTo(node.getInputPort());
			return node;
		}
		
		public void debug(){	   
			   ((PipeEditor)getWorkspace()).debug(getCode());
		}
	}