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

import com.jge.server.space.game.GameSpaceMessageProcessor;
import com.jge.server.utils.DGSLogger;

public class TurnMessageProcessor extends GameSpaceMessageProcessor {
	
	public TurnMessageProcessor(TurnMessageAdapter turnOutMessageInterface) {
		super(turnOutMessageInterface);
	}

	protected TurnMessageAdapter getTurnOut() {
		return (TurnMessageAdapter)spaceOutMessageInterface;
	}
	
	@Override
	public boolean callFunction(byte event, ByteBuffer message) {
		if (super.callFunction(event, message)) {
			return true;
		}
		
		DGSLogger.log("TurnMessageOut");
		
		if (TurnGameProtocol.NOTIFY_CURRENT_PLAYER.getId() == event) {
			short clientID = message.getShort();
			getTurnOut().notifyCurrentPlayer(clientID);
			return true;
		}
		else if (TurnGameProtocol.NOTIFY_CURRENT_PLAYER_ROBOT.getId() == event) {
			byte robotIndex = message.get();
			DGSLogger.log("TurnMessageProcessor(), robotIndex: " + robotIndex);
			getTurnOut().notifyCurrentPlayerRobot(robotIndex);
			return true;
		}
		else if (TurnGameProtocol.TURN_FINISHED.getId() == event) {
			getTurnOut().turnFinished(message);
			return true;
		}
		else if (TurnGameProtocol.MATCH_FINISHED.getId() == event) {
			getTurnOut().matchFinished(message);
			return true;
		}
		else if (TurnGameProtocol.SET_SELECTABLES_BY_PLAYER.getId() == event) {
			int playerId = message.getInt();
			boolean selectable = message.get() == 1;
			getTurnOut().setSelectablesByPlayer(playerId, selectable);
			return true;
		}
		else if (TurnGameProtocol.SET_THIS_PLAYER_SELECTABLES.getId() == event) {
			@SuppressWarnings("unused")
			boolean selectable = message.get() == 1;
			//getTurnOut().setSelectablesByPlayer(selectable);
			return true;
		}
		
		return false;
	}
}
