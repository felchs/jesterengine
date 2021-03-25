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

import com.jge.server.client.Client;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.utils.DGSLogger;

public abstract class GameSpaceMessageReceiver extends SpaceMessageReceiver {
	public GameSpaceMessageReceiver(Space gameSpace) {
		super(gameSpace);
	}
	
	@Override
	protected boolean processEvent(Channel channel, Client client, byte event, ByteBuffer msg) {
		if (super.processEvent(channel, client, event, msg)) {
			return true;
		}
		
		if (GameProtocol.GAME_RESTART.getId() == event) {
			DGSLogger.log("GameSpaceMessageReceiver.processEvent, GAME_RESTART");
			GameSpace gameSpace = (GameSpace)getSpace();
			gameSpace.restartCall();
			return true;
		}
		
		if (GameProtocol.GAME_FILL_WITH_ROBOTS.getId() == event) {
			DGSLogger.log("GameSpaceMessageReceiver.processEvent, GAME_FILL_WITH_ROBOTS");
			GameSpace gameSpace = (GameSpace)getSpace();
			if (gameSpace.canFillWithRobots()) {
				gameSpace.fillWithRobots();
			}
			return true;			
		}
		
		return false;
	}
}