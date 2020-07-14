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

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum TurnGameProtocol implements Protocol<Byte> {
	// Output events
	NOTIFY_CURRENT_PLAYER((byte)81),
	NOTIFY_CURRENT_PLAYER_ROBOT((byte)82),
	SET_SELECTABLES_BY_PLAYER((byte)83),
	SET_THIS_PLAYER_SELECTABLES((byte)84),
	TURN_STARTED((byte)85),
	TURN_FINISHED((byte)86),
	MATCH_STARTED((byte)87),
	MATCH_FINISHED((byte)88);
	
	private byte id;
	
	private TurnGameProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "TurnGameProtocol." + this.toString();
	}

	public static boolean contains(byte value) {
		TurnGameProtocol[] values = TurnGameProtocol.values();
		for (TurnGameProtocol turnGameProtocol : values) {
			if (turnGameProtocol.getId() == value) {
				return true;
			}
		}
		return false;
	}
}