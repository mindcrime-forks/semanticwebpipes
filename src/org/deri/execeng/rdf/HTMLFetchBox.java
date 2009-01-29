package org.deri.execeng.rdf;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.transform.stream.StreamSource;

import org.deri.execeng.core.PipeContext;
import org.deri.execeng.utils.XMLUtil;
import org.deri.execeng.utils.XSLTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
/**
 * The Fetch operator is used to fetch data from an URI in RDF/XML or SPARQL-RESULT/XML . There is an optional attribute &quot;accept&quot; which determines the HTTP accept header for the request. Allowed value are :&quot;rdfxml&quot; and &quot;sparqlxml&quot;.
<pre>
Syntax template:

&lt;fetch  accept=&quot;rdfxml/sparqlxml&quot;&gt;
   &lt;location&gt;
   URL OF THE WEB FILE TO FETCH
   &lt;/location&gt;
&lt;/fetch&gt;

or

&lt;fetch  accept=&quot;rdfxml/sparqlxml&quot;&gt;
   &lt;sparqlendpoint&gt;
      &lt;defaultgraph&gt;URI of the default graph&lt;/defaultgraph&gt;
      &lt;endpoint&gt;URL to the Sparql endpoint&lt;/endpoint&gt;
      &lt;query&gt;the remote SPARQL query&lt;/query&gt;
   &lt;/sparqlendpoint&gt;
&lt;/fetch&gt;

Example:

 &lt;fetch  accept=&quot;rdfxml&quot;&gt;
     &lt;location&gt;http://www.w3.org/People/Berners-Lee/card?&lt;/location&gt;
 &lt;/fetch&gt;
 </pre>
 *
 */
public class HTMLFetchBox extends RDFBox {
	final Logger logger = LoggerFactory.getLogger(HTMLFetchBox.class);
	//fuller says: WARNING WARNING WARNING MEMORY LEAKS FOLLOW IN STATIC HASHTABLES
	private static Hashtable<String,StreamSource> xsltFile=new Hashtable<String,StreamSource>();
	private static Hashtable<String,String> formats=new Hashtable<String,String>();
	private static String xsltPath=XSLTUtil.getBaseURL()+"/xslt/";
	static{		
		xsltFile.put("DC", new StreamSource(xsltPath+"dc-extract.xsl"));
		formats.put("DC", "Dublin Core");
	
		xsltFile.put("hCal", new StreamSource(xsltPath+"glean-hcal.xsl"));
		formats.put("hCal", "hCalendar");
		
		xsltFile.put("XFN", new StreamSource(xsltPath+"grokXFN.xsl"));
		formats.put("XFN", "XHTML Friends Network");
		
		xsltFile.put("hCard", new StreamSource(xsltPath+"hcard2rdf.xsl"));
		formats.put("hCard", "hCard");
		
		xsltFile.put("hReview", new StreamSource(xsltPath+"hreview2rdfxml.xsl"));
		formats.put("hReview", "hReview");
		
		xsltFile.put("RDFa",new StreamSource(xsltPath+"RDFa2RDFXML.xsl"));
		formats.put("RDFa", "RDFa");
	}
	
	private String url = null;
	private String format = null;
		
	public static Hashtable<String,String >getFormats(){
		return formats;
	}
	@Override
	public void execute() {
		buffer=new SesameMemoryBuffer();
		if(!isExecuted){
			Enumeration<String> k = xsltFile.keys();
			StreamSource stream=new StreamSource(url);
		    while (k.hasMoreElements()) {
		    	String key=k.nextElement();
		    	if(format.indexOf(key)>=0){
		    		StringBuffer textBuff=XSLTUtil.transform(stream, xsltFile.get(key));
		    		buffer.loadFromText(textBuff.toString(), url);
		    	}
		    }
			isExecuted=true;
		}
	}
	
	public void  initialize(PipeContext context,Element element){
		super.setContext(context);
    	setUrl(XMLUtil.getTextFromFirstSubEleByName(element, "location"));
		setFormat(element.getAttribute("format"));
    	
    	if((null!=url)&&(url.trim().length()>0)){
    		logger.warn("location missing for HTMLFetchBox "+element);
    	}
    	
    }
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}	
}
