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

import com.jge.server.client.MessageSender;
import com.jge.server.net.Channel;

/**
 * This class is responsible for decoding the messages that came
 * to a {@link Space} calling methods of this {@link Space} 
 *  
 */
public interface MessageReceiver {
	/**
	 * Default prefix in {@link Space}'s that are message recievers
	 */
	public static final String MSG_RECEIVER_PREFIX = "MSG_RECEIVER_";

	/**
	 * Gets the unique id of this message receiver
	 * @return the unique id of this {@link MessageReceiver}
	 */
	public String getId();
	
	/**
	 * Whether this message receiver is active or not
	 * @return whether this {@link MessageReceiver} is active or not
	 */
	public boolean isActive();
	
	/**
	 * Incoming {@link Channel}'s message 
	 * @param channel the {@link Channel} that is sending the message
	 * @param sender the {@link MessageSender} that send the message via {@link Channel}
	 * @param message the {@link ByteBuffer} message itself
	 */
	public void receivedChannelMessage(Channel channel, MessageSender sender, ByteBuffer message);

	/**
	 * Incoming {@link MessageSender}'s message 
	 * @param sender the {@link MessageSender} that is sending the message
	 * @param message the {@link ByteBuffer} message itself
	 */
	public void receivedMessage(MessageSender sender, ByteBuffer message);
} 