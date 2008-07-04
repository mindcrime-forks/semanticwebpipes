package org.deri.pipes.ui;

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

public class SPARQLEndpointNode extends InPipeNode {
	Textbox endpoint,defaulturi;
	QueryBox queryBox;
	Port endpointPort,defaulturiPort=null;
	
	protected Listbox listbox;
	
	public SPARQLEndpointNode(int x,int y){
		super(PipePortType.getPType(PipePortType.TEXTOUT),x,y,220,100);
		wnd.setTitle("SPARQL Endpoint builder");
		
        Vbox vbox=new Vbox();
        Hbox hbox= new Hbox();
		hbox.appendChild(new Label("Endpoint:"));
		hbox.appendChild(endpoint=createBox(120,16));
		vbox.appendChild(hbox);
		
		endpointPort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		endpointPort.setPosition("none");
		endpointPort.setPortType("custom");
	    addPort(endpointPort,205,35);
		
	    hbox= new Hbox();
		hbox.appendChild(new Label("Default graph URI:"));
		hbox.appendChild(defaulturi=createBox(120,16));
		vbox.appendChild(hbox);
		
		defaulturiPort=new CustomPort(OutPipeNode.getPTypeMag(),PipePortType.getPType(PipePortType.TEXTIN));
		defaulturiPort.setPosition("none");
		defaulturiPort.setPortType("custom");
	    addPort(defaulturiPort,205,57);
	    
	    hbox= new Hbox();
		hbox.appendChild(new Label("Query:"));
		hbox.appendChild(queryBox=new QueryBox());
		vbox.appendChild(hbox);
		
	    listbox =new Listbox();
        listbox.setMold("select");
        listbox.appendItem(RDFFormat.RDFXML.getName(), RDFFormat.RDFXML.getName());
        listbox.appendItem(RDFFormat.N3.getName(), RDFFormat.N3.getName());
        listbox.appendItem(RDFFormat.NTRIPLES.getName(), RDFFormat.NTRIPLES.getName());
        listbox.appendItem(RDFFormat.TRIG.getName(), RDFFormat.TRIG.getName());
        listbox.appendItem(RDFFormat.TRIX.getName(), RDFFormat.TRIX.getName());
        listbox.appendItem(RDFFormat.TURTLE.getName(), RDFFormat.TURTLE.getName());
        wnd.appendChild(listbox);
		tagName="sparqlendpoint";
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
			
	
	public String getCode(){
		if(getWorkspace()!=null){
			String code="";
			code+=getConnectedCode(endpoint, endpointPort);
			return code;
		}
		return null;
	}
	
	public String getConfig(){
		if(getWorkspace()!=null){
			String code="<"+tagName+" x=\""+getX()+"\" y=\""+getY()+"\">\n";
			code+="<base>\n"+getConnectedConfig(endpoint, endpointPort)+"</base>\n";
			code+="<default-graph-uri>\n"+getConnectedConfig(endpoint, endpointPort)+"</default-graph-uri>\n";
			code+="<![CDATA[\n"+queryBox.getQuery()+"\n]]>";
			code+="<format>\n"+getFormat()+"</format>\n";
			code+="</"+tagName+">\n";
			return code;
		}
		return null;
	}
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SPARQLEndpointNode node= new SPARQLEndpointNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node.setEndpoint(XMLUtil.getTextData(elm));
		return node;
	}
	public void debug(){
		
	}
}

