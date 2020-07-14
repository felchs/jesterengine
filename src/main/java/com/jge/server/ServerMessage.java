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
package com.jge.server;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.Channel;
import com.jge.server.net.SessionProtocol;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.Space;
import com.jge.server.space.game.AutomaticPlayerInterface;
import com.jge.server.space.game.GameSpace;
import com.jge.server.space.game.Player;
import com.jge.server.space.game.RobotMessageReceiver;
import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.ObjectPropertiesFetcher;

/**
 * ServerMessage is a helper class to handle input types by the method
 * {@link #put(Object)} and send the inputed messages directly to clients
 * or via a {@link Channel}
 * 
 */
public class ServerMessage {
	
	/**
	 * The {@link Protocol} of the message
	 */
	private Protocol<?> protocol;
	
	/**
	 * The spaceId (from {@link Space#getId()} of the message
	 */
	private int spaceId;
	
	/**
	 * The attributes of the message.
	 * The attributes must be set via {@link #put(Object)} method
	 */
	private CopyOnWriteArrayList<Object> attributes = new CopyOnWriteArrayList<Object>();
	
	/**
	 * Constructor passing spaceId and protocol
	 * 
	 * @param spaceId the id of the space (from {@link Space#getId()}
	 * @param protocol the protocol of the message
	 */
	public ServerMessage(int spaceId, Protocol<?> protocol) {
		this.spaceId = spaceId;
		this.protocol = protocol;
	}
	
	/**
	 * Gets this message protocol
	 * @return this message protocol 
	 */
	public Protocol<?> getProtocol() {
		return protocol;
	}
	
	/**
	 * This method is used to put new objects to message
	 * The objects are automatically transformed into array of bytes when sending the message
	 * @param attribute
	 */
	public void put(Object attribute) {
		attributes.add(attribute);
	}

	/**
	 * Internal method which transform the inputed spaceId, protocol and attributes into a {@link ByteBuffer}
	 * @param destiny the destiny the message is designed to
	 * @return the {@link ByteBuffer} containing the message that must be sent to clients
	 */
	private ByteBuffer getMessage(ServerMessageDestiny destiny) {
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
		
		ByteBuffer msg = ByteBuffer.allocate(1 + 4 + 1 + capacity);
		switch (destiny) {
		case CHANNEL:
			msg.put(SessionProtocol.CHANNEL_MESSAGE.getId());
			break;

		case SESSION:
			msg.put(SessionProtocol.SESSION_MESSAGE.getId());
			break;
		}
		
		msg.putInt(spaceId); // 4 bytes
		msg.put(protocol.getIdAsBytes()); // 1 byte

		if (byteArrays != null) {
			int sz = byteArrays.length;
			for (int i = 0; i < sz; i++) {
				byte[] bs = byteArrays[i];
				msg.put(bs);
			}
		}

		msg.rewind();
		
		msg = Obfuscator.get().doObfuscation(msg);
		
		msg = Compressor.get().doCompression(msg);

		return msg;
	}

	/**
	 * Sends a message to a given {@link Client}
	 * The message is composed by a protocol, spaceId, and bytes message that are
	 * sent to client via {@link ClientSession#send(ByteBuffer)}
	 * @param client the {@link Client} which message must be sent to
	 */
	public void send(Client client) {
		ClientSession session = client.getClientSession();
		ByteBuffer message = getMessage(ServerMessageDestiny.SESSION);
		session.send(message);
	}

	/**
	 * Sends a message to a given {@link Channel}
	 * The message is composed by a protocol, spaceId, and bytes message that are
	 * sent to channel via {@link Channel#send(ByteBuffer)}
	 * @param channel the {@link Channel} which message must be sent to
	 */
	public void send(Channel channel) {
		channel.send(null, getMessage(ServerMessageDestiny.CHANNEL));
	}

	/**
	 * The same behavior of {@link ServerMessage#send(Client)} but now you send a message to many clients
	 * @param clientNames the clients {@link Client} which message must be sent to
	 */
	public void sendClientMessage(List<String> clientNames) {
		Iterator<String> clientNamesIt = clientNames.iterator();
		while (clientNamesIt.hasNext()) {
			String clientName = clientNamesIt.next();
			Client client = (Client)AppContext.getDataManager().getBinding(clientName);
			DGSLogger.log("ServerMessage.sendClientMessage: " + client.getName() + ", " + client.getEmail() + ", " + client.getId());
			ByteBuffer messageToClients = getMessage(ServerMessageDestiny.SESSION);
			client.sendMessage(messageToClients);
		}
	}
	
	/**
	 * The same behavior of {@link ServerMessage#send(Client)} but now you send a 
	 * message to many clients via players that are in a given {@link GameSpace}
	 * The players to send the message are from {@link GameSpace#getPlayerNames()}
	 * @param gameSpace the {@link GameSpace} to get the the players to send the message to 
	 */
	public void sendToPlayers(GameSpace gameSpace) {
		CopyOnWriteArrayList<String> playerNames = gameSpace.getPlayerNames();
		sendMessageWithPlayerNames(playerNames, gameSpace);
		CopyOnWriteArrayList<String> automaticPlayerNames = gameSpace.getAutomaticPlayerNames();
		sendMessageWithAutomaticPlayerNames(automaticPlayerNames, gameSpace);
	}

	/**
	 * Sends a message to a list of players from a given {@link GameSpace}
	 * @param playerNames the players which the messages must be sent to
	 * @param gameSpace the {@link GameSpace} to get a {@link Player} to send a message
	 */
	private void sendMessageWithPlayerNames(List<String> playerNames, GameSpace gameSpace) {
		Iterator<String> playerIt = playerNames.iterator();
		while (playerIt.hasNext()) {
			String playerName = playerIt.next();
			Player player = gameSpace.getPlayerWithPlayerName(playerName);
			if (player != null) {
				sendToPlayer(player);
			}
		}
	}
	
	/**
	 * Sends a message to a list of automatic players from a given {@link GameSpace}
	 * @param playerNames the players which the messages must be sent to
	 * @param gameSpace the {@link GameSpace} to get a {@link AutomaticPlayerInterface} to send a message
	 */
	private void sendMessageWithAutomaticPlayerNames(List<String> playerNames, GameSpace gameSpace) {
		Iterator<String> playerIt = playerNames.iterator();
		while (playerIt.hasNext()) {
			String playerName = playerIt.next();
			AutomaticPlayerInterface player = gameSpace.getAutomaticPlayerWithPlayerName(playerName);
			player.receiveMessage(getMessage(ServerMessageDestiny.SESSION));
		}
	}

	/**
	 * Sends a message to a given {@link Player}
	 * The message is composed by a protocol, spaceId, and bytes message that are
	 * sent to client via {@link ClientSession#send(ByteBuffer)}
	 * @param player the {@link Player} which message must be sent to
	 */
	public void sendToPlayer(Player player) {
		if (player.isRobot()) {
			RobotMessageReceiver robotReceiver = (RobotMessageReceiver)player;
			ByteBuffer message = getMessage(ServerMessageDestiny.SESSION);
			robotReceiver.receiveMessage(message);
		} else {
			ClientSession session = player.getClientSession();
			if (session.isConnected()) {
				ByteBuffer message = getMessage(ServerMessageDestiny.SESSION);
				session.send(message);
			}
		}
	}
}