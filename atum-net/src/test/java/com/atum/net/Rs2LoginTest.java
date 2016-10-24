package com.atum.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Rs2LoginTest {

	@BeforeClass
	public static void testSetup() throws IOException {
		NettyBootstrap.main(null);
	}

	@Test
	public void testInvalidPasswordLength() throws IOException {
		Assert.assertEquals(serverLogin(14, 16, 255, 317, 0, 10, "Martin", "mart"), 3);
	}

	@Test
	public void testMagicNum() throws IOException {
		Assert.assertEquals(serverLogin(14, 16, 250, 317, 0, 10, "Martin", "mart"), 11);
	}

	@Test
	public void testMemory() throws IOException {
		Assert.assertEquals(serverLogin(14, 16, 255, 317, 5, 10, "Martin", "mart"), 11);
	}

	@Test
	public void testVersion() throws IOException {
		Assert.assertEquals(serverLogin(14, 16, 255, 377, 0, 10, "Martin", "mart"), 11);
	}

	@Test
	public void testConnType() throws IOException {
		Assert.assertEquals(serverLogin(14, 29, 255, 317, 0, 10, "Martin", "mart"), 11);
	}

	public int serverLogin(int requestType, int connType, int magic, int version, int memory, int rsaHeader, String user, String password) throws IOException {

		Socket s = null;
		try {
			s = new Socket(InetAddress.getByName("127.0.0.1"), 43594);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		ByteBuf buffer = Unpooled.buffer(82);
		buffer.writeByte(requestType);
		// username hash junk.
		buffer.writeByte(14);
		// connection type, new connection
		buffer.writeByte(connType);
		int encryptedLoginBlockSize = 77;
		buffer.writeByte(encryptedLoginBlockSize);
		buffer.writeByte(magic);
		buffer.writeShort(version);
		buffer.writeByte(memory);
		for (int i = 0; i < 9; i++)
			buffer.writeInt(i);
		// expected size check, 41 is the amount of bytes written so far
		int rsaLen = encryptedLoginBlockSize - 41;
		buffer.writeByte(rsaLen);
		buffer.writeByte(rsaHeader);
		// Isaac keys
		buffer.writeLong(998);
		buffer.writeLong(999);
		// uid
		buffer.writeInt(123456);
		buffer.writeBytes(user.getBytes());
		buffer.writeByte(10);
		buffer.writeBytes(user.getBytes());
		buffer.writeByte(10);
		buffer.writeBytes(password.getBytes());
		buffer.writeByte(10);
		s.getOutputStream().write(buffer.array(), 0, buffer.writerIndex());
		s.getOutputStream().flush();

		byte[] arr = new byte[17];
		s.getInputStream().read(arr, 0, arr.length);
		ByteBuf buf = Unpooled.copiedBuffer(arr);

		buf.readLong();
		buf.readByte();
		long key = buf.readLong();
		int opCode = s.getInputStream().read();
		s.close();
		System.out.println("op " + opCode);
		return opCode;

	}
}
