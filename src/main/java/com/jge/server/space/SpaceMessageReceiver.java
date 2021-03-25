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

import java.nio.ByteBuffer;

import com.jge.server.client.Client;
import com.jge.server.net.Channel;

/**
 * This class is responsible for decoding the messages that came
 * to a {@link Space} calling methods of this {@link Space} 
 *  
 * @author orochimaster
 */
public abstract class SpaceMessageReceiver implements MessageReceiver {
	protected Space space;

	public SpaceMessageReceiver(Space space) {
		setSpace(space);
	}
	
	public String getId() {
		return MSG_RECEIVER_PREFIX + space.getId();
	}
	
	public boolean isActive() {
		return true;
	}
	
	public SpaceMessageReceiver setSpace(Space space) {
		this.space = space;
		return this;
	}

	public Space getSpace() {
		return space;
	}
	
	public void receivedChannelMessage(Channel channel, Client sender, ByteBuffer msg) {
	}
	
	public void receivedMessage(Client client, ByteBuffer msg) {
		byte event = msg.get();	
		processEvent(null, client, event, msg);
	}

	protected boolean processEvent(Channel channel, Client client, byte event, ByteBuffer msg) {
		if (!client.isHuman()) {
			return false;
		}
		
		if (SpaceProtocol.ENTER.getId() == event) {
			space.putClient(client);
			return true;
		}
		else if (SpaceProtocol.EXIT.getId() == event) {
			space.spaceExit(client);
			return true;
		}
		
		return false;
	}
}