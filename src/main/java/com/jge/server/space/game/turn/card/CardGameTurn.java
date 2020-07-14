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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.jge.server.space.game.GameScore;
import com.jge.server.space.game.turn.GameTurn;
import com.jge.server.space.game.turn.ScreenPositionInterface;
import com.jge.server.space.game.turn.TurnBasedGame;
import com.jge.server.space.game.turn.TurnNode;
import com.jge.server.space.game.turn.TurnNodeInfo;
import com.jge.server.space.game.turn.TurnPlayer;
import com.jge.server.utils.DGSLogger;

public abstract class CardGameTurn extends GameTurn {

	protected Map<Byte, ArrayList<Byte>> tableCardsMap = new HashMap<Byte, ArrayList<Byte>>();

	protected CardDeck deck;
	
	protected CardHands cardHands;

	public CardGameTurn(TurnBasedGame turnBasedGame) {
		super(turnBasedGame);
	}
	
	public void setDeck(CardDeck deck) {
		this.deck = deck;
	}
	
	public CardDeck getDeck() {
		return deck;
	}
	
	public void setCardHands(CardHands cardHands) {
		this.cardHands = cardHands;
	}

	public CardHands getCardHands() {
		return cardHands;
	}
	
	@Override
	protected void setupNewMatch(TurnNodeInfo info, TurnBasedGame turnBasedGame) {
		DGSLogger.log("CardGameTurn.setupNewMatch, info: " + info);
		super.setupNewMatch(info, turnBasedGame);
		DGSLogger.log("CardGameTurn.setupNewMatch 2, info: " + info);
		TurnNode newTurnNode = createNodeInstance();
		GameScore matchScore = newTurnNode.getMatchScore();
		
		// just updating the score
		copyMatchScore(matchScore);
		
		turnBasedGame.setupNewNode(newTurnNode);
	}

	protected void copyMatchScore(GameScore newMatchScore) {
		GameScore prevMatchScore = getMatchScore();
		prevMatchScore.copyArguments(newMatchScore);
	}
	
	@Override
	public void turnStart() {
		super.turnStart();
		
		DGSLogger.log("CardGameTurn.turnStart, checkIfMustCreateCardsOnTurnStart(): " + checkIfMustCreateCardsOnTurnStart());

		if (checkIfMustCreateCardsOnTurnStart()) {
			createHands();
			
			createDeck();

			initalDistribution();
		}
		
		setAndSendSelectableCards();
	}
	
//	@Override
//	protected void checkSetupNextPlayersAfterPlay() {
//		DGSLogger.log("CardGameTurn.checkSetupNextPlayersAfterPlay(), playersToPlay: " + playersToPlay);
//		if (playersToPlay > 0) {
//			TurnBasedGame turnGame = turnBasedGame.getForUpdate();
//			turnGame.setupNextPlayers();
//		}
//	}
	
	protected boolean checkIfMustCreateCardsOnTurnStart() {
		return isFirstPlay();
	}

	public void addTableCard(Byte tableIdx, Byte cardId) {
		ArrayList<Byte> tableCards = getTableCards(tableIdx);
		
		if (tableCards.contains(cardId)) {
			throw new RuntimeException("The card: " + cardId + " already exists on tableCards");
		}
		
		tableCards.add(cardId);
		
		DGSLogger.log("CardGameTurn.addTableCard: " + cardId +  " tablecardsSz: " + tableCards.size());
	}
	
	public ArrayList<Byte> getTableCards(Byte tableIdx) {
		ArrayList<Byte> tableCards = tableCardsMap.get(tableIdx);
		if (tableCards == null) {
			tableCards = new ArrayList<Byte>();
			tableCardsMap.put(tableIdx, tableCards);
		}
		return tableCards;
	}
	
	public void setTableCards(Byte tableIdx, ArrayList<Byte> tableCards) {
		tableCardsMap.put(tableIdx, tableCards);
		//this.tableCards = tableCards;
	}
	
	public void setTableCardsMap(Map<Byte, ArrayList<Byte>> tableCardsMap) {
		Set<Byte> keys = tableCardsMap.keySet();
		for (Byte tableIdx : keys) {
			ArrayList<Byte> tableCards = tableCardsMap.get(tableIdx);
			setTableCards(tableIdx, tableCards);
		}
	}
	
	public Map<Byte, ArrayList<Byte>> getTableCardsMap() {
		return tableCardsMap;
	}
		
