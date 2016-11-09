package com.atum.net.codec;

import org.apache.log4j.Logger;

import com.atum.net.IsaacCipher;
import com.atum.net.model.GamePacket;
import com.atum.net.model.PacketHeader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class GamePacketEncoder extends MessageToByteEncoder<GamePacket> {

	IsaacCipher encryptor;
	private Logger logger = Logger.getLogger(GamePacketEncoder.class);
	
	public GamePacketEncoder(IsaacCipher encryptor) {
		this.encryptor = encryptor;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, GamePacket msg, ByteBuf out) throws Exception {
		logger.debug("encoding packet: "+msg.getOpCode()+" size: "+msg.getSize());
		out.writeByte((msg.getOpCode() + encryptor.getKey()) & 0xFF);
		if(msg.getType() == PacketHeader.VARIABLE_BYTE)
			out.writeByte(msg.getSize());
		else if(msg.getType() == PacketHeader.VARIABLE_SHORT)
			out.writeShort(msg.getSize());
		out.writeBytes(msg.getPayload());

	}

}
