package org.deri.pipes.core;

import org.deri.pipes.model.Operator;

public class Source implements Operator {

	private Operator delegate;
	@Override
	public void execute(PipeContext context) {
		delegate.execute(null);
	}

	@Override
	public ExecBuffer getExecBuffer() {
		return delegate.getExecBuffer();
	}

	@Override
	public boolean isExecuted() {
		return delegate.isExecuted();
	}

	@Override
	public void stream(ExecBuffer buffer) {
		delegate.stream(buffer);
	}

	@Override
	public void stream(ExecBuffer buffer, String context) {
		delegate.stream(buffer,context);
	}

	public Operator getDelegate() {
		return delegate;
	}

	public void setDelegate(Operator delegate) {
		this.delegate = delegate;
	}

}
