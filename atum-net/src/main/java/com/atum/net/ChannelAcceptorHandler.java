package com.atum.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.channel.ChannelHandler.Sharable;
/**
*
* @author Martin
*/
@Sharable
public class ChannelAcceptorHandler extends SimpleChannelInboundHandler<Object> {


	@Override
	protected void channelRead0(ChannelHandlerContext context, Object obj) throws Exception {
		PlayerChannel session = context.channel().attr(NetworkConstants.PLAYER_CHANNEL).get();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		PlayerChannel session = ctx.channel().attr(NetworkConstants.PLAYER_CHANNEL).get();
		
	}

}
