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
package com.jge.server.space.game.rating;

public class GameResultForRating {
	private String playerA_id;
	
	private String playerB_id;
	
	private float winPoints;
	
	private float losePoints;
	
	private float drawPoints;
	
	private float nPlays;
	
	public GameResultForRating(String playerA_id, String playerB_id, float winPoints, float losePoints, float drawPoints, float nPlays) {
		this.playerA_id = playerA_id;
		this.playerB_id = playerB_id;
		this.winPoints = winPoints;
		this.losePoints = losePoints;
		this.drawPoints = drawPoints;
		this.nPlays = nPlays;
	}
	
	public String getPlayerA_id() {
		return playerA_id;
	}
	
	public String getPlayerB_id() {
		return playerB_id;
	}

	public float getWinPoints() {
		return winPoints;
	}

	public float getLosePoints() {
		return losePoints;
	}

	public float getDrawPoints() {
		return drawPoints;
	}

	public float getNPlays() {
		return nPlays;
	}
}