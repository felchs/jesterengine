package com.jge.server;

import java.nio.ByteBuffer;

/**
 * When the user doesn't want to do any compression this class is used to pass by data
 * 
 */
public class NoCompressorImpl extends Compressor {

	/**
	 * Do nothing with the message returning the same message
	 * return the same message no obfuscated
	 */
	@Override
	public ByteBuffer doCompression(ByteBuffer message) {
		return message;
	}

}
