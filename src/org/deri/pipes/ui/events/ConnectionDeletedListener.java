package org.deri.pipes.ui.events;
import org.integratedmodelling.zk.diagram.events.ConnectionCreatedEvent;
import org.zkoss.zk.ui.event.*;
import org.deri.pipes.ui.*;
import org.integratedmodelling.zk.diagram.components.*;
public class ConnectionDeletedListener implements EventListener {	
	   
	   public void onEvent(Event event) throws org.zkoss.zk.ui.UiException {    
		     System.out.println("deleted");
	         ConnectionCreatedEvent e=(ConnectionCreatedEvent)event;
	         Node src=(Node)e.getSource().getParent();
	         Node tag=(Node)e.getDestination().getParent();
	         if((tag instanceof ConnectingInputNode)&&(src instanceof ConnectingOutputNode))
	        		 ((ConnectingInputNode)tag).onDisconnected(e.getDestination());
	   		 if((tag instanceof ConnectingOutputNode)&&(src instanceof ConnectingInputNode))
	   			((ConnectingInputNode)src).onDisconnected(e.getSource());
	   }    
 
}
