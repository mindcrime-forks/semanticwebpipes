package org.deri.execeng.model;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deri.execeng.core.ExecBuffer;
public interface Stream {
	public void stream(ExecBuffer buffer);
	public void stream(ExecBuffer buffer,String context);
	public String toString();
}
