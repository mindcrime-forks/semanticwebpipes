package org.deri.execeng.rdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Vector;

import org.deri.execeng.core.BoxParser;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Operator;
import org.deri.execeng.model.Stream;
import org.deri.execeng.revocations.RevokationFilter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.w3c.dom.Element;
import org.deri.execeng.utils.XMLUtil;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class PatchExecutorBox extends AbstractMerge{	
	final Logger logger = LoggerFactory.getLogger(PatchExecutorBox.class);
	 
	 public PatchExecutorBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
     }
	 
     public void execute(){
    	 buffer= new SesameMemoryBuffer(parser);
    	 mergeInputs();
    	 
    	 Repository rep = buffer.getConnection().getRepository();
    	 RevokationFilter revFilter = new RevokationFilter();
    	 try {
			revFilter.performFiltering(rep);
		 } catch (RepositoryException e) {
			parser.log(e);
		 }    	 
    	 
    	 isExecuted=true;
     }     
}