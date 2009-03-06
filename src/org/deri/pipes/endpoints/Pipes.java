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
package org.deri.pipes.endpoints;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.Pipe;
import org.deri.pipes.core.PipeParser;
import org.deri.pipes.model.BinaryContentBuffer;
import org.deri.pipes.model.SesameMemoryBuffer;
import org.deri.pipes.model.SesameTupleBuffer;
import org.deri.pipes.rdf.RDFBox;
import org.deri.pipes.store.DatabasePipeManager;
import org.deri.pipes.utils.XMLUtil;
import org.deri.pipes.utils.XSLTUtil;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.Sail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.zkoss.zk.ui.Executions;

import edu.mit.simile.babel.BabelWriter;
import edu.mit.simile.babel.exhibit.ExhibitJsonWriter;
import edu.mit.simile.babel.exhibit.ExhibitJsonpWriter;
/**
 * Pipes Servlet.
 * @author robful
 *
 */
public class Pipes extends HttpServlet {
	//TODO: make engine a field.
	static Engine engine = Engine.defaultEngine();
	static Logger logger = LoggerFactory.getLogger(Pipes.class);
	private static ThreadLocal<HttpServletRequest>  REQ= new ThreadLocal<HttpServletRequest>();
	public static Pipes instance;
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		instance =this;
		String format=req.getParameter("format");
		String acceptHeaderValue = getMimeHeader(req.getHeader("Accept") + "",format + "");	

