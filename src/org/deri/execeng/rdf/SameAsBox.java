package org.deri.execeng.rdf;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class SameAsBox extends AbstractMerge{ 
	final Logger logger = LoggerFactory.getLogger(SameAsBox.class);
	
     
     public void execute(){
    	 buffer= new SesameMemoryBuffer();
    	 mergeInputs();
    	 
    	 RepositoryConnection conn = buffer.getConnection();
    	 Repository rep = conn.getRepository();
    	 
    	 Smoosher smoosher = new Smoosher();
    	 try {
			smoosher.smoosh(rep);
		} catch (RepositoryException e) {
			logger.warn("problem smooshing",e);
		}
    	 
    	 isExecuted=true;
     }     
}