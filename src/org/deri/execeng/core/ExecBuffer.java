package org.deri.execeng.core;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */

public abstract class ExecBuffer {
	 public static StringBuffer log= new StringBuffer();
     public abstract void stream(ExecBuffer outputBuffer);
     public abstract void stream(ExecBuffer outputBuffer,String context);
     public abstract void stream(java.io.OutputStream output);
}
