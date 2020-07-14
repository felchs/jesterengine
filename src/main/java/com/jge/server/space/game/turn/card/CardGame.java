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

import com.jge.server.ServerMessage;
import com.jge.server.space.game.turn.TurnBasedGame;
import com.jge.server.space.game.turn.TurnNode;
import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

public abstract class CardGame extends TurnBasedGame {
	private final String CARD_PREFIX;
	
	private int numCardsByPlayer;

	public CardGame(int id, byte gameEnum, int minPlayersToStart, int maxPlayers, int numCardsByPlayer, long automaticPlayTime) {
		super(id, gameEnum, minPlayersToStart, maxPlayers, automaticPlayTime);
		this.CARD_PREFIX = id + ":CARD:";
		this.numCardsByPlayer = numCardsByPlayer;
	}
	
	public String getCARD_PREFIX() {
		return CARD_PREFIX;
	}
	
	public Card getCardWithCode(byte cardCode) {
		String cardName = CARD_PREFIX + cardCode;
		return (Card)MappingUtil.getObject(cardName);
	}
	
	public Card[] getCardsWithCodes(byte[] cardCodes) {
		int len = cardCodes.length;
		Card[] cards = new Card[len];
		for (int i = 0; i < len; i++) {
			byte cardCode = cardCodes[i];
			cards[i] = getCardWithCode(cardCode);
		}
		return cards;
	}
	
	public void setNumCardsByPlayer(int numCardsByPlayer) {
		this.numCardsByPlayer = numCardsByPlayer;
	}
	
	public int getNumCardsByPlayer() {
		return numCardsByPlayer;
	}
	
	@Override
	public void onPreStartClear() {
		super.onPreStartClear();
		sendMoveAllTableCardsToDeck();
		sendMoveAllPlayerCardsToDeck();
	}
	
	@Override
	public void setupNewNode(TurnNode node) {
		super.setupNewNode(node);
	}
	
