package com.atum.net;

import com.atum.net.model.GamePacket;
import com.atum.net.model.Revision;

public interface GameService {
	
	public void queuePacket(GamePacket packet);
	public boolean registerRevision(Revision rev);
	public Revision getClientRevision(String version);
}
