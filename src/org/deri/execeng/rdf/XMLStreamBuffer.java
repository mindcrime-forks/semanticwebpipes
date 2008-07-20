package org.deri.execeng.rdf;

import java.io.OutputStream;

import org.deri.execeng.core.ExecBuffer;
import javax.xml.transform.stream.StreamSource;
public class XMLStreamBuffer extends ExecBuffer {
	StreamSource buffer=null;
	public XMLStreamBuffer(String url){
		buffer= new StreamSource(url);
	}
	@Override
	public void streamming(ExecBuffer outputBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void streamming(ExecBuffer outputBuffer, String context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toOutputStream(OutputStream output) {
		// TODO Auto-generated method stub

	}
	public StreamSource getStreamSource(){
		return buffer;
	}

}
