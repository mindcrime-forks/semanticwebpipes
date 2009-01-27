package org.deri.execeng.rdf;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.revocations.Revoker;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class PatchGeneratorBox extends AbstractMerge{ 
	final Logger logger = LoggerFactory.getLogger(PatchGeneratorBox.class);
	
	 public PatchGeneratorBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);
		 
     }
     
     public void execute(){
    	 buffer= new SesameMemoryBuffer(parser);
    	 mergeInputs();
    	 
    	 Repository rep = buffer.getConnection().getRepository();
    	 Revoker revoker = new Revoker();
    	 try {
			revoker.revoke(rep);
		} catch (RepositoryException e) {
			parser.log(e);
		}
    	 
    	 isExecuted=true;
     }
}