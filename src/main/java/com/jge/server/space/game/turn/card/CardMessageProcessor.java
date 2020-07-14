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

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jge.server.space.game.turn.TurnMessageProcessor;
import com.jge.server.utils.DGSLogger;

public class CardMessageProcessor extends TurnMessageProcessor {

	public CardMessageProcessor(CardMessageAdapter cardMessageOutInterface) {
		super(cardMessageOutInterface);
	}

	protected CardMessageAdapter getCardOut() {
		return (CardMessageAdapter)spaceOutMessageInterface;
	}
	
	public boolean callFunction(byte event, ByteBuffer message) {
		if (super.callFunction(event, message)) {
			return true;
		}
		
		DGSLogger.log("CardMessageOut");
		
		if (CardGameProtocol.MOVE_CARD_TO_DECK.getId() == event) {
			byte cardCode = message.get();
			getCardOut().moveCardToDeck(cardCode);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARD_TO_TABLE.getId() == event) {
			byte cardCode = message.get();
			getCardOut().moveCardToTable(cardCode);
			return true;
		}
		else if (CardGameProtocol.GIVE_CARDS.getId() == event) {
			getCardOut().giveCards();
			return true;
		}
		else if (CardGameProtocol.SET_CARD_ID.getId() == event) {
			byte cardCode = message.get();
			byte id = message.get();
			getCardOut().setCardId(cardCode, id);
			return true;
		}
		else if (CardGameProtocol.SET_CARDS_ID.getId() == event) {
			ArrayList<Byte> cardCodes = new ArrayList<Byte>();
			ArrayList<Byte> ids = new ArrayList<Byte>();

			byte numObjs = message.get();
			for (byte i = 0; i < numObjs; i++) {
				byte cc = message.get();
				cardCodes.add(cc);
			}
			numObjs = message.get();
			for (byte i = 0; i < numObjs; i++) {
				byte co = message.get();
				ids.add(co);
			}
			getCardOut().setCardsIds(cardCodes, ids);
			return true;
		}
		else if (CardGameProtocol.MOVE_ALL_PLAYER_CARDS_TO_DECK.getId() == event) {
			getCardOut().moveTableCardToDeck();
			return true;
		}
		else if (CardGameProtocol.MOVE_ALL_PLAYER_CARDS_TO_DECK.getId() == event) {
			getCardOut().movePlayerCardsToDeck();
			return true;
		}
		
		return false;
	}
}