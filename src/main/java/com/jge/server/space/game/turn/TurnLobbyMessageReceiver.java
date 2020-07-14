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
package com.jge.server.space.game.turn;

import java.nio.ByteBuffer;

import com.jge.server.client.Client;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.game.lobby.LobbyMessageReceiver;
import com.jge.server.space.game.lobby.LobbySpace;
import com.jge.server.space.game.lobby.LobbySubPlace;
import com.jge.server.utils.DGSLogger;

public class TurnLobbyMessageReceiver extends LobbyMessageReceiver {

	public TurnLobbyMessageReceiver(Space gameSpace, SpaceIdMapping spaceIdMapping) {
		super(gameSpace, spaceIdMapping);
	}
	
	@Override
	protected void choosePlace(Channel channel, Client client, ByteBuffer msg) {
		byte placeId = msg.get();
		byte subSpaceId = msg.get();
		LobbySpace lobby = (LobbySpace)getSpace();
		short clientID = client.getId();
		DGSLogger.log("LobbyMessageReceiver.playNow() choosePlace, placeId: " + placeId + ", subPlaceId: " + subSpaceId);
		if (lobby.isSubspaceFree(clientID, placeId, subSpaceId)) {
			lobby.setPlaceBusy(placeId, subSpaceId, false, clientID);
			Space subSpace = lobby.getSubSpaces().get(placeId);
			String spaceName = subSpace.getName();
			DGSLogger.log("Choose place: " + placeId + " " + subSpaceId + " SpaceName: " + spaceName);
			LobbySubPlace lobbySubSpace = (LobbySubPlace)subSpace;
			lobbySubSpace.setPlaceID(placeId);
			lobbySubSpace.setSubPlaceId(subSpaceId);
			Byte screenPos = subSpaceId;
			if (subSpace.putClient(client, screenPos)) {
				lobby.sendEnterGame(client, placeId, subSpaceId);
			} else {
				lobby.sendCannotEnter(client);
			}
		} else {
			lobby.sendCannotEnter(client);
		}
	}
	
//	protected void reservePlaceByPlayNow(LobbySpace lobby, Client client, byte placeIdx, byte subSpaceIdx, byte lobbyPage) {		
//		short clientID = client.getID();
//		if (lobby.isSubspaceFree(clientID, placeIdx, subSpaceIdx)) {
//			lobby.setPlaceBusy(placeIdx, subSpaceIdx, true, clientID);
//			String spaceName = lobby.getSubSpaceNames().get(placeIdx);
//			DGSLogger.log("Choose place: " + placeIdx + " " + subSpaceIdx + " SpaceName: " + spaceName);
//			Space subSpace = lobby.getSubSpace(spaceName);
//			int gameID = subSpace.getID();
//			LobbySubPlace lobbySubSpace = (LobbySubPlace)subSpace;
//			lobbySubSpace.setPlaceID(placeIdx);
//			lobbySubSpace.setSubPlaceID(subSpaceIdx);
//			//Byte screenPos = subSpaceIdx;
//			lobby.sendSignPlaceToEnter(client, gameID, placeIdx, subSpaceIdx, lobbyPage);
//			//if (!subSpace.putClient(client, screenPos)) {
//			//	lobby.sendCannotEnter(client);
//			//	throw new RuntimeException();
//			//}
//		} else {
//			lobby.sendCannotEnter(client);
//		}
//	}
}