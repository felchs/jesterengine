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

import com.jge.server.space.SpaceState;
import com.jge.server.space.game.GameScore;
import com.jge.server.space.game.MatchScore;
import com.jge.server.utils.DGSLogger;

public abstract class TurnNode {
	protected SpaceState turnState = SpaceState.NOT_STARTED;

	protected TurnBasedGame turnBasedGame;

	protected MatchScore matchScore;

	public TurnNode(TurnBasedGame turnBasedGame) {
		this.turnBasedGame = turnBasedGame;
		this.matchScore = (MatchScore) createScoreNode();
	}
	
	public TurnBasedGame getTurnBasedGame() {
		return turnBasedGame;
	}

	public MatchScore getMatchScore() {
		return matchScore;
	}
	
	public void setMatchScore(MatchScore matchScore) {
		this.matchScore = matchScore;
	}
	
	public void setTurnState(SpaceState turnState) {
		this.turnState = turnState;
	}
	
	public SpaceState getTurnState() {
		return turnState;
	}

	/**
	 * Called by {@link TurnBasedGame} 
	 */
	public void turnStart() {
		DGSLogger.log("TurnNode.turnStart(), gamename: " + turnBasedGame.getName());
		setTurnState(SpaceState.STARTED);
	}
	
	/**
	 * Called after {@link TurnNode}{@link #onPosPlayEvent()}
	 */
	protected void updateOnPosPlayAndTurnFinished() {
		DGSLogger.log("TurnNode.updateOnPosPlayAndTurnFinished()");
		
		updateOnTurnFinish();

		TurnNodeInfo info = getTurnNodeInfo();
		
		TurnBasedGame turnBasedGame_ = turnBasedGame;
		turnBasedGame_.sendTurnFinished(info);

		setTurnState(SpaceState.FINISHED);

		DGSLogger.log("TurnNode.updateOnPosPlayAndTurnFinished(), info: " + info);
		
		if (info.hasGameFinished())
		{
			DGSLogger.log("TurnNode.updateOnPosPlayAndTurnFinished() game has finished!");
			turnBasedGame_.finish();
		}
		else if (info.isNewMatch())
		{
			setupNewMatch(info, turnBasedGame_);
		}
		else if (info.isNewTurn())
		{
			setupNewTurn(info, turnBasedGame_);
		}
		else
		{
			DGSLogger.log("TurnNode.updateOnPosPlayAndTurnFinished(), else");
		}
	}
	
	public void onPosPlayEvent(byte event) {
		DGSLogger.log("TurnNode.onPosPlayEvent(), hasTurnFinished: " + hasTurnFinished());
		if (hasTurnFinished()) {
			updateOnPosPlayAndTurnFinished();
		} else {
			updateOnPosPlayAndTurnUnfinished();
		}
	}

	public boolean hasTurnWinner() {
		return getMatchScore().hasWinner();
	}
	
	protected void setupNewMatch(TurnNodeInfo info, TurnBasedGame turnBasedGame)
	{
		turnBasedGame.sendMatchStarted(info);
		turnBasedGame.sendTurnStarted(info);
	}
	
	protected void setupNewTurn(TurnNodeInfo info, TurnBasedGame turnBasedGame)
	{
		turnBasedGame.sendTurnStarted(info);
	}
	
	public abstract void onPrePlayEvent(byte event);
	
	/**
	 * Called after {@link TurnNode}{@link #onPosPlayEvent()}
	 */
	protected abstract void updateOnPosPlayAndTurnUnfinished();
	
	protected abstract void updateBeforeTurnStart();
	
	protected abstract void updateAfterTurnStart();
	
	protected abstract void updateOnTurnFinish();
	
	protected abstract boolean hasTurnFinished();
	
	public abstract void updateScore();
	
	//protected abstract TurnNodeInfo getTurnNodeInfo();

	protected TurnNodeInfo getTurnNodeInfo() {
		TurnNodeInfo info = new TurnNodeInfo();
		
		MatchScore matchScore_ = matchScore;

		boolean hasMatchWinner = matchScore_.hasWinner();
		boolean hasGameFinished = matchScore_.getGameScore().hasWinner();
		
		info.setNewTurn(!hasMatchWinner && !hasGameFinished);
		info.setNewMatch(hasMatchWinner && !hasGameFinished);
		info.setHasGameFinished(hasGameFinished);
		
		DGSLogger.log("TurnNode.getTurnNodeInfo, HAS MATCH WINNER: " + hasMatchWinner + " IS NEW TURN: " + info.isNewTurn() +  " GAME FINISHED: " + hasGameFinished);

		return info;
	}

	public abstract TurnNode createNodeInstance();
	
	public abstract GameScore createScoreNode();
}
