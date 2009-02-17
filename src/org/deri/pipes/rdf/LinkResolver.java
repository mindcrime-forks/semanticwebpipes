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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.model.BinaryContentBuffer;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.StartTag;

/**
 * Adapts href links in html page to make those links absolute.
 * @author robful
 *
 */
public class LinkResolver{

	public static BinaryContentBuffer rewriteUrls(ExecBuffer execBuffer, String baseURL) throws IOException{
		if(!(execBuffer instanceof BinaryContentBuffer)){
			execBuffer = new BinaryContentBuffer(execBuffer);
		}
		BinaryContentBuffer input = (BinaryContentBuffer)execBuffer;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(bout,input.getCharacterEncoding());
		rewriteUrls(input.getInputStream(),writer,baseURL.indexOf('?')>0?baseURL.substring(0,baseURL.indexOf('?')):baseURL,input.getCharacterEncoding());
		writer.close();
		BinaryContentBuffer result = new BinaryContentBuffer(bout);
		result.setCharacterEncoding(input.getCharacterEncoding());
		result.setContentType(input.getContentType());
		return result;
	}

	static void rewriteUrls(InputStream inputstream, Writer writer, String baseURL, String inputEncoding)
	throws IOException
	{
		au.id.jericho.lib.html.Source source = new au.id.jericho.lib.html.Source(new InputStreamReader(inputstream, inputEncoding));
		OutputDocument outputdocument = new OutputDocument(source);
		List list = source.findAllStartTags();
		Iterator iterator = list.iterator();
		do
		{
			if(!iterator.hasNext())
				break;
			StartTag starttag = (StartTag)iterator.next();
			if(starttag.getName() != null)
			{
				Attributes attributes = starttag.getAttributes();
				if(attributes != null)
				{
					Attribute attribute = attributes.get("href");
					if(attribute != null)
					{
						String key = attribute.getKey();
						String value = attribute.getValue();
						if(value != null && !value.startsWith("javascript:") && !value.startsWith("mailto:") && !value.startsWith("#"))
						{
							Map map = outputdocument.replace(attributes, false);
							String absoluteUrl = makeAbsoluteUrl(baseURL, value);
							absoluteUrl = absoluteUrl.replace(' ', '+');
							map.put(key, absoluteUrl);                   }
					}
					attribute = attributes.get("src");
					if(attribute != null)
					{
						String key = attribute.getKey();
						String value = attribute.getValue();
						if(value != null)
						{
							Map map1 = outputdocument.replace(attributes, false);
							map1.put(key, makeAbsoluteUrl(baseURL, value));
						}
					}
				}
			}
		} while(true);
		//    super.log(outputdocument.getDebugInfo());
		//    super.log((new StringBuilder()).append("returning document:").append(outputdocument.toString()).toString());
		outputdocument.writeTo(writer);
	}

	private static String makeAbsoluteUrl(String baseUrl, String link)
	{
		if(link.startsWith("http:"))
			return link;
		if(link.startsWith("/"))
		{
			for(; baseUrl.lastIndexOf('/') > 10; baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/')));
			return (new StringBuilder()).append(baseUrl).append(link).toString();
		}
		if(baseUrl.lastIndexOf('/') > 10)
			baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));
		return (new StringBuilder()).append(baseUrl).append("/").append(link).toString();
	}





}
