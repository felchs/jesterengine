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

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.utils.MappingUtil;
import com.jge.server.utils.Randomizer;

public abstract class CardDeck implements Serializable {
	private static final long serialVersionUID = 1L;

	protected CopyOnWriteArrayList<Byte> cards = new CopyOnWriteArrayList<Byte>();
	
	protected CopyOnWriteArrayList<Byte> usedCards = new CopyOnWriteArrayList<Byte>();
	
	protected String CARD_PREFIX;
	
	public CardDeck(String cardPrefix) {
		this.CARD_PREFIX = cardPrefix;
	}
	
	public CopyOnWriteArrayList<Byte> getCards() {
		return cards;
	}
	
	public CopyOnWriteArrayList<Byte> getUsedCards() {
		return usedCards;
	}

	public Byte getRandomCard() {
		int randomCardIndex = Randomizer.nextInt(cards.size());
		Byte card = cards.remove(randomCardIndex);
		usedCards.add(card);
		return card;
	}
	
	public Card removeFirstCard() {
		byte firstCard = cards.remove(0);
		return getCardWithIndex(firstCard);
	}
	
	public byte removeFirstCardCode() {
		byte firstCard = cards.remove(0);
		return firstCard;
	}
	
	public Card removeLastCard() {
		int lastIndex = cards.size() - 1;
		byte lastCard = cards.remove(lastIndex);
		return getCardWithIndex(lastCard);
	}
	
	public byte removeLastCardCode() {
		int lastIndex = cards.size() - 1;
		byte lastCard = cards.remove(lastIndex);
		return lastCard;
	}
	
	public byte[] removeAllCardCodes() {
		int sz = cards.size();
		byte[] removedCards = new byte[sz];
		for (int i = 0; i < sz; i++) {
			byte cardCode = cards.remove(0);
			removedCards[i] = cardCode;
		}
		return removedCards;
	}
	
	public Card[] removeAllCards() {
		int sz = cards.size();
		Card[] removedCards = new Card[sz];
		for (int i = 0; i < sz; i++) {
			byte cardCode = cards.remove(0);
			Card card = getCardWithIndex(cardCode);
			removedCards[i] = card;
		}
		return removedCards;
	}
	
	public CopyOnWriteArrayList<Byte> getRandomCards(int numOfCards) {
		CopyOnWriteArrayList<Byte> randomCards = new CopyOnWriteArrayList<Byte>();
		
		for (int i = 0; i < numOfCards; i++) {
			randomCards.add(getRandomCard());
		}
		
		return randomCards;
	}
	
	public Card getCardWithIndex(byte cardIndex) {
		String cardName = getCardName(cardIndex);
		return (Card)MappingUtil.getObject(cardName);
	}
	
	public String getCardName(byte cardIndex) {
		return CARD_PREFIX + cardIndex;
	}
	
	public void reset() {
		cards.clear();
	}
	
	public abstract void createCards();
	
	public abstract void destroyCards();
}