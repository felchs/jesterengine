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
package com.jge.server.space.game;

import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.utils.DGSLogger;

public class GameTeam {
	private int handle;

	private String name;
	
	private GameScore teamScore;
	
	private CopyOnWriteArrayList<String> gamePlayerNames = new CopyOnWriteArrayList<String>();

	public GameTeam(int handle, String name) {
		this.handle = handle;
		this.name = name;
	}
	
	public int getHandle() {
		return handle;
	}

	public String getName() {
		return name;
	}
	
	public GameScore getTeamScore() {
		return teamScore;
	}

	public void addGamePlayer(String playerName) {
		if (gamePlayerNames.contains(playerName)) {
			DGSLogger.log("GameTeam.addGamePlayer(), Player name: " + playerName + " already exists on this team: " + name);
			return;
		}

		gamePlayerNames.add(playerName);
	}
	
	public CopyOnWriteArrayList<String> getGamePlayerNames() {
		return gamePlayerNames;
	}
	
	public void removePlayer(Player player) {
		String playerName = player.getPlayerName();
		gamePlayerNames.remove(playerName);
	}
	
	public void removePlayerWithName(String playerName) {
		gamePlayerNames.remove(playerName);
	}
}