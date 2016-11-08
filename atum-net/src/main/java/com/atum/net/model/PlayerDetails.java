package com.atum.net.model;

import io.netty.channel.ChannelHandlerContext;

public class PlayerDetails {

	public enum Rights {
		NORMAL(0), MOD(1), ADMIN(2), OWNER(3);

		private int rights;

		Rights(int rights) {
			this.rights = rights;
		}

		public int value() {
			return rights;
		}

	}

	private Rights rights = Rights.NORMAL;
	private String username;
	private String password;
	private String uuid;
	private ActionSender actionSender;
	private ChannelHandlerContext channelCtx;

	public PlayerDetails(ChannelHandlerContext context, String username, String password, String uuid) {
		this.channelCtx = context;
		this.username = username;
		this.password = password;
		this.uuid = uuid;
	}

	public PlayerDetails(PlayerDetails details) {
		this.channelCtx = details.getChannelContext();
		this.username = details.getName();
		this.password = details.getPassword();
		this.uuid = details.uuid;
		this.actionSender = details.getActionSender();
	}

	private ChannelHandlerContext getChannelContext() {
		return channelCtx;
	}

	public Rights getRights() {
		return rights;
	}

	public String getName() {
		return username;
	}
	
	public ActionSender getActionSender(){
		return actionSender;
	}
	
	public void write(GamePacket packet){
		channelCtx.writeAndFlush(packet.getPayload());
	}

	public String getPassword() {
		return password;
	}

	public String getUuid() {
		return uuid;
	}

	public void setActionSender(ActionSender actionSender) {
		this.actionSender = actionSender;
	}
}
