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
package com.jge.server.space.game;

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum GameProtocol implements Protocol<Byte> {
	//
	GAME_STARTED((byte)50),
	GAME_STOPPED((byte)51),
	GAME_RESULTS((byte)52),
	GAME_FINISHED((byte)53),
	UPDATE_SCORE((byte)54),
	GAME_PLAYER_GIVE_UP((byte)55),
	GAME_PLAYER_FALL((byte)56),
	GAME_CAN_RESTART((byte)57),
	GAME_RESTART((byte)58),
	
	//
	GAME_FILL_WITH_ROBOTS((byte)59),
	GAME_ROBOT_ENTER((byte)60),
	GAME_ROBOT_REPLACEMENT((byte)61),
	GAME_ROBOT_EXIT((byte)62);
	
	private final byte id;
	
	GameProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "GameProtocol." + this.toString();
	}
}