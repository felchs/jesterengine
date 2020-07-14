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

import java.util.concurrent.ConcurrentHashMap;

import com.jge.server.net.session.ClientSession;

/**
 * This class maps all {@link ClientSession}'s
 *  
 */
public class JesterSessionMap {
	/**
	 * Singleton instance
	 */
	private static JesterSessionMap instance;
	
	/**
	 * Singleton getter
	 * @return the {@link JesterSessionMap} instance
	 */
	public static JesterSessionMap get() {
		if (instance == null) {
			instance = new JesterSessionMap();
		}
		return instance;
	}
	
	/**
	 * Private constructor for singleton
	 */
	private JesterSessionMap() {
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * <pre>
	 * Map of {@link ClientSession}'s
	 *    key   <- host
	 *    value <- client session
	 * </pre>
	 */
	private ConcurrentHashMap<String, ClientSession> clientSessionMap = new ConcurrentHashMap<String, ClientSession>();
	
	/**
	 * Get client session with key (that is the host)
	 * @param host host net net
	 * @return the mapped {@link ClientSession} or null if not mapped
	 */
	public ClientSession getClientSession(String host) {
		ClientSession clientSession = clientSessionMap.get(host);
		return clientSession;
	}
	
	/**
	 * Maps a new {@link ClientSession}
	 * @param incomingSession the {@link ClientSession} to be mapped
	 */
	public void addClientSession(ClientSession incomingSession) {
		String host = incomingSession.getHost();
		ClientSession clientSession = clientSessionMap.get(host);
		if (clientSession != null) {
			updateSession(clientSession, incomingSession);
		}
		
		clientSessionMap.put(host, incomingSession);
	}

	/**
	 * Updates the incoming session with existing client session
	 * @param clientSession the existing client session
	 * @param incomingSession the new incoming client session
	 */
	private void updateSession(ClientSession clientSession, ClientSession incomingSession) {
	}
	
	/**
	 * Removes a {@link ClientSession}
	 * @param session the {@link ClientSession} to be removed
	 */
	public void removeClientSession(ClientSession session) {
		String host = session.getHost();
		removeClientSession(host);
	}
	
	/**
	 * Removes a {@link ClientSession} with host
	 * @param host the key used to map {@link ClientSession}
	 */
	public void removeClientSession(String host) {
		clientSessionMap.remove(host);
	}
}
