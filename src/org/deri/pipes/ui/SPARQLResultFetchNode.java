package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
import org.zkoss.zul.*;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.rio.RDFFormat;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SPARQLResultFetchNode extends SelectFetchNode{
	
	public SPARQLResultFetchNode(int x,int y){
		super(PipePortType.SPARQLRESULTOUT,x,y,"Sparql Result Fetch","sparqlresultfetch");
         listbox =new Listbox();
         listbox.setMold("select");
         listbox.appendItem(TupleQueryResultFormat.SPARQL.getName(), TupleQueryResultFormat.SPARQL.getName());
         listbox.appendItem(TupleQueryResultFormat.BINARY.getName(), TupleQueryResultFormat.BINARY.getName());
         listbox.appendItem(TupleQueryResultFormat.JSON.getName(), TupleQueryResultFormat.JSON.getName());
        wnd.appendChild(listbox);
    }
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		SPARQLResultFetchNode node= new SPARQLResultFetchNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		//System.out.println("load rdf fetch "+node.getUuid()+"<--> ");
		node._loadConfig(elm);
		return node;
	}
}
