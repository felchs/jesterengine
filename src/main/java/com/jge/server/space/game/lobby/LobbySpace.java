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
package com.jge.server.space.game.lobby;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.Channel;
import com.jge.server.net.Delivery;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.SpaceChannelReceiver;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.space.game.GameSpace;
import com.jge.server.utils.DGSLogger;

public class LobbySpace extends Space {
	private final LobbyPlaceState[][] places;
	private final int numPlaces;
	
	private LobbysInfo lobbysInfo;
	
	protected SpaceIdMapping spaceIdMapping;

	private boolean changed = true;

	private String lobbyChatChannelName;
	
	private short lobbyPage;

//	private ObjectWrapper<Ranking> ranking;
	
	public LobbySpace(int id, short lobbyPage, byte numPlaces, byte numSubPlaces, LobbysInfo lobbysInfo, SpaceIdMapping spaceIdMapping) {
		super(id);
		
		this.lobbyPage = lobbyPage;
		
		this.numPlaces = numPlaces;
		
		this.lobbysInfo = lobbysInfo;
		lobbysInfo.addPlaces(id, numPlaces, numSubPlaces);
		
		places = new LobbyPlaceState[numPlaces][numSubPlaces];
		for (short i = 0 ; i < numPlaces; i++) {
			LobbyPlaceState subPlaces[] = new LobbyPlaceState[numSubPlaces];
			for (short j = 0; j < numSubPlaces; j++) {
				LobbyPlaceState state = new LobbyPlaceState(LobbyPlaceStateEnum.FREE);
				state.setChanged(true);
				subPlaces[j] = state;
				
			}
			places[i] = subPlaces;
		}

		setChanged(false);

		this.spaceIdMapping = spaceIdMapping;
	}

