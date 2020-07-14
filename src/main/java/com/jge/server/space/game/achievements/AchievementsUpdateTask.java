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

import com.jge.server.net.Task;

public class AchievementsUpdateTask implements Task {
	private final ArrayList<GameStatisticsStruct> statisticsList;

	private final ArrayList<AchievementStruct> achievementsList;

	public AchievementsUpdateTask(ArrayList<GameStatisticsStruct> statisticsList, ArrayList<AchievementStruct> achievementsList) {
		this.statisticsList = statisticsList;
		this.achievementsList = achievementsList;
	}
	
	public void run() {
		int sz = statisticsList.size();
		for (int i = 0; i < sz; i++) {
			//GameStatisticsStruct gameStatisticsStruct = statisticsList.get(i);
			//DBRemoteCaller.getInstance().updateGameStatistics(gameStatisticsStruct.wins, gameStatisticsStruct.loses, gameStatisticsStruct.draws, gameStatisticsStruct.abandon, gameStatisticsStruct.gameId, gameStatisticsStruct.email);
		}
		
		sz = achievementsList.size();
		for (int i = 0; i < sz; i++) {
			//AchievementStruct achievement = achievementsList.get(i);
			//DBRemoteCaller.getInstance().updateAchievement(achievement.achievementId, achievement.quantityAchieved, achievement.quantityRequired, achievement.achievemntDate, achievement.email);
		}
	}
}