package org.deri.pipes.ui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkforge.codepress.*;
import org.zkoss.zul.Listbox;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;
public class QueryNode extends InOutNode{
	final Logger logger = LoggerFactory.getLogger(QueryNode.class);
	org.zkoss.zul.Label label;
	QueryBox queryBox=null;	
	public QueryNode(PortType outPType,int x,int y,String title){
    	super(PipePortType.getPType(PipePortType.RDFIN),outPType,x,y,230,50);
    	label=new org.zkoss.zul.Label("Query:");
    	wnd.setTitle(title);
        wnd.appendChild(label);
        
		queryBox=new QueryBox();
		
        wnd.appendChild(queryBox);
        
    }
	
	public void setTitle(String title){
		wnd.setTitle(title);
	}
	
	public void setQuery(String query){
		queryBox.setQuery(query);
	}
	
	public Node getSrcCode(Document doc,boolean config){
		if(getWorkspace()!=null){
			if (srcCode!=null) return srcCode;
			srcCode =super.getSrcCode(doc, config);
			Element queryElm=doc.createElement("query");
			queryElm.appendChild(doc.createCDATASection(queryBox.getQuery()));
	    	srcCode.appendChild(queryElm);
	    	return srcCode;
	    }
		return null;
	}
}
