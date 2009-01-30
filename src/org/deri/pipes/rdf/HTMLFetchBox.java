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
package org.deri.pipes.rdf;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.transform.stream.StreamSource;

import org.deri.pipes.utils.MappedStreamSource;
import org.deri.pipes.utils.XSLTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class HTMLFetchBox extends FetchBox {
	public static final String RDFA = "RDFa";
	public static final String HREVIEW = "hReview";
	public static final String HCARD = "hCard";
	public static final String XFN = "XFN";
	public static final String HCAL = "hCal";
	public static final String DC = "DC";
	final Logger logger = LoggerFactory.getLogger(HTMLFetchBox.class);
	//fuller says: WARNING WARNING WARNING MEMORY LEAKS FOLLOW IN STATIC HASHTABLES
	private static Hashtable<String,MappedStreamSource> xsltFile=new Hashtable<String,MappedStreamSource>();
	private static String xsltPath=XSLTUtil.getBaseURL()+"/xslt/";
	static{
		xsltFile.put(DC, MappedStreamSource.newInstance(DC,xsltPath+"dc-extract.xsl","Dublin Core"));
		xsltFile.put(HCAL,MappedStreamSource.newInstance(HCAL,xsltPath+"glean-hcal.xsl","hCalendar"));
		xsltFile.put(XFN, MappedStreamSource.newInstance(XFN, xsltPath+"grokXFN.xsl","XHTML Friends Network"));
		xsltFile.put(HCARD,MappedStreamSource.newInstance(HCARD,xsltPath+"hcard2rdf.xsl",HCARD));
		xsltFile.put(HREVIEW,MappedStreamSource.newInstance(HREVIEW,xsltPath+"hreview2rdfxml.xsl",HREVIEW));
		xsltFile.put(RDFA,MappedStreamSource.newInstance(RDFA,xsltPath+"RDFa2RDFXML.xsl",RDFA));
	}
	
	private String format = null;
		
	public static Collection<String>getFormats(){
		return xsltFile.keySet();
	}
	@Override
	public void execute() {
		buffer=new SesameMemoryBuffer();
		if(!isExecuted){
			Enumeration<String> k = xsltFile.keys();
			StreamSource stream=new StreamSource(location);
		    while (k.hasMoreElements()) {
		    	String key=k.nextElement();
		    	if(format.indexOf(key)>=0){
		    		StringBuffer textBuff=XSLTUtil.transform(stream, xsltFile.get(key).getStreamSource());
		    		((SesameMemoryBuffer)buffer).loadFromText(textBuff.toString(), location);
		    	}
		    }
			isExecuted=true;
		}
	}
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}	
}
