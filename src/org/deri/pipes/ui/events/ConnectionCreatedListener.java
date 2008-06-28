package org.deri.pipes.ui.events;
import org.integratedmodelling.zk.diagram.events.ConnectionCreatedEvent;
import org.zkoss.zk.ui.event.*;
import org.deri.pipes.ui.*;
import org.integratedmodelling.zk.diagram.components.*;
public class ConnectionCreatedListener implements EventListener {	
	   
	   public void onEvent(Event event) throws org.zkoss.zk.ui.UiException {    
	         ConnectionCreatedEvent e=(ConnectionCreatedEvent)event;
	         if(e.getDestination().getParent() instanceof FetchNode){
	        	 if(e.getSource().getParent() instanceof URLBuilderNode){
	        		 ((FetchNode)e.getDestination().getParent()).disableURLTextbox();
	        		 return;
	        	 }
	        	 if(e.getSource().getParent() instanceof ParameterNode){
	        		 ((FetchNode)e.getDestination().getParent()).disableURLTextbox();
	        		 return;
	        	 }
	         }
	         if(e.getSource().getParent() instanceof FetchNode){
	        	 if(e.getDestination().getParent() instanceof URLBuilderNode){
	        		 ((FetchNode)e.getSource().getParent()).disableURLTextbox();
	        		 return;
	        	 }
	        	 if(e.getDestination().getParent() instanceof ParameterNode){
	        		 ((FetchNode)e.getSource().getParent()).disableURLTextbox();
	        		 return;
	        	 }
	         }
	         
	         if((e.getDestination().getParent() instanceof URLBuilderNode)&&(e.getSource().getParent() instanceof ParameterNode)){
	        	 ((URLBuilderNode)e.getDestination().getParent()).disableTextbox((Port)e.getDestination());
	        	 return;
	         }
	         if((e.getDestination().getParent() instanceof URLBuilderNode)&&(e.getSource().getParent() instanceof ParameterNode)){
	        	 ((URLBuilderNode)e.getDestination().getParent()).disableTextbox((Port)e.getDestination());
	        	 return;
	         }
	         if((e.getDestination().getParent() instanceof ParameterNode)&&(e.getSource().getParent() instanceof URLBuilderNode)){
	        	 ((URLBuilderNode)e.getSource().getParent()).disableTextbox((Port)e.getSource());
	        	 return;
	         }
	   }    
 
}
