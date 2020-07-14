/*
 * Jester Game Engine is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Jester Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author: orochimaster
 * @email: orochimaster@yahoo.com.br
 */
package com.jge.server.client;

import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.LocalConnection;
import com.jge.server.LocalMessage;
import com.jge.server.LocalMessageIdx;
import com.jge.server.Protocol;
import com.jge.server.net.AppContext;
import com.jge.server.net.SessionProtocol;
import com.jge.server.net.Task;
import com.jge.server.net.TaskManager;
import com.jge.server.space.game.AutomaticPlayerInterface;
import com.jge.server.space.game.Player;
import com.jge.server.utils.ObjectPropertiesFetcher;

/**
 * An Asynchronous message from a Robot {@link Player} or an {@link AutomaticPlayerInterface} needs to send a
 * message likewise any Client. This class helps the creation of these messages 
 */
public class ClientMessage {
	/**
	 * The space id the message is destined to
	 */
	private final int spaceId;
	
	/**
	 * The protocol of the message
	 */
	private final Protocol<?> protocol;
	
	/**
	 * The attributes of the message
	 */
	private CopyOnWriteArrayList<Object> attributes = new CopyOnWriteArrayList<Object>();
	
	/**
	 * Constructor passing params
	 * @param spaceId the space Id the message is destined to
	 * @param protocol the message's protocol
	 */
	public ClientMessage(int spaceId, Protocol<?> protocol) {
		this.spaceId = spaceId;
		this.protocol = protocol;
	}
	
	/**
	 * Puts a new object into this message
	 * @param attribute the object to be set as message
	 */
	public void put(Object attribute) {
		attributes.add(attribute);
	}
	
	/**
	 * Process the spaceId, protocol and {@link ByteBuffer} message to be sent
	 * @return the {@link ByteBuffer} message to be sent
	 */
	public ByteBuffer getMessageToServer() {
		byte[][] byteArrays = null;
		int capacity = 0;
		
		if (!attributes.isEmpty()) {
			int sz = attributes.size();
			byteArrays = new byte[sz][0];
			for (int i = 0; i < sz; i++) {
				ObjectPropertiesFetcher objecProperties = ObjectPropertiesFetcher.getObjectProperties(attributes.get(i));	
				byte[] bytes = objecProperties.getBytes();			
				byteArrays[i] = bytes;
				capacity += objecProperties.getCapacity();
			}
		}
		
		ByteBuffer msg = ByteBuffer.allocate(4 + 1 + capacity);
		msg.putInt(spaceId);  // 4 bytes
		msg.put(protocol.getIdAsBytes());      // 1 byte

		if (byteArrays != null) {
			int sz = byteArrays.length;
			for (int i = 0; i < sz; i++) {
				byte[] bs = byteArrays[i];
				msg.put(bs);
			}
		}

		return msg;
	}

	/**
	 * Gets a processed message bb, and send it  
	 * @param bb the processed message to be sent 
	 * @param doAsync if the message goes in this thread or another (if async)
	 */
	private void sendMessage(ByteBuffer bb, boolean doAsync) {
		byte[] message = bb.array();
		ByteBuffer msg = ByteBuffer.allocate(1 + message.length);
		msg.put(SessionProtocol.SESSION_MESSAGE.getIdAsBytes());
		msg.put(message);
		msg.flip();
		byte[] messageArray = msg.array();
		ByteBuffer buffer = ByteBuffer.allocate(2 + messageArray.length);
		buffer.putShort((short) messageArray.length);
		buffer.put(messageArray);
		buffer.flip();
		final byte[] b = buffer.array();
		
		if (doAsync) {
			String bindingName = "LocalConnection" + spaceId;
			
			long messageIdx = getLocalMessageIdx(bindingName);
			final LocalMessage localMessage = new LocalMessage(spaceId, messageIdx, b);
			
			TaskManager taskManager = AppContext.getTaskManager();
			taskManager.startTask(
				new Task() {
					public void run() {
						LocalConnection.get().putOnQueueSender(localMessage);
					}
				}
			);
		} else {
			long messageIdx = -1;
			final LocalMessage localMessage = new LocalMessage(spaceId, messageIdx, b);
			LocalConnection.get().putOnQueueSender(localMessage);
		}
	}

	/**
	 * Gets the message index with binding name
	 * @param bindingName the binding name to get message index
	 * @return the index of the message
	 */
	private long getLocalMessageIdx(String bindingName) {
		LocalMessageIdx localMessageManaged = null;
		try {
			localMessageManaged = (LocalMessageIdx) AppContext.getDataManager().getBinding(bindingName);
		} catch (Exception e) {
			localMessageManaged = new LocalMessageIdx();
			AppContext.getDataManager().setBinding(bindingName, localMessageManaged);
		}
		if (localMessageManaged == null)
		{
			localMessageManaged = new LocalMessageIdx();
			AppContext.getDataManager().setBinding(bindingName, localMessageManaged);
		}
		
		long messageIdx = localMessageManaged.incMessageIdx();
		return messageIdx;
	}
	
	/**
	 * Process and sends this {@link ClientMessage}
	 */
	public void send() {
		send(true);
	}
	
	/**
	 * Process and sends this {@link ClientMessage}
	 * @param doAsync if the message goes in this thread or another (if async)
	 */
	public void send(boolean doAsync) {
		final ByteBuffer bb = getMessageToServer();
		sendMessage(bb, doAsync);
	}
}