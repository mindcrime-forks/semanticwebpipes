package org.deri.pipes.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Listbox;

public class SelectFetchNode extends FetchNode {
	final Logger logger = LoggerFactory.getLogger(SelectFetchNode.class);
	protected Listbox listbox;
	public SelectFetchNode(byte outType,int x,int y,String title,String tagName){
		super(outType,x,y,200,70,title,tagName);
	}
	
	public String getFormat(){
		if(listbox.getSelectedItem()!=null)
			return listbox.getSelectedItem().getValue().toString();
		return listbox.getItemAtIndex(0).getValue().toString();
	}
	
	public void setFormat(String format){
		for(int i=0;i<listbox.getItemCount();i++)
		 if(listbox.getItemAtIndex(i).getValue().toString().equalsIgnoreCase(format))
				 listbox.setSelectedIndex(i);
	}
	
}
