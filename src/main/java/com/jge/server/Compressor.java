package com.jge.server;

import java.nio.ByteBuffer;

import com.jge.server.net.AppContext;

/**
 * Interfaces that handle compression of messages
 * 
 */
public abstract class Compressor {

	/**
	 * The message compressor instance
	 */
	private static Compressor currInstance;
	
	/**
	 * Gets a compressor instance
	 * @return an {@link Compressor} instance
	 */
	public static Compressor get() {
		if (currInstance == null) {
			String compressorType = AppContext.getProperty("com.jge.server.net.compressor", "default");
			if (compressorType.equalsIgnoreCase("default")) {
				currInstance = new NoCompressorImpl();
			}
		}
		return currInstance;
	}
	
	/**
	 * If is there a need to replace the current compressor  at runtime just change it by this method 
	 * @param compressor the compressor to be set as current instance
	 */
	public static void changeCompressor(Compressor compressor) {
		currInstance = compressor;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Do an compression at an {@link ByteBuffer}
	 * @param message the message to be compressed
	 * @return the message compressed
	 */
	public abstract ByteBuffer doCompression(ByteBuffer message);
}