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
package com.jge.server.net;

import java.nio.ByteBuffer;

import com.jge.server.net.session.ClientSession;
import com.jge.server.space.SpaceChannelReceiver;

/**
 * This class manages the creation, join, leave and messages of a {@link Channel}
 * 
 */
public interface ChannelManager {
	/**
	 * Gets an existent {@link Channel}
	 * @param channelName the channel name to get
	 * @return the {@link Channel}
	 */
	Channel getChannel(String channelName);

	/**
	 * Creates a new {@link Channel}
	 * @param channelName the channel name to set when creating
	 * @param channelReceiver the {@link SpaceChannelReceiver} to set when creating
	 * @param delivery the {@link Delivery} of this {@link Channel}
	 * @return the created {@link Channel}  
	 */
	Channel createChannel(String channelName, SpaceChannelReceiver channelReceiver, Delivery delivery);

	/**
	 * Used to set a {@link ClientSession} to join a {@link Channel}
	 * @param channelName the channel name of {@link Channel} to join
	 * @param session the {@link ClientSession} to join the {@link Channel}
	 */
	void channelJoin(String channelName, ClientSession session);

	/**
	 * Used for {@link ClientSession} to leave a {@link Channel}
	 * @param channelName the channel name of {@link Channel} to leave
	 * @param session the {@link ClientSession} to leave the {@link Channel}
	 */
	void channelLeave(String channelName, ClientSession session);

	/**
	 * Sends a message through a given channel
	 * @param channelName the channel name to send the message
	 * @param message a message to be delivered through this {@link Channel}
	 */
	void channelMessage(String channelName, ByteBuffer message);
}
