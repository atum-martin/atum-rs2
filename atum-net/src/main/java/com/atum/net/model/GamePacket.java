package com.atum.net.model;

import io.netty.buffer.ByteBuf;

public class GamePacket {

	private int opCode;
	private int size;
	private ByteBuf buf;
	private PacketHeader type = null;

	public GamePacket(int packetOpCode, int packetSize, ByteBuf readBytes,PacketHeader type) {
		this.opCode = packetOpCode;
		this.size = packetSize;
		this.buf = readBytes;
		this.type = type;
	}
	
	public GamePacket(int packetOpCode, int packetSize, ByteBuf readBytes) {
		this(packetOpCode,packetSize,readBytes,null);
	}

	public int getOpCode() {
		return opCode;
	}

	public ByteBuf getPayload() {
		return buf;
	}

	public PacketHeader getType() {
		if(type == null){
			//determine type based on op code.
		}
		return type;
	}
	
	public int getSize(){
		return size;
	}

}
