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

public class ClientLogoutTask implements Task {
	private String email; 

	public ClientLogoutTask(String email) {
		this.email = email;
	}

	public void run() {
		DGSLogger.log("TODO the disconnection from server TB here: " + email);
	}
}