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

import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.space.game.lobby.LobbySpace;
import com.jge.server.space.game.lobby.LobbysInfo;

public class TurnLobbySpace extends LobbySpace {
	public TurnLobbySpace(int id, short lobbyPage, byte numPlaces, byte numSubPlaces, LobbysInfo lobbysInfo, SpaceIdMapping spaceIdMapping) {
		super(id, lobbyPage, numPlaces, numSubPlaces, lobbysInfo, spaceIdMapping);
	}

	@Override
	public SpaceMessageReceiver createMessageReceiver() {
		return new TurnLobbyMessageReceiver(this, spaceIdMapping);
	}
}
