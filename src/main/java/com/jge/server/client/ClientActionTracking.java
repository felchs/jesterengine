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
package com.jge.server.client;

import com.jge.server.net.Task;
import com.jge.server.utils.DGSLogger;

/**
 * When a client enters the Server some rules can be implemented
 * for {@link Client} disconnection like user activity
 * 
 */
public class ClientActionTracking implements Task {
	
	/**
	 * Default time to get client logged
	 */
	public static final long DELAY_CHECK_LOGGED = 1000 * 60 * 60;
	
	/**
	 * The {@link Client} to be tracked
	 */
	private Client client;

	/**
	 * Constructor with field
	 * @param client {@link Client} to be tracked
	 */
	public ClientActionTracking(Client client) {
 		this.client = client;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		if (client.getLastTick() > DELAY_CHECK_LOGGED) {
			DGSLogger.log("ClientActionTracking.run(), sending client disconnection");
			
			client.disconnect(true);
		}
	}
	
}
