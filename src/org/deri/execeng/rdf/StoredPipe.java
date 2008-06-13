package org.deri.execeng.rdf;

import org.deri.execeng.endpoints.PipeManager;
import org.deri.execeng.core.BoxParser;
import org.w3c.dom.Element;
import org.deri.execeng.model.Stream;
import java.util.ArrayList;
public class StoredPipe{
	public static Stream loadStream(Element element){
		String syntax =PipeManager.getPipeSyntax(element.getTagName());
		if (syntax==null) return null;
		ArrayList<Element> parameters =BoxParser.getSubElement(element);
		for (int i=0;i<parameters.size();i++) {			
			syntax = syntax.replace("${" + parameters.get(i).getTagName() + "}", BoxParser.getTextData(parameters.get(i)));
		}
		if (syntax != null) {
			return (new BoxParserImplRDF()).parse(syntax);
		}
		return null;
	}
}