		REQ.set(req);
		try{
			if("raw".equals(format)){
				sendRawResult(req, res);
				return;
			}
			res.setStatus(HttpServletResponse.SC_OK);
			RDFFormat rdfFormat=RDFFormat.forMIMEType(acceptHeaderValue); 
			if (rdfFormat!=null) {
				logger.info("return type is "+acceptHeaderValue);
				ServletOutputStream outputStream = res.getOutputStream();
				res.setContentType(acceptHeaderValue); 
				SesameMemoryBuffer buffer=getRDFBuffer(req, res); 		  
				if(buffer!=null){
					buffer.stream(outputStream,rdfFormat);
				}else{
					logger.error("Empty buffer was returned in the result - cannot display rdf");
					return;
				}

			}
			else if(acceptHeaderValue.contains("application/json")){
				logger.info("return type is "+acceptHeaderValue);
				res.setContentType(acceptHeaderValue);
				SesameMemoryBuffer buffer=getRDFBuffer(req, res); 
				if(buffer==null){
					logger.error("Empty buffer was returned as the results - cannot display json");
					return;
				}
				BabelWriter writer;
				if(acceptHeaderValue.equalsIgnoreCase("application/json"))
					writer =new ExhibitJsonWriter();
				else{
					writer =new ExhibitJsonpWriter();
					logger.debug("Jsonp");
				}
				try{
					java.util.Properties prop=new java.util.Properties();
					String cb=req.getParameter("cb");
					prop.put("callback",(cb!=null)?cb:"callback");
					StringWriter sw=new StringWriter();
					//writer.write(res.getWriter(),buffer.getSail(),prop,null);
					writer.write(sw,buffer.getSail(),prop,null);
					//logger.debug(sw.getBuffer().toString());
					res.getWriter().write(sw.getBuffer().toString());
				}catch(Exception e){
					logger.info("couldn't write json",e);
				}
			}else{
				logger.info("response type is text/html");
				res.setContentType("text/html");

				String rdfSourceUrl = new String(req.getRequestURL()+"?format=rdfxml&" + req.getQueryString());
				String rawSourceUrl = new String(req.getRequestURL()+"?format=raw&" + req.getQueryString());

				// read HTML template from file
				FileInputStream file = new FileInputStream (getServletContext().getRealPath("/") + "template/generic_exhibit_result_viewer.html");
				byte[] b = new byte[file.available()];
				file.read(b);
				file.close ();

				// the string that contains the HTML output. Contains placeholders that are filled out by the following code.
				String outputString = new String (b);

				// replace $rdf_source$ placeholder in the HTML template with the URL of the requested pipe.
				outputString = outputString.replace("$rdf_source$", rdfSourceUrl); // this links to the absolute path of pipes.deri.org for now. It does not work with local resources.
				outputString = outputString.replace("$raw_source$", rawSourceUrl);
				// get pipe code 
				SesameMemoryBuffer buffer=getRDFBuffer(req, res);
				String json="";
				if(buffer!=null){
					StringWriter sw=new StringWriter();
					ExhibitJsonWriter jsonWriter=new ExhibitJsonWriter();
					try{
						Sail sail = buffer.getSail();
						jsonWriter.write(sw,sail,null,null);
						json=sw.getBuffer().toString();
					}
					catch(Exception e){
						logger.error("Problem creating json",e);
					}
				}

				String pipeid = req.getParameter("id");
				String editorlink = "../";
				if(pipeid!=null){
					editorlink+="?pipeid="+URLEncoder.encode(pipeid,"UTF-8");
				}
				outputString = outputString.replace("$editor$", editorlink);
				PipeConfig config = engine.getPipeStore().getPipe(pipeid);
				String syntax = config ==  null?"":config.getSyntax();
				String pipeName = config == null?"":config.getName();

				// replace $pipe_name$ placeholder in the HTML template with the pipe name.
				outputString = outputString.replace("$pipe_name$", pipeName);
				outputString = outputString.replace("$jsondata$", json);
				String errorMessagesString = new String();
				String queryFormString = new String();
				queryFormString = "<form action=\""+XSLTUtil.getBaseURL()+"/pipes/\" method=\"get\" name=\"pipe_query_form\">\n" +
				"<input name=\"id\" type=\"hidden\" value=\"" + pipeid + "\">\n";

				if (syntax == null) {
					logger.warn("No syntax found for pipe:"+pipeid);
				}else{
					try {
						DOMParser parser = new DOMParser();

						parser.parse(new InputSource(new java.io.StringReader(syntax)));  

						Element rootElm=parser.getDocument().getDocumentElement();
						List<Element> paraElms=XMLUtil.getSubElementByName(XMLUtil.getFirstSubElementByName(rootElm,"parameters"), "parameter");
						// iterate through pipe parameter descriptions, construct query form
						String paraLbl,paraVal,paraId;

						for(int i=0;i<paraElms.size();i++){		

							paraLbl = XMLUtil.getTextFromFirstSubEleByName(paraElms.get(i), "label");
							paraLbl = ((null==paraLbl)||(paraLbl.trim()==""))?"unnamed parameter":paraLbl;
							paraId=XMLUtil.getTextFromFirstSubEleByName(paraElms.get(i), "id");
							paraVal=req.getParameter(paraId);
							paraVal = ((null==paraVal)||(paraVal.trim()==""))?XMLUtil.getTextFromFirstSubEleByName(paraElms.get(i), "default"):paraVal;

							if((null==paraVal)||(paraVal.trim()==""))							
								errorMessagesString += "Value for parameter <i>" + paraLbl + "</i> (" + paraId + ") is missing. <br />\n";

							queryFormString += "<div class=\"query_textinput_row\"><label>" + paraLbl + "</label>: <input name=\"" + paraId + "\" type=\"text\" value=\"" + paraVal + "\" size=\"60\"></div> \n";
						}

						queryFormString += "<br /><input type=\"submit\" name=\"button\" id=\"button\" /></form>";

						// replace $query_form$ placeholder in the HTML template with the query form.
						outputString = outputString.replace("$query_form$", queryFormString);
						outputString = outputString.replace("$error_messages$", errorMessagesString);



					} catch (Exception e) {
						String msg = "Exception during HTML query form creation.";
						logger.warn(msg,e);
						outputString = outputString.replace("$query_form$", "");
						outputString = outputString.replace("$error_messages$",msg);
						//logger.info(e);
					}
				}	


				PrintWriter outputWriter = res.getWriter();		
				outputWriter.write(outputString);

			}
		}finally{
			REQ.remove();
		}
	}

	private void sendRawResult(HttpServletRequest req, HttpServletResponse res) throws IOException {
		ExecBuffer result = executePipe(req,res);
		if(result == null){
			res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();
			writer.append("<html><head><title>Pipe Error</title></head><body>The pipe could not be executed, contact the system adminstrator for details</body></html>");
			return;
		}else{
			ServletOutputStream outputStream = res.getOutputStream();
			res.setStatus(HttpServletResponse.SC_OK);
			if(result instanceof BinaryContentBuffer){
				BinaryContentBuffer bcb = (BinaryContentBuffer)result;
				res.setContentType(bcb.getContentType());
				res.setCharacterEncoding(bcb.getCharacterEncoding());
				byte[] content = bcb.getContent();
				res.setContentLength(content.length);
				result.stream(outputStream);
			}else if(result instanceof SesameMemoryBuffer){
				res.setContentType(RDFFormat.RDFXML.getDefaultMIMEType());
				result.stream(outputStream);
			}else {
				res.setContentType("application/octet-stream");
				result.stream(outputStream);
			}
		}
		return;
	}

	public SesameMemoryBuffer getRDFBuffer(HttpServletRequest req, HttpServletResponse res){

		ExecBuffer result = executePipe(req,res);
		if(result != null){
			try{
				if (result instanceof SesameMemoryBuffer) {
					return (SesameMemoryBuffer)result;
				} else {
					logger.debug("stream did not return a SesameMemoryBuffer - it was a "+result.getClass()+" (will coerce if possible)");
					SesameMemoryBuffer x = new SesameMemoryBuffer();
					result.stream(x);
					return x;
				}
			}catch (Exception e) {
				logger.warn("Problem converting result to SesameMemoryBuffer");
			}
		}
		return new SesameMemoryBuffer();
	}

	/**
	 * @param req2
	 * @param res
	 * @return
	 */
	private ExecBuffer executePipe(HttpServletRequest req,
			HttpServletResponse res) {
		String pipeid = req.getParameter("id");
		PipeConfig config = engine.getPipeStore().getPipe(pipeid);
		if (config == null) {
			logger.warn("Syntax was null for pipeid=["+pipeid+"]");
			return null;
		}
		String syntax = config.getSyntax();
		try{
			Pipe pipe = (Pipe)engine.parse(syntax);
			for(String key : pipe.listParameters()){
				String value = (String) req.getParameter(key);
				if(value != null){
					pipe.setParameter(key, value);
				}
			}
			return pipe.execute(engine.newContext());
		}catch(Exception e){
			logger.debug("Problem executing pipe from syntax:"+syntax);
			logger.error("Couldn't execute pipes from syntax (see debug for syntax)",e);
		}
		return null;
	}

	private String getMimeHeader(String accept,String format){
		if(accept.contains(RDFFormat.RDFXML.getDefaultMIMEType())||format.equalsIgnoreCase("rdfxml")) 
			return RDFFormat.RDFXML.getDefaultMIMEType();
		if(accept.contains(RDFFormat.N3.getDefaultMIMEType())||format.equalsIgnoreCase("n3")) 
			return RDFFormat.N3.getDefaultMIMEType();
		if(accept.contains(RDFFormat.NTRIPLES.getDefaultMIMEType())||format.equalsIgnoreCase("ntriples")) 
			return RDFFormat.NTRIPLES.getDefaultMIMEType();
		if(accept.contains(RDFFormat.TRIG.getDefaultMIMEType())||format.equalsIgnoreCase("trig")) 
			return RDFFormat.TRIG.getDefaultMIMEType();
		if(accept.contains(RDFFormat.TRIX.getDefaultMIMEType())||format.equalsIgnoreCase("trix")) 
			return RDFFormat.TRIX.getDefaultMIMEType();
		if(accept.contains(RDFFormat.TURTLE.getDefaultMIMEType())||format.equalsIgnoreCase("turtle")) 
			return RDFFormat.TURTLE.getDefaultMIMEType();
		if(accept.contains("application/jsonp")||format.equalsIgnoreCase("jsonp")) return "application/jsonp";
		if(accept.contains("application/json")||format.equalsIgnoreCase("json")) return "application/json";

		return "text/html";
	}

	public static Pipes getInstance(){
		if (instance == null){
			instance = new Pipes();
		}
		return instance;
	}

	/**
	 * Get the current HTTP Request.
	 * @return
	 */
	public static HttpServletRequest getCurrentRequest(){
		return REQ.get();
	}



	public String getServletInfo() {
		return "Semantic PipeConfig End Points";
	}
}
