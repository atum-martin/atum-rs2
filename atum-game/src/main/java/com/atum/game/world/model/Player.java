package com.atum.game.world.model;

import com.atum.net.model.ActionSender;
import com.atum.net.model.PlayerDetails;

public class Player extends Entity {

	private PlayerDetails details;

	public Player(PlayerDetails details) {
		this.details = details;
	}

	public ActionSender getActionSender() {
		return details.getActionSender();
	}

}
