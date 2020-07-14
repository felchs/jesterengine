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

import java.io.Serializable;

public class TurnNodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	protected boolean newMatch;
	
	protected boolean newTurn;
	
	protected boolean hasGameFinished;
	
	public void setNewMatch(boolean newMatch) {
		this.newMatch = newMatch;
	}
	
	public boolean isNewMatch() {
		return newMatch;
	}
	
	public void setNewTurn(boolean newTurn) {
		this.newTurn = newTurn;
	}
	
	public boolean isNewTurn() {
		return newTurn;
	}
	
	public boolean hasGameFinished() {
		return hasGameFinished;
	}
	
	public void setHasGameFinished(boolean hasGameFinished) {
		this.hasGameFinished = hasGameFinished;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("New Match: " + newMatch + " New Turn: " + newTurn + " hasGameFinished: " + hasGameFinished);
		return str.toString();
	}
}
