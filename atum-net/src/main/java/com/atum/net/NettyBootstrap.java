package com.atum.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyBootstrap {

	public static void main(String[] args) {
		EventLoopGroup loopGroup = new NioEventLoopGroup();
		
		GameService gameService = null;

		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(loopGroup).channel(NioServerSocketChannel.class).childHandler(new PipelineInitializer(gameService)).bind(43594).syncUninterruptibly();
	}

}
