package org.deri.pipes.ui;

import java.net.URLEncoder;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.openrdf.rio.RDFFormat;
import org.w3c.dom.Element;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Vbox;

public class SPARQLEndpointNode extends InPipeNode implements ConnectingInputNode,ConnectingOutputNode {
	Textbox endpoint,defaulturi;
	QueryBox queryBox;
	Port endpointPort,defaulturiPort=null;
	
	protected Listbox listbox;
	
	public SPARQLEndpointNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,260,100);
		wnd.setTitle("SPARQL Endpoint builder");
		
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
		hbox.appendChild(new Label("Endpoint:"));
		hbox.appendChild(endpoint=createBox(120,16));
		vbox.appendChild(hbox);
		
		
		
	    hbox= new Hbox();
		hbox.appendChild(new Label("Default-graph-URI:"));
		hbox.appendChild(defaulturi=createBox(120,16));
		vbox.appendChild(hbox);
		
		
	    
	    hbox= new Hbox();
		hbox.appendChild(new Label("Query:"));
		hbox.appendChild(queryBox=new QueryBox());
		vbox.appendChild(hbox);
		        
        wnd.appendChild(vbox);
		tagName="sparqlendpoint";
	}
	
	protected void initialize(){
		endpointPort=createPort(PipePortType.TEXTIN,190,35);
	    defaulturiPort=createPort(PipePortType.TEXTIN,250,59);
	}
	
	public void onConnected(Port port){
		if(endpointPort==port){
			endpoint.setValue("text [wired]");
			endpoint.setReadonly(true);
			return;
		}
		if(defaulturiPort==port){
			defaulturi.setValue("text [wired]");
			defaulturi.setReadonly(true);
			return;
		}
	}
	
	public void onDisconnected(Port port){
		if(endpointPort==port){
			endpoint.setValue("");
			endpoint.setReadonly(false);
			return;
		}
		if(defaulturiPort==port){
			defaulturi.setValue("");
			defaulturi.setReadonly(false);
			return;
		}
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
	
	public void setEndpoint(String url){
		endpoint.setValue(url);
	}
			
	public void setDefaultURI(String uri){
		defaulturi.setValue(uri);
	}
	
	public void setQuery(String query){
		queryBox.setQuery(query);
	}
	
	public String getCode(){
		if(getWorkspace()!=null){
			String code="";
			code+=getConnectedCode(endpoint, endpointPort);
			String uri=getConnectedCode(defaulturi, defaulturiPort);
			try{
				code+="?query="+URLEncoder.encode(queryBox.getValue(),"UTF-8");
				if((null!=uri)&&(uri.indexOf("://")>0))					
						code+="&default-graph-uri="+URLEncoder.encode(uri.trim(),"UTF-8");
			}
			catch(java.io.UnsupportedEncodingException e){
				e.printStackTrace();
			}
			return code;
		}
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
			code+="<endpoint>\n"+getConnectedConfig(endpoint, endpointPort)+"</endpoint>\n";
			code+="<default-graph-uri>\n"+getConnectedConfig(defaulturi, defaulturiPort)+"</default-graph-uri>\n";
			code+="<query><![CDATA[\n"+queryBox.getQuery()+"\n]]></query>";
			code+="</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SPARQLEndpointNode node= new SPARQLEndpointNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.setEndpoint(XMLUtil.getTextFromFirstSubEleByName(elm, "endpoint"));
		node.setDefaultURI(XMLUtil.getTextFromFirstSubEleByName(elm, "default-graph-uri"));
		node.setQuery(XMLUtil.getTextFromFirstSubEleByName(elm, "query"));
		return node;
	}
	
	public void debug(){
		((PipeEditor)getWorkspace()).reloadTextDebug(getCode()) ;
		((PipeEditor)getWorkspace()).reloadTabularDebug(null);
		
	}
}

