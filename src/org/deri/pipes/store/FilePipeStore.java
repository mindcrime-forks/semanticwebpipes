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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.lf5.util.StreamUtils;
import org.deri.pipes.endpoints.PipeConfig;
import org.deri.pipes.utils.CDataEnabledDomDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import com.thoughtworks.xstream.XStream;

/**
 * PipeStore which stores pipes in files on the file system.
 * @author robful
 *
 */
public class FilePipeStore implements PipeStore {
	/**
	 * 
	 */
	private static final String PIPE_FILE_EXTENSION = ".pipe";
	Logger logger = LoggerFactory.getLogger(FilePipeStore.class);
	private File rootFolder;
	private final XStream xstream;
	/**
	 * Create a FilePipeStore in this folder.
	 * The folder will be created if it does
	 * not exist.
	 * @param rootFolder
	 */
	public FilePipeStore(File rootFolder){
		this.rootFolder = rootFolder;
		this.xstream = configureXstream();
		logger.info("Storing pipes in folder "+rootFolder);
	}
	private XStream configureXstream() {
		XStream xstream = new XStream( new CDataEnabledDomDriver());
		xstream.autodetectAnnotations(true);
		xstream.alias("pipeConfig", PipeConfig.class);
		return xstream;
	}
	/**
	 * Create a new FilePipeStore beneath the temporary folder.
	 */
	public FilePipeStore(){
		String folderName = "semanticWebPipeStore";	
		try {
			File tmp = File.createTempFile("pipestore", "xxx");
			rootFolder = new File(tmp.getParentFile(),folderName);
		} catch (IOException e) {
			rootFolder = new File(System.getProperty("java.io.tmpdir"),folderName);
		}
		this.xstream = configureXstream();
		logger.info("Storing pipes in folder "+rootFolder);
                String[] listing = rootFolder.list();
		if(listing==null||listing.length == 0){
			copyDemonstrationPipes();
		}else{ 
                  logger.info("Not adding demonstration pipes - there is content in the PipeStore already");
                }
	}
	/**
	 * 
	 */
	private void copyDemonstrationPipes() {
		//TODO Move this functionality elsewhere.
		rootFolder.mkdirs();
		String rn = "pipes.list";
		logger.debug("attempting to copy demonstration pipes to "+rootFolder+", reading list from "+rn);
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(rn);
		if(in == null){
			logger.info("Not copying in demonstration pipes, cannot load "+rn);
			return;
		}
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			try{
				String line;
				while((line = r.readLine())!= null){
					if(line.trim().startsWith("#")){
						continue;
					}
					String pipeId = line.trim();
					if(pipeId.length() > 0){
						copyPipeResource(pipeId);
					}
				}
			}finally{
				r.close();
			}
		}catch(IOException e){
			logger.warn("Couldn't copy demonstration pipes to "+rootFolder+" because "+e,e);
		}
	}
	/**
	 * @param pipeId
	 */
	private void copyPipeResource(String pipeId) {
		logger.info("copying "+pipeId+" to "+rootFolder);
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(pipeId);
		try{
			try{
				if(in == null){
					logger.info("couldn't read "+pipeId);
					return;
				}
				File file = new File(rootFolder,pipeId);
				file.getParentFile().mkdirs();
				FileOutputStream out = new FileOutputStream(file);
				try{
					StreamUtils.copy(in, out);
				}finally{
					out.close();
				}
			}finally{
				in.close();
			}
		}catch(Throwable t){
			logger.warn("couldn't copy "+pipeId+" to "+rootFolder+" because "+t,t);
		}
	}
	/**
	 * Create a FilePipeStore at the given path
	 * @param string
	 */
	public FilePipeStore(String filepath) {
		this(new File(filepath));
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.store.PipeStore#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String pipeid) {
		return getPipeFile(pipeid).exists();
	}

	/**
	 * @param pipeid
	 * @return
	 */
	private File getPipeFile(String pipeid) {
		return new File(rootFolder,pipeid+PIPE_FILE_EXTENSION);
	}
	/**
	 * Delete the file corresponding to this pipeid.
	 */
	@Override
	public void deletePipe(String pipeid) {
		File pipeFile = getPipeFile(pipeid);
		if(pipeFile.exists()){
			boolean deleted = pipeFile.delete();
			if(deleted){
				logger.info("deleted pipe "+pipeid);
			}else{
				logger.warn("couldn't delete pipe "+pipeid+" in file "+pipeFile);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.store.PipeStore#getPipe(java.lang.String)
	 */
	@Override
	public PipeConfig getPipe(String pipeid) {
		File pipeFile = getPipeFile(pipeid);
		if(!pipeFile.exists()){
			logger.warn("Cannot get pipe which does not exist ["+pipeid+"] file = "+pipeFile);
			return null;
		}
		return getPipeConfigFromFile(pipeFile);
	}
	private PipeConfig getPipeConfigFromFile(File pipeFile) {
		try{
			InputStream in = new FileInputStream(pipeFile);
			try{
				return(PipeConfig)xstream.fromXML(in);
			}catch(Exception e){
				logger.warn("Could not read a PipeConfig for from file "+pipeFile,e);
			}finally{
				in.close();
			}
		}catch(IOException ioe){
			logger.error("Problem reading pipe from file "+pipeFile,ioe);
		}
		return null;
	}

	/**
	 * Navigates the filesystem below the root folder,
	 * creating a pipeConfig for each file with '.pipe' extension.
	 * 
	 */
	@Override
	public List<PipeConfig> getPipeList() {
		List<PipeConfig> configs = new ArrayList<PipeConfig>();
		if(rootFolder.isDirectory()){
			collectPipes(configs,rootFolder);
		}
		return configs;
	}

	/**
	 * @param configs
	 * @param rootFolder2
	 */
	private void collectPipes(List<PipeConfig> configs, File folder) {
		//Load pipe configs from files in the folder, then navigate
		// for files in folders beneath
		File[] pipeFiles = folder.listFiles(new FilenameFilter(){
			public boolean accept(File root, String name) {
				return name.endsWith(PIPE_FILE_EXTENSION);
			}
		});
		Arrays.sort(pipeFiles, new Comparator<File>(){

			@Override
			public int compare(File arg0, File arg1) {
				if(arg0.isDirectory() && arg1.isFile()){
					return 1;
				}
				if(arg0.isFile() && arg1.isDirectory()){
					return -1;
				}
				return arg0.getName().compareToIgnoreCase(arg1.getName());
			}
			
		});
		for(File pipeFile: pipeFiles){
			PipeConfig config = getPipeConfigFromFile(pipeFile);
			if(config != null){
				configs.add(config);
			}
		}
		File[] folders = folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}
			
		});
		for(File f : folders){
			collectPipes(configs,f);
		}
		
	}
	/* (non-Javadoc)
	 * @see org.deri.pipes.store.PipeStore#save(org.deri.pipes.endpoints.PipeConfig)
	 */
	@Override
	public boolean save(PipeConfig pipeConfig) {
		String pipeid = pipeConfig.getId();
		if(pipeid == null){
			logger.info("not saving pipeConfig having no id");
			return false;
		}
		File pipeFile = getPipeFile(pipeid);
		if(pipeFile.exists()){
			logger.info("Will replace ["+pipeid+"] file = "+pipeFile);
			PipeConfig oldConfig = getPipeConfigFromFile(pipeFile);
			if(oldConfig != null && !isEmptyOrNull(oldConfig.getPassword())&& isEmptyOrNull(pipeConfig.getPassword())){
				pipeConfig.setPassword(oldConfig.getPassword());
			}
		}
		File tmpFile = new File(pipeFile.getAbsolutePath()+".tmp");
		tmpFile.getParentFile().mkdirs();
		try{
			OutputStream out = new FileOutputStream(tmpFile);
			try{
				xstream.toXML(pipeConfig, out);
				logger.debug("wrote pipeConfig "+pipeid+" to temp file "+tmpFile);
			}catch(Exception e){
				logger.warn("Could not read a PipeConfig for id=["+pipeid+"] from file "+pipeFile,e);
				return false;
			}finally{
				out.close();
			}
			pipeFile.delete();
			if(tmpFile.renameTo(pipeFile)){
				logger.info("wrote pipeConfig ["+pipeid+"] to file "+pipeFile);
				return true;
			}else{
				logger.warn("Could not rename temp file from to target, check "+pipeFile);
				tmpFile.delete();
			}
		}catch(IOException ioe){
			logger.error("Problem reading pipe ["+pipeid+"] from file "+pipeFile,ioe);
		}
		return false;
	}
	/**
	 * @param password
	 * @return
	 */
	private boolean isEmptyOrNull(String password) {
		return password ==  null || password.trim().length()==0;
	}

}
