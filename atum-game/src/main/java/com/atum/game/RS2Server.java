package com.atum.game;

import org.apache.log4j.BasicConfigurator;

import com.atum.game.revision.r317.Revision317;
import com.atum.net.GameService;
import com.atum.net.NettyBootstrap;

public class RS2Server {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		Configuration config = Configuration.loadServerConfig();
		GameService service = new GameServiceImpl();
		service.registerRevision(new Revision317());
		NettyBootstrap.listen(service, config.getPort());
	}

	

}
