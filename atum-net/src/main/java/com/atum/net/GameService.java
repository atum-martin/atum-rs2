package com.atum.net;

import com.atum.net.model.GamePacket;

public interface GameService {
	
	public void queuePacket(GamePacket packet);
}
