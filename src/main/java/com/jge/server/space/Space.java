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
package com.jge.server.space;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.Channel;
import com.jge.server.net.session.ClientSession;
import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

/**
 * A Space is a place where you put clients and setup rules
 * eg. a Lobby, a Game, a Room within a game
 * 
 * Inner a space there are channels: {@link Channel} for massive message
 * sending. You can handle client join via {@link #joinChannels(ClientSession)}
 * Message sending can be handled using methods: 
 * {@link #sendMessage(Channel, ClientSession, ByteBuffer)}
 * {@link #send(ClientSession, ByteBuffer)} 
 * 
 * The message receiving is handled in a space by {@link SpaceMessageReceiver}.
 * You must override the a specific MessageReceiver implementing the rules
 * of message receiving you want
 *
 */
public abstract class Space {	
	/**
	 * The unique id of this {@link Space}
	 */
	protected int id;
	
	/**
	 * The global state of this {@link Space}
	 */
	private SpaceState spaceState = SpaceState.NOT_STARTED;
	
	/**
	 * The max number of clients that can be connected into this {@link Space}
	 * If the maxClients == -1 no limit is imposed
	 */
	protected int maxClients = -1;
	
	/**
	 * The parent {@link Space} that is connected to this client in a Tree like structure
	 * e.g. you have a game world, within this world you have some areas that have
	 * specific rules, so you should implement a sub space and connect it to the root (parent) {@link Space}
	 */
	protected Space parentSpace;

	/**
	 * The {@link Space} list that is children of this {@link Space}
	 */
	protected List<Space> subSpaces = new CopyOnWriteArrayList<Space>();
	
	/**
	 * The clients that are connected to this {@link Space}
	 */
	protected List<String> clientNames = new CopyOnWriteArrayList<String>();
		
