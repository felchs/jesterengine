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
package com.jge.server.space.game.turn;

import com.jge.server.utils.DGSLogger;

public abstract class GameTurn extends TurnNode {
	protected int numPlay;
	
	protected byte turnPlay;
	
	// the number of players to play on this turn
	// 0 means the turn is over
	protected int playersToPlay;
	
	protected boolean isOneTurnManyPlaysGame;
	
	public GameTurn(TurnBasedGame turnBasedGame) {
		super(turnBasedGame);
		
		resetPlayersToPlay();
	}
	
	private void incNumPlay() {
		numPlay++;
	}
	
	public boolean isFirstPlay() {
		return numPlay == 0;
	}

	public int getNumPlay() {
		return numPlay;
	}
	
	private void resetNumPlay() {
		numPlay = 0;
	}
	
	public void setTurnPlay(byte turnPlay) {
		DGSLogger.log("GameTurn.setTurnPlay: " + turnPlay);
		this.turnPlay = turnPlay;
	}
	
	public boolean isOneTurnManyPlaysGame() {
		return isOneTurnManyPlaysGame;
	}
	
	public void setOneTurnManyPlaysGame(boolean isOneTurnManyPlaysGame) {
		this.isOneTurnManyPlaysGame = isOneTurnManyPlaysGame;
	}
	
	public byte getTurnPlay() {
		return turnPlay;
	}
	
	public boolean isFirstMatchPlay() {
		return turnPlay == 0;
	}
	
	private void incTurnPlay() {
		turnPlay++;	
	}
	
	@Override
	public void onPrePlayEvent(byte event) {
		decPlayersToPlay();
	}
	
	protected void updateOnPosPlayAndTurnUnfinished() {
		DGSLogger.log("GameTurn.updateOnPosplayAndTurnUnfinished()");
		checkSetupNextPlayersAfterPlay();

		doActionBasedOnPlayersToPlay();

		incNumPlay();
	}

	protected void doActionBasedOnPlayersToPlay() {
		DGSLogger.log("GameTurn.doActionBasedOnPlayersToPlay(), playersToPlay: " + playersToPlay);
		
//		if (playersToPlay > 0) {
//			turnBasedGame.get().notifyCurrentPlayer();
//		}
	}
		
	@Override
	protected void updateOnTurnFinish() {
		DGSLogger.log("GameTurn.updateOnTurnFinish()");
		incTurnPlay();
		
		resetPlayersToPlay();

		resetNumPlay();
		
		updateScore();
	}
	
	protected void decPlayersToPlay() {
		if (!isOneTurnManyPlaysGame()) {
			playersToPlay--;
		}
	}
	
	public void setPlayersToPlay(int playersToPlay) {
		if (!isOneTurnManyPlaysGame()) {
			this.playersToPlay = playersToPlay;
		}
	}
	
	protected void resetPlayersToPlay() {
		int numPlayerToPlayByTurn = getNumPlayerToPlayByTurn();
		setPlayersToPlay(numPlayerToPlayByTurn);
	}
	
	@Override
	protected boolean hasTurnFinished() {
		boolean hasTurnFinishedByPlayersToPlay = hasTurnFinishedByPlayersToPlay();
		boolean hasTurnFinishedByEvent = hasTurnFinishedByEvent();
		DGSLogger.log("GameTurn.hasTurnFinished(), hasTurnFinishedByPlayersToPlay: " + hasTurnFinishedByPlayersToPlay + ", hasTurnFinishedByEvent: " + hasTurnFinishedByEvent);
		boolean hasTurnFinished = hasTurnFinishedByPlayersToPlay || hasTurnFinishedByEvent;
		return hasTurnFinished;
	}

	protected boolean hasTurnFinishedByPlayersToPlay() {
		boolean hasTurnFinished = false;
		if (!isOneTurnManyPlaysGame()) {
			hasTurnFinished = playersToPlay == 0;
		}
		DGSLogger.log("GameTurn.hasTurnFinished(), hasTurnFinished: " + hasTurnFinished + " playerstoPlay: " + playersToPlay);
		return hasTurnFinished;
	}
	
	//protected abstract void checkSetupNextPlayersAfterPlay();
	protected void checkSetupNextPlayersAfterPlay() {
		DGSLogger.log("GameTurn.checkSetupNextPlayersAfterPlay(), playersToPlay: " + playersToPlay);
		if (isOneTurnManyPlaysGame() || playersToPlay > 0) {
			TurnBasedGame turnGame = turnBasedGame;
			turnGame.setupNextPlayers();
			turnBasedGame.notifyCurrentPlayer();
		}
	}
	
	protected abstract int getNumPlayerToPlayByTurn();

	protected abstract boolean hasTurnFinishedByEvent();
}