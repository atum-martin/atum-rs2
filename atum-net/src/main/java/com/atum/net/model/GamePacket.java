package com.atum.net.model;

import io.netty.buffer.ByteBuf;

public class GamePacket {

	private int opCode;
	private int size;
	private ByteBuf buf;

	public GamePacket(int packetOpCode, int packetSize, ByteBuf readBytes) {
		this.opCode = packetOpCode;
		this.size = packetSize;
		this.buf = readBytes;
	}

}
