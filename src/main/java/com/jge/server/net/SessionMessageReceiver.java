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

import com.jge.server.client.ClientMessage;
import com.jge.server.net.session.ClientSession;

/**
 * Interface to session messages
 */
public interface SessionMessageReceiver {
	/**
	 * Signs a login request
	 * 
	 * @param session the {@link ClientSession} that is asking login
	 * @param message following message to login
	 */
	public void onLoginRequest(ClientSession session, ByteBuffer message);
    
	/**
	 * Signs a reconnect request
	 * @param session the {@link ClientSession} that is asking reconnection
	 * @param message following message to reconnection
	 */
    public void onReconnectRequest(ClientSession session, ByteBuffer message);
    
    /**
     * Signs a session message
     * @param session the {@link ClientSession} that is sending the message
     * @param message the {@link ClientSession}'s message
     */
    public void onSessionMessage(ClientSession session, ByteBuffer message);
    
    /**
     * Signs a logout request
     * @param session the {@link ClientMessage} that is asking logout
     * @param message following message to logout
     */
    public void onLogoutRequest(ClientSession session, ByteBuffer message);
    
    /**
     * Signs a channel join
     * @param channelName the {@link Channel}'s name on join
     * @param session following message to channel join
     */
    public void onChannelJoin(String channelName, ClientSession session);
    
    /**
     * Signs a channel leave
     * @param channelName the {@link Channel}'s name on leave
     * @param session following message to channel leave
     */
    public void onChannelLeave(String channelName, ClientSession session);
    
    /**
     * Signs a channel message
     * @param channelName the {@link Channel}'s name that is sending the message
     * @param message the {@link Channel}'s message
     */
    public void onChannelMessage(String channelName, ByteBuffer message);
}