	public LobbyPlaceState[][] getPlaces() {
		return places;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean changed() {
		return changed;
	}
	
//	public HashMap<Byte, Byte> getPlayablePlaceMap() {
//		return playablePlaceMap;
//	}

	public boolean isSubspaceFree(short clientId, byte placeIdx, byte subPlaceIdx) {
		DGSLogger.log("LobbyMessageReceiver.isSubspaceFree() numPlaces: " + numPlaces + ", placeIdx: " + placeIdx + ", subPlaceIdx: " + subPlaceIdx);
		
		// prevents undesired messages
		if (placeIdx > numPlaces - 1) {
			return false;
		}
		
		if (places[placeIdx].length == 0) {
			return true;
		}
		
		if (subPlaceIdx > places[placeIdx].length - 1) {
			return false;
		}

		LobbyPlaceState lobbyPlaceState = places[placeIdx][subPlaceIdx];
		boolean free = lobbyPlaceState.getStateType() == LobbyPlaceStateEnum.FREE;
		boolean reserved = lobbyPlaceState.isReserved(clientId);
		DGSLogger.log("LobbyMessageReceiver.isSubspaceFree() free: " + free + ", reserved: " + reserved);
		return free || reserved;
	}
	
	protected String getEventsChannelName() {
		return getName() + "ChEvents";
	}
	
	public Channel getLobbyChatChannel() {
		return AppContext.getChannelManager().getChannel(lobbyChatChannelName);
	}

	@Override
	public void joinChannels(ClientSession clientSession) {
		getLobbyChatChannel().join(clientSession);
	}
	
	@Override
	public void createChannels() {
		AppContext.getChannelManager().createChannel(getEventsChannelName(), new SpaceChannelReceiver(createMessageReceiver()), Delivery.RELIABLE);
	}
	
	public void setLobbyChatChannelName(String lobbyChatChannelName) {
		this.lobbyChatChannelName = lobbyChatChannelName;
	}

	@Override
	public SpaceMessageReceiver createMessageReceiver() {
		return new LobbyMessageReceiver(this, spaceIdMapping);
	}

	@Override
	protected void reconnectClient(Client client) { }
	
	@Override
	protected void sendAdditionalInitInfo(ServerMessage serverMessage) { }

	public String getSubSpacePrefix() {
		return null;
	}
	
	@Override
	public boolean putClient(Client client) {
		return super.putClient(client);
		
//		if (hasRanking()) {
//			sendAllRankingInfoToclient(client);
//		}
	}

	public void setPlaceBusy(byte placeIdx, byte subPlaceIdx, boolean reserved, short clientID) {
		DGSLogger.log("LobbySpace.setPlaceBusy(), placeId: " + placeIdx + " subPlaceId: " + subPlaceIdx + " connectedId: " + clientID);
		if (places[placeIdx].length > 0) {
			LobbyPlaceState lobbyPlaceState = places[placeIdx][subPlaceIdx];
			lobbyPlaceState.setStateType(LobbyPlaceStateEnum.BUSY);
			lobbyPlaceState.setPlaceIdx(placeIdx);
			lobbyPlaceState.setSubPlaceIdx(subPlaceIdx);
			lobbyPlaceState.setReserved(reserved);
			lobbyPlaceState.setConnectedID(clientID);
			
			lobbysInfo.setPlaceBusy(getId(), placeIdx, subPlaceIdx);
		}
	}
	
	public LobbysInfo getLobbysInfo() {
		return lobbysInfo;
	}

	public void onGameStart(GameSpace gameSpace) {
		// if the game is full of users don't put this space as not available
		//if (!gameSpace.canEnterSpace()) {
		//	spaceIdMapping.getForUpdate().moveToUsed(gameSpace);
		//}		
	}
	
	public void onPlayerExitGame(byte placeIdx, byte subPlaceIdx, short connectedID) {
		DGSLogger.log("LobbySpace.onPlayerExitGame: " + connectedID);
		LobbyPlaceState lobbyPlaceState = places[placeIdx][subPlaceIdx];
		lobbyPlaceState.setStateType(LobbyPlaceStateEnum.FREE);
		lobbyPlaceState.setPlaceIdx((byte)0);
		lobbyPlaceState.setSubPlaceIdx((byte)0);
		lobbyPlaceState.setConnectedID((byte)0);
		
		DGSLogger.log("LobbySpace.onPlayerExitGame(), placeID: " + placeIdx + " subPlaceID: " + subPlaceIdx +  " connectedID: " + connectedID + " thisID: " + id);
		
		lobbysInfo.setPlaceFree(getId(), placeIdx, subPlaceIdx);
	}
	
//	public void setRanking(Ranking ranking) {
//		this.ranking = new ObjectWrapper<Ranking>(ranking);
//		
//		if (ranking != null) {
//			ranking.addListener(this);
//		}
//	}
//	
//	public ObjectWrapper<Ranking> getRanking() {
//		return ranking;
//	}
//	
//	public boolean hasRanking() {
//		return ranking != null;
//	}
//	
//	// Ranking Methods --------------------------------------------------------
//	
//	public void sendAllRankingInfoToAllClients() {
//		sendAllRankingInfo(getClientNames());
//	}
//	
//	public void sendAllRankingInfoToclient(Client client) {
//		ServerMessage serverMessage = mountRankingInfo();
//		if (serverMessage != null) {
//			serverMessage.send(client);
//		}
//	}
//	
//	private void sendAllRankingInfo(CopyOnWriteArrayList<String> clientNames) {
//		ServerMessage serverMessage = mountRankingInfo();
//		if (serverMessage != null) {
//			serverMessage.sendClientMessage(clientNames);
//		}
//	}
//	
//	private ServerMessage mountRankingInfo() {
//		Ranking ranking_ = ranking.get();
//		Collection<ClientRankingPoints> rankingValues = ranking_.getClientRankingPointsValues();
//		short numClients = (short)rankingValues.size();
//		if (numClients > 0) {
//			ServerMessage serverMessage = new ServerMessage(getID(), LobbyProtocol.UPDATE_RANKING_POINS);
//			serverMessage.put(numClients);
//			for (ClientRankingPoints rankingValue : rankingValues) {
//				serverMessage.put(rankingValue.getClientID());
//				serverMessage.put(rankingValue.getPoints());
//			}
//			return serverMessage;
//		} else {
//			return null;
//		}
//	}
//
//	@Override
//	public void updatePoints(short clientID, float points, float tiePoints) {
//		ServerMessage serverMessage = new ServerMessage(getID(), LobbyProtocol.UPDATE_INDIVIDUAL_RANKING);
//		serverMessage.put(clientID);
//		serverMessage.put(points);
//		
//		serverMessage.sendClientMessage(getClientNames());
//	}	
	
	// Output events ----------------------------------------------------------
	
	public void sendCannotEnter(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), LobbyProtocol.CANNOT_ENTER);
		DGSLogger.log("LobbySpace.sendCannotEnter: " + id);
		serverMessage.send(client);
	}
	
	public void sendEnterGame(Client client, byte placeIdx, byte subPlaceIdx) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.ENTER_GAME);
		msg.put(placeIdx);
		msg.put(subPlaceIdx);
		msg.put(lobbyPage);
		DGSLogger.log("LobbySpace.sendEnterGame: " + id + " lobbyPage: " + lobbyPage);
		msg.send(client);
	}
	
	public void sendCreateGame(Client client, byte placeIdx, byte subPlaceIdx) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.CREATE_GAME);
		msg.put(placeIdx);
		msg.put(subPlaceIdx);
		msg.put(lobbyPage);
		DGSLogger.log("LobbySpace.sendCreateGame: " + id + " lobbyPage: " + lobbyPage);
		msg.send(client);		
	}	
	
	public void sendSignPlaceToEnter(Client client, int gameId, byte placeIdx, byte subPlaceIdx, byte lobbyPage) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.SIGN_PLACE_TO_ENTER);
		msg.put(placeIdx);
		msg.put(subPlaceIdx);
		msg.put(lobbyPage);
		DGSLogger.log("LobbySpace.sendSignPlaceToEnter(), thisId: " + id + " gameId: " + gameId + " placeId: " + placeIdx + " subPlaceId: " + subPlaceIdx + " lobbyPage: " + lobbyPage);
		msg.send(client);
	}
	
	public void sendGameStart(Client client, byte spaceID, byte subSpaceID) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.ON_GAME_START);
		msg.put(spaceID);
		msg.put(subSpaceID);
		DGSLogger.log("LobbySpace.sendOnGameStart: " + id);
		msg.send(client);
	}

	public void sendGameFinish(Client client, byte spaceID, byte subSpaceID) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.ON_GAME_FINISH);
		msg.put(spaceID);
		msg.put(subSpaceID);
		DGSLogger.log("LobbySpace.sendOnGameFinish: " + id);
		msg.send(client);
	}

	public void sendSwitchLobby(Client client, int lobbyToSwithID, byte lobbyPage) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.ON_SWITCH_LOBBY);
		msg.put(lobbyToSwithID);
		msg.put(lobbyPage);
		DGSLogger.log("LobbySpace.sendSwitchLobby, lobbyToSwithID: " + lobbyToSwithID + " lobbyPage: " + lobbyPage);
		msg.send(client);
	}

	public void sendSwitchPage(Client client, int lobbyId, byte lobbyPage) {
		ServerMessage msg = new ServerMessage(getId(), LobbyProtocol.ON_SWITCH_PAGE);
		msg.put(lobbyId);
		msg.put(lobbyPage);
		DGSLogger.log("LobbySpace.sendSwitchLobby, lobbyId: " + lobbyId);
		msg.send(client);
	}

	
}