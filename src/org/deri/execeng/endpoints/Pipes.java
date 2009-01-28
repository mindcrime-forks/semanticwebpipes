package org.deri.execeng.endpoints;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xerces.parsers.DOMParser;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Stream;
import org.deri.execeng.rdf.RDFBox;
import org.deri.execeng.rdf.SesameMemoryBuffer;
import org.deri.execeng.utils.XMLUtil;
import org.deri.execeng.utils.XSLTUtil;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.zkoss.zk.ui.Executions;

import edu.mit.simile.babel.BabelWriter;
import edu.mit.simile.babel.exhibit.ExhibitJsonWriter;
import edu.mit.simile.babel.exhibit.ExhibitJsonpWriter;
public class Pipes extends HttpServlet {
	static Logger logger = LoggerFactory.getLogger(Pipes.class);
  public static HttpServletRequest  REQ=null;
  public static Pipes instance;
  public static String OP_MAP="operatormapping.properties";
  public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
	  instance =this;
	  String format=req.getParameter("format");
	  String acceptHeaderValue = getMimeHeader(req.getHeader("Accept") + "",format + "");	
	  
	  res.setStatus(HttpServletResponse.SC_OK);
	  REQ=req; 	
	 RDFFormat rdfFormat=RDFFormat.forMIMEType(acceptHeaderValue); 
	 if (rdfFormat!=null) {
		  res.setContentType(acceptHeaderValue); 
		  SesameMemoryBuffer buffer=getRDFBuffer(req, res); 		  
		  if(buffer!=null)
			  buffer.stream(res.getOutputStream(),rdfFormat);
			
	 }
	 else if(acceptHeaderValue.contains("application/json")){
		    res.setContentType(acceptHeaderValue);
		    SesameMemoryBuffer buffer=getRDFBuffer(req, res); 
		    if(buffer!=null){
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
		    }
	 }
	  else{
			res.setContentType("text/html");
			
		    String rdfSourceUrl = new String(req.getRequestURL()+"?format=rdfxml&" + req.getQueryString());
			
		  	// read HTML template from file
		  	FileInputStream file = new FileInputStream (getServletContext().getRealPath("/") + "template/generic_exhibit_result_viewer.html");
			byte[] b = new byte[file.available()];
	        file.read(b);
	        file.close ();
	        
	        // the string that contains the HTML output. Contains placeholders that are filled out by the following code.
	        String outputString = new String (b);
	        
            // replace $rdf_source$ placeholder in the HTML template with the URL of the requested pipe.
	        outputString = outputString.replace("$rdf_source$", rdfSourceUrl); // this links to the absolute path of pipes.deri.org for now. It does not work with local resources.
			    
	        // get pipe code 
	        SesameMemoryBuffer buffer=getRDFBuffer(req, res);
	        String json="";
	        if(buffer!=null){
	        	StringWriter sw=new StringWriter();
	        	ExhibitJsonWriter jsonWriter=new ExhibitJsonWriter();
	        	try{
	        		jsonWriter.write(sw,buffer.getSail(),null,null);
	        		json=sw.getBuffer().toString();
	        	}
	        	catch(Exception e){
	        		
	        	}
	        }
	        
		    String pipeid = req.getParameter("id");			
			String syntax = PipeManager.getPipeSyntax(pipeid);
			String pipeName = PipeManager.getPipe(pipeid).getName();
			
            // replace $pipe_name$ placeholder in the HTML template with the pipe name.
			outputString = outputString.replace("$pipe_name$", pipeName);
			outputString = outputString.replace("$jsondata$", json);
			String errorMessagesString = new String();
			String queryFormString = new String();
			queryFormString = "<form action=\""+XSLTUtil.getBaseURL()+"/pipes/\" method=\"get\" name=\"pipe_query_form\">\n" +
	        	"<input name=\"id\" type=\"hidden\" value=\"" + pipeid + "\">\n";
			
	        if (syntax != null) {
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
					logger.debug("Exception during HTML query form creation.");
		            outputString = outputString.replace("$query_form$", "");
		            outputString = outputString.replace("$error_messages$", "");
					//logger.info(e);
				}
			}	
				
	        
			PrintWriter outputWriter = res.getWriter();		
			outputWriter.write(outputString);
			
		}
	}
  
  	public SesameMemoryBuffer getRDFBuffer(HttpServletRequest req, HttpServletResponse res){
  		String pipeid = req.getParameter("id");
  		String syntax = PipeManager.getPipeSyntax(pipeid);
		if (syntax == null) {
			logger.info("Syntax was null for pipeid=["+pipeid+"]");
			return null;
		}
		DOMParser parser = new DOMParser();
		try{
			parser.parse(new InputSource(new java.io.StringReader(syntax)));  
			Element rootElm=parser.getDocument().getDocumentElement();
			List<Element> paraElms=XMLUtil.getSubElementByName(XMLUtil.getFirstSubElementByName(rootElm,"parameters"), "parameter");
			for(int i=0;i<paraElms.size();i++){		
				String paraId = XMLUtil.getTextFromFirstSubEleByName(paraElms.get(i),"id").trim();
				String paraVal = (String) req.getParameter(paraId);
				paraVal=(paraVal!=null)?paraVal:XMLUtil.getTextFromFirstSubEleByName(paraElms.get(i),"default");
				syntax = syntax.replace("${" + paraId + "}", paraVal);
				try{
					syntax=syntax.replace(URLEncoder.encode("${" + paraId + "}","UTF-8"),
							URLEncoder.encode(paraVal,"UTF-8"));
				}
				catch(java.io.UnsupportedEncodingException e){
					logger.info("UTF-8 Encoding must be supported by JVM specification",e);
				}
			}
			//logger.debug(syntax);
			PipeParser pipeParser= new PipeParser();
			Stream stream = pipeParser.parse(syntax);
			if (stream instanceof RDFBox) {
				((RDFBox) stream).execute();
				return (SesameMemoryBuffer)((RDFBox) stream).getExecBuffer();

			} else {
				logger.debug("parsing error: stream was not an RDFBox");
			}
		}catch (Exception e) {				
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
    	if (instance!= null) return instance;
    	return new Pipes(); 
    }
    
    public static Properties getOperatorProps(){ 
	    Properties prop = new Properties();
		try
		{
			if(Executions.getCurrent()!=null)
				prop.load(new FileReader(Executions.getCurrent().getDesktop().getWebApp().getRealPath("/WEB-INF/"+OP_MAP)));
			else{
				if(getInstance()!=null){
					logger.debug("servlet "+((getInstance()!=null)?getInstance().getServletInfo():"null"));
					logger.debug(getInstance().getServletContext().getRealPath("/WEB-INF/"+OP_MAP));
					prop.load(new FileReader(getInstance().getServletContext().getRealPath("/WEB-INF/"+OP_MAP)));
				}
			}
		}
		catch(Exception e)
		{
			logger.info("Could not read operator properties",e);
		}
		return prop;
    }
    
    public String getServletInfo() {
        return "Semantic Pipe End Points";
    }
}
