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

import java.nio.ByteBuffer;
import java.util.logging.Level;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.client.MessageSender;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.space.game.GameSpace;
import com.jge.server.utils.DGSLogger;

public class LobbyMessageReceiver extends SpaceMessageReceiver {
	private SpaceIdMapping spaceIdMapping;
	
	public LobbyMessageReceiver(Space space, SpaceIdMapping spaceIdMapping) {
		super(space);
		this.spaceIdMapping = spaceIdMapping;
	}

	protected boolean processEvent(Channel channel, MessageSender sender, byte event, ByteBuffer msg) {
		if (super.processEvent(channel, sender, event, msg)) {
			return true;
		}
		
		if (!sender.isHuman()) {
			return false;
		}

		Client client = (Client)sender;
		
		if (LobbyProtocol.PLAY_NOW.getId() == event) {
			playNow(channel, client, msg, false);
			return true;
		}
		if (LobbyProtocol.PLAY_NOW_WITH_ROBOTS.getId() == event) {
			playNow(channel, client, msg, true);
			return true;
		}
		else if (LobbyProtocol.CHOOSE_PLACE.getId() == event) {
			choosePlace(channel, client, msg);
			return true;
		}
		else if (LobbyProtocol.GET_LOBBY_INFO.getId() == event) {
			getLobbyInfo(channel, client, msg);
			return true;
		}
		else if (LobbyProtocol.SWITCH_PAGE.getId() == event) {
			switchPage(channel, client, msg);
			return true;
		}
		else if (LobbyProtocol.SWITCH_LOBBY.getId() == event) {
			switchLobby(channel, client, msg);
			return true;
		}
		else if (LobbyProtocol.ENTER_PAGE.getId() == event) {
			enterPage(channel, client, msg);
			return true;
		}
		else if (LobbyProtocol.EXIT_PAGE.getId() == event) {
			exitPage(channel, client, msg);
			return true;
		}
		
		return false;
	}

	protected void playNow(Channel channel, Client client, ByteBuffer msg, boolean fillWithRobots) {
		byte matchType = msg.get();
		DGSLogger.log("LobbyMessageReceiver.playNow(), hasAvailableId: " + spaceIdMapping.hasAvailableId() + ", matchType: " + matchType);
		
		LobbySpace thisLobby = (LobbySpace)getSpace();
		
		if (spaceIdMapping.hasAvailableId()) {
			DGSLogger.log("LobbyMessageReceiver.playNow() hasAvailableId.");
			LobbysInfo lobbysInfo = thisLobby.getLobbysInfo();
			int[] freePlace = lobbysInfo.getFreePlace();
			int lobbyId = freePlace[0];
			byte placeIdx = (byte) freePlace[1];
			byte subPlaceIdx = (byte) freePlace[2];
			byte gameIdx = (byte) freePlace[3];
			byte lobbyPage = (byte) (gameIdx / (6 * 4)); // 6 tables * 4 places
			DGSLogger.log("LobbyMessageReceiver.playNow(), gameIdx: " + gameIdx + " lobbyPage: " + lobbyPage);
			LobbySpace lobby = (LobbySpace) SpaceIdMapping.getSpaceWithId(lobbyId);
			
			DGSLogger.log("LobbyMessageReceiver.playNow(), lobbyId: " +  lobbyId + " placeIdx: " + placeIdx + " subPlaceIdx: " + subPlaceIdx);
			short clientId = client.getId();
			if (lobby.isSubspaceFree(clientId, placeIdx, subPlaceIdx)) {
				lobby.setPlaceBusy(placeIdx, subPlaceIdx, true, clientId);
				GameSpace subSpace = (GameSpace) lobby.getSubSpaces().get(placeIdx);
				Byte initInfo = (Byte)subPlaceIdx;
				lobby.sendCreateGame(client, placeIdx, subPlaceIdx);
				if (!subSpace.putClient(client, initInfo)) {
					DGSLogger.log(Level.WARNING, "LobbyMessageReceiver.playNow, maybe wrong putting client, cannot put client, id: " + client.getId() + ", email: " + client.getEmail());
					return;
				}
				lobby.sendEnterGame(client, placeIdx, subPlaceIdx);
				if (fillWithRobots) {
					subSpace.fillWithRobots();
				}
			} else {
				lobby.sendCannotEnter(client);
			}

		} else {
			DGSLogger.log("LobbyMessageReceiver.playNow() !hasAvailableID");
			thisLobby.sendCannotEnter(client);
		}
	}

