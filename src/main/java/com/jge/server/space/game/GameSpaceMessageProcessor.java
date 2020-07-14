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

import java.nio.ByteBuffer;

import com.jge.server.space.SpaceMessageProcessor;
import com.jge.server.utils.DGSLogger;

public class GameSpaceMessageProcessor extends SpaceMessageProcessor {
	
	public GameSpaceMessageProcessor(GameSpaceMessageAdapter gameSpaceOutInterface) {
		super(gameSpaceOutInterface);
	}

	protected GameSpaceMessageAdapter getGameSpaceOut() {
		return (GameSpaceMessageAdapter)spaceOutMessageInterface;
	}
	
	@Override
	public boolean callFunction(byte event, ByteBuffer message) {
		if (super.callFunction(event, message)) {
			return true;
		}
		
		DGSLogger.log("GameMessageOut");

		if (GameProtocol.GAME_PLAYER_GIVE_UP.getId() == event) {
			//	var handle:int = message.readShort();
			//	var client:Client = getClientWithHandle(handle);
				
			//	(graphics as GameGraphics).gamePlayerGiveUp(client);
				
			//'	gamePlayers.remove(handle);
			return true;
		}
		else if (GameProtocol.GAME_PLAYER_FALL.getId() == event) {
			//	var handle:int = message.readShort();
			//	var client:Client = getClientWithHandle(handle);
				
			//	(graphics as GameGraphics).gamePlayerFall(client);
				
			//	clientList.remove(handle);
			return true;
		}
		else if (GameProtocol.GAME_STARTED.getId() == event) {
			getGameSpaceOut().gameStarted(message);
			return true;
		}
		else if (GameProtocol.GAME_STOPPED.getId() == event) {
			getGameSpaceOut().gameStopped(message);
			return true;
		}
		else if (GameProtocol.GAME_RESULTS.getId() == event) {
			getGameSpaceOut().gameResults(message);
			return true;
		}
		else if (GameProtocol.UPDATE_SCORE.getId() == event) {
			getGameSpaceOut().updateScore(message);
			return true;
		}
		else if (GameProtocol.GAME_FINISHED.getId() == event) {
			getGameSpaceOut().gameFinished(message);
			return true;
		}
		else if (GameProtocol.GAME_CAN_RESTART.getId() == event) {
			getGameSpaceOut().gameCanRestart();
			return true;
		}
		else if (GameProtocol.GAME_ROBOT_ENTER.getId() == event) {
			getGameSpaceOut().putGameRobotEnter(message);
			return true;
		}
		else if (GameProtocol.GAME_ROBOT_REPLACEMENT.getId() == event) {
			getGameSpaceOut().gameRobotReplacement(message);
			return true;
		}
		else if (GameProtocol.GAME_ROBOT_EXIT.getId() == event) {
			getGameSpaceOut().onGameRobotExit(message);
			return true;
		}
		
		return false;
	}
}
