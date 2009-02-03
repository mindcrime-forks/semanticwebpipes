/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.deri.pipes.rdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 This operator produces a merge of all the sourceOperators smooshing URIs based on the owl:sameAs statement included in the sourceOperators themselves.
<p>
For example (see also http://pipes.deri.org:8080/pipes/Pipes/?id=smoosher?), let us consider two RDF files as input. The first one (published at http://bobwebsite.org/foaf.rdf ?) says:

    <pre>    http://bobwebsite.org/Bob/ ?  ex:skill   ex:JavaProgramming
</pre>

and the second (published at http://charleswebsite.org/info.rdf ?) says:
<pre>
    http://charleswebsite.org/Bob/?   foaf:knows  http://charleswebsite.org/Charles/?
    http://bobwebsite.org/Bob/?   owl:sameAs   http://charleswebsite.org/Charles/ ?
</pre>
By creating a pipe like the following:
<pre>
    &lt;smoosher&gt;
          &lt;source&gt;
            &lt;fetch&gt;&lt;location&gt;http://bobwebsite.org/foaf.rdf?&lt;/location&gt;&lt;/fetch&gt;
          &lt;/source&gt;
          &lt;source&gt;
            &lt;fetch&gt;&lt;location&gt;http://charleswebsite.org/info.rdf?&lt;/location&gt;&lt;/fetch&gt;
          &lt;/source&gt;
    &lt;/smoosher&gt;
</pre>
I would obtain, as output, an RDF including the following triples:
<pre>
    http://bobwebsite.org/Bob/?   ex:skill  ex:JavaProgramming
    http://bobwebsite.org/Bob/?   foaf:knows   http://charleswebsite.org/Charles/ ?
</pre>
where only one URI is used to address Bob as an entity (the shortest one). 
</p>
 *
 */
public class Smoosher {
	private transient Logger logger = LoggerFactory.getLogger(Smoosher.class);

	private ArrayList<Resource> visitedNodes = new ArrayList<Resource>();
	
	private ArrayList<Statement> getSameAsStatements(RepositoryConnection conn, Resource resource, boolean inverse) throws RepositoryException {
		RepositoryResult<Statement> iter;
		if (inverse) {
			iter = conn.getStatements(null, OWL.SAMEAS, resource, false);
		} else {
			iter = conn.getStatements(resource, OWL.SAMEAS, null, false);
		}

		ArrayList<Statement> sameases = asList(iter); 
		
		return sameases;
	} 
    
    private void findSameAs(Resource resource, Repository rep, ArrayList<Resource> sames) {
   	 try {
			if (visitedNodes.contains(resource)) return;
			
   		 	visitedNodes.add(resource);
   		 	sames.add(resource);
   		 	
   		 	RepositoryConnection conn = rep.getConnection();
			ArrayList<Statement> sameases = getSameAsStatements(conn, resource, false);
			for (Statement stat : sameases) {
				Value obj = stat.getObject();
				if (!(obj instanceof Resource)) continue;
				conn.remove(stat);
				findSameAs((Resource)obj, rep, sames);
			}
			
			sameases = getSameAsStatements(conn, resource, true);
			for (Statement stat : sameases) {
				Resource subj = stat.getSubject();
				conn.remove(stat);
				findSameAs((Resource)subj, rep, sames);
			}
			
		} catch (RepositoryException e) {
			logger.info("Repository exception occurred while executing findSameAs ["+resource+"]",e);
		}
   	 
    }
	
    private ArrayList<Statement> asList(RepositoryResult<Statement> iter) throws RepositoryException {
    	ArrayList<Statement> result = new ArrayList<Statement>();
    	while (iter.hasNext()) {
			result.add(iter.next());
		}
    	return result;
    }
    
	private HashMap<Resource,ArrayList<Resource>> getCorrespondences(Repository rep) throws RepositoryException {
		
		RepositoryConnection conn = rep.getConnection();
		
		try {
    		HashMap<Resource, ArrayList<Resource>> correspondences = new HashMap<Resource, ArrayList<Resource>>();
			
    		while (true) {
    			ArrayList<Statement> sameases = getSameAsStatements(conn, null, false);
        		if (sameases.size()==0) break;
    			Statement stat = sameases.get(0);
    				Resource subject = stat.getSubject();
    				ArrayList<Resource> sames;
    				if (correspondences.containsKey(subject)) {
    					sames = correspondences.get(subject);
    				} else {
    					sames = new ArrayList<Resource>();
    					correspondences.put(subject, sames);
    				}
    				findSameAs(subject, rep, sames);		
    				
    		}
    		return correspondences;
			
		} catch (RepositoryException e) {
			logger.info("RepositoryException occurred while trying getCorrespondences",e);
		}
		 return null;
	}
	
	public void smoosh (Repository rep) throws RepositoryException {
		RepositoryConnection conn = rep.getConnection();
		HashMap<Resource, ArrayList<Resource>> corrs = getCorrespondences(rep);
		for (Resource key : corrs.keySet()) {
			ArrayList<Resource> cluster = corrs.get(key);
			cluster.remove(key);
			for (Resource resource : cluster) {
				ArrayList<Statement> info = asList(conn.getStatements(resource, null, null, false));
				for (Statement statement : info) {
					URI predicate = statement.getPredicate();
					Value object = statement.getObject();
					conn.remove(statement);
					conn.add(key, predicate, object);
				}
				info = asList(conn.getStatements(null, null, resource, false));
				for (Statement statement : info) {
					URI predicate = statement.getPredicate();
					Resource subject = statement.getSubject();
					conn.remove(statement);
					conn.add(subject, predicate, key);
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws RepositoryException, RDFParseException, FileNotFoundException, IOException, RDFHandlerException {
		Smoosher smusher = new Smoosher();
		Repository rep = new SailRepository(new MemoryStore());
		rep.initialize();
		rep.getConnection().add(new FileInputStream("test/sameAs1.rdf"),"",RDFFormat.RDFXML);
		
		/*
		HashMap<Resource, ArrayList<Resource>> corr = smusher.getCorrespondences(rep);
		for (Resource res : corr.keySet()) {
			logger.debug("\nCorrepondences for " + res.toString());
			for (Resource same : corr.get(res)) {
				logger.debug(same.toString());
			}
		}
		*/
		
		smusher.smoosh(rep);
		StringWriter wr = new StringWriter();
		rep.getConnection().export(new NTriplesWriter(wr));
		System.out.println(wr.toString());
	}
	
}
