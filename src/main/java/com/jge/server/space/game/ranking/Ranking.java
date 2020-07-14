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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.RegionalOptions;
import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.space.game.GameTeam;
import com.jge.server.space.game.Player;
import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

public class Ranking {
	private final int id;
	private final byte gameEnum;
	private final float WIN_POINTS;
	private final float DRAW_POINTS;
	
	private static final int DISCONN_LOSE_POINTS = -1;
	private static final int ON_EXIT_LOSE_POINTS = -3;
	
	private final byte dayOfWeekToClean;
	private boolean canClean = true;

	private Vector<RankingListener> rankingListeners = new Vector<RankingListener>();
	
	public Ranking(int id, byte gameEnum, byte timeToClean) {
		this(id, gameEnum, timeToClean, 3, 1);
	}
	
	public Ranking(int id, byte gameEnum, byte dayOfWeekToClean, float winPoints, float drawPoints) {
		this.id = id;
		this.gameEnum = gameEnum;
		this.dayOfWeekToClean = dayOfWeekToClean;
		this.WIN_POINTS = winPoints;
		this.DRAW_POINTS = drawPoints;
	}
	
	public int getId() {
		return id;
	}
	
	public void addListener(RankingListener rankingListener) {
		rankingListeners.add(rankingListener);
	}
	
	public Vector<RankingListener> getRankingListeners() {
		return rankingListeners;
	}
	
	public void checkCleanPoints() {
		TimeZone timeZone = RegionalOptions.get().getTimezone();
		Calendar cal = Calendar.getInstance(timeZone);
		if (canClean && cal.get(Calendar.DAY_OF_WEEK) == dayOfWeekToClean) {
			canClean = false;
		} else {
			canClean = false;
		}
	}

	private void updatePoints(float points, float tiePoints, CopyOnWriteArrayList<String> teamPlayerNames) {
		List<RankingInfoStruct> rankingInfoAsyncStructureList = new Vector<RankingInfoStruct>();
		
		for (String playerName : teamPlayerNames) {
			DGSLogger.log("Ranking.updatePoints, playerName: " + playerName);
			Player player = (Player)MappingUtil.getObject(playerName);
			if (player.isRobot()) {
				continue;
			}
			
			Client client = player.getClient();
			String email = client.getEmail();
			short clientID = client.getId();
			
			DGSLogger.log("Ranking.updatePoints(), email: " + email + " points: " + points + " tiePoints: " + tiePoints);
			
			for (RankingListener rankingListener : rankingListeners) {
				rankingListener.updatePoints(clientID, points, tiePoints);
			}
			
			RankingInfoStruct rankingInfoStruct = new RankingInfoStruct();
			rankingInfoStruct.email = email;
			rankingInfoStruct.points = points;
			rankingInfoAsyncStructureList.add(rankingInfoStruct);
			
			RankingUpdateTask rankingUpdateTask = new RankingUpdateTask(gameEnum, rankingInfoAsyncStructureList);
			AppContext.getTaskManager().startTask(rankingUpdateTask);
		}
	}
	
//	private ClientRankingPoints getRankingPoints(short clientID, String clientName) {
//		ScalableHashMap<String, ClientRankingPoints> clientsRankingMap = clientRankingPoints.getForUpdate();
//		ClientRankingPoints clientRankingPoints = clientsRankingMap.get(clientName);
//		if (clientRankingPoints == null) {
//			clientRankingPoints = new ClientRankingPoints(clientID, clientName);
//			clientsRankingMap.put(clientName, clientRankingPoints);
//		} 
//		return clientRankingPoints;
//	}

	public void updateWinner(GameTeam teamWinner, GameTeam teamLooser, float drawPoints) {
		float winPoints = WIN_POINTS;
		CopyOnWriteArrayList<String> teamPlayerNames = teamWinner.getGamePlayerNames();
		updatePoints(winPoints, drawPoints, teamPlayerNames);
	}

	public void updateDraw(GameTeam teamWinner, GameTeam teamLoser, float tiePoints) {
		float drawPoints = DRAW_POINTS;
		CopyOnWriteArrayList<String> teamPlayerNames = teamWinner.getGamePlayerNames();
		updatePoints(drawPoints, tiePoints, teamPlayerNames);
	}

	public void updateRankingOnExit(Player player, boolean disconnected) {
		short clientID = player.getClientId();
		float points = disconnected ? DISCONN_LOSE_POINTS : ON_EXIT_LOSE_POINTS;
		for (RankingListener rankingListener : rankingListeners) {
			rankingListener.updatePoints(clientID, points, 0);
		}
	}

}