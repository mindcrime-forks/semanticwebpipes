package org.deri.execeng.rdf;

import org.deri.execeng.core.PipeParser;
import org.w3c.dom.Element;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.xml.transform.stream.StreamSource;
import org.deri.execeng.utils.*;

public class HTMLFetchBox extends RDFBox {
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
	
	String url,format;
	private boolean isExecuted=false;
	PipeParser parser;
	public HTMLFetchBox(PipeParser parser, Element element){
		this.parser=parser;
		initialize(element);
	}
		
	public static Hashtable<String,String >getFormats(){
		return formats;
	}
	@Override
	public void execute() {
		buffer=new SesameMemoryBuffer(parser);
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
	
	private void  initialize(Element element){
    	url=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	
    	if((null!=url)&&(url.trim().length()>0))
    		format=element.getAttribute("format");
    	
    	parser.log("Error in fetchbox");
    	parser.log(element.toString());
    }	
}
