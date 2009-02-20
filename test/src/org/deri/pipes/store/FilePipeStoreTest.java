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

package org.deri.pipes.store;

import java.io.File;

import org.deri.pipes.endpoints.PipeConfig;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class FilePipeStoreTest extends TestCase {
	File pipestoreFolder = null;
	FilePipeStore store;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		File tmp = File.createTempFile("pipestore", "xxx");
		pipestoreFolder = new File(tmp.getParentFile(),"pipestore-for-unit-tests");
		tmp.delete();
		deltree(pipestoreFolder);
		pipestoreFolder.mkdirs();	
		store = new FilePipeStore(pipestoreFolder);
	}

	/**
	 * @param tmpFolderForTests2
	 */
	private boolean deltree(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deltree(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}

	@Override
	protected void tearDown() throws Exception {
		deltree(pipestoreFolder);
		super.tearDown();
	}
	
	private void resetStore(){
		deltree(pipestoreFolder);
		pipestoreFolder.mkdirs();
	}

	public void test(){
		resetStore();
		System.out.println("folder is "+pipestoreFolder);
		assertFalse("should not contain aaa",store.contains("aaa"));
		store.save(createPipeConfig("aaa"));
		assertTrue("should contain aaa",store.contains("aaa"));
		store.save(createPipeConfig("bbb"));
		store.save(createPipeConfig("ccc"));
		assertEquals("Wrong number of pipes in store",3,store.getPipeList().size());
		store.save(createPipeConfig("xxx/aaa"));
		store.save(createPipeConfig("xxx/bbb"));
		assertEquals("Wrong number of pipes in store",5,store.getPipeList().size());
		store.deletePipe("xxx/aaa");
		assertEquals("Wrong number of pipes in store",4,store.getPipeList().size());
		assertPipesEqual(createPipeConfig("aaa"),store.getPipe("aaa"));
	}
	public void testRename(){
		resetStore();
		PipeConfig aaa = createPipeConfig("aaa");
		store.save(aaa);
	}

	/**
	 * @param createPipeConfig
	 * @param pipe
	 */
	private void assertPipesEqual(PipeConfig expected, PipeConfig pipe) {
		assertEquals("wrong id",expected.getId(),pipe.getId());
		assertEquals("wrong name",expected.getName(),pipe.getName());
		assertEquals("wrong password",expected.getPassword(),pipe.getPassword());
		assertEquals("wrong config",expected.getConfig(),pipe.getConfig());
		assertEquals("wrong syntax",expected.getSyntax(),pipe.getSyntax());
	}

	private PipeConfig createPipeConfig(String key) {
		PipeConfig config = new PipeConfig();
		config.setId(key);
		config.setName("pipe name "+key);
		config.setPassword("password for "+key);
		config.setConfig("<pipe><!--config "+key+"--></pipe>");
		config.setSyntax("<pipe><!--syntax "+key+"--></pipe>");
		return config;
	}

}
