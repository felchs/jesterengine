package com.jge.server.net.netty;

import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Netty utility class
 *  
 */
public class JesterNettyUtils {

	/**
	 * Gets the host with a given {@link ChannelHandlerContext}
	 * @param ctx the Netty {@link ChannelHandlerContext}
	 * @return the host
	 */
	public static String getHostWithContext(ChannelHandlerContext ctx) {
		InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		if (remoteAddress == null) {
			return null;
		}
		String host = remoteAddress.getHostName();
		return host;
	}
	
	/**
	 * Gets a string UTF-8 message with a {@link ByteBuffer}  message
	 * @param message the {@link ByteBuffer} incoming message
	 * @return a UTF-8 string
	 */
	public static String readStringWithMessage(ByteBuffer message) {
		short strLen = message.getShort();
		byte[] dstBytes = new byte[strLen];
		message.get(dstBytes);
		
		String string = null;
		try {
			string = new String(dstBytes, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return string;
	}
}
