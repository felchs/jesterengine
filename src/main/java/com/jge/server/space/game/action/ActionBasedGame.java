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
package com.jge.server.space.game.action;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.TaskHandle;
import com.jge.server.space.game.GameSpace;
import com.jge.server.space.game.Player;
import com.jge.server.utils.DGSLogger;

public abstract class ActionBasedGame extends GameSpace {
	private long delay;

	private long period;
	
	private TaskHandle taskHandle;

	private HashMap<Short, ActionBasedGameObject> playersLocationMap = new HashMap<Short, ActionBasedGameObject>();

	public ActionBasedGame(int id, byte gameType, int minPlayersToStart, int maxPlayers, long delay, long period) {
		super(id, gameType, minPlayersToStart, maxPlayers);
		this.delay = delay;
		this.period = period;
	}
	
	@Override
	public void addPlayer(Player player) {
		super.addPlayer(player);
		
		short playerKey = player.getClientId();
		ActionBasedGameObject newLocation = new ActionBasedGameObject();
		playersLocationMap.put(playerKey, newLocation);
	}
	
	@Override
	public void removePlayer(Player player) {
		super.removePlayer(player);
		
		short playerKey = player.getPlayerHandle();
		playersLocationMap.remove(playerKey);
	}
	
	@Override
	protected Player createGamePlayer(Client client, Object initInfo) {
		return new ActionBasedGamePlayer(client, PLAYER_PREFIX + client.getName());
	}

	@Override
	public void start() {
		if (taskHandle != null) {
			throw new RuntimeException();
		}
		
		ActionBasedGameTickTask tickTask = new ActionBasedGameTickTask(this);
		this.taskHandle = AppContext.getTaskManager().schedulePeriodicTask(tickTask, delay, period);
		super.start();
	}

	@Override
	public void finish() {
		taskHandle.cancel();
		super.finish();
	}
	
	protected void onDeltaTime(long delta, long serverTime) {
		DGSLogger.log("ActionBsedGame.onDeltaTime: " + delta + ", serverTime: " + serverTime);
		sendSnapshotToPlayers(delta, serverTime);
	}

	protected void sendSnapshotToPlayers(long delta, long serverTime)
	{
		ServerMessage message = new ServerMessage(getId(), ActionGameProtocol.SNAPSHOT);
		Set<Short> keys = playersLocationMap.keySet();
		short numPlayers = (short) keys.size();
		message.put(numPlayers);
		for (short playerKey : keys) {
			ActionBasedGameObject location = playersLocationMap.get(playerKey);
			message.put(playerKey);
			message.put(location.posX);
			message.put(location.posY);
			message.put(location.posZ);
			message.put(location.rotX);
			message.put(location.rotY);
			message.put(location.rotZ);
		}
	}
	
	public abstract void onSnapshotRequest(ByteBuffer msg);
	
	public abstract void onMove(ByteBuffer msg);
	
	public abstract void onAction(ByteBuffer msg);
}