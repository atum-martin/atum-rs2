package com.atum.game.revision.r317;

import org.atum.model.entity.Player;

import com.atum.net.codec.GamePacketBuilder;
import com.atum.net.codec.builder.DataTransformation;
import com.atum.net.codec.builder.DataType;
import com.atum.net.model.PlayerDetails;

public class ActionSender implements com.atum.net.model.ActionSender {
	
	private Player player;

	public void setPlayer(Object player) {
		this.player = (Player) player;
	}
	
	public void sendMapRegion() {
		GamePacketBuilder builder = new GamePacketBuilder(73);
		builder.put(DataType.SHORT, DataTransformation.ADD, player.getPosition().getCentralRegionX());
		builder.put(DataType.SHORT, player.getPosition().getCentralRegionY());
		player.write(builder.toGamePacket());
	}

	public void sendInitalLoginDetails() {
		
	}

	

	
	
	
}
