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
package com.jge.server.space.game.turn.card;

import com.jge.server.Protocol;
import com.jge.server.utils.ByteUtils;

public enum CardGameProtocol implements Protocol<Byte> {
	GIVE_CARDS((byte)90),
	SET_CARD_ID((byte)91),
	SET_CARDS_ID((byte)92),
	
	MOVE_CARD_TO_DECK((byte)93),
	MOVE_CARD_TO_DECK_IDX((byte)94),
	MOVE_CARDS_TO_DECK((byte)95),
	MOVE_CARDS_TO_DECK_IDX((byte)96),
	
	MOVE_CARD_TO_TABLE((byte)97),
	MOVE_CARD_TO_TABLE_IDX((byte)98),
	MOVE_CARDS_TO_TABLE((byte)99),
	MOVE_CARDS_TO_TABLE_IDX((byte)100),
	
	MOVE_CARD_TO_PLAYER((byte)101),
	MOVE_CARDS_TO_PLAYER((byte)102),
	
	MOVE_ALL_TABLE_CARDS_TO_DECK((byte)103),
	MOVE_ALL_PLAYER_CARDS_TO_DECK((byte)104);
	
	private byte id;
	
	private CardGameProtocol(byte id) {
		this.id = id;
	}
	
	public Byte getId() {
		return id;
	}
	
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}

	public String getName() {
		return "CardGameProtocolS." + this.toString();
	}
	
	public static boolean contains(Byte value) {
		CardGameProtocol[] values = CardGameProtocol.values();
		for (CardGameProtocol cardGameProtocol : values) {
			if (cardGameProtocol.getId() == value) {
				return true;
			}
		}
		return false;
	}
	
}