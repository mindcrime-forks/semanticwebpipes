package org.deri.pipes.ui;
import org.integratedmodelling.zk.diagram.components.*;
public interface ConnectingInputNode {
	public void onConnected(Port port);
	public void onDisconnected(Port port);
}
