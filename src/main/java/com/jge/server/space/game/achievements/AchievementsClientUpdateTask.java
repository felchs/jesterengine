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
package com.jge.server.space.game.achievements;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.Task;

public class AchievementsClientUpdateTask implements Task {
	public static final long DELAY_TASK = 0;

	private Client client;
	
	public AchievementsClientUpdateTask(Client client) {
		this.client = client;
	}

	public void run() {
		ArrayList<AchievementStruct> achievementsToSaveOnDB = new ArrayList<AchievementStruct>();
		ArrayList<GameStatisticsStruct> statisticsToSaveOnDB = new ArrayList<GameStatisticsStruct>();
		
		String email = client.getEmail();
		
		Hashtable<Byte, Achievements> achievementsMap = client.getAchievementsMap();
		Set<Byte> gameTypes = achievementsMap.keySet();
		for (Byte gameId : gameTypes) {
			Achievements achievements = achievementsMap.get(gameId);
			
			GameStatisticsStruct statisticsDBDTO = new GameStatisticsStruct();
			statisticsDBDTO.wins = achievements.getWin();
			statisticsDBDTO.loses = achievements.getLose();
			statisticsDBDTO.draws = achievements.getDraw();
			statisticsDBDTO.abandon = achievements.getAbandon();
			statisticsDBDTO.gameId = achievements.getGameId();
			statisticsDBDTO.email = email;
			statisticsToSaveOnDB.add(statisticsDBDTO);
			
			Vector<Achievement> achievementsList = achievements.getAchievementsList();
			for (Achievement achievement : achievementsList) {
				if (!achievement.isExported()) {
					AchievementStruct achievementDBDTO = new AchievementStruct();
					achievementDBDTO.gameId = achievement.getGameId();
					achievementDBDTO.achievementId = achievement.getAchievementId();
					achievementDBDTO.quantityAchieved = achievement.getQuantityAchieved();
					achievementDBDTO.quantityRequired = achievement.getQuantityRequired();
					achievementDBDTO.achievemntDate = achievement.getAchievementDate();
					achievementDBDTO.email = email;
					achievementsToSaveOnDB.add(achievementDBDTO);
				}
			}
		}
		
		AchievementsUpdateTask achievementsUpdateTask = new AchievementsUpdateTask(statisticsToSaveOnDB, achievementsToSaveOnDB);
		
		AppContext.getTaskManager().startTask(achievementsUpdateTask);
	}	
}
