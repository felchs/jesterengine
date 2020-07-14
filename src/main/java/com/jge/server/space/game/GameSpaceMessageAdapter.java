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
package com.jge.server.space.game;

import java.nio.ByteBuffer;

import com.jge.server.space.SpaceMessageAdapter;

public class GameSpaceMessageAdapter extends SpaceMessageAdapter {
	public void gameStarted(ByteBuffer message) { }

	public void gameStopped(ByteBuffer message) { }

	public void gameResults(ByteBuffer message) { }

	public void updateScore(ByteBuffer message) { }

	public void gameFinished(ByteBuffer message) { }

	public void gameCanRestart() { }

	public void putGameRobotEnter(ByteBuffer message) { }

	public void gameRobotReplacement(ByteBuffer message) { }

	public void onGameRobotExit(ByteBuffer message) { }
}
