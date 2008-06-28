package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.PortType;

public class PipePortType implements PortType {	
	private static PipePortType typeList[]= new PipePortType[7];
	public static final byte NONE=0;
	public static final byte RDFIN=1;
	public static final byte RDFOUT=2;
	public static final byte TEXTIN=3;	
	public static final byte TEXTOUT=4;
	public static final byte SPARQLRESULTIN=5;
	public static final byte SPARQLRESULTOUT=6;
	
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
		//System.out.println(idx+"-->"+pType.idx);
		if((pType.getIdx()==RDFOUT)&&(idx==RDFIN))return true;
		if((pType.getIdx()==TEXTOUT)&&(idx==TEXTIN))return true;
		if((pType.getIdx()==SPARQLRESULTOUT)&&(idx==SPARQLRESULTIN))return true;
		return false;
	}
	
	public byte  getIdx(){
		return idx;
	}
}
