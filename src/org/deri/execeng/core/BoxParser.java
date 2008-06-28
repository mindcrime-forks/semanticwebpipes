package org.deri.execeng.core;
import org.deri.execeng.model.Stream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public abstract class BoxParser {
   public abstract Stream parse(String syntax);   
}
