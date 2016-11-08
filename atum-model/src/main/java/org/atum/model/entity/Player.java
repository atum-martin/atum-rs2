package org.atum.model.entity;

import org.atum.model.Position;

import com.atum.net.model.ActionSender;
import com.atum.net.model.GamePacket;
import com.atum.net.model.PlayerDetails;

public class Player extends Entity {

	private PlayerDetails details;
	private ActionSender actionSender;
	
	public Player(PlayerDetails details) {
		this.details = details;
		this.actionSender = details.getActionSender();
		actionSender.setPlayer(this);
	}

	public ActionSender getActionSender() {
		return actionSender;
	}

	public Position getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(GamePacket gamePacket) {
		details.write(gamePacket);
	}

}
