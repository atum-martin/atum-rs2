package com.atum.net;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 *
 * @author Martin
 */
public class LoginHeaderDecoder extends ByteToMessageDecoder {

	private static final int GAME_SEVER_OPCODE = 14;
	private static final int FILE_SERVER_OPCODE = 15;
	private static final int NEW_CONNECTION_OPCODE = 16;
	private static final int RECONNECTION_OPCODE = 18;

	private int encryptedLoginBlockSize;

	private static final Random random = new SecureRandom();

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> outStream) throws Exception {
		LoginState state = context.channel().attr(PipelineInitializer.LOGIN_STATE).get();
		switch (state) {
		case HANDSHAKE:
			handleHandshake(context, buffer);
			break;
		case HEADER:
			handleLoginHeader(context, buffer);
			break;
		case LOGIN_BLOCK_HEADER:
			handleLoginBlockHeader(context, buffer);
			break;
		case LOGIN_BLOCK:
			handleLoginBlock(context, buffer);
			break;
		default:
			throw new IllegalStateException("Invalid state during login decoding.");
		}
	}

	private void sendFinalResponse(ChannelHandlerContext context, LoginResponse response) {
		ByteBuf buffer = Unpooled.buffer(1);
		buffer.writeByte(response.getOpcode());
		context.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
	}

	private void handleHandshake(ChannelHandlerContext context, ByteBuf buffer) {
		if (buffer.readableBytes() < 2) {
			return;
		}
		int requestOpCode = buffer.readUnsignedByte();
		int nameHash = buffer.readUnsignedByte();

		if (requestOpCode != GAME_SEVER_OPCODE && requestOpCode != FILE_SERVER_OPCODE) {
			sendFinalResponse(context, LoginResponse.INVALID_LOGIN_SERVER);
			return;
		}

		ByteBuf buf = Unpooled.buffer(19);
		buf.writeLong(0);
		buf.writeByte(0);
		buf.writeLong(random.nextLong());
		context.writeAndFlush(buf);
		context.channel().attr(PipelineInitializer.LOGIN_STATE).set(LoginState.HEADER);
	}

	private void handleLoginHeader(ChannelHandlerContext context, ByteBuf buffer) {
		if (buffer.readableBytes() < 2) {
			return;
		}
		int connectionType = buffer.readUnsignedByte();

		if (connectionType != NEW_CONNECTION_OPCODE && connectionType != RECONNECTION_OPCODE) {
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}

		context.channel().attr(PipelineInitializer.LOGIN_STATE).set(LoginState.LOGIN_BLOCK_HEADER);

	}

	private void handleLoginBlockHeader(ChannelHandlerContext context, ByteBuf buffer) {
		if (buffer.readableBytes() < 78) {
			return;
		}
		// usually 77 according to docs.
		encryptedLoginBlockSize = buffer.readUnsignedByte();

		if (encryptedLoginBlockSize < buffer.readableBytes()) {
			return;
		}
		context.channel().attr(PipelineInitializer.LOGIN_STATE).set(LoginState.LOGIN_BLOCK);
	}

	private void handleLoginBlock(ChannelHandlerContext context, ByteBuf buffer) {

	}
}
