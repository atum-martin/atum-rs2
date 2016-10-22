package com.atum.net;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
/**
*
* @author Martin
*/
public class LoginHeaderDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> outStream) throws Exception {
		LoginState state = context.channel().attr(PipelineInitializer.LOGIN_STATE).get();
		switch (state) {
		case HANDSHAKE:
			
			break;
		case HEADER:

			break;
		case LOGIN_BLOCK:

			break;
		default:
			throw new IllegalStateException("Invalid state during login decoding.");
		}
	}

}
