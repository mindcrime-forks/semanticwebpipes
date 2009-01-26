package org.deri.execeng.rdf;

import org.deri.execeng.endpoints.PipeManager;
import org.deri.execeng.core.PipeParser;
import org.w3c.dom.Element;
import org.deri.execeng.model.Stream;
import java.util.ArrayList;
import org.deri.execeng.utils.XMLUtil;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class StoredPipe{
	public static Stream loadStream(Element element){
		String syntax =PipeManager.getPipeSyntax(element.getTagName());
		if (syntax==null) return null;
		ArrayList<Element> parameters =XMLUtil.getSubElement(element);
		for (int i=0;i<parameters.size();i++) {			
			syntax = syntax.replace("${" + parameters.get(i).getTagName() + "}", XMLUtil.getTextData(parameters.get(i)));
		}
		if (syntax != null) {
			return (new PipeParser()).parse(syntax);
		}
		return null;
	}
}
