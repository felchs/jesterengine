package com.jge.server;

import java.nio.ByteBuffer;

import com.jge.server.net.AppContext;

/**
 * Interfaces to handle obfuscation of messages
 * 
 */
public abstract class Obfuscator {

	/**
	 * The obfuscator instance
	 */
	private static Obfuscator currInstance;
	
	/**
	 * Gets an obfuscator instance
	 * @return an {@link Obfuscator} instance
	 */
	public static Obfuscator get() {
		if (currInstance == null) {
			String obfuscatorType = AppContext.getProperty("com.jge.server.net.obfuscator", "default");
			if (obfuscatorType.equalsIgnoreCase("default")) {
				currInstance = new NoObfuscatorImpl();
			}
		}
		return currInstance;
	}
	
	/**
	 * If is there a need to replace the current obfuscator at runtime just change it by this method 
	 * @param obfuscator the obfuscator to be set as current instance
	 */
	public static void changeObfuscator(Obfuscator obfuscator) {
		currInstance = obfuscator;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Do an obfuscation at an {@link ByteBuffer}
	 * @param message the message to be obfuscated
	 * @return the message obfuscated
	 */
	public abstract ByteBuffer doObfuscation(ByteBuffer message);
}