package org.deri.pipes.ui;

import java.net.URLEncoder;

import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

public class SPARQLEndpointNode extends InPipeNode implements ConnectingInputNode,ConnectingOutputNode {
	final Logger logger = LoggerFactory.getLogger(SPARQLEndpointNode.class);
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
		super.initialize();
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
	
	public String getSrcCode(boolean config){
		if(getWorkspace()!=null){
			if (config) return "";
			String code="";
			code+=getConnectedCode(endpoint, endpointPort);
			String uri=getConnectedCode(defaulturi, defaulturiPort);
			try{
				code+="?query="+URLEncoder.encode(queryBox.getValue(),"UTF-8");
				if((null!=uri)&&(uri.indexOf("://")>0))					
						code+="&default-graph-uri="+URLEncoder.encode(uri.trim(),"UTF-8");
			}
			catch(java.io.UnsupportedEncodingException e){
				logger.info("UTF-8 support is required by the JVM specification",e);
			}
			return code;
		}
		return null;
	}
	
	@Override
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if(srcCode!=null) return srcCode;
			srcCode =doc.createElement(tagName);
			if(config) setPosition((Element)srcCode);
			
			Element endpointElm=doc.createElement("endpoint");
			endpointElm.appendChild(getConnectedCode(doc, endpoint, endpointPort, config));
			srcCode.appendChild(endpointElm);
			
			Element graphElm=doc.createElement("default-graph-uri");
			graphElm.appendChild(getConnectedCode(doc, defaulturi, defaulturiPort, config));
			srcCode.appendChild(endpointElm);
			
			srcCode.appendChild(XMLUtil.createElmWithText(doc, "query", queryBox.getQuery()));
			return srcCode;
		}
		return null;
	}
	
	public void _loadConfig(Element elm){	
		loadConnectedConfig(XMLUtil.getFirstSubElementByName(elm, "endpoint"), endpointPort, endpoint);
		loadConnectedConfig(XMLUtil.getFirstSubElementByName(elm, "default-graph-uri"), defaulturiPort, defaulturi);
		setQuery(XMLUtil.getTextFromFirstSubEleByName(elm, "query"));
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SPARQLEndpointNode node= new SPARQLEndpointNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
		return node;
	}
	
	public void debug(){
		((PipeEditor)getWorkspace()).reloadTextDebug(getSrcCode(false)) ;
		((PipeEditor)getWorkspace()).reloadTabularDebug(null);
		
	}
}

