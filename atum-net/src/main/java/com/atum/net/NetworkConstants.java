package com.atum.net;

import com.atum.net.codec.LoginState;
import com.atum.net.model.Revision;

import io.netty.util.AttributeKey;

public class NetworkConstants {

	public static final AttributeKey<Revision> REVISION = AttributeKey.newInstance("REVISION");
	public static final AttributeKey<PlayerChannel> PLAYER_CHANNEL = AttributeKey.newInstance("PLAYER_CHANNEL");
	public static final AttributeKey<LoginState> LOGIN_STATE = AttributeKey.newInstance("LoginState");

}
