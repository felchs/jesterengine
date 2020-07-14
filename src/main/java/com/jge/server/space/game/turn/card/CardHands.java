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

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

public class CardHands {
	protected final String CARD_PREFIX;
	
	protected int numPlayers;
	protected int numCardsByPlayer;
	
	// card codes
	protected CopyOnWriteArrayList<CopyOnWriteArrayList<Byte>> playerCards = new CopyOnWriteArrayList<CopyOnWriteArrayList<Byte>>();
		
	public CardHands(String cardPrefix, int numPlayers, int numCardsByPlayer) {
		this.CARD_PREFIX = cardPrefix;
		this.numPlayers = numPlayers;
		this.numCardsByPlayer = numCardsByPlayer;
		
		for (int i = 0; i < numPlayers; i++) {
			playerCards.add(null);
		}
	}
	
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public void setNumCardsByPlayer(int numCardsByPlayer) {
		this.numCardsByPlayer = numCardsByPlayer;
	}
	
	public int getNumCardsByPlayer() {
		return numCardsByPlayer;
	}
	
	public int getNumCardsOnDeckByPlayerWithIndex(int index) {
		CopyOnWriteArrayList<Byte> cards = playerCards.get(index);
		return cards.size();
	}
	
	public boolean isHandEmpty(int index) {
		CopyOnWriteArrayList<Byte> cards = playerCards.get(index);
		return cards.isEmpty();
	}
	
	public CopyOnWriteArrayList<Byte> getPlayerCardsWithIndex(int index) {
		return playerCards.get(index);
	}
	
	public byte[] getPlayersCardsCodeCopy(int index) {
		CopyOnWriteArrayList<Byte> cards = playerCards.get(index);
		if (cards == null) {
			return new byte[0];
		}
		int szCards = cards.size();
		byte[] cardsList = new byte[szCards];
		for (int i = 0; i < szCards; i++) {
			cardsList[i] = cards.get(i);
		}
		return cardsList;
	}
	
	public Card[] getPlayersCards(int index) {
		CopyOnWriteArrayList<Byte> cards = playerCards.get(index);
		if (cards == null) {
			return new Card[0];
		}
		int szCards = cards.size();
		Card[] cardsList = new Card[szCards];
		for (int i = 0; i < szCards; i++) {
			Byte cardCode = cards.get(i);
			Card card = getCardWithCode(cardCode);
			cardsList[i] = card;
		}
		return cardsList;
	}
	
	public byte[] getPlayerCardsCodeSuitNumberCopy(int index) {
		CopyOnWriteArrayList<Byte> cards = playerCards.get(index);
		if (cards == null) {
			return new byte[0];
		}
		int szCards = cards.size();
		byte[] cardsList = new byte[szCards * 3];
		int idx = 0;
		for (int i = 0; i < szCards; i++) {
			Byte cardCode = cards.get(i);
			Card card = getCardWithCode(cardCode);
			
			// code
			cardsList[idx++] = cardCode;
			// suit
			cardsList[idx++] = (byte) card.getSuit().ordinal();
			// number
			cardsList[idx++] = card.getNumber();
		}
		return cardsList;		
	}

	public Card getCardWithCode(byte cardCode) {
		String cardName = CARD_PREFIX + cardCode;
		return (Card)MappingUtil.getObject(cardName);
	}
	
	public Card[] getCardsWithCodes(CopyOnWriteArrayList<Byte> cardCodes) {
		int len = cardCodes.size();
		Card[] cards = new Card[len];
		Iterator<Byte> it = cardCodes.iterator();
		int i = 0;
		while (it.hasNext()) {
			byte cardCode = it.next();
			cards[i++] = getCardWithCode(cardCode);
		}
		return cards;
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
	
	public void setCards(int groupIndex, CopyOnWriteArrayList<Byte> cards) {
		playerCards.set(groupIndex, cards);
		
		int sz = cards.size();
		for (int i = 0; i < sz; i++) {
			String cardName = CARD_PREFIX + cards.get(i);
			Card card = (Card)MappingUtil.getObject(cardName);
			card.setScreenOwnerIndex(groupIndex);
		}
	}
	
	public void updateSelectables(int groupIndex, boolean selectableState) {		
		CopyOnWriteArrayList<Byte> selectable = playerCards.get(groupIndex);
		
		int sz = selectable.size();
		for (int i = 0; i < sz; i++) {
			String cardName = CARD_PREFIX + selectable.get(i);
			Card card = (Card)MappingUtil.getObject(cardName);
			card.setSelectable(selectableState);
		}
	}
	
	public CopyOnWriteArrayList<Byte> getGroupCards(int groupIndex) {
		return playerCards.get(groupIndex);
	}
	
	// maybe this can be slow
	public CopyOnWriteArraySet<Card> getSelectablesByGroup(int groupIndex) {
		CopyOnWriteArraySet<Card> cardSet = new CopyOnWriteArraySet<Card>();
		CopyOnWriteArrayList<Byte> group = playerCards.get(groupIndex);
		int sz = group.size();
		for (int i = 0; i < sz; i++) {
			Byte cardIndex = group.get(i);
			Card card = getCardWithCode(cardIndex);
			if (card.isSelectable()) {
				cardSet.add(card);
			}
		}
		return cardSet;
	}
	
	public void unSelectCards() {
		for (int i = 0; i < numPlayers; i++) {
			updateSelectables(0, false);
		}
	}

	public int removeCardsWithCodes(int playerCardsIndex, byte[] cardCodes) {
		CopyOnWriteArrayList<Byte> playerCards = getPlayerCardsWithIndex(playerCardsIndex);
		for (int i = 0; i < cardCodes.length; i++) {
			Byte cardCode = cardCodes[i];
			DGSLogger.log("Cardhands.removeCardWithCodes, playerCardsIndex: " + playerCardsIndex + ", cardCode: " + cardCode);
			boolean remove = playerCards.remove(cardCode);
			if (!remove) {
				throw new RuntimeException("Card could not be removed, cardCode: " + cardCode + ", playerCardsIndex: " + playerCardsIndex);
			}
		}
		
		return playerCards.size();
	}
	
	public Card removeCardWithCode(int playerCardsIndex, Byte cardCode) {
		DGSLogger.log("Cardhands.removeCardWithCode, playerCardsIndex: " + playerCardsIndex + ", cardCode: " + cardCode);
		CopyOnWriteArrayList<Byte> playerCards = getPlayerCardsWithIndex(playerCardsIndex);
		boolean remove = playerCards.remove(cardCode);
		if (!remove) {
			DGSLogger.log("removeCardWithCode, playerCardsIndex: " + playerCardsIndex + ", cardCode: " + cardCode);
			Iterator<Byte> it = playerCards.iterator();
			while (it.hasNext()) {
				DGSLogger.log("removeCardWithCode, playerCardCode: " + it.next());
			}
			throw new RuntimeException();
			
		}
		return getCardWithCode(cardCode);
	}
}