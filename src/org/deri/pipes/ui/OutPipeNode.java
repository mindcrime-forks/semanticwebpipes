package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.integratedmodelling.zk.diagram.components.PortTypeManager;
import java.util.ArrayList;
public class OutPipeNode extends PipeNode {
		
	    protected Port input =null;
		public static ArrayList<TextInNode> paraList =new ArrayList();
		private static PortTypeManager pTypeMag=null;
		private static Workspace wsp=null;
		public OutPipeNode(int x,int y){
			super(x,y,200,25);
			wnd.setTitle("Output");		}
		
		public static PortTypeManager getPTypeMag(){
			if(pTypeMag==null) pTypeMag=new PortTypeManager(wsp);
			return pTypeMag;
		}
		
		public  void setWorkspace(Workspace _wsp){
			pTypeMag=new PortTypeManager(_wsp);
			wsp=_wsp;
			input =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.RDFIN));
			input.setPosition("top");
			input.setPortType("custom");
	        addPort(input,0,0);
		}
		public String getCode(){
			paraList.removeAll(paraList);
			String pipe="<pipe>\n";
			String code="";
			for(Port p:getWorkspace().getIncomingConnections(input.getUuid())){
				if(p.getParent() instanceof PipeNode){
					code+="<code>\n"+((PipeNode)p.getParent()).getCode()+"</code>\n";					
					break;
				}
			}
			pipe+="<parameters>\n";
			for(int i=0;i<paraList.size();i++){
				pipe+=paraList.get(i).getCode();
			}
			pipe+="</parameters>\n";
			pipe+=code;
			pipe+="</pipe>\n";
			return pipe;
		}
	}