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
package com.jge.server.space;

import java.nio.ByteBuffer;

/**
 * This class represents an adaptor for a {@link Space}'s methods
 *  
 */
public class SpaceMessageAdapter {
	
	public void init(ByteBuffer message) { }

	public void enter(ByteBuffer message) { }

	public void onExit(ByteBuffer message) { }

	public void info(ByteBuffer message) { }

	public void updateConnectedClients(ByteBuffer message) { }

	public void setByteEnabled(byte objectId, boolean enabled) { }	
	public void setShortEnabled(short objectId, boolean enabled) { }
	
	public void setByteVisible(byte objectId, boolean visible) { }
	public void setShortVisible(short objectId, boolean visible) { }

	public void setByteSelected(byte objectId, boolean selected) { }
	public void setShortSelected(short objectId, boolean selected) { }

	public void setByteSelectable(byte objectId, boolean selected) { }
	public void setShortSelectable(short objectId, boolean selected) { }
}