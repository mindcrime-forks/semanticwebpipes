package org.deri.execeng.rdf;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.model.Stream;
import org.w3c.dom.Element;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import org.deri.execeng.utils.*;
public class HTMLFetchBox extends RDFBox {
	private static Hashtable<String,StreamSource> xsltFile=new Hashtable<String,StreamSource>();
	private static Hashtable<String,String> formats=new Hashtable<String,String>();
	private static String xsltPath="./xslt/";
	static{		
		xsltFile.put("DC", new StreamSource(new File(xsltPath+"dc-extract.xsl")));
		formats.put("DC", "Dublin Core");
	
		xsltFile.put("hCal", new StreamSource(new File(xsltPath+"glean-hcal.xsl")));
		formats.put("hCal", "hCalendar");
		
		xsltFile.put("XFN", new StreamSource(new File(xsltPath+"grokXFN.xsl")));
		formats.put("XFN", "XHTML Friends Network");
		
		xsltFile.put("hCard", new StreamSource(new File(xsltPath+"hcard2rdf.xsl")));
		formats.put("hCard", "hCard");
		
		xsltFile.put("hReview", new StreamSource(new File(xsltPath+"hreview2rdfxml.xsl")));
		formats.put("hReview", "hReview");
		
		xsltFile.put("RDFa",new StreamSource(new File(xsltPath+"RDFa2RDFXML.xsl")));
		formats.put("RDFa", "RDFa");
	}
	
	String url,format;
	private boolean isExecuted=false;
	public HTMLFetchBox(String url,String format){
		this.format=format;
		this.url=url;
	}
	
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
		    		String text=XSLTUtil.transform(stream, xsltFile.get(key));
		    		buffer.loadFromText(text, url);
		    	}
		    }
			isExecuted=true;
		}
	}
	
	public static Stream loadStream(Element element){
    	String tmpStr=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	
    	if(tmpStr!=null)
    		return new HTMLFetchBox(tmpStr,element.getAttribute("format"));
    	
    	Stream.log.append("Error in fetchbox\n");
    	Stream.log.append(element.toString()+"\n");
    	return null;
    }	
}
