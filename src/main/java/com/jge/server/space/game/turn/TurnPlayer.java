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

import com.jge.server.client.Client;
import com.jge.server.space.game.Player;
import com.jge.server.utils.MappingUtil;

public class TurnPlayer extends Player {
	private TurnPlayerState playerState;

	private ScreenPositionInterface screenPosition;

	private String nextPlayerName;

	public TurnPlayer(Client client, String playerName, ScreenPositionInterface screenPosition) {
		super(client, playerName);

		this.screenPosition = screenPosition;
	}

	public void setPlayerState(TurnPlayerState playerState) {
		this.playerState = playerState;
	}

	public TurnPlayerState getPlayerState() {
		return playerState;
	}

	public ScreenPositionInterface getScreenPosition() {
		return screenPosition;
	}
	
	public void setScreenPosition(ScreenPositionInterface screenPosition) {
		this.screenPosition = screenPosition;
	}

	public void setNextPlayerName(String nextPlayerName) {
		this.nextPlayerName = nextPlayerName;
	}

	public TurnPlayer getNextPlayer() {
		return (TurnPlayer) MappingUtil.getObject(nextPlayerName);
	}
}