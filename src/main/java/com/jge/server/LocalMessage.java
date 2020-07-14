package com.jge.server;

/**
 * Helper structure to put the messages info in queue in
 * {@link LocalConnection} class.
 * The LocalMessage must be queued by the method
 * {@link LocalConnection#putOnQueueSender(LocalMessage)}
 * 
 */

public class LocalMessage {
	int game;
	long messageIdx;
	byte[] message;
	
	/**
	 * Constructor passing fields
	 * 
	 * @param game the game id the message is destined to
	 * @param messageIdx the global index of messages 
	 * @param message the message itself as array of bytes
	 */
	public LocalMessage(int game, long messageIdx, byte[] message) {
		this.game = game;
		this.messageIdx = messageIdx;
		this.message = message;
	}
}