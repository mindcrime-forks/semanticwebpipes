package org.deri.execeng.rdf;
import org.deri.execeng.revocations.RevokationFilter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 The purpose of these operators is to provide means for producing and automatically applying patches to RDF data sources.
 <p>
Let us explain their usage with a use case (see also http://pipes.deri.org:8080/pipes/Pipes/?id=patch ?).
</p>
<p>
Suppose I want to build a pipe that outputs the merge of my own and Bob's FOAF files, but I also want to automatically remove from the output some statements that are included in Bob's file, which I think are wrong (e.g. Bob is declaring to be able to program in Java, but I'm sure he is cheating  ). Of course Bob has the right to state such a fact in his own RDF file, and obviously I don't have the rights (and the technical means) to hack the file. So I need a mechanism to automatically exclude the statement in question from the &quot;view&quot; I want to produce and perhaps expose to the world, while being able to reflect the future updates Bob will apply to his FAOF description. Let's see how to do it with Semantic Web pipes.
</p>
<p>
I first have to publish on the web (say to http://mywebsite/patch.rdf ?) an RDF file including, in this case, a single triple:
</p>
<pre>
    ex:Bob   ex:skill   ex:JavaProgramming
</pre>
<p>
Then I use the patch-generator block to produce the patch:
<pre>
        &lt;patch-generator&gt;
           &lt;source&gt;
                &lt;fetch&gt;&lt;location&gt;http://mywebsite/patch.rdf?&lt;/location&gt;&lt;/fetch&gt;
          &lt;/source&gt;
        &lt;/patch-generator&gt;
</pre>
<p>
<p>
The output of this simple operator will be an RDF which express the &quot;negation&quot; of all the triples in the source. In this case something that means: NOT(ex:Bob  ex:skill  ex:JavaProgramming).
</p>
<p>
The next step is to create a pipe that applies the patch to Bob's FOAF file. To do this I'll use the patch-executor operator, with the previous block nested:
</p>
<pre>
    &lt;patch-executor&gt;
          &lt;source&gt;
                &lt;fetch&gt;&lt;location&gt;http://bobswebsite.org/foaf.rdf?&lt;/location&gt;&lt;/fetch&gt;
          &lt;/source&gt;
        &lt;source&gt;
                &lt;fetch&gt;&lt;location&gt;http://mywebsite.org/foaf.rdf?&lt;/location&gt;&lt;/fetch&gt;
          &lt;/source&gt;
          &lt;source&gt;
                &lt;patch-generator&gt;
                   &lt;source&gt;
                        &lt;fetch&gt;&lt;location&gt;http://mywebsite/patch.rdf?&lt;/location&gt;&lt;/fetch&gt;
                  &lt;/source&gt;
                &lt;/patch-generator&gt;
          &lt;/source&gt;
    &lt;/patch-executor&gt;
</pre>
<p>
This composite pipe will take as input the Bob's FOAF, my FOAF file and my patch and will produce as output an RDF which contains all the statements form the two FOAF minus the one I distrusted.
</p>
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