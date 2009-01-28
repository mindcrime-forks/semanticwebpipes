package org.deri.execeng.rdf;
import org.deri.execeng.revocations.RevokationFilter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class PatchExecutorBox extends AbstractMerge{	
	final Logger logger = LoggerFactory.getLogger(PatchExecutorBox.class);
	 
	 
     public void execute(){
    	 buffer= new SesameMemoryBuffer();
    	 mergeInputs();
    	 
    	 Repository rep = buffer.getConnection().getRepository();
    	 RevokationFilter revFilter = new RevokationFilter();
    	 try {
			revFilter.performFiltering(rep);
		 } catch (RepositoryException e) {
			logger.warn("problem executing revocation filter",e);
		 }    	 
    	 
    	 isExecuted=true;
     }     
}