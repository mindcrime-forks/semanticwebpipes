package org.deri.pipes.ui;

import org.integratedmodelling.zk.diagram.components.PortType;

public class PipePortType implements PortType {	
	private static PipePortType typeList[]= new PipePortType[12];
	public static final byte NONE=0;
	public static final byte RDFOUT=1;
	public static final byte RDFIN=2;
	public static final byte TEXTOUT=3;
	public static final byte TEXTIN=4;	
	public static final byte SPARQLRESULTOUT=5;
	public static final byte SPARQLRESULTIN=6;
	public static final byte XMLOUT=8;
	public static final byte XMLIN=9;
	public static final byte XSLOUT=10;
	public static final byte XSLIN=11;
	
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
		if((pType.getIdx()==RDFOUT)&&(idx==RDFIN))return true;
		if((pType.getIdx()==TEXTOUT)&&(idx==TEXTIN))return true;
		if((pType.getIdx()==SPARQLRESULTOUT)&&(idx==SPARQLRESULTIN))return true;
		if((pType.getIdx()==XMLOUT)&&(idx==XMLIN))return true;
		if((pType.getIdx()==XSLOUT)&&(idx==XSLIN))return true;
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
