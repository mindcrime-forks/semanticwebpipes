package org.deri.execeng.revocations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.deri.execeng.utils.Base64;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.rdfcontext.model.RDFContextOnt;
import org.rdfcontext.model.MSG.sesame.RDFN;
import org.rdfcontext.model.MSG.sesame.SMSG;

public class RevokationFilter extends GraphFilter{

	/*
	 * Removes from the untrusted graph all the MSGs which revoke an MSG present in the trusted graph
	 */
	protected void removeUnacceptableNegations(Repository trusted, Repository untrusted) throws RepositoryException {
		ArrayList<Statement> negatives = getNegativeStatements(untrusted);
		for (Statement statement : negatives) {
			byte[] hashCode=Base64.decode(statement.getObject().toString());
			URI uri = getAnInvolvedURI(statement.getSubject(), untrusted);
			if (uri != null) {
				try {
					SMSG revokedMsg = searchForRevokedMSG(hashCode, uri, trusted);
					if (revokedMsg != null) {
						SMSG revokationMSG = SMSG.getInstance(untrusted,statement,null);
						untrusted.getConnection().remove(revokationMSG.getGraph());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

	private ArrayList<SMSG> getNegativeMSGs(Repository graph) throws RepositoryException {
		
		ArrayList<SMSG> negativeMSGs = new ArrayList<SMSG>();
		
		RepositoryResult<Statement> iter = graph.getConnection().getStatements(
				null, 
				new URIImpl(RDFContextOnt.MSG_REVOCATIONBYHASH.getURI()), 
				null,
				false);
		
		while (iter.hasNext()) {
			Statement element = (Statement) iter.next();
			negativeMSGs.add(SMSG.getInstance(graph,element,null));
		}
		return negativeMSGs;
	}
	
	
	private ArrayList<Statement> getNegativeStatements(Repository graph) throws RepositoryException {
		RepositoryConnection conn = graph.getConnection();
		ArrayList<Statement> negatives = new ArrayList<Statement>();
		RepositoryResult<Statement> iter = conn.getStatements(
				null, 
				new URIImpl(RDFContextOnt.MSG_REVOCATIONBYHASH.getURI()), 
				null,
				false);
		while (iter.hasNext()) { 
			negatives.add(iter.next());
		}
		return negatives;
	}
	
	private URI getAnInvolvedURI(Resource res, Repository graph) throws RepositoryException {
		RepositoryConnection conn = graph.getConnection();
		URI involvedURI = null;
		RepositoryResult<Statement> iter2 = conn.getStatements(
				res, 
				new URIImpl(RDFContextOnt.INVOLVED_URI.getURI()), 
				null,
				false);
		if (iter2.hasNext()) {
			involvedURI=(URI)iter2.next().getObject();
		}
		return involvedURI;
	}
	
	private SMSG searchForRevokedMSG(byte[] hashCode, URI uri, Repository graph) throws IOException, RepositoryException {
		RDFN rdfn = new RDFN(graph,uri, null);
		SMSG msgToBeRevoked=rdfn.getMSGbyHash(hashCode);
		return msgToBeRevoked;
	}
	
	/*
	 * Removes for the list of negative MSGs, those MSGs
	 */
	protected void simplifyNegations(Repository graph) throws RepositoryException {
		
		ArrayList<Statement> negatives = getNegativeStatements(graph);
		
		//For each of these statements:
		for (Statement element : negatives) {
			try {
				
				byte[] hashCode=Base64.decode(element.getObject().toString());
				if ((hashCode==null)) continue;
				// grab the a (only one is required) uri which is INVOLVED by the MSG to be removed
				URI involvedURI = getAnInvolvedURI(element.getSubject(), graph);
				if (involvedURI != null) {
					SMSG msgToBeRevoked=searchForRevokedMSG(hashCode, involvedURI, graph);
					if (msgToBeRevoked!=null) {
//						Remove the revokation itself fom the resulting graph...
						SMSG revokationMSG = SMSG.getInstance(graph,element,null);
						graph.getConnection().remove(revokationMSG.getGraph());
						graph.getConnection().remove(msgToBeRevoked.getGraph());
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

	protected void removeNegations(Repository graph) throws RepositoryException {
		ArrayList<SMSG> negativeMSGs = getNegativeMSGs(graph);
		for (SMSG smsg : negativeMSGs) {
			Set statements = smsg.getStatements();
			for (Object object : statements) {
				Statement stat = (Statement)object;
				graph.getConnection().remove(stat);
			}
		}
		
	}
	

	public void performFiltering(Repository initialGraph, Repository revokationsSourceGraph) throws RepositoryException {	
		//simplifyNegations(revokationsSourceGraph);
		removeUnacceptableNegations(revokationsSourceGraph, initialGraph);
		initialGraph.getConnection().add(revokationsSourceGraph.getConnection().getStatements(null, null, null, false));
		simplifyNegations(initialGraph);
		//removeNegations(initialGraph);
	}
	
	public void performFiltering(Repository graph) throws RepositoryException {
		simplifyNegations(graph);
		removeNegations(graph);
	}

	public String getID() {
		// TODO Auto-generated method stub
		return "RevokationFilter";
	}

}
