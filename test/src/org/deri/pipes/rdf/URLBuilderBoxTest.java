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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.internals.Source;
import org.deri.pipes.model.TextBuffer;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class URLBuilderBoxTest extends TestCase {
	
	public void test() throws Exception{
		String s1= "http://api.opencalais.com/enlighten/rest?content=%3C%21DOCTYPE+HTML+PUBLIC+%22-%2F%2FW3C%2F%2FDTD+HTML+4.01%2F%2FEN%22%0D++++++++%22http%3A%2F%2Fwww.w3.org%2FTR%2Fhtml4%2Fstrict.dtd%22%3E%0D%0D%3Chtml+lang%3D%22en-IE%22%3E%0D%3Chead%3E%0D%3Ctitle%3EApplepie+Solutions%3C%2Ftitle%3E%0D%0D%3Clink+type%3D%22text%2Fcss%22+rel%3D%22stylesheet%22+media%3D%22all%22+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fstyle.css%22%3E%0D%3Clink+type%3D%22text%2Fcss%22+rel%3D%22stylesheet%22+media%3D%22print%22+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fprint.css%22%3E%0D%3C%2Fhead%3E%0D%0D%3Cbody%3E%0D%0D%0D%3Cdiv+class%3D%22container%22%3E%0D%0D%3Cdiv+class%3D%22topcontainer%22%3E%0D%3Cdiv+class%3D%22logo%22%3E%0D%3Cimg+src%3D%22http%3A%2F%2Fwww.aplpi.com%2Fimages%2Flogo-225x75.png%22+height%3D%2275%22+width%3D%22225%22+alt%3D%22Applepie+Solutions+Logo%22%3E%0D%3C%2Fdiv%3E%0D%3Cdiv+class%3D%22topimage%22%3E%0D%3Cimg+src%3D%22http%3A%2F%2Fwww.aplpi.com%2Fimages%2Fmoher.jpg%22+height%3D%2279%22+width%3D%22495%22+alt%3D%22OBriens+tower%22%3E%0D%3C%2Fdiv%3E%0D%3C%2Fdiv%3E%0D%0D%3Cdiv+class%3D%22menucontainer%22%3E%0D%3Cdiv+id%3D%22menu%22%3E%0D%3Cul%3E%0A%09%3Cli+id%3D%22current%22%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Findex.shtml%22%3Ehome%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fnews.shtml%22%3Enews%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fservices.shtml%22%3Eservices%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fclients.shtml%22%3Ecustomers%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fblog%22%3Eblog%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fdownloads.shtml%22%3Edownloads%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fcontact.shtml%22%3Econtact%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fjobs.shtml%22%3Ejobs%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fabout.shtml%22%3Eabout%3C%2Fa%3E%3C%2Fli%3E%0A%3C%2Ful%3E%0A%0A%0A%0A%0D%3C%2Fdiv%3E%0D%3C%2Fdiv%3E%0D%0D%0D%3Cdiv+class%3D%22maincontainer%22%3E%0D%0D%3Cdiv+class%3D%22newsbox%22%3E%0D%09%3Ch2%3ELatest+News%3C%2Fh2%3E%0D%09%3C%21--+edit+the+text+properties+in+the+newsbox+p+in+the+stylesheet+--%3E%0A%3Cp%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fnews.html%2326-Nov-2007%22%3E%3Cem%3E26-Nov-2007%3C%2Fem%3E%3C%2Fa%3E+Applepie+Solutions+launches+new+Linux+consulting+and+support+business.%3C%2Fp%3E%0A%3Cp%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fnews.html%2317-May-2007%22%3E%3Cem%3E17-May-2007%3C%2Fem%3E%3C%2Fa%3E+We%27re+hiring.+We+have+a+vacancy+for+a+%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fjobs.html%23Vacancies%22%3EJava+Software+Engineer%3C%2Fa%3E.%3C%2Fp%3E%0A%3Cp%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fnews.html%2311-Dec-06%22%3E+%3Cem%3E11-Dec-2006%3C%2Fem%3E%3C%2Fa%3E+Applepie+Solutions+partners+with+Finnish+Semantic+Web+specialists%3C%2Fp%3E%0A%0D%09%3Cdiv%3E%3Cb+class%3D%22close%22%3E%3C%2Fb%3E%3C%2Fdiv%3E%0D%3C%2Fdiv%3E%0D%0D%0D%0D%3Cdiv+id%3D%22maintext%22%3E%0D%0D%3Cdiv+class%3D%22tagline%22%3EDesigns+for+Tomorrow%2C+Solutions+for+Today%3C%2Fdiv%3E%0D%0A%3Cp%3E%0ASoftware+for+%3Cem%3ENews+and+Media%3C%2Fem%3E.+Software+for+%3Cem%3EFinancial+Services%3C%2Fem%3E.+%0A%3C%2Fp%3E%0D%3Cp%3E%0DApplepie+Solutions+is+a+technology+consulting+firm+based+in+the+West+of+Ireland+providing+application+development+and+software+consulting.+We+deliver+best+in+class+systems+tailored+to+the+business+needs+of+our+customers.%0D%3C%2Fp%3E%0D%3Cp%3E%0DWe+deliver+robust+high+performance+solutions+that+meet+our+customers%27+requirements+on+time+and+within+budget.%0D%3C%2Fp%3E%0D%3C%2Fdiv%3E%0D%0D%3C%2Fdiv%3E%0D%0D%3C%2Fdiv%3E%0D%0D%0A%3C%21--+block+of+code+for+google+analytics+--%3E%0A%3Cscript+src%3D%22http%3A%2F%2Fwww.google-analytics.com%2Furchin.js%22+type%3D%22text%2Fjavascript%22%3E%0A%3C%2Fscript%3E%0A%3Cscript+type%3D%22text%2Fjavascript%22%3E%0A_uacct+%3D+%22UA-366606-1%22%3B%0AurchinTracker%28%29%3B%0A%3C%2Fscript%3E%0A%0A%3Cdiv+class%3D%22legal%22%3E%0AThanks+to+%3Ca+href%3D%22http%3A%2F%2Fwww.focuspocus.org%2F%22%3EFocus+Pocus%3C%2Fa%3E+for+%0Asupplying+the+stock+photography.%0A%3Cbr%2F%3E%0ACopyright+%26copy%3B+1998-2008+Applepie+Solutions+Ltd.%2C+feedback+to+%0A%3Ca+href%3D%22mailto%3Awebmaster%40aplpi.com%22%3Ewebmaster%40aplpi.com%3C%2Fa%3E%0A%3C%2Fdiv%3E%0A%0D%0D%3C%2Fbody%3E%0D%0D%3C%2Fhtml%3E%0D%0D%0A&licenseId=8zqqt6d7f7akn5vcnjhbd2qu";
		String s2= "http://api.opencalais.com/enlighten/rest?content=%3C%21DOCTYPE+HTML+PUBLIC+%22-%2F%2FW3C%2F%2FDTD+HTML+4.01%2F%2FEN%22%0D++++++++%22http%3A%2F%2Fwww.w3.org%2FTR%2Fhtml4%2Fstrict.dtd%22%3E%0D%0D%3Chtml+lang%3D%22en-IE%22%3E%0D%3Chead%3E%0D%3Ctitle%3EApplepie+Solutions%3C%2Ftitle%3E%0D%0D%3Clink+type%3D%22text%2Fcss%22+rel%3D%22stylesheet%22+media%3D%22all%22+href%3D%22style.css%22%3E%0D%3Clink+type%3D%22text%2Fcss%22+rel%3D%22stylesheet%22+media%3D%22print%22+href%3D%22print.css%22%3E%0D%3C%2Fhead%3E%0D%0D%3Cbody%3E%0D%0D%0D%3Cdiv+class%3D%22container%22%3E%0D%0D%3Cdiv+class%3D%22topcontainer%22%3E%0D%3Cdiv+class%3D%22logo%22%3E%0D%3Cimg+src%3D%22images%2Flogo-225x75.png%22+height%3D%2275%22+width%3D%22225%22+alt%3D%22Applepie+Solutions+Logo%22%3E%0D%3C%2Fdiv%3E%0D%3Cdiv+class%3D%22topimage%22%3E%0D%3Cimg+src%3D%22images%2Fmoher.jpg%22+height%3D%2279%22+width%3D%22495%22+alt%3D%22OBriens+tower%22%3E%0D%3C%2Fdiv%3E%0D%3C%2Fdiv%3E%0D%0D%3Cdiv+class%3D%22menucontainer%22%3E%0D%3Cdiv+id%3D%22menu%22%3E%0D%3Cul%3E%0A%09%3Cli+id%3D%22current%22%3E%3Ca+href%3D%22index.shtml%22%3Ehome%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22news.shtml%22%3Enews%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22services.shtml%22%3Eservices%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22clients.shtml%22%3Ecustomers%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22http%3A%2F%2Fwww.aplpi.com%2Fblog%22%3Eblog%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22downloads.shtml%22%3Edownloads%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22contact.shtml%22%3Econtact%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22jobs.shtml%22%3Ejobs%3C%2Fa%3E%3C%2Fli%3E%0A%09%3Cli%3E%3Ca+href%3D%22about.shtml%22%3Eabout%3C%2Fa%3E%3C%2Fli%3E%0A%3C%2Ful%3E%0A%0A%0A%0A%0D%3C%2Fdiv%3E%0D%3C%2Fdiv%3E%0D%0D%0D%3Cdiv+class%3D%22maincontainer%22%3E%0D%0D%3Cdiv+class%3D%22newsbox%22%3E%0D%09%3Ch2%3ELatest+News%3C%2Fh2%3E%0D%09%3C%21--+edit+the+text+properties+in+the+newsbox+p+in+the+stylesheet+--%3E%0A%3Cp%3E%3Ca+href%3D%22news.html%2326-Nov-2007%22%3E%3Cem%3E26-Nov-2007%3C%2Fem%3E%3C%2Fa%3E+Applepie+Solutions+launches+new+Linux+consulting+and+support+business.%3C%2Fp%3E%0A%3Cp%3E%3Ca+href%3D%22news.html%2317-May-2007%22%3E%3Cem%3E17-May-2007%3C%2Fem%3E%3C%2Fa%3E+We%27re+hiring.+We+have+a+vacancy+for+a+%3Ca+href%3D%22jobs.html%23Vacancies%22%3EJava+Software+Engineer%3C%2Fa%3E.%3C%2Fp%3E%0A%3Cp%3E%3Ca+href%3D%22news.html%2311-Dec-06%22%3E+%3Cem%3E11-Dec-2006%3C%2Fem%3E%3C%2Fa%3E+Applepie+Solutions+partners+with+Finnish+Semantic+Web+specialists%3C%2Fp%3E%0A%0D%09%3Cdiv%3E%3Cb+class%3D%22close%22%3E%3C%2Fb%3E%3C%2Fdiv%3E%0D%3C%2Fdiv%3E%0D%0D%0D%0D%3Cdiv+id%3D%22maintext%22%3E%0D%0D%3Cdiv+class%3D%22tagline%22%3EDesigns+for+Tomorrow%2C+Solutions+for+Today%3C%2Fdiv%3E%0D%0A%3Cp%3E%0ASoftware+for+%3Cem%3ENews+and+Media%3C%2Fem%3E.+Software+for+%3Cem%3EFinancial+Services%3C%2Fem%3E.+%0A%3C%2Fp%3E%0D%3Cp%3E%0DApplepie+Solutions+is+a+technology+consulting+firm+based+in+the+West+of+Ireland+providing+application+development+and+software+consulting.+We+deliver+best+in+class+systems+tailored+to+the+business+needs+of+our+customers.%0D%3C%2Fp%3E%0D%3Cp%3E%0DWe+deliver+robust+high+performance+solutions+that+meet+our+customers%27+requirements+on+time+and+within+budget.%0D%3C%2Fp%3E%0D%3C%2Fdiv%3E%0D%0D%3C%2Fdiv%3E%0D%0D%3C%2Fdiv%3E%0D%0D%0A%3C%21--+block+of+code+for+google+analytics+--%3E%0A%3Cscript+src%3D%22http%3A%2F%2Fwww.google-analytics.com%2Furchin.js%22+type%3D%22text%2Fjavascript%22%3E%0A%3C%2Fscript%3E%0A%3Cscript+type%3D%22text%2Fjavascript%22%3E%0A_uacct+%3D+%22UA-366606-1%22%3B%0AurchinTracker%28%29%3B%0A%3C%2Fscript%3E%0A%0A%3Cdiv+class%3D%22legal%22%3E%0AThanks+to+%3Ca+href%3D%22http%3A%2F%2Fwww.focuspocus.org%2F%22%3EFocus+Pocus%3C%2Fa%3E+for+%0Asupplying+the+stock+photography.%0A%3Cbr%2F%3E%0ACopyright+%26copy%3B+1998-2008+Applepie+Solutions+Ltd.%2C+feedback+to+%0A%3Ca+href%3D%22mailto%3Awebmaster%40aplpi.com%22%3Ewebmaster%40aplpi.com%3C%2Fa%3E%0A%3C%2Fdiv%3E%0A%0D%0D%3C%2Fbody%3E%0D%0D%3C%2Fhtml%3E%0D%0D%0A&licenseId=8zqqt6d7f7akn5vcnjhbd2qu";
		assertEquals(s1.length(),s2.length());
		URLBuilderBox x = new URLBuilderBox();
		String baseUrl="http://api.opencalais.com/";
		x.setBaseUrl(baseUrl);
		x.addPath("enlighten/rest");
		HttpGetBox y = new HttpGetBox();
		y.setLocation("http://www.aplpi.com/");
		x.addParameter("content", new Source(y));
		x.addParameter("licenseId","8zqqt6d7f7akn5vcnjhbd2qu");
		Engine.defaultEngine().parse("<http-get/>");
		String xSerialized = Engine.defaultEngine().serialize(x);
		System.out.println(xSerialized);
		callOpenCalais(x);
		
		String code = "<urlbuilder><base>"+baseUrl+"</base><path>foo</path></urlbuilder>";
		x=(URLBuilderBox)Engine.defaultEngine().parse(xSerialized);
		String ySerialized = Engine.defaultEngine().serialize(x);
		assertEquals("wrong xml",xSerialized,ySerialized);
		
	}

	private void callOpenCalais(URLBuilderBox x) throws Exception, IOException {
		ExecBuffer execBuffer = x.execute(Engine.defaultEngine().newContext());
		assertTrue("Result should be a TextBuffer",execBuffer instanceof TextBuffer);
		TextBuffer text = (TextBuffer)execBuffer;
		String calaisUrl = text.toString();
		System.out.println(calaisUrl);
		HttpGetBox getbox = new HttpGetBox();
		getbox.setLocation(calaisUrl);
		execBuffer = getbox.execute(Engine.defaultEngine().newContext());
		execBuffer.stream(System.out);
	}

}
