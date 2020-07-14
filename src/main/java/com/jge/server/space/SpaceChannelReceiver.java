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

import com.jge.server.net.Channel;
import com.jge.server.net.ChannelListener;
import com.jge.server.net.session.ClientSession;

/**
 * Implementation the listener of {@link ChannelListener} for a given {@link Space}
 */
public class SpaceChannelReceiver implements ChannelListener {
	/**
	 * When the message comes it delivers it to a given {@link SpaceMessageReceiver} 
	 */
	protected SpaceMessageReceiver messageReciver;
	
	/**
	 * Constructor passing {@link SpaceMessageReceiver}
	 * @param messageReceiver the message receiver to process the {@link Channel}'s message
	 */
	public SpaceChannelReceiver(SpaceMessageReceiver messageReceiver) {
		this.messageReciver = messageReceiver;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void receivedMessage(Channel channel, ClientSession sendingSession, ByteBuffer msg) {
		throw new IllegalAccessError("to implement on subclass");
	}
}
