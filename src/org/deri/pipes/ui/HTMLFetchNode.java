package org.deri.pipes.ui;

import org.w3c.dom.Element;
import java.util.Hashtable;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Hbox;
import java.util.Enumeration;
import org.deri.execeng.rdf.HTMLFetchBox;
public class HTMLFetchNode extends FetchNode{
	Hashtable<String,Checkbox> checkboxes=new Hashtable<String,Checkbox>();
	public HTMLFetchNode(int x,int y){
		super(PipePortType.RDFOUT,x,y,200,110,"HTML Fetch","htmlfetch");
		Vbox vbox=new Vbox();
		Enumeration<String> keys=HTMLFetchBox.getFormats().keys();
		Hbox hbox=new Hbox();
		int count=0;
		while (keys.hasMoreElements()) {
	    	String key=keys.nextElement();
	    	Checkbox checkbox=new Checkbox(key);
	    	checkboxes.put(key,checkbox);
	    	hbox.appendChild(checkbox);
	    	count++;
	    	if(count%3==0){
	    		vbox.appendChild(hbox);
	    		hbox=new Hbox();
	    	}
		}
		wnd.appendChild(vbox);
	}
	
	public String getFormat(){
		String format="";
		Enumeration<String> keys=HTMLFetchBox.getFormats().keys();
		while (keys.hasMoreElements()) {
	    	String key=keys.nextElement();
	    	if(checkboxes.get(key).isChecked()) format+=key+"|";
		}
		return format;
	}
	
	public void setFormat(String format){
		Enumeration<String> keys=HTMLFetchBox.getFormats().keys();
		while (keys.hasMoreElements()) {
	    	String key=keys.nextElement();
	    	if(format.indexOf(key)>=0) checkboxes.get(key).setChecked(true);
		}
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		HTMLFetchNode node= new HTMLFetchNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
		return node;
	}
}
