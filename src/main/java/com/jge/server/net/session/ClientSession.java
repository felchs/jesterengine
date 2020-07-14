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
package com.jge.server.net.session;

import java.nio.ByteBuffer;

/**
 * This class represents a client session
 * It must be implemented by a given Net engine
 */
public abstract class ClientSession {
	/**
	 * The unique id of this client session
	 */
	private long id;
	
	/**
	 * The host which the client is located
	 */
	private String host;
	
	/**
	 * A custom name for this client session
	 * It's defined by the user
	 */
	private String name;
	
	/**
	 * Constructor passing arguments
	 * @param id the client session id
	 * @param host the host of this client session
	 */
	public ClientSession(long id, String host) {
		this.id = id;
		this.host = host;
	}
	
	/**
	 * Gets this client session id
	 * @return this client session id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the host of this client session
	 * @return the host of this client session
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * The custom name of this client session
	 * @return the custom name of this client session
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the custom name of this client session
	 * @param name the custom name of this client session
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Whether this client session is connected or not
	 * @return whether this client session is connected or not
	 */
	public abstract boolean isConnected();
    
	/**
	 * Sends a message to this client session
	 * @param message the {@link ByteBuffer} message to be sent
	 * @return
	 */
    public abstract ClientSession send(ByteBuffer message);
}