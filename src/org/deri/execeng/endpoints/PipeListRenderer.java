package org.deri.execeng.endpoints;
import org.zkoss.zul.Row;
import org.zkoss.zul.Label;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;
import org.zkforge.codepress.Codepress;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Separator;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Html;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zul.impl.MessageboxDlg;
import org.zkoss.zkex.zul.LayoutRegion;

public class PipeListRenderer implements RowRenderer {
	Textbox pipeidTextbox=null,pipenameTextbox=null,debugView=null,checkPassText;
	Codepress syntaxTextbox=null;
	LayoutRegion debugViewArea=null;
	Window checkPassWin=null;
	Button checkPassEnter,checkPassCancel;
	CheckPassListener checkPassListener;
	public PipeListRenderer(Textbox pipeidTextbox,Textbox pipenameTextbox,Codepress syntaxTextbox,Textbox debugView,LayoutRegion debugViewArea,Window checkPassWin){
		this.pipeidTextbox=pipeidTextbox;
		this.pipenameTextbox=pipenameTextbox;
		this.syntaxTextbox=syntaxTextbox;
		this.debugView=debugView;
		this.debugViewArea=debugViewArea;
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
      String pipeid=((Pipe)data).pipeid;
      Html pipeLink=new Html("<a href='./pipes/?id="+pipeid+"'>"+pipeid+"</a><br />" + 
    		  justify(((Pipe)data).pipename,30));
      pipeLink.setParent(row);

      
      Menubar menuBar =new Menubar();
      Menu action=new Menu("actions");
      Menupopup popup=new Menupopup();
      popup.setParent(action);
       
      Menuitem  copy2Editor=new Menuitem("Copy code to current editor");
      copy2Editor.addEventListener("onClick", new PipeListener(pipeid,PipeListener.COPY_SYNTAX));
      copy2Editor.setParent(popup);
      Menuitem  copySource2Editor=new Menuitem("Insert as source in editor");
      copySource2Editor.addEventListener("onClick", new PipeListener(pipeid,PipeListener.COPY_URL_AS_SOURCE));
      copySource2Editor.setParent(popup);
      Menuitem  copyUrl2Editor=new Menuitem("Insert URL in editor");
      copyUrl2Editor.addEventListener("onClick", new PipeListener(pipeid,PipeListener.COPY_URL));
      copyUrl2Editor.setParent(popup);      
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
        public static final int COPY_SYNTAX=1;
        public static final int COPY_URL=2;
        public static final int COPY_URL_AS_SOURCE=3;
        public static final int EDIT=4;
        public static final int DELETE=5;
        public static final int DEBUG=6;
    	private String pipeid=null;
    	private int type;
    	public PipeListener(String pipeid,int type){
    		this.pipeid=pipeid;
    		this.type=type;
    	}
    	public void copy(String str){
    		int start=0;
    		int end=0;
    		try{
    		   start=Integer.parseInt(syntaxTextbox.getAttribute("selStart").toString());
    		   end=Integer.parseInt(syntaxTextbox.getAttribute("selEnd").toString());
    		}
    		catch(ClassCastException e){
    			//System.out.println(" sel"+start+"---"+end);
    		}
    		syntaxTextbox.setSelectedText(start,end,str,true);
    	}
    	public String getBaseUrl(){
    		Execution exec=Executions.getCurrent();
    		return "http://"+exec.getServerName()+":"+exec.getServerPort()+exec.getContextPath();
    	}
    	public void onEvent(org.zkoss.zk.ui.event.Event event) throws org.zkoss.zk.ui.UiException {
    		switch(type){
    		    case COPY_SYNTAX:
    		    		copy(PipeManager.getPipeSyntax(pipeid));
    		    	break;
    		    case COPY_URL:
		    		copy(getBaseUrl()+"/pipe/?id="+pipeid);
		    	break;	
    		    case COPY_URL_AS_SOURCE:    		    	
		    		copy("<source url=\""+getBaseUrl()+"/pipe/?id="+pipeid+"\"/>");
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
    		    	Pipe pipe=PipeManager.getPipe(pipeid);    		    	
    		    	pipeidTextbox.setValue(pipe.pipeid);
    		    	pipeidTextbox.focus();
    		    	pipenameTextbox.setValue(pipe.pipename);
    		    	syntaxTextbox.setValue(pipe.syntax);
		    		break;
    		    case DEBUG:  
    		    	debugView.setValue("");
    		    	//debugViewArea.setOpen(true); //opening/unncollapsing the debug area does not work for unknown reasons
    		    	PipeManager.debugPipe(PipeManager.getPipeSyntax(pipeid),debugView);
    		    	//debugView.setVisible(true);
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