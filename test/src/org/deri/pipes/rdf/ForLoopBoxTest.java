package org.deri.pipes.rdf;

import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Pipe;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.store.FilePipeStore;

import junit.framework.TestCase;

public class ForLoopBoxTest extends TestCase {
	public void test() throws Exception{
		Engine engine = Engine.defaultEngine();
		engine.setPipeStore(new FilePipeStore("test/data/pipe-library"));
		PipeConfig config = engine.getPipeStore().getPipe("TBLonTheSW");
		Pipe pipe = (Pipe)engine.parse(config.getSyntax());
		ExecBuffer result = pipe.execute(engine.newContext());
		result.stream(System.out);
	}
}
