package com.atum.game.world;

import java.util.ArrayList;

import com.atum.game.world.model.Entity;
import com.atum.game.world.model.Player;
import com.atum.net.model.PlayerDetails;

public class World {
	
	private ArrayList<Player> playersLoginQueue = new ArrayList<Player>();
	private ArrayList<Entity> npcs = new ArrayList<Entity>();
	
	private static World singleton = new World();
	
	public boolean registerPlayer(PlayerDetails playerDetails){
		Player player = new Player(playerDetails);
		player.getActionSender().sendInitalLoginDetails();
		synchronized(playersLoginQueue){
			playersLoginQueue.add(player);
		}
		return true;
	}

	public static World getWorld() {
		return singleton;
	}
}
