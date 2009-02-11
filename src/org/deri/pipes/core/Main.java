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

package org.deri.pipes.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a pipe syntax from the command line.
 */
public class Main {
	static Logger logger = LoggerFactory.getLogger(Main.class);
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws Exception{
			logger.debug("starting main method");
			//CommandLine cmd = parseCommandLineArgs(args);
			if(args.length == 0){
				showUsageAndExit();
			}
			String syntax = args[0];
			Map<String,String> params = parseParams(args,1);
			try{
				logger.debug("parsed parameters");
				FileInputStream in = new FileInputStream(new File(syntax));
				Engine engine = Engine.defaultEngine();
				logger.debug("loaded engine");
				Pipe pipe = (Pipe) engine.parse(in);
				if(logger.isDebugEnabled()){
					logger.debug("parsed pipe:\n"+engine.serialize(pipe));
				}
				for(String key : params.keySet()){
					pipe.setParameter(key, params.get(key));
				}
				ExecBuffer result = pipe.execute(engine.newContext());
				logger.debug("done execute");
				if(result != null){
					result.stream(System.out);
				}
			}catch(FileNotFoundException e){
				System.err.println("ERROR: cannot read syntax file "+syntax);
				System.exit(2);
			}
	}

	/**
	 * parse the key=value parameters
	 * @param args the command line args
	 * @param n the index position to start at.
	 * @return
	 */
	private static Map<String, String> parseParams(String[] args, int n) {
		Map<String,String> map = new HashMap<String,String>();
		for(int i=n;i<args.length;i++){
			String arg = args[i];
			String[] keyval = arg.split("=",2);
			if(keyval.length==1){
				System.err.println("Bad parameter (must be in name=value notation):"+arg);
			}else{
				map.put(keyval[1], keyval[1]);
			}
		}
		return map;
	}

	/**
	 * 
	 */
	private static void showUsageAndExit() {
		System.err.println("\nUSAGE: pipes path/to/pipe-syntax-file <param1=value1> <param2=value2> ...");
		System.exit(2);
	}

	private static CommandLine parseCommandLineArgs(String[] args)
			throws ParseException {
		Option syntax = OptionBuilder.withArgName( "file" )
		.hasArg()
		.withDescription(  "use pipe syntax contained in this file" )
		.create( "syntax" );
		syntax.setRequired(true);
		Option property  = OptionBuilder.withArgName( "property=value" )
        .hasArgs()
        .withValueSeparator()
        .withDescription( "use value for given property" )
        .create( "D" );
		Options options = new Options();
		options.addOption(syntax);
		options.addOption(property);
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse( options, args);
		return cmd;
	}
	
	

}
