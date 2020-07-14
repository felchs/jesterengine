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
package com.jge.server.space.game.ranking;


public class ClientRankingPoints {
	private final short clientID;
	
	private final String clientName;
	
	private float points;
	
	private float tiePoints;
	
	public ClientRankingPoints(short clientID, String clientName) {
		this.clientID = clientID;
		this.clientName = clientName;
	}
	
	public short getClientID() {
		return clientID;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setPoints(float points) {
		this.points = points;
	}
	
	public void addToPoints(float points) {
		this.points += points;
		if (this.points < 0) {
			this.points = 0;
		}
	}

	public float getPoints() {
		return points;
	}
	
	public void setTiePoints(float tiePoints) {
		this.tiePoints = tiePoints;
	}
	
	public void addToTiePoints(float tiePoints) {
		this.tiePoints += tiePoints;
	}
	
	public float getTiePoints() {
		return tiePoints;
	}
}