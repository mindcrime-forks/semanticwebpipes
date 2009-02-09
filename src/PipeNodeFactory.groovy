import org.deri.pipes.ui.*;
import org.integratedmodelling.zk.diagram.components.Shape;


import org.w3c.dom.Element;

public class PipeNodeFactory implements IPipeNodeFactory {
	/**
	 * @param tagName The xml tag name for the shape
	 * @param x The x position
	 * @param y The y position
	 * @return a new shape, or null if one cannot be created.
	 */
	public Shape createShape(String tagName, int x, int y) {
		if(tagName.equalsIgnoreCase("rdffetchop")){
			return new RDFFetchNode(x,y);
		}
		if(tagName.equalsIgnoreCase("htmlfetchop")){
			return new  HTMLFetchNode(x,y);
		}
		if(tagName.equalsIgnoreCase("sparqlresultfetchop")){
			return new SPARQLResultFetchNode(x,y);
		}
		if(tagName.equalsIgnoreCase("simplemixop")){
			return new SimpleMixNode(x,y);
		}
		if(tagName.equalsIgnoreCase("constructop")){
			return new ConstructNode(x,y);
		}
		if(tagName.equalsIgnoreCase("selectop")){
			return new SelectNode(x,y);
		}
		if(tagName.equalsIgnoreCase("patch-gen")){
			return new PatchGeneratorNode(x,y);
		}
		if(tagName.equalsIgnoreCase("patch-exec")){
			return new PatchExecutorNode(x,y);
		}
		if(tagName.equalsIgnoreCase("rdfsmixop")){
			return new RDFSMixNode(x,y);
		}
		if(tagName.equalsIgnoreCase("smoosherop")){
			return new SmoosherNode(x,y);
		}
		if(tagName.equalsIgnoreCase("forop")){
			return new ForNode(x,y);
		}
		if(tagName.equalsIgnoreCase("xsltop")){
			return new XSLTNode(x,y);
		}
		if(tagName.equalsIgnoreCase("xmlfetchop")){
			return new XMLFetchNode(x,y);
		}
		if(tagName.equalsIgnoreCase("xslfetchop")){
			return new XSLFetchNode(x,y);
		}
		if(tagName.equalsIgnoreCase("urlbuilder")){
			return new URLBuilderNode(x,y);
		}
		if(tagName.equalsIgnoreCase("parameter")){
			return new ParameterNode(x,y);
		}
		if(tagName.equalsIgnoreCase("variable")){
			return new VariableNode(x,y);
		}
		if(tagName.equalsIgnoreCase("sparqlendpoint")){
			return new SPARQLEndpointNode(x,y);
		}
		return null;
	}

	@Override
	public PipeNode createPipeNode(Element element, PipeEditor pipeEditor) {
			String tagName = element.getTagName();

			if(tagName.equalsIgnoreCase("code"))
				return OutPipeNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("rdffetch"))    
				return RDFFetchNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("simplemix"))    
				return SimpleMixNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("construct"))    
				return ConstructNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("for"))    
				return ForNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("xslt"))    
				return XSLTNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("htmlfetch"))    
				return HTMLFetchNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("xmlfetch"))    
				return XMLFetchNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("xslfetch"))    
				return XSLFetchNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("parameter"))    
				return ParameterNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("patch-executor"))    
				return PatchExecutorNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("patch-generator"))    
				return PatchGeneratorNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("smoosher"))    
				return SmoosherNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("rdfs"))    
				return RDFSMixNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("select"))    
				return SelectNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("tuplefetch"))    
				return TupleQueryResultFetchNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("urlbuilder"))    
				return URLBuilderNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("variable"))    
				return VariableNode.loadConfig(element,pipeEditor);

			if(tagName.equalsIgnoreCase("sparqlendpoint"))    
				return SPARQLEndpointNode.loadConfig(element,pipeEditor);

			return null;
		}

}
