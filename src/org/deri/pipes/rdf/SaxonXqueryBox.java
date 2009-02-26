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
import java.io.InputStream;
import java.util.Properties;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.functions.ExtensionFunctionFactory;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;

import org.deri.pipes.core.Context;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.model.BinaryContentBuffer;
import org.deri.pipes.model.InputStreamProvider;
import org.deri.pipes.model.Stream;
import org.deri.pipes.xquery.Functions;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Runs an XQuery operation on the input.
 * Pipes functions can be used in the xquery operator by using
 * the 'pipes:' prefix.
 * @see org.deri.pipes.xquery.Functions
 * @author robful
 *
 */
@XStreamAlias("xquery")
public class SaxonXqueryBox implements Operator {
	private String query;
    private String contentType = "text/xml";
    private Source source;
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.Operator#execute(org.deri.pipes.core.Context)
	 */
	@Override
	public ExecBuffer execute(Context context) throws Exception {
		ExecBuffer in = source.execute(context);
		final Configuration config = new Configuration();
		final StaticQueryContext sqc = new StaticQueryContext(config);
		sqc.declareNamespace("pipes", "java:org.deri.pipes.xquery.Functions");
		final XQueryExpression exp = sqc.compileQuery(query);
		final DynamicQueryContext dynamicContext = new DynamicQueryContext(config);
		if(!(in instanceof InputStreamProvider)){
			in = new BinaryContentBuffer(in);
		}
		InputStream input = ((InputStreamProvider)in).getInputStream();
		final DocumentInfo document  = config.buildDocument(new StreamSource(input));
		dynamicContext.setContextItem(document);
		final Properties props = new Properties();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Functions.context.set(context);
		try{
			exp.run(dynamicContext, new StreamResult(bout), props);
		}finally{
			Functions.context.remove();
		}
		BinaryContentBuffer binaryContentBuffer = new BinaryContentBuffer(bout);
		binaryContentBuffer.setContentType(contentType);
		return binaryContentBuffer;
	}
    public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}

}
