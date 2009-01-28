package org.deri.execeng.model;

import org.deri.execeng.core.ExecBuffer;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public interface Box extends Stream{
     public ExecBuffer getExecBuffer();
     public void execute();
     public boolean isExecuted();
}
