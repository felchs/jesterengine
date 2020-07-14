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

import com.jge.server.client.Client;
import com.jge.server.net.session.ClientSession;

/**
 * This interface listens the messages send to a given {@link ClientSession}
 *  
 */
public interface ClientSessionListener {
	/**
	 * Client receives a message 
	 * @param message a {@link ByteBuffer} message
	 */
	void receivedMessage(ByteBuffer message);

	/**
	 * Clients disconnects
	 * Graceful means the disconnection shutdown was done with aware of client and server
	 * and no data was lost during the shutdown, so completed gracefully
	 * If not graceful a client or server may not be aware that its peer wants to end the 
	 * connection at all and maybe some some data could be lost.
	 * So a research for graceful TCP connection termination for further information   
	 * @param graceful whether the disconnection was graceful or not
	 */
    void disconnected(boolean graceful);
    
    /**
     * Then {@link Client} return to the Server
     * Check what to do when reconnecting
     */
    void reconnect();
}