	public ArrayList<Byte> getTableCardsIds(Byte tableIdx) {
		ArrayList<Byte> tableCardsAsBytes = new ArrayList<Byte>();
		ArrayList<Byte> tableCards = getTableCards(tableIdx);
		Iterator<Byte> it = tableCards.iterator();
		while (it.hasNext()) {
			tableCardsAsBytes.add(it.next());
		}
		return tableCardsAsBytes;
	}
	
	public byte[] getTableCardsCopy(Byte tableIdx) {
		ArrayList<Byte> tableCards = getTableCards(tableIdx);
		int sz = tableCards.size();
		byte[] tableCardsCopy = new byte[sz];
		for (int i = 0; i < sz; i++) {
			tableCardsCopy[i] = tableCards.get(i);
		}
		return tableCardsCopy;
	}
	
	public ArrayList<Byte> getTableCardsIndex(Byte tableIdx) {
		ArrayList<Byte> tableCards = getTableCards(tableIdx);
		return tableCards;
	}
	
	public Byte getLastTableCardIndex(Byte tableIdx) {
		ArrayList<Byte> tableCards = getTableCards(tableIdx);
		int sz = tableCards.size();
		if (sz <= 0) {
			return -1;
		}
		
		return tableCards.get(sz);
	}
	
	public Card getLastTableCard(Byte tableIdx) {
		Byte lastIndex = getLastTableCardIndex(tableIdx);
		if (lastIndex == -1) {
			return null;
		}
		
		CardDeck cardDeck = deck;
		return cardDeck.getCardWithIndex(lastIndex);
	}

	public byte[] getPlayersCardsCodeCopy(int screenPos) {
		CardHands cardHands_ = cardHands;
		return cardHands_.getPlayersCardsCodeCopy(screenPos);		
	}
	
	public byte[] getPlayerCardsCodeSuitNumberCopy(int screenPos) {
		return cardHands.getPlayerCardsCodeSuitNumberCopy(screenPos);
	}
	
	public void moveTableCardsToDeck() {
		CardGame game = (CardGame)turnBasedGame;
		DGSLogger.log("CardGameTurn.moveTableCardsToDeck");
		game.sendMoveAllTableCardsToDeck();
	}

	@Override
	protected void updateOnPosPlayAndTurnUnfinished() {
		super.updateOnPosPlayAndTurnUnfinished();

		DGSLogger.log("CardGameTurn.updateOnPosplayAndTurnUnfinished()");

		setAndSendUnselectedCards();

		setAndSendSelectableCards();
	}
	
//	@Override
//	protected void updateOnTurnFinish() {
//		super.updateOnTurnFinish();
//		DGSLogger.log("CardGameTurn.updateOnTurnFinish()");
//		
//		updateScore();
//	}
	
	public String getCARD_PREFIX() {
		return ((CardGame)turnBasedGame).getCARD_PREFIX();
	}
	
	public abstract void createHands();

	public abstract void createDeck();

	public abstract void initalDistribution();
	
	public void setAndSendSelectableCards() {
		DGSLogger.log("CardTurn.setAndSendSelectableCards");
		TurnBasedGame game = turnBasedGame;

		TurnPlayer player = game.getCurrentPlayer();
		ScreenPositionInterface screenPos = player.getScreenPosition();
	
		cardHands.updateSelectables(screenPos.getPositionIdx(), true);

		((CardGame)game).sendSelectables(player);
	}

	public void setAndSendUnselectedCards() {
		DGSLogger.log("CardGameTurn.setAndSendUnselectedAllObjects");
		cardHands.unSelectCards();

		//((CardGame) turnBasedGame.get()).sendUnSelectAllObjects();
	}
	
	
	// It's your choose to implement the card movement behavior 
	// which the card game you implementing needs
	
	public void moveCardToDeck(byte cardCode) { }
	
	public void moveCardsToDeck(byte[] cardCodes) { }
	
	public void moveCardToDeckIdx(byte cardCode, byte deckIdx) { }
	
	public void moveCardsToDeckIdx(byte[] cardCodes, byte deckIdx) { }
	
	public void moveCardToTable(byte cardCode) { }
	
	public void moveCardsToTable(byte[] cardCodes) { }
	
	public void moveCardToTableIdx(byte cardCode, byte tableIdx) { }
	
	public void moveCardsToTableIdx(byte[] cardCodes, byte tableIdx) { }
}