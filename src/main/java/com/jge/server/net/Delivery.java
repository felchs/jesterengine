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

/**
 * Message delivery guarantees. 
 * 
 */
public enum Delivery {
	/**
	 * Message delivery is not guaranteed. 
	 * Faster in some cases
	 * It may be send as UDP
	 */
	UNRELIABLE,
	
	/**
	 * Message delivery is guaranteed unless there is a network failure
	 * Slower in some cases
	 * It sends as TCP or UPD with message receive confirmation schema
	 */
    RELIABLE;
}