	/**
	 * Constructor passing unique id
	 * @param id the unique id of this {@link Space}
	 */
	public Space(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the unique id of this {@link Space}
	 * @return the unique id of this {@link Space}
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the custom name of this {@link Space}
	 * @note the name of a {@link Space} always include the prefix {@link SpaceIdMapping#SPACE_PREFIX}
	 * @return the custom name of this {@link Space}
	 */
	public String getName() {
		return SpaceIdMapping.SPACE_PREFIX + id;
	}
	
	/**
	 * Gets the number of clients connected to this {@link Space}
	 * @return the number of clients connected to this {@link Space}
	 */
	public int getNumClients() {
		return clientNames.size();
	}

	/**
	 * Gets the max number of clients that can connect to this {@link Space}
	 * @return the max number of clients that can connect to this {@link Space}
	 */
	public int getMaxClients() {
		return maxClients;
	}
	
	/**
	 * Sets the max number of clients that can connect to this {@link Space}
	 * @param maxClients the max number of clients that can connect to this {@link Space}
	 */
	public void setMaxClients(int maxClients) {
		this.maxClients = maxClients;
	}
	
	/**
	 * Sets the parent {@link Space} that is connected to this {@link Space}
	 * @param parentSpace the parent {@link Space} that is connected to this {@link Space}
	 */
	public void setParentSpace(Space parentSpace) {
		this.parentSpace = parentSpace;
	}
	
	/**
	 * Gets the parent space of this {@link Space}
	 * @return the parent space of this {@link Space}
	 */
	public Space getParentSpace() {
		return parentSpace;
	}
	
	/**
	 * Sets current state of this {@link Space} 
	 * @param spaceState current state of this {@link Space}
	 */
	public void setSpaceState(SpaceState spaceState) {
		this.spaceState = spaceState;
	}

	/**
	 * Gets the current state of this space
	 * @return the current state of this space
	 */
	public SpaceState getSpaceState() {
		return spaceState;
	}

	/**
	 * Adds a given {@link Space} as a child of this {@link Space} (it becomes a subspace of the parent space)
	 * @param space the {@link Space} to be added as a child (subspace)
	 */
	public void addSubSpace(Space space) {
		String spaceName = space.getName();
		DGSLogger.log("Space.addSubSpace.subspaceName: " + spaceName);
		
		if (subSpaces.contains(space)) {
			throw new RuntimeException("The sub space name: " + spaceName + ", is already on Space: " + spaceName);
		}
		
		space.setParentSpace(this);
		subSpaces.add(space);
	}

	/**
	 * Gets a subspace by space name
	 * @param spaceName the name to search
	 * @return the subspace if found, if not found returns null
	 */
	public Space getSubSpace(String spaceName) {	
		return (Space)MappingUtil.getObject(spaceName); 
	}
	
	/**
	 * When sending connected client it update the server message to contains specific info
	 * This method can be overloaded for specific info of a given {@link Space}  
	 * @param serverMessage the {@link ServerMessage} to input info 
	 * @param client the {@link Client} which message is destined to
	 */
	protected void updateServerMessageOnConnectedClient(ServerMessage serverMessage, Client client) {
		short handle = client.getId();
		serverMessage.put(handle);
		DGSLogger.log("Space.updateServerMsgWithClientInfo(), handle: " + handle);
		serverMessage.put(client.getSessionName());
		serverMessage.put((byte)client.getClientStatus().ordinal());
	}
	
	/**
	 * Client has disconnected
	 * A signal for client choose on disconnecting is given by {@link #clientLogoff(Client)}
	 * @param client the {@link Client} that had disconnected
	 */
	public void clientDisconnected(Client client) {
		onExit(client, true);
	}

	/**
	 * Clients exits this {@link Space}
	 * @param client the {@link Client} itself
	 */
	public void spaceExit(Client client) {
		onExit(client, false);
	}
	
	/**
	 * Gets whether a given {@link Client} is in this space or not
	 * @param client the {@link Client} to check if is in this {@link Space}
	 * @return whether a given {@link Client} is in this space or not
	 */
	public boolean isClientInSpace(Client client) {
		return clientNames.contains(client.getName());
	}	
	
	/**
	 * Puts a new {@link Client} to this {@link Space}
	 * @param client the {@link Client} that must be set
	 * @return whether the client was inserted into this {@link Space} or not  
	 */
	public boolean putClient(Client client) {
		return putClient(client, null);
	}
	
	/**
	 * Puts a new {@link Client} to this {@link Space}
	 * @param client the {@link Client} that must be set
	 * @param initInfo the initialization Object that can be anything user wanted 
	 * @return whether the client was inserted into this {@link Space} or not
	 */
	public boolean putClient(Client client, Object initInfo) {
		DGSLogger.log("Space.putClient space: " + getClass().getName() + " client name: " + client.getName());
		//client.attribId();
		String clientName = client.getName();
		if (clientNames.contains(clientName))
		{
			DGSLogger.log("Space.putClient space: client name: " + clientName + ", already present"); 
			return true;
		}
		
		clientNames.add(client.getName());
		client.addSpace(this);
		
		sendSpaceInit(client);

		joinChannels(client.getClientSession());
		SpaceMessageReceiver messageReceiver = createMessageReceiver();
		client.addMessageReceiver(messageReceiver);
		
		// send client enter for all this connected clients of this space
		sendClientEnter(client);

		sendConnectedClients(client);
		
		return true;
	}
	
	/**
	 * Do reconnect a {@link Client} into this {@link Space}
	 * @param client the {@link Client} to be reconnected
	 */
	protected void reconnectClient(Client client) {
		client.addSpace(this);
	}
	
	/**
	 * Gets the list of children spaces (the subspaces)
	 * @return the list of children spaces (the subspaces)
	 */
	public List<Space> getSubSpaces() {
		return subSpaces;
	}
	
	/**
	 * Gets a list of client names connected to this {@link Space}
	 * @return a list of client names connected to this {@link Space}
	 */
	public List<String> getClientNames() {
		return clientNames;
	}
	
	/**
	 * This method is called when a client exits this {@link Space} 
	 * It's called when there is a disconnection or when there is a user request for the client to exit
	 * @param client the {@link Client} that had disconnected
	 * @param disconnected whether there was a disconnection or not
	 * @return whether this {@link Client} was in this space and so if was removed from this {@link Space} 
	 */
	public boolean onExit(Client client, boolean disconnected) {
		DGSLogger.log("Space.removeClient, clientName: " + client.getName() + " Disconnected: " + disconnected + " Name: " + getName());
			
		sendClientExit(client);

		boolean hasPlayer = clientNames.remove(client.getName());
		client.removeMessageReceiver(getId());
		
		return hasPlayer;
	}

	/**
	 * Check whether it is possible for a {@link Client} to enter in this space or not
	 * By default it checks the number of clients that was connected to this {@link Space}
	 * But it can be overloaded and specific rule can be set on a given {@link Space}
	 * @return whether a new client can enter this {@link Space} or not 
	 */
	public boolean canEnterSpace() {
		//logger.log(Level.INFO, "NUM PLAYERS: " + numPlayers + " MAXCLIENTS: " + maxClients + " SPACE NAME: " + this.getName());
		return maxClients == -1 || getNumClients() < maxClients;
	}

	/**
	 * Default behavior: the client joins all channels
	 * Whether more specialized behavior is needed, to implement it on subclass
	 * @param clientSession the {@link ClientSession} to join the channels
	 */
	public abstract void joinChannels(ClientSession clientSession);
	
	/**
	 * Abstract method for {@link Channel}'s creation
	 * To overload this method and implement specific {@link Channel}'s creation 
	 */
	protected abstract void createChannels();
	
	/**
	 * For every {@link Space} a {@link SpaceMessageReceiver} must be implemented
	 * The {@link SpaceMessageReceiver} creation must be done by this method  
	 * @return
	 */
	public abstract SpaceMessageReceiver createMessageReceiver();
	
	// Output Events ----------------------------------------------------------
	
	/**
	 * Sends space initialization message
	 * @param client the {@link Client} to send the message
	 */
	public void sendSpaceInit(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.INIT);
		sendAdditionalInitInfo(serverMessage);
		DGSLogger.log("Space.sendSpaceInit: " + id);
		serverMessage.send(client);
	}
	
	/**
	 * Sends space client enter message
	 * @param client the {@link Client} to send the message
	 */
	public void sendClientEnter(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.ENTER);
		serverMessage.put(client.getId());
		serverMessage.put(client.getSessionName());
		serverMessage.put((byte)client.getClientStatus().ordinal());
		DGSLogger.log("Space.sendClientEnter, id: " + id + " client: " + client.getSessionName() + ", clientNamesLen: " + getClientNames().size());
		serverMessage.sendClientMessage(getClientNames());
	}

