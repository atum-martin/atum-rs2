package com.atum.net;

import io.netty.channel.socket.SocketChannel;
/**
*
* @author Martin
*/
public class PlayerChannel {
	
	private SocketChannel channel;

	public PlayerChannel(SocketChannel channel) {
		this.channel = channel;
	}

}
