package org.deri.pipes.ui;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */

import java.util.Hashtable;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;
import java.net.URLEncoder;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import java.util.ArrayList;

public class URLBuilderNode extends InPipeNode{
	Hashtable<String,Port> pathPorts= new Hashtable<String,Port>();
	Hashtable<String,Port> paraPorts= new Hashtable<String,Port>();
	
	Textbox baseURL=null;
	Port basePort=null;
	Vbox vbox,pathVbox,paraVbox;
	int _rs=23;
	public static final String ADD_ICON="img/edit_add-48x48.png";
	public static final String REMOVE_ICON="img/edit_remove-48x48.png";
	
	class AddRemoveListener implements org.zkoss.zk.ui.event.EventListener {
		   public void onEvent(Event event) throws  org.zkoss.zk.ui.UiException {	
			    //System.out.println(((Image)event.getTarget()).getSrc()+"->"+event.getTarget().getParent().getClass());
				if(((Image)event.getTarget()).getSrc().equals(ADD_ICON)){
					if (event.getTarget().getParent().getParent()==pathVbox) addPath();
					else 
						if (event.getTarget().getParent().getParent()==paraVbox) addParameter();
				}
				if(((Image)event.getTarget()).getSrc().equals(REMOVE_ICON)){
					if(event.getTarget().getParent().getParent()==pathVbox){
						System.out.println("remove "+event.getTarget().getParent().getUuid());
						if(pathPorts.get(event.getTarget().getParent().getUuid())!=null)
							pathPorts.get(event.getTarget().getParent().getUuid()).detach();
						else
							System.out.println(event.getTarget().getParent().getUuid()+"--> null");
						pathPorts.remove(event.getTarget().getParent().getUuid());
						event.getTarget().getParent().detach();
						relayoutPathPorts(1);
						relayoutParaPorts(1);
					}
					else
						if(event.getTarget().getParent().getParent()==paraVbox){
							paraPorts.get(event.getTarget().getParent().getUuid()).detach();
							paraPorts.remove(event.getTarget().getParent().getUuid());
							event.getTarget().getParent().detach();
							relayoutParaPorts(1);
						}
				}
				relayout();
		   }    
	}
	
