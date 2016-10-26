package com.atum.net;

import io.netty.channel.ChannelHandler.Sharable;

import com.atum.net.codec.LoginDecoder;
import com.atum.net.codec.LoginState;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

/**
 *
 * @author Martin
 */
@Sharable
public class PipelineInitializer extends ChannelInitializer<SocketChannel> {

	public static final AttributeKey<PlayerChannel> PLAYER_CHANNEL = AttributeKey.newInstance("PLAYER_CHANNEL");
	public static final AttributeKey<LoginState> LOGIN_STATE = AttributeKey.newInstance("LoginState");

	private final ChannelAcceptorHandler ACCECPTOR_HANDLER = new ChannelAcceptorHandler();
	private final GameService gameService;
	
	public PipelineInitializer(GameService gameService){
		this.gameService = gameService;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		final ChannelPipeline pipeline = channel.pipeline();

		channel.attr(PLAYER_CHANNEL).setIfAbsent(new PlayerChannel(channel));
		channel.attr(LOGIN_STATE).setIfAbsent(LoginState.HANDSHAKE);

		pipeline.addLast("timeout", new IdleStateHandler(10000, 0, 0));
		pipeline.addLast("login-header-decoder", new LoginDecoder(gameService));
		// pipeline.addLast("packet-encoder", new PacketEncoder());

		pipeline.addLast("channel-handler", ACCECPTOR_HANDLER);
	}

}
