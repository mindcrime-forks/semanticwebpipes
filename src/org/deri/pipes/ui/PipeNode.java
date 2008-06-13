package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.*;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class PipeNode extends ZKNode{  
  
/**
	 * 
	 */
	private static final long serialVersionUID = -1520720934219234911L;
	protected String tagName=null;	
	
	public class DeleteListener implements org.zkoss.zk.ui.event.EventListener {
		   PipeNode node;
		   public DeleteListener(PipeNode node){
			   this.node=node;
		   }
		   public void onEvent(Event event) throws UiException {
			     if(!(node instanceof OutPipeNode))
			    	 this.node.detach();
		   }
   }
	
   protected Window wnd=null;
   
   public PipeNode(int x,int y,int width,int height){
	   super(x,y,width,height);
	   wnd =new Window();
	   appendChild(wnd); 
	   
   }
   
   public String getCode(){
	   return null;
   }

   public byte getTypeIdx(){
	   return PipePortType.NONE;
   }
}
