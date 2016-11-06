package com.atum.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atum.game.world.World;
import com.atum.game.world.model.Player;
import com.atum.net.GameService;
import com.atum.net.model.ActionSender;
import com.atum.net.model.GamePacket;
import com.atum.net.model.PlayerDetails;
import com.atum.net.model.Revision;

public class GameServiceImpl implements GameService {

	private Map<String, Revision> clientRevs = new HashMap<String, Revision>();
	private Logger logger = Logger.getLogger(GameServiceImpl.class);

	public void queuePacket(PlayerDetails player, GamePacket packet) {
		logger.debug("packet: " + player.getName() + " id: "
				+ packet.getOpCode() + " size: " + packet.getSize());
	}

	public boolean registerRevision(Revision rev) {
		if (clientRevs.containsKey(rev.getVersion()))
			return false;
		clientRevs.put(rev.getVersion(), rev);
		return true;
	}

	public Revision getClientRevision(String version) {
		return clientRevs.get(version);
	}

	public boolean registerPlayer(PlayerDetails details) {
		return World.getWorld().registerPlayer(details);
	}

}
