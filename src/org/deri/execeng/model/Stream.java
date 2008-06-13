package org.deri.execeng.model;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
import org.deri.execeng.core.ExecBuffer;
import org.w3c.dom.Element;
public interface Stream {
	public static StringBuffer log= new StringBuffer();
	public void streamming(ExecBuffer buffer);
	public void streamming(ExecBuffer buffer,String context);
	public String toString();
}
