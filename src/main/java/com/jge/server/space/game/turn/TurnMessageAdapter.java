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
package com.jge.server.space.game.turn;

import java.nio.ByteBuffer;

import com.jge.server.space.game.GameSpaceMessageAdapter;

public class TurnMessageAdapter extends GameSpaceMessageAdapter {
	public void notifyCurrentPlayer(short clientID) { }

	public void notifyCurrentPlayerRobot(byte robotIndex) { }

	public void turnFinished(ByteBuffer message) { }
	
	public void matchFinished(ByteBuffer message) { }

	public void setSelectablesByPlayer(int playerId, boolean selectable) { }
}
