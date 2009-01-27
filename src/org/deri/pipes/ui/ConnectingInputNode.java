package org.deri.pipes.ui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.integratedmodelling.zk.diagram.components.*;
public interface ConnectingInputNode {
	public void onConnected(Port port);
	public void onDisconnected(Port port);
}
