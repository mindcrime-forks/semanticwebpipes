package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
import org.zkoss.zul.*;
public class FetchNode extends InPipeNode{
	Textbox urlTextbox=null;
	Port urlPort=null;
	Listbox listbox;
	
	public FetchNode(int x,int y){
		super(PipePortType.getPType(PipePortType.RDFOUT),x,y,200,70);
		urlPort =new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		urlPort.setPosition("none");
		urlPort.setPortType("custom");
        addPort(urlPort,35,33);
        
		wnd.setTitle("Fetch");
		org.zkoss.zul.Label label=new org.zkoss.zul.Label(" URL: ");
        wnd.appendChild(label);
        urlTextbox =new Textbox();
		label=new org.zkoss.zul.Label("Format :");
		wnd.appendChild(urlTextbox);
		//urlTextbox.setReadonly(true);
        wnd.appendChild(label);
         listbox =new Listbox();
         listbox.setMold("select");
         listbox.appendItem("RDF/XML", "rdf/xml");
         listbox.appendItem("N3", "n3");
        wnd.appendChild(listbox);
    }
	
	public String getCode(){
		if(getWorkspace()!=null){
			String code="<fetch>\n<location>";
			boolean isConnected=false;
			for(Port p:getWorkspace().getIncomingConnections(urlPort.getUuid())){
				if(p.getParent() instanceof URLBuilderNode){
					code+=((URLBuilderNode)p.getParent()).getCode();
					isConnected=true;
					break;
				}
				if(p.getParent() instanceof TextInNode){
					code+=((TextInNode)p.getParent()).getParameter();
					isConnected=true;
					if (OutPipeNode.paraList.indexOf((TextInNode)p.getParent())<0){
						OutPipeNode.paraList.add((TextInNode)p.getParent());
					}
					break;
				}
			}
			if(!isConnected){
				code+=urlTextbox.getValue();
			}
			code+="</location>\n</fetch>\n";
			return code;
		}
		return null;
	}
	
	public void disableURLTextbox(){
		urlTextbox.setValue("text [wired]");
		urlTextbox.setReadonly(true);
	}
	
	public void enableURLTextbox(){
		urlTextbox.setValue("");
		urlTextbox.setReadonly(false);
	}
}
