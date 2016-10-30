package org.atum.game;

import org.apache.log4j.BasicConfigurator;
import org.atum.revision.r317.Revision317;

import com.atum.net.GameService;
import com.atum.net.NettyBootstrap;

public class Main {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		GameService service = new GameServiceImpl();
		service.registerRevision(new Revision317());
		NettyBootstrap.listen(service,43594);
	}

}