	public void moveCardToDeck(byte cardCode) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardToDeck(cardCode);
	}
	
	public void moveCardsToDeck(byte[] cardCodes) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardsToDeck(cardCodes);
	}
	
	public void moveCardToDeckIdx(byte cardCode, byte deckIdx) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardToDeckIdx(cardCode, deckIdx);
	}
	
	public void moveCardsToDeckIdx(byte[] cardCodes, byte deckIdx) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardsToDeckIdx(cardCodes, deckIdx);
	}
	
	public void moveCardToTable(byte cardCode) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardToTable(cardCode);
	}
	
	public void moveCardsToTable(byte[] cardCodes) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardsToTable(cardCodes);
	}
	
	public void moveCardToTableIdx(byte cardCode, byte tableIdx) {
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardToTableIdx(cardCode, tableIdx);
	}
	
	public void moveCardsToTableIdx(byte[] cardCodes, byte tableIdx) {
		DGSLogger.log("CardGame.moveCardsToTableIdx(), numCardCodes: " + cardCodes.length + " tableIdx:" + tableIdx);
		CardGameTurn cardGameTurn = ((CardGameTurn)currentTurnNode);
		cardGameTurn.moveCardsToTableIdx(cardCodes, tableIdx);
	}
	
	// Output events ----------------------------------------------------------

	public void sendCardId(byte cardId, byte codes) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.SET_CARD_ID);
		serverMessage.put(cardId);
		serverMessage.put(codes);
		DGSLogger.log("CardGame.sendCardId id: " + id, false);
		serverMessage.sendToPlayers(this);
	}

	public void sendCardsIds(byte cardIds[], byte codes[]) {
		DGSLogger.log("CardGame.sendCardsId, cardcodeslen: " + cardIds.length + " objectslen: " + codes.length);
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.SET_CARDS_ID);
		byte numObjects = (byte)cardIds.length;
		serverMessage.put(numObjects);
		
		for (int i = 0; i < numObjects; i++) {
			byte cc = cardIds[i];
			DGSLogger.log("Card code: " + cc);
			serverMessage.put(cc);
		}
		numObjects = (byte)codes.length;
		serverMessage.put(numObjects);
		for (int i = 0; i < numObjects; i++) {
			byte co = codes[i];
			DGSLogger.log("Object: " + co);
			serverMessage.put(co);
		}
		serverMessage.sendToPlayers(this);
	}

	public void sendGiveCards() {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.GIVE_CARDS);
		DGSLogger.log("CardGame.sendGiveCards id: " + id);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveCardToDeck(byte cardCode) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARD_TO_DECK);
		serverMessage.put(cardCode);
		DGSLogger.log("CardGame.sendMoveCardToDeck id: " + id + " cardIndex: " + cardCode);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveCardsToDeck(byte[] cardCodes) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARD_TO_DECK);
		byte numCards = (byte) cardCodes.length;
		serverMessage.put(numCards);
		for (byte i = 0; i < numCards; i++) {
			serverMessage.put(cardCodes[i]);
		}
		DGSLogger.log("CardGame.sendMoveCardToDeck id: " + id + " numCards: " + numCards);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveCardToDeckIdx(byte cardCode, byte deckIdx) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARD_TO_DECK_IDX);
		serverMessage.put(cardCode);
		serverMessage.put(deckIdx);
		DGSLogger.log("CardGame.sendMoveCardToDeck id: " + id + " cardIndex: " + cardCode);
		serverMessage.sendToPlayers(this);
	}

	public void sendMoveCardsToDeckIdx(byte[] cardCodes, byte deckIdx) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARDS_TO_DECK_IDX);
		byte numCards = (byte) cardCodes.length;
		serverMessage.put(numCards);
		serverMessage.put(deckIdx);
		for (byte i = 0; i < numCards; i++) {
			serverMessage.put(cardCodes[i]);
		}
		DGSLogger.log("CardGame.sendMoveCardToDeck id: " + id + " numCards: " + numCards);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveCardToTable(byte cardCode) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARD_TO_TABLE);
		serverMessage.put(cardCode);
		DGSLogger.log("CardGame.sendMoveCardToTable id: " + id + " cardIndex: " + cardCode);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveCardsToTable(byte[] cardCodes) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARD_TO_TABLE);
		byte numCards = (byte) cardCodes.length;
		serverMessage.put(numCards);
		for (byte i = 0; i < numCards; i++) {
			serverMessage.put(cardCodes[i]);
		}
		DGSLogger.log("CardGame.sendMoveCardToTable id: " + id + " numCards: " + numCards);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveCardToTableIdx(byte cardCode, byte tableIdx) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARD_TO_TABLE_IDX);
		serverMessage.put(cardCode);
		serverMessage.put(tableIdx);
		DGSLogger.log("CardGame.sendMoveCardToTable id: " + id + " cardIndex: " + cardCode);
		serverMessage.sendToPlayers(this);
	}

	public void sendMoveCardsToTableIdx(byte[] cardCodes, byte tableIdx) {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_CARDS_TO_TABLE_IDX);
		byte numCards = (byte) cardCodes.length;
		serverMessage.put(numCards);
		serverMessage.put(tableIdx);
		for (byte i = 0; i < numCards; i++) {
			serverMessage.put(cardCodes[i]);
		}
		DGSLogger.log("CardGame.sendMoveCardToTable id: " + id + " numCards: " + numCards);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveAllTableCardsToDeck() {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_ALL_TABLE_CARDS_TO_DECK);
		serverMessage.put(false);
		DGSLogger.log("CardGame.sendMoveCardsToDeck id: " + id);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMoveAllPlayerCardsToDeck() {
		ServerMessage serverMessage = new ServerMessage(getId(), CardGameProtocol.MOVE_ALL_PLAYER_CARDS_TO_DECK);
		DGSLogger.log("CardGame.sendMovePlayerCardsToDeck id: " + id);
		serverMessage.sendToPlayers(this);
	}

}