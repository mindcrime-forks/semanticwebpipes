package org.deri.pipes.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
/**
 * An Empty TupleQueryResult.
 * @author robful
 *
 */
public class EmptyTupleQueryResult implements TupleQueryResult {

	/**
	 * Returns a new empty list.
	 */
	public List<String> getBindingNames() {
		return new ArrayList<String>();
	}

	/**
	 * Does nothing.
	 */
	public void close() throws QueryEvaluationException {
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BindingSet next() throws QueryEvaluationException {
		throw new NoSuchElementException("The QueryResult is empty");
	}

	@Override
	public void remove() throws QueryEvaluationException {
		throw new IllegalStateException("The QueryResult is empty");
	}

}
