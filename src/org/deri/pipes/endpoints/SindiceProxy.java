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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.xerces.parsers.DOMParser;
import org.deri.pipes.utils.XMLUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Pipes Servlet.
 * @author danh.lephuoc
 *
 */
public class SindiceProxy extends HttpServlet {
	
	static Logger logger = LoggerFactory.getLogger(SindiceProxy.class);
	Hashtable<String,String> firstURI= new Hashtable<String,String>();
	ArrayList<String> domains= new ArrayList<String>();
	Hashtable<String,String> sparqlEndPoints= new Hashtable<String,String>();
	Hashtable<String,String> descs= new Hashtable<String,String>();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		String query=req.getParameter("query");
		searchSindice(query);
		ArrayList<JSONObject> results= new ArrayList<JSONObject>();
		for(int i=0;i<domains.size();i++){
			if(sparqlEndPoints.containsKey(domains.get(i))){
				JSONObject item =new JSONObject();
				try {
					item.put("title", descs.get(domains.get(i)));
					item.put("sparqlendpoint", sparqlEndPoints.get(domains.get(i)));
					item.put("firstURI", firstURI.get(domains.get(i)));
					results.add(item);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String jsonString=(new JSONArray(results)).toString();
		res.getWriter().print(jsonString);
	}
    
	private static String createSindiceSearchString(String term,int page){
		try{
			term=URLEncoder.encode(term, "UTF-8");
		}
		catch(java.io.UnsupportedEncodingException e){
			logger.info("UTF-8 support is required by the JVM specification",e);
		}
		return "http://api.sindice.com/v2/search?q="+term+"&qt=term&page="+page+"&count=100";
	}
	
	public void searchSindice(String term){
		int noResults=0,totalResults;
		String searchUrl;
		searchUrl=createSindiceSearchString(term, 1);
		
		while (true){
					    
		    try {
		      String output=readHTTP(searchUrl);	
	          if (output==null) return;
		      JSONObject result=new JSONObject(output);
	          JSONArray resultList =result.getJSONArray("entries");
	          totalResults=result.getInt("totalResults");
	          
	          for (int i=0;i<resultList.length();i++){
	             
	        	 noResults++;
	             String link=((JSONObject)resultList.get(i)).getString("link");
	             URL url;
	             String domain="";
				 try {
					url = new URL(link);
					domain=url.getHost().toLowerCase();
					
				 } catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
	             
	             if(!domains.contains(domain)) {
	            	domains.add(domain);
	            	System.out.println("domain" +domain);
	            	if(hasSitemap(domain)){
		            	firstURI.put(domain,link);	                    
		         		try {
	
		         			DOMParser parser = new DOMParser();
		    	
							parser.parse("http://"+domain+"/sitemap.xml");
							Document doc =parser.getDocument();
							if (doc==null) {
								System.out.println("doc null");
								return;
							}
							Element rootElm=doc.getDocumentElement();
							if (rootElm==null) {
								System.out.println("root element null");
								return;
							}
							Element datasetElm=XMLUtil.getFirstSubElementByName(rootElm,"sc:dataset");
							if(datasetElm!=null){
								System.out.println("first child tag " + datasetElm.getTagName());
								String sparqlEndpoint= XMLUtil.getTextFromFirstSubEleByName(datasetElm, "sc:sparqlEndpointLocation");
								if(sparqlEndpoint!=null){
									sparqlEndPoints.put(domain,sparqlEndpoint);
									descs.put(domain, ""+XMLUtil.getTextFromFirstSubEleByName(datasetElm, "sc:datasetLabel"));
								}
							}
	
		         		}catch(SAXException se) {
		         			se.printStackTrace();
		         		}catch(IOException ioe) {
		         			ioe.printStackTrace();
		         		}
	               }
	             }
	             if((noResults>=1000)||(noResults>=totalResults)) return;
	          }
	          searchUrl=result.getString("next");
	          
		    } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public String readHTTP(String url){
		HttpClient client = new HttpClient();
		
		GetMethod method = new GetMethod(url);
		    
		    method.addRequestHeader("Accept", "application/json");
		    // Provide custom retry handler is necessary
		    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
		    		new DefaultHttpMethodRetryHandler(3, false));
		    
		    //TODO:set Accept : "Accept: application/json"
		    
		    try {
	
		      int statusCode = client.executeMethod(method);
	
		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		        return null;
		      }
	           
		      // Read the response body.
		      return new String(method.getResponseBody());
		    }
	          catch (HttpException e) {
		      System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		    } catch (IOException e) {
		      System.err.println("Fatal transport error: " + e.getMessage());
		      e.printStackTrace();
		    } 
			finally {
		      // Release the connection.
		      method.releaseConnection();
		    } 
			return null;
	}
	
	public boolean hasSitemap(String domain){
		return true;
		/*String robot=readHTTP("http://"+domain+"/robot.txt");
		if (robot!=null)
		 return robot.toLowerCase().indexOf("sitemap: http//"+domain+"/sitemap.xml")>=0;
		return false;*/
	}
	
	public String getServletInfo() {
		return "Semantic PipeConfig End Points";
	}
}
