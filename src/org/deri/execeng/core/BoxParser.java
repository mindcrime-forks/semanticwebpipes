package org.deri.execeng.core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deri.execeng.model.Stream;

/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public abstract class BoxParser {
   public abstract Stream parse(String syntax);   
}
