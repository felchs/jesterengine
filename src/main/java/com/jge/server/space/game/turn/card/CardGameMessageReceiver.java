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

import com.jge.server.client.Client;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.game.turn.TurnBasedGame;
import com.jge.server.space.game.turn.TurnGameMessageReceiver;
import com.jge.server.utils.DGSLogger;

public abstract class CardGameMessageReceiver extends TurnGameMessageReceiver {

	public CardGameMessageReceiver(Space gameSpace) {
		super(gameSpace);
	}
	
	@Override
	public boolean isPlayMessage(byte protocol) {
		return CardGameProtocol.MOVE_CARD_TO_DECK.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARD_TO_DECK_IDX.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARDS_TO_DECK.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARDS_TO_DECK_IDX.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARD_TO_TABLE.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARD_TO_TABLE_IDX.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARDS_TO_TABLE.getId().byteValue() == protocol ||
			CardGameProtocol.MOVE_CARDS_TO_TABLE_IDX.getId().byteValue() == protocol;
	}

	@Override
	public boolean receivedTurnMessage(Channel channel, Client client, byte event, TurnBasedGame game, ByteBuffer msg) {
		if (CardGameProtocol.MOVE_CARD_TO_DECK.getId() == event) {
			byte cardCode = msg.get();
			DGSLogger.log("Move card to deck. Card: " + cardCode);
			((CardGame)game).moveCardToDeck(cardCode);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARD_TO_DECK_IDX.getId() == event) {
			byte cardCode = msg.get();
			byte deckIdx = msg.get();
			DGSLogger.log("Move card to deck. Card: " + cardCode + " deck idx: " + deckIdx);
			((CardGame)game).moveCardToDeckIdx(cardCode, deckIdx);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARDS_TO_DECK.getId() == event) {
			byte numCards = msg.get();
			byte cardCodes[] = new byte[numCards];
			for (byte i = 0; i < numCards; i++) {
				byte cardCode = msg.get();
				cardCodes[i] = cardCode;
			}
			DGSLogger.log("Move cards to table. num cards: " + numCards);
			((CardGame)game).moveCardsToDeck(cardCodes);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARDS_TO_DECK_IDX.getId() == event) {
			byte numCards = msg.get();
			byte deckIdx = msg.get();
			byte cardCodes[] = new byte[numCards];
			for (byte i = 0; i < numCards; i++) {
				byte cardCode = msg.get();
				cardCodes[i] = cardCode;
			}
			DGSLogger.log("Move card to deck. num cards: " + numCards + " deck idx: " + deckIdx);
			((CardGame)game).moveCardsToDeckIdx(cardCodes, deckIdx);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARD_TO_TABLE.getId() == event) {
			byte cardCode = msg.get();
			DGSLogger.log("Move card to table. Card: " + cardCode);
			((CardGame)game).moveCardToTable(cardCode);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARD_TO_TABLE_IDX.getId() == event) {
			byte cardCode = msg.get();
			byte tableIdx = msg.get();
			DGSLogger.log("Move card to table. Card: " + cardCode + " deck idx: " + tableIdx);
			((CardGame)game).moveCardToDeckIdx(cardCode, tableIdx);
			return true;
		}
		else if (CardGameProtocol.MOVE_CARDS_TO_TABLE.getId() == event) {
			byte numCards = msg.get();
			byte cardCodes[] = new byte[numCards];
			for (byte i = 0; i < numCards; i++) {
				byte cardCode = msg.get();
				cardCodes[i] = cardCode;
			}
			DGSLogger.log("Move cards to table. num cards: " + numCards);
			((CardGame)game).moveCardsToTable(cardCodes);
			return true;			
		}
		else if (CardGameProtocol.MOVE_CARDS_TO_TABLE_IDX.getId() == event) {
			byte numCards = msg.get();
			byte tableIdx = msg.get();
			byte cardCodes[] = new byte[numCards];
			for (byte i = 0; i < numCards; i++) {
				byte cardCode = msg.get();
				cardCodes[i] = cardCode;
			}
			DGSLogger.log("Move card to table. num cards: " + numCards + " table idx: " + tableIdx);
			((CardGame)game).moveCardsToTableIdx(cardCodes, tableIdx);
			return true;
		}
		
		return false;
	}
}