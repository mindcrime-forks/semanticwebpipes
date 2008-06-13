package org.deri.execeng.core;

public abstract class ExecBuffer {
	 public static StringBuffer log= new StringBuffer();
     public abstract void streamming(ExecBuffer outputBuffer);
     public abstract void streamming(ExecBuffer outputBuffer,String context);
     public abstract void toOutputStream(java.io.OutputStream output);
}
