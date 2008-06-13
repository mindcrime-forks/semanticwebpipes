package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.zkforge.codepress.*;
import org.zkoss.zul.Listbox;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;
public class QueryNode extends InOutNode{
	Bandbox queryBox=null;
	//Codepress textBox=null;
	Textbox textBox=null;
	String query=null;
	org.zkoss.zul.Label label;
	Button butt=null;
	Bandpopup group=null;
	class QueryChangeListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {
						 if(event.getTarget()==textBox){
							 queryBox.setValue(textBox.getValue());
						 }
						 else{
							 textBox.setValue(queryBox.getValue());
						 }
				
		   }    
	}
	public Textbox getQueryBox(){
		return queryBox;
	}
	
	public InputElement getTextBox(){
		return textBox;
	}
	
	public void setQuery(String query){
		this.query=query;
		queryBox.setValue(query);
		//textBox.setValue(query);
	}
	
	public QueryNode(PortType outPType,int x,int y,String title){
    	super(PipePortType.getPType(PipePortType.RDFIN),outPType,x,y,230,50);
    	label=new org.zkoss.zul.Label("Query:");
    	wnd.setTitle(title);
        wnd.appendChild(label);
        
		queryBox=new Bandbox("");
		textBox=new Textbox("");
		textBox.setRows(8);
		textBox.setCols(60);
		//textBox =new Codepress();
		
		
		QueryChangeListener listener =new QueryChangeListener();
		queryBox.addEventListener("onChange", new QueryChangeListener());
		textBox.addEventListener("onChange", new QueryChangeListener());
		/*textBox.setAutocomplete(true);
		textBox.setLanguage("sparql");
		textBox.setStyle("width:400px; height:200px;");*/
		
        group=new Bandpopup();
        group.appendChild(textBox);
        queryBox.appendChild(group);
       /* butt= new Button("open");
        butt.addEventListener("onClick", new EventListener(){
        	public void onEvent(Event event) throws org.zkoss.zk.ui.UiException {
        		if(butt.getLabel().equalsIgnoreCase("open")){
	        		((Codepress)textBox).invalidate();  
	        		butt.setLabel("close");
        		}
        		else{
        			group.setFocus(false);
        			butt.setLabel("open");
        		}
		   } 
        });
        group.appendChild(butt);*/
        wnd.appendChild(queryBox);
        
        //((Codepress)textBox).invalidate();
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
	    	code+="<![CDATA[\n"+textBox.getValue()+"\n]]>";
	    	code+="</query>\n";
	    	code+="</"+tagName+">\n";
	    	return code;
	    }
		return null;
	}
}
