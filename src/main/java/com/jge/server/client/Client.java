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
import java.util.Vector;

import com.jge.server.net.AppContext;
import com.jge.server.net.DataManager;
import com.jge.server.net.Task;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.MessageReceiver;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.space.game.Player;
import com.jge.server.space.game.achievements.Achievement;
import com.jge.server.space.game.achievements.Achievements;
import com.jge.server.space.game.rating.ClientRating;
import com.jge.server.utils.IdList;
import com.jge.server.utils.MappingUtil;

/**
 * Client abstraction
 * When a {@link ClientSession} enters the server it can be transformed into a 
 * {@link Client} which is an authenticated {@link ClientSession} also inserted into a {@link Space}
 *  
 */
public class Client implements MessageSender {
	/**
	 * Global client prefix name
	 */
	public static final String CLIENT_PREFIX = "Client_";
	
	/**
	 * Gets a {@link Client} with a session name
	 * @param sessionName the session name to get the {@link Client}
	 * @return a {@link Client} 
	 */
	public static Client getClientWithSession(String sessionName) {
		Client client = (Client)MappingUtil.getObject(Client.CLIENT_PREFIX + sessionName);
		
		return client;
	}
	
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Id of {@link Client}
	 */
	protected short id;
	
	/**
	 * Name of {@link Client}
	 */
	protected String name;
	
	/**
	 * Whether the {@link Client} is anonymous or not
	 */
	protected boolean anonymous;
	
	/**
	 * The client type
	 */
	protected ClientType clientType;
	
	/**
	 * Current status of this {@link Client}
	 */
	protected ClientStatus clientStatus = ClientStatus.INVITED;	
	
	/**
	 * It's the last time the client done something
	 * Useful when you want to track the user activity such as not more playing 
	 */
	private long lastTick;
	
	/**
	 * {@link ClientSession} associated with this {@link Client}
	 */
	private ClientSession clientSession;
	
	/**
	 * Global client rating
	 */
	private ClientRating ratings;
	
	/**
	 * Whether there is an associated player to this {@link Client} or not
	 * The gamePlayer can be null, so the client didn't enter any game yet
	 * If gamePlayer is null doesn't mean it is not in the server environment, 
	 * the client can be surfing in in any {@link Space} that is not a game like a lobby
	 * The client can be only in one game simultaneously
	 */
	private Player player;
	
	/**
	 * A list of {@link Space}'s the {@link Client} is inserted in
	 */
	private Vector<Space> spaceList = new Vector<Space>();

	/**
	 * A litener of {@link Client}'s actions
	 */
	private ClientListener clientListener;
	
	/**
	 * key: String - game id
	 * value: Achievements - A list of achievements 
	 */
	private Hashtable<Byte, Achievements> achievementsMap = new Hashtable<Byte, Achievements>();

	/**
	 * Constructor passing name
	 * @param name the name of this client
	 */
	public Client(String name) {
		this.name = name;
		this.id = getIdNameList().getNextId();
	}
	
	/**
	 * Sets up the client action tracking as a {@link Task} that will track user action and 
	 * manage client permanence in the server based on specific rules
	 * @param clientActionTracking the tracking object
	 */
	protected void setupClientActionTracking(ClientActionTracking clientActionTracking) {
		AppContext.getTaskManager().scheduleTask(clientActionTracking, ClientActionTracking.DELAY_CHECK_LOGGED);
	}
	
	/**
	 * Sets whether the {@link Client} is anonymous or not
	 * @param anonymous whether the {@link Client} is anonymous or not
	 */
	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	/**
	 * Gets the name of this {@link Client}
	 * @return the name of this {@link Client}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the email of this {@link Client}
	 * @return the email of this {@link Client}
	 */
	public String getEmail() {
		String replace = name.replace(Client.CLIENT_PREFIX, "");
		return replace;
	}
	
	/**
	 * Gets the session name of this client
	 * @return the session name of this client
	 */
	public String getSessionName() {
		if (this.clientSession == null) {
			throw new NullPointerException();
		}
		return clientSession.getName();
	}
	
	/**
	 * Sets the {@link ClientSession} associated with this client
	 * @param clientSession the {@link ClientSession} of this client
	 */
	public void setClientSession(ClientSession clientSession) {
		this.clientSession = clientSession;
	}
	
	/**
	 * Gets the {@link ClientSession} associated with this client
	 * @return the {@link ClientSession} associated with this client
	 */
	public ClientSession getClientSession() {
		return clientSession;
	}
	
	/**
	 * Gets the client status of this {@link Client}
	 * @return the client status of this {@link Client}
	 */
	public ClientStatus getClientStatus() {
		return clientStatus;
	}
	
	/**
	 * Gets the last tick of this client
	 * It's the last time the client done something
	 * @return the last tick of this client
	 */
	public long getLastTick() {
		return lastTick;
	}
	
	/**
	 * Sets the last tick of this client
	 * It's the last time the client done something
	 */
	public void setLastTick(long lastTick) {
		this.lastTick = lastTick;
	}
	
	/**
	 * Sets the id of this {@link Client}
	 * @param id the id of this {@link Client}
	 */
	public void setId(short id) {
		this.id = id;
	}

