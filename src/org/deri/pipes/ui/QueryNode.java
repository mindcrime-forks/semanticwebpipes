package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.w3c.dom.Element;
import org.zkforge.codepress.*;
import org.zkoss.zul.Listbox;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;
public class QueryNode extends InOutNode{
	org.zkoss.zul.Label label;
	QueryBox queryBox=null;
	
	public void setQuery(String query){
		queryBox.setQuery(query);
	}
	
	public QueryNode(PortType outPType,int x,int y,String title){
    	super(PipePortType.getPType(PipePortType.RDFIN),outPType,x,y,230,50);
    	label=new org.zkoss.zul.Label("Query:");
    	wnd.setTitle(title);
        wnd.appendChild(label);
        
		queryBox=new QueryBox();
		
        wnd.appendChild(queryBox);
        
    }
	
	public void setTitle(String title){
		wnd.setTitle(title);
	}
	
	public String getCode(){
		if(getWorkspace()!=null){
	    	String code="<"+tagName+">\n";
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<source>\n";
	    		code+=((PipeNode)port.getParent()).getCode();
	    		code+="</source>\n";
	    	}
	    	code+="<query>\n";
	    	code+="<![CDATA[\n"+queryBox.getQuery()+"\n]]>";
	    	code+="</query>\n";
	    	code+="</"+tagName+">\n";
	    	return code;
	    }
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
	    	for(Port port:getWorkspace().getIncomingConnections(input.getUuid())){
	    		code+="<source>\n";
	    		code+=((PipeNode)port.getParent()).getConfig();
	    		code+="</source>\n";
	    	}
	    	code+="<query>\n";
	    	code+="<![CDATA[\n"+queryBox.getQuery()+"\n]]>";
	    	code+="</query>\n";
	    	code+="</"+tagName+">\n";
	    	return code;
	    }
		return null;
	}
}
