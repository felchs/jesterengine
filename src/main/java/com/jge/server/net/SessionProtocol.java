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

import com.jge.server.Protocol;
import com.jge.server.net.session.ClientSession;
import com.jge.server.utils.ByteUtils;

/**
 * Protocol interface to handle session's messages
 */
public enum SessionProtocol implements Protocol<Byte> {
	/**
	 * The version of the engine
	 */
    VERSION((byte)5),
    
    /**
     * {@link ClientSession}'s login request
     */
	LOGIN_REQUEST((byte)16),
	
	/**
	 * {@link ClientSession}'s login success
	 */
    LOGIN_SUCCESS((byte)17),
    
    /**
     * {@link ClientSession}'s login failure
     */
    LOGIN_FAILURE((byte)18),
    
    /**
     * {@link ClientSession}'s reconnection request
     */
    RECONNECT_REQUEST((byte)32),
    
    /**
     * {@link ClientSession}'s reconnection success
     */
    RECONNECT_SUCCESS((byte)33),
    
    /**
     * {@link ClientSession}'s reconnection failure
     */
    RECONNECT_FAILURE((byte)34),
    
    /**
     * {@link ClientSession}'s message
     */
    SESSION_MESSAGE((byte)48),
    
    /**
     * {@link ClientSession}'s logout request
     */
    LOGOUT_REQUEST((byte)64),
    
    /**
     * {@link ClientSession}'s logout success
     */
    LOGOUT_SUCCESS((byte)65),
    
    /**
     * {@link ClientSession}'s channel join
     */
    CHANNEL_JOIN((byte)80),
    
    /**
     * {@link Channel}'s leave message
     */
    CHANNEL_LEAVE((byte)81),
    
    /**
     * {@link ClientSession}'s message
     */
    CHANNEL_MESSAGE((byte)82);
    
    /**
     * Protocol's id
     */
    private byte id;
    
    /**
     * Constructor passing protocol's id
     * @param id protocl's id
     */
    private SessionProtocol(byte id) {
    	this.id = id;
	}

    /**
     * Gets the protocol's id
     * return protocol's id
     */
	public Byte getId() {
		return id;
	}
	
	/**
	 * Gets protocol's id as array of bytes
	 * return the protocol's id as array of bytes
	 */
	public byte[] getIdAsBytes() {
		return ByteUtils.getBytes(id);
	}
	
	/**
	 * Gets this protocol's as String name
	 * return this protocol's as String name
	 */
	public String getName() {
		return this.toString();
	}
}
