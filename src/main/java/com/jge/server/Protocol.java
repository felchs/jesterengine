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
package com.jge.server;

/**
 * Protocol interface to handle the server messages
 * 
 * @param <T> the type which the protocol is assigned to
 * 
 */
public interface Protocol<T> {
	/**
	 * Gets the protocol id
	 * @return the protocol id
	 */
	public T getId();
	
	/**
	 * Gets the protocol id as bytes
	 * @return the protocol id as bytes
	 */
	public byte[] getIdAsBytes();
	
	/**
	 * Gets the name of this protocol
	 * @return the name of this protocol
	 */
	public String getName();
}
