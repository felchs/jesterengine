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
package com.jge.server.space.game.action;

import java.nio.ByteBuffer;

import com.jge.server.client.MessageSender;
import com.jge.server.net.Channel;
import com.jge.server.space.game.GameSpaceMessageReceiver;

public class ActionGameMessageReceiver extends GameSpaceMessageReceiver {
	
	public ActionGameMessageReceiver(ActionBasedGame actionBasedGame) {
		super(actionBasedGame);
	}
	
	protected boolean processEvent(Channel channel, MessageSender sender, byte event, ByteBuffer msg) {
		if (super.processEvent(channel, sender, event, msg)) {
			return true;
		}
		
		if (ActionGameProtocol.SNAPSHOT.getId() == event) {
			ActionBasedGame actionGameSpace = (ActionBasedGame)getSpace();
			actionGameSpace.onSnapshotRequest(msg);
			return true;
		}
		
		if (ActionGameProtocol.ACTION.getId() == event) {
			ActionBasedGame actionGameSpace = (ActionBasedGame)getSpace();
			actionGameSpace.onAction(msg);
			return true;
		}
		
		if (ActionGameProtocol.MOVE.getId() == event) {
			ActionBasedGame actionGameSpace = (ActionBasedGame)getSpace();
			actionGameSpace.onMove(msg);
			return true;
		}
		
		return false;
	}

}