	protected void choosePlace(Channel channel, Client client, ByteBuffer msg) {
		byte placeIdx = msg.get();
		byte subPlaceIdx = msg.get();
		LobbySpace lobby = (LobbySpace)getSpace();
		DGSLogger.log("LobbyMessageReceiver.choosePlace() lobbyId: " + lobby.getId() + ", placeId: " + placeIdx + ", subPlaceId: " + subPlaceIdx);
		if (lobby.isSubspaceFree(client.getId(), placeIdx, subPlaceIdx)) {
			short clientId = client.getId();
			lobby.setPlaceBusy(placeIdx, subPlaceIdx, false, clientId);
			Space subSpace = lobby.getSubSpaces().get(placeIdx);
			subSpace.putClient(client);
			lobby.sendEnterGame(client, placeIdx, subPlaceIdx);
		} else {
			DGSLogger.log("LobbyMessageReceiver.choosePlace() choosePlace, cannotEnter");
			lobby.sendCannotEnter(client);
		}
	}
	
	protected void getLobbyInfo(Channel channel, Client client, ByteBuffer msg) {
		LobbySpace lobby = (LobbySpace)getSpace();
		ServerMessage serverMsg = new ServerMessage(lobby.getId(), LobbyProtocol.LOBBY_INFO);
		LobbyPlaceState[][] places = lobby.getPlaces();

		// send the busy places
		for (byte i = 0; i < places.length; i++) {
			LobbyPlaceState[] sub = places[i];
			for (byte j = 0; j < sub.length; j++) {
				if (sub[j].getStateType() == LobbyPlaceStateEnum.BUSY) {
					byte buf = i; // 4 bits for table
					buf = (byte) ((buf << 4) | j); // 4 bits for table
					serverMsg.put(buf); // 1 byte, 3 bits for chair place
					short clientId = sub[j].getConnectedID();
					serverMsg.put(clientId); // 1 short
					sub[j].setChanged(false);
				}
			}
		}
		serverMsg.send(client);
	}

	protected void switchPage(Channel channel, Client client, ByteBuffer msg) {
		DGSLogger.log("LobbyMessageReceiver.switchPage(), session name: " + client.getName());
		exitPage(channel, client, msg);
		
		enterPage(channel, client, msg);		
	}
	
	protected void exitPage(Channel channel, Client client, ByteBuffer msg) {
		// remove from current lobby
		LobbySpace currLobby = (LobbySpace)getSpace();
		DGSLogger.log("LobbyMessageReceiver.exitPage(), session name: " + client.getName() + " currLobby: " + currLobby.getId());
		
		currLobby.onExit(client, false);
	}
	
	protected void enterPage(Channel channel, Client client, ByteBuffer msg) {
		// put in another lobby
		int gameId = msg.getInt();
		Space lobby = SpaceIdMapping.getSpaceWithId(gameId);
		
		DGSLogger.log("LobbyMessageReceiver.enterPage(), session name: " + client.getName() + " lobbyId: " + gameId);
		
		lobby.putClient(client);
		
	}
	
	private void switchLobby(Channel channel, Client client, ByteBuffer msg) {
		int lobbyToSwithId = msg.getInt();
		LobbySpace lobby = (LobbySpace)SpaceIdMapping.getSpaceWithId(lobbyToSwithId);
		switchLobby(lobby, client, lobbyToSwithId, (byte)0);
	}
	
	private void switchLobby(LobbySpace lobby, Client client, int lobbyToSwithId, byte lobbyPage) {
		// first remove from current lobby
		LobbySpace currLobby = (LobbySpace)getSpace();	
		currLobby.onExit(client, false);
		
		// sign the user this client switched
		currLobby.sendSwitchLobby(client, lobbyToSwithId, lobbyPage);

		// put in another lobby
		lobby.putClient(client);
	}
}