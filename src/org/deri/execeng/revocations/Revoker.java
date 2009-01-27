package org.deri.execeng.revocations;

import java.util.List;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.rdfcontext.model.RDFContextOnt;
import org.rdfcontext.model.MSG.sesame.SMSG;
import org.rdfcontext.rdfsync.MSGModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Revoker {
	final Logger logger = LoggerFactory.getLogger(Revoker.class);

	public void revoke(org.openrdf.repository.Repository repository) throws RepositoryException {
		MSGModel model = new MSGModel(repository);
		List<SMSG> msgs = model.getAllMSGs();
		for (SMSG smsg : msgs) {
			revokeMSG(smsg, repository);
		}
	}

	public static boolean revokeMSG (SMSG msg, Repository repository) throws RepositoryException {

		//This is the digest attached to the MSG, 
		//used as an inverse functional property that unequivocally identifies the revoked MSG.
		String value = null; 

		ValueFactory f = repository.getValueFactory();
		RepositoryConnection conn = repository.getConnection();

		org.openrdf.model.Resource rev = f.createBNode();

		value = org.deri.execeng.utils.Base64.encodeToString(msg.hashMD5());

		conn.add(rev, f.createURI(RDFContextOnt.MSG_REVOCATIONBYHASH.toString()),f.createLiteral(value));

		String[] uris = msg.getInvolvedURIs();
		for (int i = 0; i < uris.length; i++) {
			conn.add(rev, f.createURI(RDFContextOnt.INVOLVED_URI.toString()),f.createURI(uris[i]));
		}

		conn.remove(msg.getGraph());

		return true;
	}

}
