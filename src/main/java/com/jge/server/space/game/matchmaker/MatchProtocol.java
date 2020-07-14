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
package com.jge.server.space.game.matchmaker;

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum MatchProtocol implements Protocol<Byte> {
	// server input events
	PLAY_NOW((byte)30),
	PLAY_NOW_WITH_ROBOTS((byte)31),
	
	// server output events
	CANNOT_ENTER((byte)32),
	ENTER_GAME((byte)33),
	MATCHMAKER_INFO((byte)34),
	ON_GAME_START((byte)35),
	ON_GAME_FINISH((byte)36);

	private byte id;
	
	private MatchProtocol(byte id) {
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