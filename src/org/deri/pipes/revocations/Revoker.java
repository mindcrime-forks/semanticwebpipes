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
package org.deri.pipes.revocations;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
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

		value = new String(Base64.encodeBase64(msg.hashMD5()));

		conn.add(rev, f.createURI(RDFContextOnt.MSG_REVOCATIONBYHASH.toString()),f.createLiteral(value));

		String[] uris = msg.getInvolvedURIs();
		for (int i = 0; i < uris.length; i++) {
			conn.add(rev, f.createURI(RDFContextOnt.INVOLVED_URI.toString()),f.createURI(uris[i]));
		}

		conn.remove(msg.getGraph());

		return true;
	}

}
