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

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.session.ClientSession;
import com.jge.server.utils.MappingUtil;

/**
 * The <code>GamePlayer</code> class represents a proxy to 
 * <code>Client class.
 * The players are created when the user enters on a game space
 * that holds a game
 * 
 */
public class Player {
	private Client client;
	
	private String playerName;
	
	private int gameTeamHandle;
	
	private String gameTeamName;
	
	public Player(Client client, String playerName) {
		if (client != null) {
			setClient(client);
			client.setPlayer(this);
		}
		
		setPlayerName(playerName);
	}
	
	public void setClient(Client client) {
		if (this.client != null) {
			throw new RuntimeException("Player.setClient, Client was not null: " + this.client.getName());
		}
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setTeamName(String gameTeamName) {
		this.gameTeamName = gameTeamName;
	}
	
	public short getPlayerHandle() {
		return client.getId();
	}
	
	public String getTeamName() {
		return gameTeamName;
	}
	
	public void setTeamHandle(int teamHandle) {
		this.gameTeamHandle = teamHandle;
	}
	
	public int getGameTeamHandle() {
		return gameTeamHandle;
	}

	public GameTeam getGameTeam() {
		return (GameTeam)MappingUtil.getObject(gameTeamName);
	}
	
	protected GameScore createScore() {
		return new GameScore();
	}
	
	public ClientSession getClientSession() {
		return client.getClientSession();
	}
	
	public String getClientName() {
		return client.getName();
	}
	
	public short getClientId() {
		return client.getId();
	}
	
	public String getSessionName() {
		return client.getSessionName();
	}
	
	public void setPlayerName(String playerName) {
		if (this.playerName != null)  {
			throw new RuntimeException("Player.setPlerName, this.playerName != null: " + this.playerName);
		}
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	public void getInfo(ServerMessage info) {
		info.put(gameTeamHandle);
	}
	
	public boolean isRobot() {
		return false;
	}
	
	public boolean isAutomaticPlayer() {
		return false;
	}
}