/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.deri.pipes.endpoints;
import org.deri.pipes.ui.PipeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class PipeListRenderer implements RowRenderer {
	final Logger logger = LoggerFactory.getLogger(PipeListRenderer.class);
	Textbox checkPassText;
	Window checkPassWin=null;
	Button checkPassEnter,checkPassCancel;
	CheckPassListener checkPassListener;
	PipeEditor wsp;
	public PipeListRenderer(PipeEditor wsp,Window checkPassWin){
		this.wsp=wsp;
		this.checkPassWin=checkPassWin;
		init();
	}
	public void init(){
		checkPassListener=new CheckPassListener();
		checkPassText= new Textbox();
		//checkPassText.addEventListener("onChange",checkPassListener);
		checkPassText.setParent(checkPassWin);    		    			
		checkPassEnter= new Button("Enter");
		checkPassEnter.addEventListener("onClick",checkPassListener);
		checkPassEnter.setParent(checkPassWin);
		checkPassCancel= new Button("Cancel");		
		checkPassCancel.addEventListener("onClick",new EventListener(){
			public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
				checkPassWin.setVisible(false);
			}
		});
		checkPassCancel.setParent(checkPassWin);
	}
	
	String justify(String str,int length){
		StringBuffer result=new StringBuffer();
		String[] words= str.split(" ");
		int count=0;
		for(int i=0;i<words.length;i++){
			if(count+words[i].length()>length){
				result.append("<br />\n");
				count=0;
			}else{
				result.append(" ");
			}
			result.append(words[i]);
			count+=words[i].length();
		}
		return result.toString();
	}
	
    public void render(Row row, Object data) {
      row.setValign("top");	
      String pipeid=((Pipe)data).getId();
      Html pipeLink=new Html("<a href='./pipes/?id="+pipeid+"'>"+pipeid+"</a><br />" + 
    		  justify(((Pipe)data).getName(),30));
      pipeLink.setParent(row);

      
      Menubar menuBar =new Menubar();
      Menu action=new Menu("actions");
      Menupopup popup=new Menupopup();
      popup.setParent(action);
       
      Menuitem  copy2Editor=new Menuitem("Clone this pipe");
      copy2Editor.addEventListener("onClick", new PipeListener(pipeid,PipeListener.CLONE));
      copy2Editor.setParent(popup);
      if(!(pipeid.equalsIgnoreCase("nested")||pipeid.equalsIgnoreCase("simplemix")||pipeid.equalsIgnoreCase("transform"))){
    	  Menuitem edit=new Menuitem("Edit this pipe");
	      edit.addEventListener("onClick", new PipeListener(pipeid,PipeListener.EDIT));
	      edit.setParent(popup);
    	  Menuitem delete=new Menuitem("Delete this pipe");
	      delete.addEventListener("onClick", new PipeListener(pipeid,PipeListener.DELETE));
	      delete.setParent(popup);
      }
      Menuitem  debug=new Menuitem("Debug run this pipe");
      debug.addEventListener("onClick", new PipeListener(pipeid,PipeListener.DEBUG));
      debug.setParent(popup);
      action.setStyle("color: red;font-weight: bold;");
      action.setParent(menuBar);
      menuBar.setParent(row);
      row.setNowrap(true);
    }
    public class PipeListener implements EventListener{
        public static final int CLONE=1;
        public static final int EDIT=2;
        public static final int DELETE=3;
        public static final int DEBUG=4;
    	private String pipeid=null;
    	private int type;
    	public PipeListener(String pipeid,int type){
    		this.pipeid=pipeid;
    		this.type=type;
    	}
    	public String getBaseUrl(){
    		Execution exec=Executions.getCurrent();
    		return "http://"+exec.getServerName()+":"+exec.getServerPort()+exec.getContextPath();
    	}
    	public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
    		switch(type){
    		    case CLONE:
    		    		wsp.clone(pipeid);
    		    	break;
    		    case DELETE:
    		    	if(PipeManager.getPassword(pipeid)!=null){   
    		    		try{
    		    			checkPassListener.setPipeId(pipeid);
    		    			checkPassWin.doModal();    		    			
    	    			}
    	    			catch(java.lang.InterruptedException e){
    	    				checkPassText.setParent(null);
    	    			} 
    		    	}
    		    	else{
    		    		try{
    		    			if (Messagebox.show("Are you sure want delete this Pipe?", "Delete?", Messagebox.YES | Messagebox.NO,
        		    				Messagebox.QUESTION) == Messagebox.YES) {
        		    			  PipeManager.deletePipe(pipeid);
        		    		}
    	    			}
    	    			catch(java.lang.InterruptedException e){
    	    			}    		    		
    		    	}
		    		break;	
    		    case EDIT:
    		    	wsp.edit(pipeid);
		    		break;
    		    case DEBUG:  
    		    	wsp.debug(PipeManager.getPipeSyntax(pipeid));
    	
		    		break;	    		    	
    		}
    		
  		  
  	    }
    }
    public class CheckPassListener implements EventListener{
    	String pipeid;
    	public void setPipeId(String pipeid){
    		this.pipeid=pipeid;
    	}
    	public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
    		if(PipeManager.getPassword(pipeid).matches(checkPassText.getValue())){
    		   PipeManager.deletePipe(pipeid);
    		   checkPassWin.setVisible(false);
    		}
    		else{
    			try{
    			    Messagebox.show("Password is incorrect, please re-enter the password for overwriting the Pipe!");
    			}
    			catch(java.lang.InterruptedException e){
    			}
    		}
    	}
    }
  }