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
package com.jge.server.space.game.action;

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum ActionGameProtocol implements Protocol<Byte> {
	SNAPSHOT((byte)81),
	ACTION((byte)82),
	MOVE((byte)83);
	
	private byte id;

	private ActionGameProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "ActionGameProtocol." + this.toString();
	}

	public static boolean contains(byte value) {
		ActionGameProtocol[] values = ActionGameProtocol.values();
		for (ActionGameProtocol actionGameProtocol : values) {
			if (actionGameProtocol.getId() == value) {
				return true;
			}
		}
		return false;
	}
}
