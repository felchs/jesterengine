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
package com.jge.server.space.game.tournament;

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum TournamentProtocol implements Protocol<Byte> {
	// server input events
	CHECK_CAN_ENTER((byte)100),
	GIVE_UP((byte)101),
	
	// server output events
	SEND_CAN_ENTER((byte)102),
	UPDATE_CURRENT_GAMES((byte)103),
	ON_GAME_START((byte)104),
	ON_GAME_FINISH((byte)105),
	UPDATE_SCORE((byte)106),
	ON_PLAYER_GIVE_UP((byte)107),
	FINAL_RESULTS((byte)108);

	private byte id;
	
	private TournamentProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "TournamentProtocol." + this.toString();
	}
}