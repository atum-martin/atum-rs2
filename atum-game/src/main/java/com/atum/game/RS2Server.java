package com.atum.game;

import org.apache.log4j.BasicConfigurator;

import com.atum.net.GameService;
import com.atum.net.NettyBootstrap;
import com.atum.revision.r317.Revision317;

public class RS2Server {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		GameService service = new GameServiceImpl();
		service.registerRevision(new Revision317());
		NettyBootstrap.listen(service,43594);
	}

}
