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

import com.jge.server.space.game.turn.TurnGameObject;

public class Card extends TurnGameObject {
	private static final long serialVersionUID = 1L;

	protected byte number = -1; // by now invalid card

	protected CardSuit suit;
		
	public Card(byte id, byte number, CardSuit suit) {
		super(id);
		this.number = number;
		this.suit = suit;
	}
	
	@Override
	public Byte getId() {
		return (Byte)super.getId();
	}
	
	public byte getNumber() {
		return number;
	}
		
	public CardSuit getSuit() {
		return suit;
	}
	
	@Override
	public String toString() {
		return "Card, number: " + number + " suit: " + suit; 
	}
}