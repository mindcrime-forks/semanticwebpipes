package org.deri.execeng.revocations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

public abstract class GraphFilter {

	public abstract void performFiltering(Repository graph) throws RepositoryException;
	
	public abstract void performFiltering(Repository graph1, Repository graph2) throws RepositoryException;
	
	public abstract String getID();
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GraphFilter)) return false;
		return ((GraphFilter)obj).getID().equals(this.getID());
	}
	
	@Override
	public int hashCode() {
		return getID().hashCode();
	}
	
}
