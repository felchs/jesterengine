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

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum LobbyProtocol implements Protocol<Byte> {
	// server input events
	PLAY_NOW((byte)30),
	PLAY_NOW_WITH_ROBOTS((byte)31),
	CHOOSE_PLACE((byte)32),
	GET_LOBBY_INFO((byte)33),
	SWITCH_PAGE((byte)34),
	SWITCH_LOBBY((byte)35),
	ENTER_PAGE((byte)36),
	EXIT_PAGE((byte)37),
	
	// server output events
	CANNOT_ENTER((byte)38),
	ENTER_GAME((byte)39),
	SIGN_PLACE_TO_ENTER((byte)40),
	LOBBY_INFO((byte)41),
	ON_GAME_START((byte)42),
	ON_GAME_FINISH((byte)43),
	ON_SWITCH_LOBBY((byte)44),
	ON_SWITCH_PAGE((byte)45),
	CREATE_GAME((byte)46);
	//UPDATE_INDIVIDUAL_RANKING((byte)45),
	//UPDATE_RANKING_POINS((byte)46);

	private byte id;
	
	private LobbyProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "GameLobbyProtocol." + this.toString();
	}
}