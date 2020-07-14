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
package com.jge.server.space.game.achievements;

import java.util.Vector;

public class Achievements {
	private byte gameId;
	
	private int win;
	
	private int lose;
	
	private int draw;
	
	private int abandon;
	
	private Vector<Achievement> achievementsList = new Vector<Achievement>();
	
	public Achievements(byte gameId) {
		this.gameId = gameId;
	}
	
	public Vector<Achievement> getAchievementsList() {
		return achievementsList;
	}
	
	public byte getGameId() {
		return gameId;
	}

	public void addWin(int inc) {
		this.win += inc;
	}
	
	public int getWin() {
		return win;
	}
	
	public void addLose(int inc) {
		this.lose += inc;
	}
	
	public int getLose() {
		return lose;
	}
	
	public void addDraw(int inc) {
		this.draw += inc;
	}
	
	public int getDraw() {
		return draw;
	}

	public void addAbandon(int inc) {
		this.abandon += inc;
	}
	
	public int getAbandon() {
		return abandon;
	}
}