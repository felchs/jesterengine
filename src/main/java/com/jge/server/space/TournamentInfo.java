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
package com.jge.server.space;

public class TournamentInfo {
	public int id;
	public long initTime;
	public long roundTime;
	public int rounds;
	public int playersPerGame;
	public int playersPerTeam;
	public int minPlayersToStart;
	
	public TournamentInfo(int id, long initTime, long roundTime, int rounds, int playersPerGame, int playersPerTeam, int minPlayersToStart) {
		this.id = id;
		this.initTime = initTime;
		this.roundTime = roundTime;
		this.rounds = rounds;
		this.playersPerGame = playersPerGame;
		this.playersPerTeam = playersPerTeam;
		this.minPlayersToStart = minPlayersToStart;
	}
}
