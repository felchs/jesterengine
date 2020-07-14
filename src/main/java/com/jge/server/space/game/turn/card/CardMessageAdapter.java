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

import java.util.ArrayList;

import com.jge.server.space.game.turn.TurnMessageAdapter;

public class CardMessageAdapter extends TurnMessageAdapter {
	public void moveCardToDeck(byte cardCode) { }

	public void moveCardToTable(byte cardCode) { }
	
	public void moveCardToUnhiddenDeck(byte cardCode) { }

	public void giveCards() { }

	public void setCardId(byte cardCode, byte id) { }

	public void setCardsIds(ArrayList<Byte> cardCodes, ArrayList<Byte> ids) { }

	public void moveTableCardToDeck() { }

	public void movePlayerCardsToDeck() { }

}
