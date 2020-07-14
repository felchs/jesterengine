package com.jge.server;

import java.nio.ByteBuffer;

/**
 * When the user doesn't want to do any obfuscation this class is used to pass by data
 * 
 */
public class NoObfuscatorImpl extends Obfuscator {

	/**
	 * Do nothing with the message returning the same message
	 * return the same message no obfuscated
	 */
	@Override
	public ByteBuffer doObfuscation(ByteBuffer message) {
		return message;
	}

}
