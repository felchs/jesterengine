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
 * This class represents a chat processor of a {@link Space}
 */
public class ChatMessageReceiver extends SpaceMessageReceiver {
	
	/**
	 * Constructor passing parameters
	 * @param gameSpace the {@link Space} to receive the chat
	 */
	public ChatMessageReceiver(Space gameSpace) {
		super(gameSpace);
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */	
	public void receivedChannelMessage(Channel channel, MessageSender sender, ByteBuffer msg) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void receivedMessage(MessageSender sender, ByteBuffer msg) {
	}
}