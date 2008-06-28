package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
import org.zkoss.zul.*;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import org.openrdf.rio.RDFFormat;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFFetchNode extends SelectFetchNode{
	
	public RDFFetchNode(int x,int y){
		super(PipePortType.RDFOUT,x,y,"RDF Fetch","rdffetch");
         listbox =new Listbox();
         listbox.setMold("select");
         listbox.appendItem(RDFFormat.RDFXML.getName(), RDFFormat.RDFXML.getName());
         listbox.appendItem(RDFFormat.N3.getName(), RDFFormat.N3.getName());
         listbox.appendItem(RDFFormat.NTRIPLES.getName(), RDFFormat.NTRIPLES.getName());
         listbox.appendItem(RDFFormat.TRIG.getName(), RDFFormat.TRIG.getName());
         listbox.appendItem(RDFFormat.TRIX.getName(), RDFFormat.TRIX.getName());
         listbox.appendItem(RDFFormat.TURTLE.getName(), RDFFormat.TURTLE.getName());
        wnd.appendChild(listbox);
    }
	
	public static PipeNode loadConfig(Element elm,PipeEditor wsp){
		RDFFetchNode node= new RDFFetchNode(Integer.parseInt(elm.getAttribute("x")),Integer.parseInt(elm.getAttribute("y")));
		wsp.addFigure(node);
		//System.out.println("load rdf fetch "+node.getUuid()+"<--> ");
		node._loadConfig(elm);
		return node;
	}
}