package com.atum.net;

import io.netty.channel.socket.SocketChannel;
/**
*
* @author Martin
*/
public class PlayerChannel {
	
	@SuppressWarnings("unused")
	private SocketChannel channel;

	public PlayerChannel(SocketChannel channel) {
		this.channel = channel;
	}

}
