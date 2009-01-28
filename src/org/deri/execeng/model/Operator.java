package org.deri.execeng.model;

import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeContext;
import org.w3c.dom.Element;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public interface Operator extends Stream{	
     public ExecBuffer getExecBuffer();
     public void execute();
     public boolean isExecuted();
     /**
      * Initialize this operator from the dom element. Beware
      * this method may be changed or removed in subsequent releases.
      * @param context
      * @param element
      */
     public void initialize(PipeContext context, Element element);
}
