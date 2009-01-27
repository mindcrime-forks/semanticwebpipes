package org.deri.execeng.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openrdf.rio.RDFFormat;

public class PipeExhibit extends HttpServlet {
	final Logger logger = LoggerFactory.getLogger(PipeExhibit.class);
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType("text/html");
		
		FileInputStream file = new FileInputStream (getServletContext().getRealPath("/") + "template/generic_exhibit_result_viewer.html");
		byte[] b = new byte[file.available()];
        file.read(b);
        file.close ();
        String templateString = new String (b);
        String outputString = templateString.replace("$rdf_source$", "http://pipes.deri.org:8080/pipes/Pipes/?" + req.getQueryString()); // this links to the absolute path of pipes.deri.org for now. This is just a hack because it does not seem to work with local resources.
		        
		PrintWriter outputWriter = res.getWriter();		
		outputWriter.write(outputString);
	}

}
