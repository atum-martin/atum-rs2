package com.atum.game;

import java.util.HashMap;
import java.util.Map;

import com.atum.net.GameService;
import com.atum.net.model.GamePacket;
import com.atum.net.model.Revision;

public class GameServiceImpl implements GameService {

	private Map<String,Revision> clientRevs = new HashMap<String,Revision>();
	
	public void queuePacket(GamePacket packet) {
		
	}

	public boolean registerRevision(Revision rev) {
		if(clientRevs.containsKey(rev.getVersion()))
			return false;
		clientRevs.put(rev.getVersion(), rev);
		return true;
	}

	public Revision getClientRevision(String version) {
		return clientRevs.get(version);
	}

}
