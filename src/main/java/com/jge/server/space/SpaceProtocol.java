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
package com.jge.server.space;

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum SpaceProtocol implements Protocol<Byte> {
	ENTER((byte)1),
	EXIT((byte)2),
	INIT((byte)3),
	INFO((byte)4),
	UPDATE_CONNECTED_CLIENTS((byte)5),
	
	BYTE_ENABLE((byte)6),
	BYTE_ENABLES((byte)7),
	SHORT_ENABLE((byte)8),
	SHORT_ENABLES((byte)9),
	
	BYTE_VISIBLE((byte)10),
	BYTE_VISIBLES((byte)11),
	SHORT_VISIBLE((byte)12),
	SHORT_VISIBLES((byte)13),
	
	BYTE_SELECTION((byte)14),
	BYTE_SELECTIONS((byte)15),
	SHORT_SELECTION((byte)16),
	SHORT_SELECTIONS((byte)17),
	
	BYTE_SELECTABLE((byte)18),
	BYTE_SELECTABLES((byte)19),
	SHORT_SELECTABLE((byte)20),
	SHORT_SELECTABLES((byte)21),
	
	SET_BYTE_OBJ_BYTE_ID((byte)22),
	SET_BYTE_OBJS_BYTE_IDS((byte)23),
	SET_SHORT_OBJ_BYTE_ID((byte)24),
	SET_SHORT_OBJS_BYTE_IDS((byte)25),
	SET_SHORT_OBJ_SHORT_ID((byte)26),
	SET_SHORT_OBJS_SHORT_IDS((byte)27);

	private byte id;
	
	SpaceProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}
	
	public String getName() {
		return "SpaceProtocol." + this.toString();
	}
}