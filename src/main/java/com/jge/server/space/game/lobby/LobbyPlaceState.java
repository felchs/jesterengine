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
package com.jge.server.space.game.lobby;

import java.io.Serializable;

public class LobbyPlaceState implements Serializable {
	private static final long serialVersionUID = 1L;

	private LobbyPlaceStateEnum stateType;
	
	private short connectedID;
	private byte placeIdx;
	private byte subPlaceIdx;
	
	private boolean reserved;
	
	private boolean changed;
	
	public LobbyPlaceState(LobbyPlaceStateEnum stateType) {
		this.stateType = stateType;
	}
	
	public void setStateType(LobbyPlaceStateEnum stateType) {
		this.stateType = stateType;
	}
	
	public LobbyPlaceStateEnum getStateType() {
		return stateType;
	}
	
	public void setConnectedID(short connectedID) {
		this.connectedID = connectedID;
	}
	
	public short getConnectedID() {
		return connectedID;
	}
	
	public void setPlaceIdx(byte placeIdx) {
		this.placeIdx = placeIdx;
	}
	
	public byte getPlaceIdx() {
		return placeIdx;
	}
	
	public void setSubPlaceIdx(byte subPlaceIdx) {
		this.subPlaceIdx = subPlaceIdx;
	}
	
	public byte getSubPlaceIdx() {
		return subPlaceIdx;
	}
	
	public boolean isReserved(short clientID) {
		return reserved && getConnectedID() == clientID;
	}
		
	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public boolean hasChanged() {
		return changed;
	}
}
