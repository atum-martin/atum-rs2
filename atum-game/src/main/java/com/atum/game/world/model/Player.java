package com.atum.game.world.model;

import com.atum.net.model.ActionSender;
import com.atum.net.model.PlayerDetails;

public class Player extends PlayerDetails {

	public Player(String username, String password, String uuid, ActionSender actionSender) {
		super(username,password,uuid,actionSender);
	}

}
