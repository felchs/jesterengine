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

import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.jge.server.utils.DGSLogger;

public class GameScore {
	protected final float MAX_POINTS; // if is -1 there is no limit
	
	protected SortedMap<String, Float> pointsMap = Collections.synchronizedSortedMap(new TreeMap<String, Float>());
	
	protected boolean forceWin;
	
	public GameScore() {
		MAX_POINTS = -1;
	}
	
	public GameScore(float maxPoints) {
		MAX_POINTS = maxPoints;
	}

	public Float addToScore(String teamName, float points) {
		Float currPoints = pointsMap.get(teamName);

		if (currPoints == null) {
			currPoints = 0f;
			DGSLogger.log("GameScore.addToScore Curr points null: " + currPoints + " Team: " + teamName);
			pointsMap.put(teamName, currPoints);
		} else {
			DGSLogger.log("GameScore.addToScore Curr points: " + currPoints + " team: " + teamName);
		}
		
		Set<String> keys = pointsMap.keySet();
		for (String key : keys) {
			DGSLogger.log("GameScore.addToScore key: " + key + " val: " + pointsMap.get(key));
		}
		
		Float pointsToAdd = currPoints + points;

		pointsMap.put(teamName, pointsToAdd);
		DGSLogger.log("GameScore.addToScore team: " + teamName + " Points: " + pointsMap.get(teamName) +  " points to add: " + pointsToAdd);
		return pointsToAdd;
	}
	
	public float getPointsFromTeam(String teamName) {
		return pointsMap.get(teamName);
	}
	
	public SortedMap<String, Float> getPointsMap() {
		return pointsMap;
	}

	public void setForceWin(boolean forceWin) {
		this.forceWin = forceWin;
	}

	public boolean hasWinner() {
		DGSLogger.log("GameScore.hasWinner, force: " + forceWin);
		if (forceWin) {
			return true;
		}
		
		Set<String> keys = pointsMap.keySet();
		for (String teamName : keys) {
			Float points = pointsMap.get(teamName);
			DGSLogger.log(">>>>GameScore.hasWinner, teamName: " + teamName + ", points: " + points);
			float maxPoints = getMaxPoints();
			if (maxPoints > 0 && points >= maxPoints) {
				DGSLogger.log(">>>>GameScore.hasWinner, points: " + points + " MaxPoints: " + maxPoints);
				return true;
			}
		}
		
		return false;
	}

	protected float getMaxPoints() {
		return MAX_POINTS;
	}

	public void reset() {
		DGSLogger.log("GameScore.reset ....");
		setForceWin(false);
		
		pointsMap.clear();
	}
	
	public void copyArguments(GameScore gameScore) {
		SortedMap<String, Float> newPointsMap = gameScore.getPointsMap();
		Set<String> keys = pointsMap.keySet();
		for (String key_ : keys) {
			Float float_ = pointsMap.get(key_);
			newPointsMap.put(key_, float_);
		}
	}
}
