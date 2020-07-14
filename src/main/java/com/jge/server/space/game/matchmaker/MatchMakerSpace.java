package com.jge.server.space.game.matchmaker;

import java.util.concurrent.ConcurrentHashMap;

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

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.Channel;
import com.jge.server.net.Delivery;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceChannelReceiver;
import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.space.game.GameFactory;
import com.jge.server.space.game.GameSpace;
import com.jge.server.utils.DGSLogger;

public class MatchMakerSpace extends Space {
	protected SpaceIdMapping spaceIdMapping;

	private String matchChatChannelName;
	
	private ConcurrentHashMap<String, GameSpace> openGames = new ConcurrentHashMap<String, GameSpace>();
	
	
	public MatchMakerSpace(int id, SpaceIdMapping spaceIdMapping) {
		super(id);
		
		this.spaceIdMapping = spaceIdMapping;
		
	}
	
	private String getMatchAndGameKey(byte matchType, byte gameType) {
		return matchType + "_" + gameType;
	}

	protected String getEventsChannelName() {
		return getName() + "ChEvents";
	}
	
	public Channel getMatchMakerChatChannel() {
		return AppContext.getChannelManager().getChannel(matchChatChannelName);
	}

	@Override
	public void joinChannels(ClientSession clientSession) {
		getMatchMakerChatChannel().join(clientSession);
	}
	
	@Override
	public void createChannels() {
		AppContext.getChannelManager().createChannel(getEventsChannelName(), new SpaceChannelReceiver(createMessageReceiver()), Delivery.RELIABLE);
	}
	
	public void setLobbyChatChannelName(String lobbyChatChannelName) {
		this.matchChatChannelName = lobbyChatChannelName;
	}

	@Override
	public SpaceMessageReceiver createMessageReceiver() {
		return new MatchMakerMessageReceiver(this, spaceIdMapping);
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
		if (!canPutOneMoreClient())	 {
			return false;
		}
		
		return super.putClient(client);
	}

	public void onGameStart(GameSpace gameSpace) {
		// if the game is full of users don't put this space as not available
		//if (!gameSpace.canEnterSpace()) {
		//	spaceIdMapping.getForUpdate().moveToUsed(gameSpace);
		//}		
	}
	
	public void onPlayerExitGame(byte placeIdx, byte subPlaceIdx, short connectedID) {

	}
	
	/**
	 * Due to processing/resources restrictions check if it is possible to put one more player on this machine 
	 * @return whether can put or not one more player, by default returns true
	 */
	private boolean canPutOneMoreClient() {
		return true;
	}
	
	
	public void wantToPlayNow(Client client, byte matchType, byte gameEnum, boolean fillWithRobots) {
		//
		// do checks to see if the matchType and gameType are valid ones
		//
		if (!MathType.contains(matchType)) {
			DGSLogger.log("MatchMaker(), Wrong mathtype: " + matchType);
			return;
		}
		
		GameFactory spaceFactory = GameFactory.getInstance();
		
		if (!spaceFactory.getGameTypes().contains(gameEnum)) {
			DGSLogger.log("MatchMaker(), Wrong gameType: " + gameEnum);
			return;
		}

		//
		// creates the games on demand
		// choose players to enter the game, by now choose them in the order of arrival
		//
		String key = getMatchAndGameKey(matchType, gameEnum);
		GameSpace openGame = null;
		if (openGames.isEmpty()) {
			openGame = spaceFactory.createGame(matchType, gameEnum, fillWithRobots);
			openGames.put(key, openGame);
		} else {
			openGame = openGames.get(key);
		}
		
		openGame.putClient(client);
		
		//
		// sign to enter the game
		//
		// removes from the opengames list once it reached the max players
		//
		if (!openGame.canEnterSpace()) {
			sendEnterGame(client, openGame.getId(), gameEnum);
			
			openGames.remove(key);
		}
	}
	
	// Output events ----------------------------------------------------------
	
	public void sendCannotEnter(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), MatchProtocol.CANNOT_ENTER);
		DGSLogger.log("LobbySpace.sendCannotEnter: " + id);
		serverMessage.send(client);
	}
	
	public void sendEnterGame(Client client, int gameId, byte gameEnum) {
		ServerMessage msg = new ServerMessage(getId(), MatchProtocol.ENTER_GAME);
		msg.put(gameId);
		msg.put(gameEnum);
		DGSLogger.log("LobbySpace.sendEnterGame, MatchMaker id: " + id + " gameId: " + gameId);
		msg.send(client);
	}
	
	public void sendGameStart(Client client, int gameId) {
		ServerMessage msg = new ServerMessage(getId(), MatchProtocol.ON_GAME_START);
		msg.put(gameId);
		DGSLogger.log("LobbySpace.sendOnGameStart, MatchMaker id: " + id + ", gameId: " + gameId);
		msg.send(client);
	}

	public void sendGameFinish(Client client, int gameId) {
		ServerMessage msg = new ServerMessage(getId(), MatchProtocol.ON_GAME_FINISH);
		msg.put(gameId);
		DGSLogger.log("LobbySpace.sendOnGameFinish, MatchMaker id: " + id + ", gameId: " + gameId);
		msg.send(client);
	}
	
}
