package com.atum.net.codec;

import java.util.List;

import com.atum.net.GameService;
import com.atum.net.IsaacCipher;
import com.atum.net.NetworkConstants;
import com.atum.net.model.GamePacket;
import com.atum.net.model.PacketHeader;
import com.atum.net.model.PlayerDetails;
import com.atum.net.model.Revision;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class GamePacketDecoder extends ByteToMessageDecoder {

	private final IsaacCipher decryptor;
	private State state = State.OPCODE;

	private int packetOpCode = 0;
	private int packetSize = 0;
	private PlayerDetails player;
	private PacketHeader packetType = PacketHeader.EMPTY;
	private GameService service;

	private enum State {
		OPCODE, SIZE, PAYLOAD;
	}

	public GamePacketDecoder(GameService service, PlayerDetails player,IsaacCipher decryptor) {
		this.decryptor = decryptor;
		this.player = player;
		this.service = service;
	}

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
		switch (state) {

		case OPCODE:
			Revision rev = context.channel().attr(NetworkConstants.REVISION).get();
			decodeOpcode(in, rev);
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
		service.queuePacket(player,new GamePacket(packetOpCode, packetSize, readBytes));
	}

	private void decodePayload(ByteBuf in) {
		if (in.isReadable(packetType.size())) {
			queuePacket(in.readBytes(packetSize));
			state = State.OPCODE;
		}
	}

	private void decodeSize(ByteBuf in) {
		if (in.isReadable(packetSize)) {
			if (packetType == PacketHeader.VARIABLE_BYTE) {
				packetSize |= in.readUnsignedByte();
			} else if (packetType == PacketHeader.VARIABLE_SHORT) {
				packetSize |= in.readUnsignedByte();
				packetSize |= in.readUnsignedByte() << 8;
			}
			state = State.PAYLOAD;
		}
	}

	private void decodeOpcode(ByteBuf in, Revision rev) {
		if (in.isReadable()) {
			packetOpCode = (in.readByte() - decryptor.getKey() & 0xFF);
			packetSize = rev.getServerPacketSizes()[packetOpCode];

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
