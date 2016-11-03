package com.atum.net.codec;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.atum.net.ByteBufUtils;
import com.atum.net.GameService;
import com.atum.net.IsaacCipher;
import com.atum.net.NetworkConstants;
import com.atum.net.model.PlayerDetails;
import com.atum.net.model.Revision;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.apache.log4j.Logger;

/**
 *
 * @author Martin
 */
public class LoginDecoder extends ByteToMessageDecoder {

	private static final int GAME_SEVER_OPCODE = 14;
	private static final int FILE_SERVER_OPCODE = 15;
	private static final int NEW_CONNECTION_OPCODE = 16;
	private static final int RECONNECTION_OPCODE = 18;

	@SuppressWarnings("unused")
	private static final BigInteger RSA_MODULUS = new BigInteger(
			"94904992129904410061849432720048295856082621425118273522925386720620318960919649616773860564226013741030211135158797393273808089000770687087538386210551037271884505217469135237269866084874090369313013016228010726263597258760029391951907049483204438424117908438852851618778702170822555894057960542749301583313");
	@SuppressWarnings("unused")
	private static final BigInteger RSA_EXPONENT = new BigInteger(
			"72640252303588278644467876834506654511692882736878142674473705672822320822095174696379303197013981434572187481298130748148385818094460521624198552406940508805602215708418094058951352076283100448576575511642453669107583920561043364042814766866691981132717812444681081534760715694225059124574441435942822149161");

	private int encryptedLoginBlockSize;
	private GameService gameService;

	private static final Random random = new SecureRandom();

	public LoginDecoder(GameService gameService) {
		this.gameService = gameService;
	}
	
	private Logger logger = Logger.getLogger(LoginDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> outStream) throws Exception {
		LoginState state = context.channel().attr(NetworkConstants.LOGIN_STATE).get();
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
		logger.debug("loginDecoding failed: "+response.getOpcode());
		context.writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE);
	}

	private void handleHandshake(ChannelHandlerContext context, ByteBuf buffer) {
		if (buffer.readableBytes() < 2) {
			return;
		}
		int requestOpCode = buffer.readUnsignedByte();
		@SuppressWarnings("unused")
		int nameHash = buffer.readUnsignedByte();

		if (requestOpCode != GAME_SEVER_OPCODE && requestOpCode != FILE_SERVER_OPCODE) {
			logger.debug("Invalid request opcode "+requestOpCode);
			sendFinalResponse(context, LoginResponse.INVALID_LOGIN_SERVER);
			return;
		}

		ByteBuf buf = Unpooled.buffer(19);
		buf.writeLong(0);
		buf.writeByte(0);
		long randomLong = random.nextLong();
		buf.writeLong(randomLong);
		context.writeAndFlush(buf);
		context.channel().attr(NetworkConstants.LOGIN_STATE).set(LoginState.HEADER);
	}

	private void handleLoginHeader(ChannelHandlerContext context, ByteBuf buffer) {
		if (buffer.readableBytes() < 1) {
			return;
		}
		int connectionType = buffer.readUnsignedByte();

		if (connectionType != NEW_CONNECTION_OPCODE && connectionType != RECONNECTION_OPCODE) {
			logger.debug("Invalid connection type "+connectionType);
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}

		context.channel().attr(NetworkConstants.LOGIN_STATE).set(LoginState.LOGIN_BLOCK_HEADER);
	}

	private void handleLoginBlockHeader(ChannelHandlerContext context, ByteBuf buffer) {
		if (buffer.readableBytes() < 78) {
			return;
		}
		// usually 77 according to docs.
		encryptedLoginBlockSize = buffer.readUnsignedByte();
		context.channel().attr(NetworkConstants.LOGIN_STATE).set(LoginState.LOGIN_BLOCK);
	}

	private void handleLoginBlock(ChannelHandlerContext context, ByteBuf buffer) {
		if (!buffer.isReadable(encryptedLoginBlockSize)) {
			return;
		}
		int magicValue = buffer.readUnsignedByte();
		if (magicValue != 255) {
			logger.debug("Invalid magic value "+magicValue);
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}

		int clientVersion = buffer.readUnsignedShort();
		Revision revision = gameService.getClientRevision(""+clientVersion);
		if (revision == null) {
			logger.debug("Invalid version "+clientVersion);
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}

		int memoryVersion = buffer.readUnsignedByte();

		if (memoryVersion != 0 && memoryVersion != 1) {
			logger.debug("Invalid memory version "+memoryVersion);
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}

		int[] crcs = new int[9];

		for (int index = 0; index < crcs.length; index++) {
			crcs[index] = buffer.readInt();
		}

		int expectedSize = buffer.readUnsignedByte();
		
		if (expectedSize != encryptedLoginBlockSize - 41) {
			logger.debug("Invalid rsa length "+expectedSize+" "+(encryptedLoginBlockSize - 41));
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}

		//byte[] rsaBytes = new byte[encryptedLoginBlockSize - 41];
		//buffer.readBytes(rsaBytes);

		//ByteBuf rsaBuffer = Unpooled.wrappedBuffer(new BigInteger(rsaBytes).modPow(RSA_EXPONENT, RSA_MODULUS).toByteArray());

		int rsa = buffer.readUnsignedByte();
		if (rsa != 10) {
			logger.debug("Invalid rsa byte "+rsa);
			sendFinalResponse(context, LoginResponse.LOGIN_SERVER_REJECTED_SESSION);
			return;
		}
		long clientHalf = buffer.readLong();
		long serverHalf = buffer.readLong();

		int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };

		IsaacCipher decryptor = new IsaacCipher(isaacSeed);

		for (int index = 0; index < isaacSeed.length; index++) {
			isaacSeed[index] += 50;
		}

		IsaacCipher encryptor = new IsaacCipher(isaacSeed);

		@SuppressWarnings("unused")
		int uid = buffer.readInt();
		
		String uuid = ByteBufUtils.readJagString(buffer);
		String username = ByteBufUtils.readJagString(buffer);
		String password = ByteBufUtils.readJagString(buffer);

		logger.debug("uuid: "+uuid);
		logger.debug("username: "+username);
		logger.debug("password: "+password);
		
		if (password.length() < 6 || password.length() > 20 || username.isEmpty() || username.length() > 12) {
			logger.debug("Invalid password length "+password);
			sendFinalResponse(context, LoginResponse.INVALID_CREDENTIALS);
			return;
		}
		
		PlayerDetails player = gameService.registerPlayer(username,password,uuid);
		
		
		context.pipeline().addLast("game-packet-encoder", new GamePacketEncoder(encryptor));
		context.pipeline().replace("login-header-decoder","game-packet-decoder", new GamePacketDecoder(gameService,decryptor));

		// out.add(new LoginDetailsPacket(ctx, username, password, uuid, encryptor, decryptor));
	}
}
