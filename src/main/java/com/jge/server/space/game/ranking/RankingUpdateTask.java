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

import java.util.List;

import com.jge.server.net.Task;

public class RankingUpdateTask implements Task {
	@SuppressWarnings("unused")
	private byte gameEnum;
	
	@SuppressWarnings("unused")
	private List<RankingInfoStruct> rankingInfoStructureList;

	public RankingUpdateTask(byte gameEnum, List<RankingInfoStruct> rankingInfoStructureList) {
		this.gameEnum = gameEnum;
		this.rankingInfoStructureList = rankingInfoStructureList;
	}

	public void run() {
		/*
    	int sz = rankingInfoStructureList.size();
    	for (int i = 0; i < sz; i++) {
    		RankingInfoStruct rankingInfo = rankingInfoStructureList.get(i);
    		String email = rankingInfo.email;
    		float points = rankingInfo.points;
    		int gameId = rankingInfo.gameId;
    		
    		try {
				DBRemoteCaller.getInstance().updateRanking(email, gameId, points);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	*/
    }
}