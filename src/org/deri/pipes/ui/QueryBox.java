package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;

public class QueryBox extends Bandbox {
	final Logger logger = LoggerFactory.getLogger(QueryBox.class);
	protected class QueryChangeListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {
						 if(event.getTarget()==query){
							 setValue(query.getValue());
						 }
						 else{
							 query.setValue(getValue());
						 }
				
		   }    
	}
	Textbox query;
	Bandpopup group;
	public QueryBox(){
		super();
		group= new Bandpopup();
		query= new Textbox();
		query.setRows(8);
		query.setCols(60);
		query.addEventListener("onChange", new QueryChangeListener());
		group.appendChild(query);
		appendChild(group);
		addEventListener("onChange", new QueryChangeListener());
	}
	
	public QueryBox(String q){
		super(q);
		group= new Bandpopup();
		query= new Textbox(q);
		query.setRows(8);
		query.setCols(60);
		query.addEventListener("onChange", new QueryChangeListener());
		group.appendChild(query);
		appendChild(group);
		addEventListener("onChange", new QueryChangeListener());
	}
	
	public String getQuery(){
		return query.getValue();
	}
	
	public void setQuery(String q){
		query.setValue(q);
		setValue(q);
	}
}
