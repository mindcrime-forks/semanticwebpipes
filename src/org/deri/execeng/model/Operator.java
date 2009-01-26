package org.deri.execeng.model;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public interface Operator extends Stream{	
     public org.deri.execeng.core.ExecBuffer getExecBuffer();
     public void execute();
     public boolean isExecuted();
}
