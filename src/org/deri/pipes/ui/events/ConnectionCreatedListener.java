package org.deri.pipes.ui.events;
import org.deri.pipes.ui.ConnectingInputNode;
import org.deri.pipes.ui.ConnectingOutputNode;
import org.integratedmodelling.zk.diagram.components.Node;
import org.integratedmodelling.zk.diagram.events.ConnectionCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
public class ConnectionCreatedListener implements EventListener {	
	final Logger logger = LoggerFactory.getLogger(ConnectionCreatedListener.class);
	   
	   public void onEvent(Event event) throws org.zkoss.zk.ui.UiException {    
	         ConnectionCreatedEvent e=(ConnectionCreatedEvent)event;
	         Node src=(Node)e.getSource().getParent();
	         Node tag=(Node)e.getDestination().getParent();
	         if((tag instanceof ConnectingInputNode)&&(src instanceof ConnectingOutputNode))
	        		 ((ConnectingInputNode)tag).onConnected(e.getDestination());
	   		 if((tag instanceof ConnectingOutputNode)&&(src instanceof ConnectingInputNode))
	   			((ConnectingInputNode)src).onConnected(e.getSource());
	   }    
 
}
