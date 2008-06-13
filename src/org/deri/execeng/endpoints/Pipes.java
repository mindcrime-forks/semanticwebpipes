package org.deri.execeng.endpoints;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.deri.execeng.model.Stream;
import org.deri.execeng.rdf.BoxParserImplRDF;
import org.deri.execeng.rdf.RDFBox;
import org.deri.execeng.rdf.SesameMemoryBuffer;
import org.deri.execeng.rdf.SesameTupleBuffer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.xpath.*;

import org.openrdf.rio.RDFFormat;
public class Pipes extends HttpServlet {

  public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
	  
	  String acceptHeaderValue = req.getHeader("Accept") + "";	
	  String pipeOutputFormat = req.getParameter("pipe_output_format") + "";
	  //System.out.println("acceptHeaderValue: " + acceptHeaderValue);
	  
	  res.setStatus(HttpServletResponse.SC_OK);
	  	
	  // choose output format depending on Accept header sent by client. "pipe_output_format=rdfxml" in the GET request forces the output to be RDF/XML, regardless of the Accept header.
	 if (acceptHeaderValue.contains("application/n3") || pipeOutputFormat.equalsIgnoreCase("n3")) {
		  res.setContentType(RDFFormat.N3.getDefaultMIMEType());
		  
			// get pipe code 
			String pipeid = req.getParameter("id");
			BoxParserImplRDF parser = new BoxParserImplRDF();
			Stream stream = null;
			String syntax = PipeManager.getPipeSyntax(pipeid);
			
			for (Enumeration parameterIds = req.getParameterNames(); parameterIds.hasMoreElements();) {
				String parameterId = parameterIds.nextElement().toString();
				String parameterValue = (String) req.getParameter(parameterId);
				syntax = syntax.replace("${" + parameterId + "}", parameterValue);
			}
			if (syntax != null) {
				stream = parser.parse(syntax);
				if (stream instanceof RDFBox) {
					((RDFBox) stream).execute();
					((SesameMemoryBuffer)((RDFBox) stream).getExecBuffer())
					              .toOutputStreamN3(res.getOutputStream());
				} else {
					System.out.println("parsing error");
				}
			}
	  }
	  else if(acceptHeaderValue.contains("application/rdf+xml") || pipeOutputFormat.equalsIgnoreCase("rdfxml")){
		  res.setContentType(RDFFormat.RDFXML.getDefaultMIMEType());
		  
		// get pipe code 
		String pipeid = req.getParameter("id");
		BoxParserImplRDF parser = new BoxParserImplRDF();
		Stream stream = null;
		String syntax = PipeManager.getPipeSyntax(pipeid);
		
		for (Enumeration parameterIds = req.getParameterNames(); parameterIds.hasMoreElements();) {
			String parameterId = parameterIds.nextElement().toString();
			String parameterValue = (String) req.getParameter(parameterId);
			syntax = syntax.replace("${" + parameterId + "}", parameterValue);
		}
		if (syntax != null) {
			stream = parser.parse(syntax);
			if (stream instanceof RDFBox) {
				((RDFBox) stream).execute();
				((RDFBox) stream).getExecBuffer().toOutputStream(
						res.getOutputStream());
			} else {
				System.out.println("parsing error");
			}
		}	
	  }	
	  else{
			res.setContentType("text/html");
			
		  	// the URL of the pipe result in RDF/XML format
		  	//String rdfSourceUrl = new String("http://pipes.deri.org:8080/pipes/Pipes/?pipe_output_format=rdfxml&" + req.getQueryString());
		    String rdfSourceUrl = new String(req.getRequestURL()+"?pipe_output_format=n3&" + req.getQueryString());
			
		  	// read HTML template from file
		  	FileInputStream file = new FileInputStream (getServletContext().getRealPath("/") + "template/generic_exhibit_result_viewer.html");
			byte[] b = new byte[file.available()];
	        file.read(b);
	        file.close ();
	        
	        // the string that contains the HTML output. Contains placeholders that are filled out by the following code.
	        String outputString = new String (b);
	        
            // replace $rdf_source$ placeholder in the HTML template with the URL of the requested pipe.
	        outputString = outputString.replace("$rdf_source$", rdfSourceUrl); // this links to the absolute path of pipes.deri.org for now. It does not work with local resources.
			    
	        // TODO: Implement HTML template substitution for query form, based on pipe parameters and default values
	        // get pipe code 
		    String pipeid = req.getParameter("id");
			DOMParser myDOMParser = new DOMParser();
			String syntax = PipeManager.getPipeSyntax(pipeid);
			String pipeName = PipeManager.getPipe(pipeid).pipename;
			
            // replace $pipe_name$ placeholder in the HTML template with the pipe name.
			outputString = outputString.replace("$pipe_name$", pipeName);
			
			String errorMessagesString = new String();
			String queryFormString = new String();
			queryFormString = "<form action=\"http://pipes.deri.org:8080/pipes/Pipes/\" method=\"get\" name=\"pipe_query_form\">\n" +
	        	"<input name=\"id\" type=\"hidden\" value=\"" + pipeid + "\">\n";
			
	        if (syntax != null) {
	        	try {
					myDOMParser.parse(new InputSource(new java.io.StringReader(syntax)));
					NodeList pipeParameterXMLElements = ((Element)myDOMParser.getDocument().getDocumentElement().getElementsByTagName("parameters").item(0)).getElementsByTagName("parameter");
		            
		            // iterate through pipe parameter descriptions, construct query form
					String parameterLabel;
	            	String defaultParameterValue;
	            	String parameterId;
	            	String parameterComment;
					for(int i=0; i<pipeParameterXMLElements.getLength(); i++){
		            	//System.out.println(((Element)pipeParameterXMLElements.item(i)).getTagName());
		            	parameterLabel = "unnamed parameter";
		            	defaultParameterValue = "";
		            	parameterId = "";
		            	parameterComment = "";
		            	
		            	try {
							parameterLabel = ((Element)((Element)pipeParameterXMLElements.item(i)).getElementsByTagName("label").item(0)).getTextContent().toString();
						} catch (Exception e) {
							//e.printStackTrace();
						}
		            	try {
							parameterId = ((Element)((Element)pipeParameterXMLElements.item(i)).getElementsByTagName("id").item(0)).getTextContent().toString();
						} catch (Exception e) {
							//e.printStackTrace();
						}
		            	try {
							// defaultParameterValue = ((Element)((Element)pipeParameterXMLElements.item(i)).getElementsByTagName("default_value").item(0)).getTextContent().toString();
		            		// use the URL parameter value as default value
		            		defaultParameterValue = req.getParameter(parameterId);
		            		if (defaultParameterValue == null) {
		            			defaultParameterValue = "";
		            			errorMessagesString += "Value for parameter <i>" + parameterLabel + "</i> (" + parameterId + ") is missing. <br />\n";
		            		}
		            	} catch (Exception e) {
							//e.printStackTrace();
						}
						try {
							parameterComment = ((Element)((Element)pipeParameterXMLElements.item(i)).getElementsByTagName("comment").item(0)).getTextContent().toString();
						} catch (Exception e) {
							//e.printStackTrace();
						}
						
						// add parameter field to queryFormString
						queryFormString += "<div class=\"query_textinput_row\"><label>" + parameterLabel + "</label>: <input name=\"" + parameterId + "\" type=\"text\" value=\"" + defaultParameterValue + "\" size=\"60\"></div> \n";
		            }
		            
		            queryFormString += "<br /><input type=\"submit\" name=\"button\" id=\"button\" /></form>";
		            
		            // replace $query_form$ placeholder in the HTML template with the query form.
		            outputString = outputString.replace("$query_form$", queryFormString);
		            outputString = outputString.replace("$error_messages$", errorMessagesString);



				} catch (Exception e) {
					System.out.println("Exception during HTML query form creation.");
		            outputString = outputString.replace("$query_form$", "");
		            outputString = outputString.replace("$error_messages$", "");
					//e.printStackTrace();
				}
			}	
				

			PrintWriter outputWriter = res.getWriter();		
			outputWriter.write(outputString);
			
		}
	}
	
   
  
  public String getServletInfo() {
    return "Semantic Pipe End Points";
  }
}
