package org.deri.pipes.ui;

import org.zkoss.zul.Listbox;

public class SelectFetchNode extends FetchNode {
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
