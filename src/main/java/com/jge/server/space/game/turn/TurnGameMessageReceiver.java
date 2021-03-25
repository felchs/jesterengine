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

import java.nio.ByteBuffer;
import java.util.logging.Level;

import com.jge.server.client.Client;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.game.GameSpace;
import com.jge.server.space.game.GameSpaceMessageReceiver;
import com.jge.server.utils.DGSLogger;

public abstract class TurnGameMessageReceiver extends GameSpaceMessageReceiver {
	public TurnGameMessageReceiver(Space gameSpace) {
		super(gameSpace);
	}

	protected boolean canSendMessage(byte protocol, boolean sessionTurn, boolean playMessage) {
		return sessionTurn && playMessage || !playMessage;
	}
	
	@Override
	protected boolean processEvent(Channel channel, Client client, byte event, ByteBuffer msg) {
		if (super.processEvent(channel, client, event, msg)) {
			return true;
		}
		
		GameSpace game = (GameSpace) getSpace();
		
		if (game.isGameRunning()) {
			TurnBasedGame turnGame = (TurnBasedGame)game;

			DGSLogger.log("TurnGameMessage.processEvent(), gameState: " + game.getSpaceState());

			boolean sessionTurn = true;//fixme uncoment: !sender.isHuman() || turnGame.isClientTurn((Client)sender);
			boolean playMessage = isPlayMessage(event);
			
			String senderId = client.isHuman() ? client.getName() : Client.ROBOT_PREFIX;
			DGSLogger.log("TurnGameMessageReceiver.processEvent(), playMessage: " + playMessage + " sessionTurn: " + sessionTurn + ", sender: " + senderId);
	
			if (!canSendMessage(event, sessionTurn, playMessage)) {
				DGSLogger.log(Level.SEVERE, "It isn't the client turn");
				return false;
			}
			
			if (playMessage) {
				turnGame.onPrePlayEvent(event);
			}
	
			receivedTurnMessage(channel, client, event, turnGame, msg);
			
			if (playMessage) {
				turnGame.onPosPlayEvent(event);
			}
		}
		
		return true;
	}
	
	public abstract boolean isPlayMessage(byte protocol);

	protected abstract boolean receivedTurnMessage(Channel channel, Client client, byte event, TurnBasedGame game, ByteBuffer msg);
}
