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
package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.PortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipePortType implements PortType {	
	//TODO: Refactor this class.
	final Logger logger = LoggerFactory.getLogger(PipePortType.class);
	private static PipePortType typeList[]= new PipePortType[17];
	public static final byte NONE=0;
	public static final byte RDFOUT=1;
	public static final byte RDFIN=2;
	public static final byte TEXTOUT=3;
	public static final byte TEXTIN=4;	
	public static final byte SPARQLRESULTOUT=5;
	public static final byte SPARQLRESULTIN=6;
	public static final byte XMLOUT=7;
	public static final byte XMLIN=8;
	public static final byte XSLOUT=9;
	public static final byte XSLIN=10;
	public static final byte ANYOUT=11;
	public static final byte ANYIN=12;
	public static final byte SOURCEORSTRINGOUT=13;
	public static final byte SOURCEORSTRINGIN=14;
	public static final byte CONDITIONOUT=15;
	public static final byte CONDITIONIN=16;

	
	byte idx;
    public PipePortType(byte idx){
    	this.idx=idx;    	
    }
    
    public static PipePortType getPType(byte idx){
    	if(typeList[idx]==null) typeList[idx]=new PipePortType(idx);
    	return typeList[idx];
    }
    
	@Override
	public boolean isConnectableFrom(PortType portType) {
		PipePortType pType=(PipePortType)portType;
		if((pType.getIdx() == ANYOUT)&& (idx==RDFIN || idx==SPARQLRESULTIN || idx==XMLIN || idx == XSLIN || idx == ANYIN)) return true;
		if((pType.getIdx()==RDFOUT)&&((idx==RDFIN)||(idx==XMLIN) ||(idx == ANYIN)))return true;
		if((pType.getIdx()==TEXTOUT)&&((idx==TEXTIN)||(idx == ANYIN)||(idx==SOURCEORSTRINGIN)))return true;
		if((pType.getIdx()==SPARQLRESULTOUT)&&((idx==SPARQLRESULTIN)||(idx==XMLIN)||(idx == ANYIN)))return true;
		if((pType.getIdx()==XMLOUT)&&((idx==XMLIN)||(idx==XSLIN)||(idx==SPARQLRESULTIN)||(idx==RDFIN)||(idx == ANYIN)))return true;
		if((pType.getIdx()==XSLOUT)&&((idx==XSLIN)||(idx==XMLIN)||(idx == ANYIN)))return true;
		if((pType.getIdx()==SOURCEORSTRINGOUT)&&(idx==SOURCEORSTRINGIN))return true;
		if((pType.getIdx()==CONDITIONOUT)&& idx == CONDITIONIN) return true;
		return false;
	}
	
	public static void generateAllPortTypes(PipeEditor wsp){
		for(byte i=0;i<typeList.length;i++){
			wsp.getPTManager().addPortType(getPType(i));
		}
	}
	
	public byte  getIdx(){
		return idx;
	}
}