	/**
	 * Gets the id of this {@link Client}
	 * @return the id of this {@link Client}
	 */
	public short getId() {
		return id;
	}
	
	/**
	 * Gets the id name list for global mapping with {@link DataManager} mapping
	 * @return the {@link IdList}
	 */
	private IdList getIdNameList() {
		IdList nameList = (IdList)MappingUtil.getObject("ClientIdList");
		if (nameList == null) {
			nameList = new IdList();
			MappingUtil.addObject("ClientIdList", nameList);
		}
		return nameList;
	}
	
//	public void attribId() {
//		if (id == 0) {
//			this.id = getIdNameList().getNextId();
//		}
//	}

	/**
	 * Gets the client rating of this {@link Client}
	 * @return the {@link ClientRating} of this client
	 */
	public ClientRating getRatings() {
		return ratings;
	}
	
	/**
	 * Gets the {@link Achievements} map of this {@link Client}
	 * @return the {@link Achievements} map of this {@link Client}
	 */
	public Hashtable<Byte, Achievements> getAchievementsMap() {
		return achievementsMap;
	}
	
	/**
	 * Get an {@link Achievements} with a game Id
	 * @param gameId the game Id to get the {@link Achievements}
	 * @return an {@link Achievements} instance of a game
	 */
	public Achievements getAchievements(Byte gameId) {
		return achievementsMap.get(gameId);
	}
	
	/**
	 * Associate a gameId with an {@link Achievements} putting it into a map of achievements
	 * @param gameId the gameId to associate the {@link Achievement}
	 * @param achievements the {@link Achievement} to be associated with gameId
	 */
	public void putAchievements(Byte gameId, Achievements achievements) {
		achievementsMap.put(gameId, achievements);
	}
	
	/**
	 * Sets the client type of this {@link Client}
	 * @param clientType the client type of this {@link Client}
	 */
	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}
	
	/**
	 * Gets the client type of this {@link Client}
	 * @return the client type of this {@link Client}
	 */
	public ClientType getClientType() {
		return clientType;
	}
	
	/**
	 * Whether this {@link Client} is human or not
	 */
	public boolean isHuman() {
		return true;
	}

	/**
	 * Sends a message via this {@link Client}
	 * @param bufferMsg the {@link ByteBuffer} message to be sent
	 */
	public void sendMessage(ByteBuffer bufferMsg) {
		ClientSession session = getClientSession();
		if (session.isConnected()) {
			session.send(bufferMsg);
		}
	}
	
	/**
	 * Sets the player of this {@link Client}
	 * @param player the game player of this {@link Client}
	 */
	public void setPlayer(Player gamePlayer) {
		this.player = gamePlayer;
	}

	/**
	 * Gets the game player of this client
	 * @return the {@link Player} of this client
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Whether this {@link Client} is a {@link Player} or not
	 * @return whether this {@link Client} is a {@link Player} or not
	 */
	public boolean isPlayer() {
		return player != null;		
	}

//	public void getInfo(ServerMessage info) {
//		info.put(id);
//		// get player info if is player exists
//		if (isPlayer()) {
//			player.getInfo(info);
//		}
//	}
	
	/**
	 * Adds a given {@link Space} to {@link Client}'s space list
	 * @param space a given {@link Space} to be added to {@link Client}'s space list
	 */
	public void addSpace(Space space) {
		spaceList.add(space);
	}
	
	/**
	 * Disconnects this client from the server
	 * @param graceful whether the disconnection is graceful or not 
	 */
	public void disconnect(boolean graceful) {
		removeFromExistingSpaces();
		
		doAsyncDBLogout();
		
		String objectName = getName();
		MappingUtil.removeObject(objectName);
		
		clientSession = null;
	}
	
	/**
	 * Removes this {@link Client} from the connected {@link Space}'s
	 */
	public void removeFromExistingSpaces() {
		int sz = spaceList.size();
		for (int i = 0; i < sz; i++) {
			Space space = spaceList.get(i);
			space.clientDisconnected(this);
		}
	}

	/**
	 * Disconnect this user from external Data Base
	 */
	private void doAsyncDBLogout() {
		String email = getEmail();
		ClientLogoutTask asyncLogout = new ClientLogoutTask(email);
		long delay = 0;
		AppContext.getTaskManager().scheduleTask(asyncLogout, delay);
	}
	
	/**
	 * Sets the client listener
	 * @param clientListener the {@link ClientListener} to be set
	 */
	public void setClientListener(ClientListener clientListener) {
		this.clientListener = clientListener;
	}

	/**
	 * Add message receiver to this {@link Client}
	 * @param messageReceiver the message receiver to this {@link Client}
	 */
	public void addMessageReceiver(SpaceMessageReceiver messageReceiver) {
		clientListener.addMessageReceiver(messageReceiver);
	}
	
	/**
	 * Remove the message receiver to this {@link Client}
	 * @param spaceId the space id to be removed as a message receiver
	 */
	public void removeMessageReceiver(int spaceId) {
		String receiverId = MessageReceiver.MSG_RECEIVER_PREFIX + spaceId;
		clientListener.removeMessageReceiver(receiverId);
	}
}