	/**
	 * Sends space client exit
	 * @param client the {@link Client} to send the message
	 */
	public void sendClientExit(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.EXIT);
		serverMessage.put(client.getId());
		DGSLogger.log("Space.sendClientExit id: " + id + " client: " + client.getId());
		serverMessage.sendClientMessage(getClientNames());
	}
	
	/**
	 * Sends space connected clients
	 * @param enteringClient the {@link Client} that is entering the space now
	 */
	public void sendConnectedClients(Client enteringClient) {
		int sz = clientNames.size();

		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.UPDATE_CONNECTED_CLIENTS);
		serverMessage.put((short) sz);
		
		// send the user client as the first one
		updateServerMessageOnConnectedClient(serverMessage, enteringClient);
		
		for (int i = 0; i < sz; i++) {
			String clientName = clientNames.get(i);

			DGSLogger.log("Client name: " + clientName);
			Client client = (Client)MappingUtil.getObject(clientName);
			
			String enteringClientName = enteringClient.getName();
			String name = client.getName();
			DGSLogger.log("--- Name: " + name);
			if (name.equalsIgnoreCase(enteringClientName)) {
				continue;
			}
			
			updateServerMessageOnConnectedClient(serverMessage, client);
		}
		
		DGSLogger.log("Space.sendConnectedClients id: " + id + " sz: " + sz + " Client name: " + enteringClient.getName());
		serverMessage.send(enteringClient);
	}
	
	/**
	 * Sends a byte enabling to clients
	 * @param objCode the object to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 */
	public void sendByteEnable(byte objCode, boolean enable) {
		sendByteEnable(objCode, enable, clientNames);
	}
	
	/**
	 * Sends a byte enabling to clients
	 * @param objCode the object to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 * @param clientNames the clients to send the message to
	 */
	public void sendByteEnable(byte objCode, boolean enable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_ENABLE);
		serverMessage.put(objCode);
		serverMessage.put(enable);
		DGSLogger.log("Space.sendByteEnable objcode: " + objCode + " enable: " + id);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of objects to be enabled or not
	 * @param objCodes the objects to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 */
	public void sendByteEnables(byte[] objCodes, boolean enable) {
		sendByteEnables(objCodes, enable, clientNames);
	}

	/**
	 * Sends an array of objects to be enabled or not
	 * @param objCodes the objects to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 * @param clientNames the clients to send the message to
	 */
	public void sendByteEnables(byte[] objCodes, boolean enable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_ENABLES);
		byte numObjs = (byte) objCodes.length;
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			byte objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		serverMessage.put(enable);
		DGSLogger.log("Space.sendByteEnable objcodes numObjs: " + numObjs + " enable: " + enable);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a short object code to be enabled or not
	 * @param objCode the object to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 */	
	public void sendShortEnable(short objCode, boolean enable) {
		sendShortEnable(objCode, enable, clientNames);
	}
	
	/**
	 * Sends a short object code to be enabled or not
	 * @param objCode the object to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 * @param clientNames the clients to send the message to
	 */
	public void sendShortEnable(short objCode, boolean enable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_ENABLE);
		serverMessage.put(objCode);
		serverMessage.put(enable);
		DGSLogger.log("Space.sendShortEnable objcode: " + objCode + " enable: " + enable);
		serverMessage.sendClientMessage(clientNames);
	}

	/**
	 * Sends a short object code to be enabled or not
	 * @param objCode the objects to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 */
	public void sendShortEnables(short[] objCodes, boolean enable) {
		sendShortEnables(objCodes, enable, clientNames);
	}
	
	/**
	 * Sends an array of short objects codes to be enabled or not
	 * @param objCodes the objects to be enabled or disabled
	 * @param enable whether the object is enabled or disabled
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortEnables(short[] objCodes, boolean enable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_ENABLES);
		short numObjs = (short) objCodes.length;
		serverMessage.put(enable);
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			short objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		DGSLogger.log("Space.sendByteEnable objcodes numObjs: " + numObjs + " enable: " + enable);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a byte object code to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 */	
	public void sendByteSelectable(byte objCode, boolean selectable) {
		sendByteSelectable(objCode, selectable, clientNames);
	}
	
	/**
	 * Sends a byte object code to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendByteSelectable(byte objCode, boolean selectable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_SELECTABLE);
		serverMessage.put(objCode);
		serverMessage.put(selectable);
		DGSLogger.log("Space.sendByteEnable objcode: " + objCode + " selectable: " + selectable);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of byte object codes to be set selectable or not
	 * @param objCodes the objects to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 */	
	public void sendByteSelectables(byte[] objCodes, boolean selectable) {
		sendByteSelectables(objCodes, selectable, clientNames);
	}

	/**
	 * Sends an array of byte object codes to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendByteSelectables(byte[] objCodes, boolean selectable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_SELECTABLES);
		byte numObjs = (byte) objCodes.length;
		serverMessage.put(selectable);
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			byte objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		DGSLogger.log("Space.sendByteEnables objcodes numObjs: " + numObjs + " selectable: " + selectable);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a short object codes to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 */
	public void sendShortSelectable(short objCode, boolean selectable) {
		sendShortSelectable(objCode, selectable, clientNames);
	}
	
	/**
	 * Sends a short object codes to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 * @param clientNames the clients to send the message to
	 */
	public void sendShortSelectable(short objCode, boolean selectable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_SELECTABLE);
		serverMessage.put(objCode);
		serverMessage.put(selectable);
		DGSLogger.log("Space.sendShortSelectable objcode: " + objCode + " selectable: " + selectable);
		serverMessage.sendClientMessage(clientNames);
	}

	/**
	 * Sends an array of short object codes to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 */	
	public void sendShortSelectables(short[] objCodes, boolean selectable) {
		sendShortSelectables(objCodes, selectable, clientNames);
	}

	/**
	 * Sends an array of short object codes to be set selectable or not
	 * @param objCode the object to be set selectable or not
	 * @param selectable whether the object is selectable or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortSelectables(short[] objCodes, boolean selectable, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_SELECTABLES);
		short numObjs = (short) objCodes.length;
		serverMessage.put(numObjs);
		for (short i = 0; i < numObjs; i++) {
			short objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		serverMessage.put(selectable);
		DGSLogger.log("Space.sendShortSelectables objcodes numObjs: " + numObjs + " selectable: " + selectable);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a byte object code to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 */	
	public void sendByteSelection(byte objCode, boolean selection) {
		sendByteSelection(objCode, selection, clientNames);
	}
	
	/**
	 * Sends a byte object code to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 * @param clientNames the clients to send the message to
	 */		
	public void sendByteSelection(byte objCode, boolean selection, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_SELECTION);
		serverMessage.put(objCode);
		serverMessage.put(selection);
		DGSLogger.log("Space.sendByteSelection objcode: " + objCode + " selection: " + selection);
		serverMessage.sendClientMessage(clientNames);
	}

	/**
	 * Sends an array of byte object codes to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 */
	public void sendByteSelections(byte[] objCodes, boolean selection) {
		sendByteSelections(objCodes, selection, clientNames);
	}

	/**
	 * Sends an array of byte object codes to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 * @param clientNames the clients to send the message to
	 */		
	public void sendByteSelections(byte[] objCodes, boolean selection, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_SELECTIONS);
		byte numObjs = (byte) objCodes.length;
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			byte objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		serverMessage.put(selection);
		DGSLogger.log("Space.sendByteSelections objcodes numObjs: " + numObjs + " selection: " + selection);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a short object code to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 * @param clientNames the clients to send the message to
	 */		
	public void sendShortSelection(short objCode, boolean selection) {
		sendShortSelection(objCode, selection, clientNames);
	}
	
	/**
	 * Sends a short object code to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 * @param clientNames the clients to send the message to
	 */		
	public void sendShortSelection(short objCode, boolean selection, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_SELECTION);
		serverMessage.put(objCode);
		serverMessage.put(selection);
		DGSLogger.log("Space.sendShortSelection objcode: " + objCode + " selection: " + selection);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of short object codes to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 */
	public void sendShortSelections(short[] objCodes, boolean selection) {
		sendShortSelections(objCodes, selection, clientNames);
	}

	/**
	 * Sends an array of short object codes to be set selected or not
	 * @param objCode the object to be set selected or not
	 * @param selection whether the object is selected or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortSelections(short[] objCodes, boolean selection, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_SELECTIONS);
		short numObjs = (short) objCodes.length;
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			short objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		serverMessage.put(selection);
		DGSLogger.log("Space.sendShortSelections objcodes numObjs: " + numObjs + " selection: " + selection);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a byte object code to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 */
	public void sendByteVisible(byte objCode, boolean visible) {
		sendByteVisible(objCode, visible, clientNames);
	}
	
	/**
	 * Sends a byte object code to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendByteVisible(byte objCode, boolean visible, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_VISIBLE);
		serverMessage.put(objCode);
		serverMessage.put(visible);
		DGSLogger.log("Space.sendByteVisible objcode: " + objCode + " visible: " + visible);
		serverMessage.sendClientMessage(clientNames);
	}

	/**
	 * Sends an array of bytes of object codes to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 */	
	public void sendByteVisibles(short[] objCodes, boolean visible) {
		sendByteVisibles(objCodes, visible, clientNames);
	}
	
	/**
	 * Sends an array of bytes of object codes to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendByteVisibles(short[] objCodes, boolean visible, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.BYTE_VISIBLES);
		byte numObjs = (byte) objCodes.length;
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			short objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		serverMessage.put(visible);
		DGSLogger.log("Space.sendByteVisibles objcodes numObjs: " + numObjs + " visible: " + visible);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a short object code to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortVisible(short objCode, boolean visible) {
		sendShortVisible(objCode, visible, clientNames);
	}
	
	/**
	 * Sends a byte object code to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortVisible(short objCode, boolean visible, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_VISIBLE);
		serverMessage.put(objCode);
		serverMessage.put(visible);
		DGSLogger.log("Space.sendShortVisible objcode: " + objCode + " visible: " + visible);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of short object codes to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortVisibles(short[] objCodes, boolean visible) {
		sendShortVisibles(objCodes, visible, clientNames);
	}

	/**
	 * Sends an array of short object codes to be set visible or not
	 * @param objCode the object to be set visible or not
	 * @param visible whether the object is visible or not
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortVisibles(short[] objCodes, boolean visible, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SHORT_VISIBLES);
		short numObjs = (short) objCodes.length;
		serverMessage.put(numObjs);
		for (byte i = 0; i < numObjs; i++) {
			short objCode = objCodes[i];
			serverMessage.put(objCode);
		}
		serverMessage.put(visible);
		DGSLogger.log("Space.sendShortVisibles objcodes numObjs: " + numObjs + " visible: " + visible);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a byte object code to set the id as byte
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */	
	public void sendByteObjByteId(byte objCode, byte id) {
		sendByteObjByteId(objCode, id, clientNames);
	}
	
	/**
	 * Sends a byte object code to set the id as byte
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */		
	public void sendByteObjByteId(byte objCode, byte id, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SET_BYTE_OBJ_BYTE_ID);
		serverMessage.put(objCode);
		serverMessage.put(id);
		DGSLogger.log("Space.sendObjId objcode: " + objCode + " id: " + id);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of byte object codes to set the ids
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */	
	public void sendByteObjsByteIds(byte[] objCodes, byte[] ids) {
		sendByteObjsByteIds(objCodes, ids, clientNames);
	}
	
	/**
	 * Sends an array of byte object codes to set the ids
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */
	public void sendByteObjsByteIds(byte[] objCodes, byte[] ids, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SET_BYTE_OBJS_BYTE_IDS);
		byte numObjs = (byte) objCodes.length;
		serverMessage.put(numObjs);
		DGSLogger.log("Space.sendByteObjsByteIds num objs: " + numObjs);
		for (byte i = 0; i < numObjs; i++) {
			DGSLogger.log("objCode, i: " + i + " code: " + objCodes[i]);
			serverMessage.put(objCodes[i]);
		}
		for (byte i = 0; i < numObjs; i++) {
			DGSLogger.log("objId, i: " + i + " id: " + ids[i]);
			serverMessage.put(ids[i]);
		}
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a short object code to set the id as byte
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 */		
	public void sendShortObjByteId(short objCode, byte id) {
		sendShortObjByteId(objCode, id, clientNames);
	}
	
	/**
	 * Sends a short object code to set the id as byte
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */		
	public void sendShortObjByteId(short objCode, byte id, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SET_SHORT_OBJ_BYTE_ID);
		serverMessage.put(objCode);
		serverMessage.put(id);
		DGSLogger.log("Space.sendObjId objcode: " + objCode + " id: " + id);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of short object codes to set the ids
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 */	
	public void sendShortObjsByteIds(short[] objCodes, byte[] ids) {
		sendShortObjsByteIds(objCodes, ids, clientNames);
	}
	
	/**
	 * Sends an array of byte object codes to set the ids
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortObjsByteIds(short[] objCodes, byte[] ids, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SET_SHORT_OBJS_BYTE_IDS);
		short numObjs = (short) objCodes.length;
		serverMessage.put(numObjs);
		for (short i = 0; i < numObjs; i++) {
			serverMessage.put(objCodes[i]);
		}
		for (short i = 0; i < numObjs; i++) {
			serverMessage.put(ids[i]);
		}
		DGSLogger.log("Space.sendObjsIds num objs: " + numObjs);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends a short object code to set the id as short
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */			
	public void sendShortObjShortId(short objCode, short id) {
		sendShortObjShortId(objCode, id, clientNames);
	}
	
	/**
	 * Sends a short object code to set the id as short
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */			
	public void sendShortObjShortId(short objCode, short id, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SET_SHORT_OBJ_SHORT_ID);
		serverMessage.put(objCode);
		serverMessage.put(id);
		DGSLogger.log("Space.sendObjId objcode: " + objCode + " id: " + id);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * Sends an array of byte object codes to set the ids
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 */
	public void sendShortObjsShortIds(short[] objCodes, short[] ids) {
		sendShortObjsShortIds(objCodes, ids, clientNames);
	}
	
	/**
	 * Sends an array of byte object codes to set the ids
	 * @param objCode the object to set the id
	 * @param id the id of the object
	 * @param clientNames the clients to send the message to
	 */	
	public void sendShortObjsShortIds(short[] objCodes, short[] ids, List<String> clientNames) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.SET_SHORT_OBJS_SHORT_IDS);
		short numObjs = (short) objCodes.length;
		serverMessage.put(numObjs);
		for (short i = 0; i < numObjs; i++) {
			serverMessage.put(objCodes[i]);
		}
		for (short i = 0; i < numObjs; i++) {
			serverMessage.put(ids[i]);
		}
		DGSLogger.log("Space.sendObjsIds num objs: " + numObjs);
		serverMessage.sendClientMessage(clientNames);
	}
	
	/**
	 * When initializing the {@link Space} it sends additional info if needed
	 * @param serverMessage the {@link ServerMessage} to set the info
	 */
	protected abstract void sendAdditionalInitInfo(ServerMessage serverMessage);
}