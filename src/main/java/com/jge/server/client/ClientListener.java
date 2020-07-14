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
import java.util.Hashtable;
import java.util.Set;

import com.jge.server.net.ClientSessionListener;
import com.jge.server.space.MessageReceiver;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.utils.DGSLogger;

/**
 * All messages that came a {@link Client} is listened by this implementation
 * 
 */
public class ClientListener implements ClientSessionListener {
	/**
	 * A map of message receivers
	 * When this listener listen a message it follows to receivers for further processing
	 */
	private Hashtable<String, MessageReceiver> receivers = new Hashtable<String, MessageReceiver>();
	
	/**
	 * {@link Client} to listen to
	 */
	private Client client;
	
	/**
	 * Constructor passing field
	 * @param client the {@link Client} to listen to
	 */
	public ClientListener(Client client) {
		this.client = client;
	}
	
	/**
	 * Adds a new message receiver
	 * The {@link MessageReceiver} is mapped on {@link ClientListener#receivers} map 
	 * with key being the message receiver id
	 * @param messageReceiver the {@link MessageReceiver} to be added
	 */
	public void addMessageReceiver(MessageReceiver messageReceiver) {
		DGSLogger.log("Add message receiver: " + messageReceiver.toString() + " Id: " + messageReceiver.getId());
		if (receivers.containsKey(messageReceiver.getId())) {
			DGSLogger.log("Receiver already added: " + messageReceiver.getId());
			return;
		}
		receivers.put(messageReceiver.getId(), messageReceiver);
	}
	
	/**
	 * Removes a {@link MessageReceiver} from mapping 
	 * @param messageReceiver the mapped {@link MessageReceiver} to be removed
	 */
	public void removeMessageReceiver(MessageReceiver messageReceiver) {
		DGSLogger.log("Remove message receiver: " + messageReceiver.toString());
		receivers.remove(messageReceiver.getId());
	}

	/**
	 * Removes a {@link MessageReceiver} from mapping with receiver id 
	 * @param receiverId the id of the mapped {@link MessageReceiver} to be removed
	 */
	public void removeMessageReceiver(String receiverId) {
		DGSLogger.log("Remove message receiver str: " + receiverId);
		receivers.remove(receiverId);
	}

	/**
	 * Listens to a message and follows the mapped receivers
	 * @param message a {@link ByteBuffer} message to be handled
	 */
	public void receivedMessage(ByteBuffer message) {
		int spaceId = message.getInt();
		String spaceMsgReceiverId = SpaceMessageReceiver.MSG_RECEIVER_PREFIX + spaceId;
		
		MessageReceiver msgReceiverRef = receivers.get(spaceMsgReceiverId);
	
		client.setLastTick(System.currentTimeMillis());
		
		if (msgReceiverRef == null) {
			DGSLogger.log("ClientListener.receivedMessage, msgReceiver, spaceId: " + spaceId + " is Null");
			Set<String> keySet = receivers.keySet();
			for (String key : keySet) {
				DGSLogger.log("ClientListener.receivedMessage key: " + key + ", id: " + receivers.get(key).getId());
			}
		} else {
			if (msgReceiverRef.isActive()) {
				MessageSender sender = (MessageSender)client;
				msgReceiverRef.receivedMessage(sender, message);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void disconnected(boolean graceful) {
		DGSLogger.log("ClientListener.disconnected, id: " + client.getId() + ", name: " + client.getName());
		client.disconnect(graceful);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void reconnect() {
		client.removeFromExistingSpaces();
	}
}
