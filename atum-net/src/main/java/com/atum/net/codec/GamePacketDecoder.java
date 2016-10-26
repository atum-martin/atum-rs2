package com.atum.net.codec;

import java.util.List;

import com.atum.net.GameService;
import com.atum.net.IsaacCipher;
import com.atum.net.model.GamePacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class GamePacketDecoder extends ByteToMessageDecoder {

	private final IsaacCipher decryptor;
	private State state = State.OPCODE;
	public static final int PACKET_SIZES[] = new int[257];

	private int packetOpCode = 0;
	private int packetSize = 0;
	private PacketHeader packetType = PacketHeader.EMPTY;
	private GameService service;

	private enum State {
		OPCODE, SIZE, PAYLOAD;
	}
	
	private enum PacketHeader {
		VARIABLE_BYTE(1), VARIABLE_SHORT(2), FIXED(0), EMPTY(0);
		private int size;
		
		PacketHeader(int size){
			this.size = size;
		}
		
		public int size(){
			return size;
		}
	}

	public GamePacketDecoder(GameService service,IsaacCipher decryptor) {
		this.decryptor = decryptor;
		this.service = service;
	}

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in,
			List<Object> out) throws Exception {
		switch (state) {

		case OPCODE:
			decodeOpcode(in);
			break;

		case SIZE:
			decodeSize(in);
			break;

		case PAYLOAD:
			decodePayload(in);
			break;

		}
	}
	
	private void queuePacket(ByteBuf readBytes) {
		service.queuePacket(new GamePacket(packetOpCode,packetSize,readBytes));
	}

	private void decodePayload(ByteBuf in) {
		if (in.isReadable(packetType.size())) {
			queuePacket(in.readBytes(packetSize));
			state = State.OPCODE;
		}
	}

	private void decodeSize(ByteBuf in) {
		if (in.isReadable(packetSize)) {
			if(packetType == PacketHeader.VARIABLE_BYTE){
				packetSize |= in.readUnsignedByte();
			} else if(packetType == PacketHeader.VARIABLE_SHORT){
				packetSize |= in.readUnsignedByte();
				packetSize |= in.readUnsignedByte() << 8;
			}
			state = State.PAYLOAD;
		}
	}

	private void decodeOpcode(ByteBuf in) {
		if (in.isReadable()) {
			packetOpCode = (in.readByte() - decryptor.getKey() & 0xFF);
			packetSize = PACKET_SIZES[packetOpCode];

			if (packetSize == -1) {
				packetType = PacketHeader.VARIABLE_BYTE;
			} else if (packetSize == -2) {
				packetType = PacketHeader.VARIABLE_SHORT;
			} else {
				packetType = PacketHeader.FIXED;
			}
		}

		if (packetSize == 0) {
			queuePacket(Unpooled.EMPTY_BUFFER);
			return;
		}
		
		state = packetSize == -2 || packetSize == -1 ? State.SIZE : State.PAYLOAD;
	}
	
}
