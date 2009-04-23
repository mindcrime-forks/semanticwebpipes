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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.deri.pipes.ui.SindiceSearchResult;
import org.integratedmodelling.zk.diagram.components.Workspace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;


/**
 * @author Danh Le Phuoc
 *
 */
public class SindiceSearchRenderer implements RowRenderer{
	static Logger logger = LoggerFactory.getLogger(SindiceSearchRenderer.class);
	private String searchGridId,searchPageId;
	private Workspace wsp;
	private int noPages=0;
	public SindiceSearchRenderer(String searchGridId,String searchPageId, Workspace wsp){
		this.searchGridId=searchGridId;
		this.searchPageId=searchPageId;
		this.wsp=wsp;
	}
	/* (non-Javadoc)
	 * @see org.zkoss.zul.RowRenderer#render(org.zkoss.zul.Row, java.lang.Object)
	 */
	@Override
	public void render(Row row, Object data) throws Exception {
		// TODO Auto-generated method stub
		logger.debug(data.toString());		
		row.appendChild(new SindiceSearchResult((JSONObject)data));
	}
	
	public void refreshSearch (String term,int page){
		Grid grid = (Grid)wsp.getFellow(searchGridId);
		grid.setModel(new SimpleListModel(searchSindice(term,page)));
		
		Combobox pageCBox= (Combobox)wsp.getFellow(searchPageId);
		if(pageCBox.getItemCount()!=noPages){
			if(pageCBox.getItemCount()>noPages){
				for(;pageCBox.getItemCount()>noPages;)
					pageCBox.removeItemAt(pageCBox.getItemCount()-1);
			}
			else{
				for(;pageCBox.getItemCount()<noPages;)
					pageCBox.appendItem(""+(pageCBox.getItemCount()+1));
			}
		}
		if (pageCBox.getItemCount()>0)
			pageCBox.setSelectedIndex(page-1);
	}
	
	public List searchSindice(String term,int page){
		List jList=new ArrayList();
		
	    HttpClient client = new HttpClient();
	    try{
			term=URLEncoder.encode(term, "UTF-8");
		}
		catch(java.io.UnsupportedEncodingException e){
			logger.info("UTF-8 support is required by the JVM specification",e);
		}
	    GetMethod method = new GetMethod("http://api.sindice.com/v2/search?q="+term+"&qt=term&page="+page+"&count=100");
	    
	    method.addRequestHeader("Accept", "application/json");
	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
	    		new DefaultHttpMethodRetryHandler(3, false));
	    
	    //TODO:set Accept : "Accept: application/json"
	    
	    try {

	      int statusCode = client.executeMethod(method);

	      if (statusCode != HttpStatus.SC_OK) {
	        System.err.println("Method failed: " + method.getStatusLine());
	      }

	      // Read the response body.
	      JSONObject result=new JSONObject(new String(method.getResponseBody()));
          JSONArray resultList =result.getJSONArray("entries");
          noPages=result.getInt("totalResults")/100;
          for (int i=0;i<resultList.length();i++)
        	  jList.add(resultList.get(i));
          return jList;
          
	    } catch (HttpException e) {
	      System.err.println("Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    } catch (IOException e) {
	      System.err.println("Fatal transport error: " + e.getMessage());
	      e.printStackTrace();
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	      // Release the connection.
	      method.releaseConnection();
	    }  
        return jList;
	}

}
