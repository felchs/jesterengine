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

/**
 * When a channel deliver a message it's listened by this interface
 * 
 */
public interface ChannelListener {
	/**
	 * the message sent by a {@link Channel}
	 * @param channel the {@link Channel} that is sending the message
	 * @param sender the {@link ClientSession} that is delivering the message
	 * @param message the incoming message 
	 */
    void receivedMessage(Channel channel, ClientSession sender, ByteBuffer message);
}
