package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
import org.zkoss.zul.*;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import org.openrdf.rio.RDFFormat;
import org.openrdf.query.resultio.TupleQueryResultFormat;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class TupleQueryResultFetchNode extends SelectFetchNode{
	public TupleQueryResultFetchNode (int x,int y){
		super(PipePortType.SPARQLRESULTOUT,x,y,"Tuple Result Fetch","tuplefetch");
		
         listbox =new Listbox();
         listbox.setMold("select");
         listbox.appendItem(TupleQueryResultFormat.SPARQL.getName(), TupleQueryResultFormat.SPARQL.getName());
         listbox.appendItem(TupleQueryResultFormat.JSON.getName(), TupleQueryResultFormat.JSON.getName());
         listbox.appendItem(TupleQueryResultFormat.BINARY.getName(), TupleQueryResultFormat.BINARY.getName());
        wnd.appendChild(listbox);
    }
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		TupleQueryResultFetchNode node= new TupleQueryResultFetchNode(Integer.parseInt(elm.getAttribute("x")),
				                                                        Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		node._loadConfig(elm);
		return node;
	}
}
