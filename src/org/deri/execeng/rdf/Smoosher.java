package org.deri.execeng.rdf;

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

public class Smoosher {
	final Logger logger = LoggerFactory.getLogger(Smoosher.class);

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