	public URLBuilderNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,220,138);
		wnd.setTitle("URL builder");
		vbox=new Vbox();
		wnd.appendChild(vbox);
		paraVbox =new Vbox();
		pathVbox =new Vbox();
			    
	    Hbox hbox= new Hbox();
	    hbox.appendChild(new Label("Base:"));
	    hbox.appendChild(baseURL=createParaBox(160,16));
	    vbox.appendChild(hbox);
	    basePort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		basePort.setPosition("none");
		basePort.setPortType("custom");
        addPort(basePort,205,35);
		
	    vbox.appendChild(pathVbox);
	    vbox.appendChild(paraVbox);
	    addLabel("Path elements",pathVbox);
	    addPath();
	    addLabel("Query paramters",paraVbox);
		addParameter();
	}
	
	public URLBuilderNode(int x,int y,Element elm){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,220,138);
		wnd.setTitle("URL builder");
		vbox=new Vbox();
		wnd.appendChild(vbox);
		paraVbox =new Vbox();
		pathVbox =new Vbox();
	}
	
	public void loadContent(Element elm){
		Hbox hbox= new Hbox();
	    hbox.appendChild(new Label("Base:"));
		basePort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		basePort.setPosition("none");
		basePort.setPortType("custom");
        addPort(basePort,205,35);
        
	    Element baseElm=XMLUtil.getFirstSubElementByName(elm,"base");
	    Element baseChildElm=XMLUtil.getFirstSubElement(baseElm);
	    if(baseChildElm!=null){
	    	hbox.appendChild(baseURL=createParaBox(160,16));
	    	PipeNode.loadConfig(baseChildElm,(PipeEditor)getWorkspace()).connectTo(basePort);
	    	baseURL.setValue("[text wired]");
	    	baseURL.setReadonly(true);
	    }
	    else{
	    	hbox.appendChild(baseURL=createParaBox(160,16,XMLUtil.getTextData(baseElm)));
	    }
	    vbox.appendChild(hbox);
	    		
	    vbox.appendChild(pathVbox);
	    vbox.appendChild(paraVbox);
	    addLabel("Path elements",pathVbox);
	    ArrayList<Element> pathElms=XMLUtil.getSubElementByName(elm, "path");
	    for(int i=0;i<pathElms.size();i++)
	    	addPath(pathElms.get(i));
	    	
	    addLabel("Query paramters",paraVbox);	    
	    ArrayList<Element> paraElms=XMLUtil.getSubElementByName(elm, "para");
	    for(int i=0;i<paraElms.size();i++)
	    	addParameter(paraElms.get(i));
	    relayout();
	}
	
	public Image addImage(String src){
		Image img= new Image(src);
		img.setWidth("14px");
		img.setHeight("14px");
		img.addEventListener("onClick", new AddRemoveListener());
		return img;
	}
	
	public void addLabel(String label,Vbox box){
		Hbox hbox= new Hbox();
	    hbox.appendChild(addImage("img/edit_add-48x48.png"));
	    hbox.appendChild(new Label(label));
	    box.appendChild(hbox);
	}
	
	public void addPath(){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		hbox.appendChild(createParaBox(150,16));
		pathVbox.appendChild(hbox);
		Port nPort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		nPort.setPosition("none");
		nPort.setPortType("custom");
        addPort(nPort,175,57+(pathVbox.getChildren().size()-1)*_rs);
		pathPorts.put(hbox.getUuid(), nPort);
		relayoutParaPorts(1);
	}
	
	public void addPath(Element pathElm){
		Hbox hbox= new Hbox();
		Port nPort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		nPort.setPosition("none");
		nPort.setPortType("custom");
        addPort(nPort,175,57+(pathVbox.getChildren().size()-1)*_rs);
		pathPorts.put(hbox.getUuid(), nPort);
		
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		if(XMLUtil.getFirstSubElement(pathElm)!=null){
			Textbox txtBox=createParaBox(150,16);
			hbox.appendChild(txtBox);
			txtBox.setValue("[text wired]");
			txtBox.setReadonly(true);
			PipeNode.loadConfig(XMLUtil.getFirstSubElement(pathElm),(PipeEditor)getWorkspace()).connectTo(nPort);
		}
		else		
			hbox.appendChild(createParaBox(150,16,XMLUtil.getTextData(pathElm)));
		pathVbox.appendChild(hbox);
	}
	
	public void addParameter(){
		Hbox hbox= new Hbox();
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		hbox.appendChild(createParaBox(80,16));
		hbox.appendChild(new Label(" = "));
		hbox.appendChild(createParaBox(80,16));
		paraVbox.appendChild(hbox);
		Port nPort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		nPort.setPosition("none");
		nPort.setPortType("custom");
        addPort(nPort,203,57+(pathVbox.getChildren().size()+paraVbox.getChildren().size()-1)*_rs);
		paraPorts.put(hbox.getUuid(), nPort);
	}
	
	public void addParameter(Element paraElm){
		Hbox hbox= new Hbox();
		Port nPort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		nPort.setPosition("none");
		nPort.setPortType("custom");
        addPort(nPort,203,57+(pathVbox.getChildren().size()+paraVbox.getChildren().size()-1)*_rs);
		paraPorts.put(hbox.getUuid(), nPort);
		
		hbox.appendChild(addImage("img/edit_remove-48x48.png"));
		hbox.appendChild(createParaBox(80,16,paraElm.getAttribute("name")));
		hbox.appendChild(new Label(" = "));
		
		if(XMLUtil.getFirstSubElement(paraElm)!=null){
			Textbox txtBox=createParaBox(80,16);
			hbox.appendChild(txtBox);
			txtBox.setValue("[text wired]");
			txtBox.setReadonly(true);
			PipeNode.loadConfig(XMLUtil.getFirstSubElement(paraElm),(PipeEditor)getWorkspace()).connectTo(nPort);
		}else			
			hbox.appendChild(createParaBox(80,16,XMLUtil.getTextData(paraElm)));
		
		paraVbox.appendChild(hbox);
		
	}
	
	public void relayout(){
		setDimension(220, 94+(pathVbox.getChildren().size()+paraVbox.getChildren().size()-2)*_rs);
	}
	
	public void relayoutParaPorts(int from){
		for(int i=from;i<paraVbox.getChildren().size();i++){
			paraPorts.get(((Hbox)paraVbox.getChildren().get(i)).getUuid())
			           .setPosition(203,57+(pathVbox.getChildren().size()+i)*_rs);
			//System.out.println("relayout id"+((Hbox)paraVbox.getChildren().get(i)).getUuid());
		}
	}
	
	public void relayoutPathPorts(int from){
		for(int i=from;i<pathVbox.getChildren().size();i++){
			pathPorts.get(((Hbox)pathVbox.getChildren().get(i)).getUuid())
			           .setPosition(175,57+i*_rs);
		}
	}
	
	public Textbox createParaBox(int w,int h,String value){
		Textbox box=new Textbox(value);
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	
	public Textbox createParaBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
	}
	
	public void disableTextbox(Port port){
		if(basePort==port){
			baseURL.setValue("text [wired]");
			baseURL.setReadonly(true);
			return;
		}
		for(int i=1;i<pathVbox.getChildren().size();i++){
			if(pathPorts.get(((Hbox)pathVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setValue("text [wired]");
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setReadonly(true);
				return;
			}
		}
		for(int i=1;i<paraVbox.getChildren().size();i++){
			if(paraPorts.get(((Hbox)paraVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setValue("text [wired]");
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setReadonly(true);
				return;
			}
		}
	}
	
	public void enableTextbox(Port port){
		if(basePort==port){
			baseURL.setValue("");
			baseURL.setReadonly(false);
			return;
		}
		for(int i=1;i<pathVbox.getChildren().size();i++){
			if(pathPorts.get(((Vbox)pathVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setValue("");
				((Textbox)((Hbox)pathVbox.getChildren().get(i)).getLastChild()).setReadonly(false);
				return;
			}
		}
		for(int i=1;i<paraVbox.getChildren().size();i++){
			if(paraPorts.get(((Vbox)paraVbox.getChildren().get(i)).getUuid())==port){
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setValue("false");
				((Textbox)((Hbox)paraVbox.getChildren().get(i)).getLastChild()).setReadonly(false);
				return;
			}
		}
	}
	
	
	public String getCode(){
		if(getWorkspace()!=null){
			String code=null;
			boolean isConnected;
			isConnected=false;		
					
			for(Port p:getWorkspace().getIncomingConnections(basePort.getUuid())){
				if(p.getParent() instanceof ParameterNode){
					code=((ParameterNode)p.getParent()).getParameter();				
					isConnected=true;
					if (OutPipeNode.paraList.indexOf((ParameterNode)p.getParent())<0){
						OutPipeNode.paraList.add((ParameterNode)p.getParent());
					}
					break;
				}
			}
			
			if(!isConnected){		
				code=baseURL.getValue().trim();
			}
			
			String tmp=null;
			for(int i=1;i<pathVbox.getChildren().size();i++){
				isConnected=false;
				Hbox hbox=(Hbox)pathVbox.getChildren().get(i);
				tmp=null;
				for(Port p:getWorkspace().getIncomingConnections(pathPorts.get(hbox.getUuid()).getUuid())){
					if(p.getParent() instanceof ParameterNode){
						tmp=((ParameterNode)p.getParent()).getParameter();
						isConnected=true;
						if (OutPipeNode.paraList.indexOf((ParameterNode)p.getParent())<0){
							OutPipeNode.paraList.add((ParameterNode)p.getParent());
						}
						break;
					}
				}
				if(!isConnected){		
					tmp=((Textbox)hbox.getLastChild()).getValue().trim();
				}
				if(null!=tmp&&tmp.trim()!=""){
					if((code.charAt(code.length()-1)=='/')||(tmp.charAt(0)=='/'))
						code+=tmp;
					else
						code+="/"+tmp;
				}
				
			}
			String and="";
		
			for(int i=1;i<paraVbox.getChildren().size();i++){
				try{
					Hbox hbox=(Hbox)paraVbox.getChildren().get(i);
					if(((Textbox)hbox.getFirstChild().getNextSibling()).getValue().trim()!=""){
						isConnected=false;
						tmp=null;
						for(Port p:getWorkspace().getIncomingConnections(paraPorts.get(hbox.getUuid()).getUuid())){
							if(p.getParent() instanceof ParameterNode){
								tmp=((ParameterNode)p.getParent()).getParameter();
								isConnected=true;
								if (OutPipeNode.paraList.indexOf((ParameterNode)p.getParent())<0){
									OutPipeNode.paraList.add((ParameterNode)p.getParent());
								}
								break;
							}
						}
						
						if(!isConnected){					
							tmp=URLEncoder.encode(((Textbox)hbox.getLastChild()).getValue().trim(),"UTF-8");
						}
						code+=and+URLEncoder.encode(((Textbox)hbox.getFirstChild().getNextSibling()).getValue(),"UTF-8")+"="+tmp;
						and="&";
					}
				}
				catch(java.io.UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}
			
			return code;
		}
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<urlbuilder x=\""+getX()+"\" y=\""+getY()+"\">\n";
			boolean isConnected;
			isConnected=false;		
					
			for(Port p:getWorkspace().getIncomingConnections(basePort.getUuid())){
				if(p.getParent() instanceof ParameterNode){
					code+="<base>\n"+OutPipeNode.getParaNode(((ParameterNode)p.getParent()).getParameter()).getConfig()+"</base>\n";				
					isConnected=true;
					if (OutPipeNode.paraList.indexOf((ParameterNode)p.getParent())<0){
						OutPipeNode.paraList.add((ParameterNode)p.getParent());
					}
					break;
				}
			}
			
			if(!isConnected){		
				code+="<base>\n"+baseURL.getValue().trim()+"</base>\n";
			}
			
			for(int i=1;i<pathVbox.getChildren().size();i++){
				isConnected=false;
				Hbox hbox=(Hbox)pathVbox.getChildren().get(i);
				for(Port p:getWorkspace().getIncomingConnections(pathPorts.get(hbox.getUuid()).getUuid())){
					if(p.getParent() instanceof ParameterNode){
						code+="<path>\n"+OutPipeNode.getParaNode(((ParameterNode)p.getParent()).getParameter()).getConfig()+"</path>\n";
						isConnected=true;
						if (OutPipeNode.paraList.indexOf((ParameterNode)p.getParent())<0){
							OutPipeNode.paraList.add((ParameterNode)p.getParent());
						}
						break;
					}
				}
				if(!isConnected){		
					code+="<path>\n"+((Textbox)hbox.getLastChild()).getValue().trim()+"</path>\n";
				}								
			}
			String tmp=null;
			for(int i=1;i<paraVbox.getChildren().size();i++){
				Hbox hbox=(Hbox)paraVbox.getChildren().get(i);
				if(((Textbox)hbox.getFirstChild().getNextSibling()).getValue().trim()!=""){
					isConnected=false;
					tmp=null;
					for(Port p:getWorkspace().getIncomingConnections(paraPorts.get(hbox.getUuid()).getUuid())){
						if(p.getParent() instanceof ParameterNode){
							tmp=OutPipeNode.getParaNode(((ParameterNode)p.getParent()).getParameter()).getConfig();
							isConnected=true;
							if (OutPipeNode.paraList.indexOf((ParameterNode)p.getParent())<0){
								OutPipeNode.paraList.add((ParameterNode)p.getParent());
							}
							break;
						}
					}
					
					if(!isConnected){					
						tmp=((Textbox)hbox.getLastChild()).getValue().trim();
					}
					code+="<para name=\""+((Textbox)hbox.getFirstChild().getNextSibling()).getValue()+"\">\n"+tmp+"</para>\n";
				}
			}
			
			return code;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		URLBuilderNode node= new URLBuilderNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")),elm);
		wsp.addFigure(node);
		node.loadContent(elm);
		return node;
	}
	
	public void debug(){
		((PipeEditor)getWorkspace()).reloadTextDebug(getCode()) ;
		((PipeEditor)getWorkspace()).reloadTabularDebug(null);
	}
}