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
package com.jge.server.space.game.rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import com.jge.server.utils.DGSLogger;

public class RatingElo implements Rating {
	private HashMap<String, Float> ratings = new HashMap<String, Float>();

	/**
	 * 
	 * @param playerID
	 * @param value - put value < 0 for new rating calculation
	 */
	public void putRating(String playerID, Float value) {
		ratings.put(playerID, value);
	}
	
	public float calcInitialRating(float winPoints, float losePoints, float nPlays) {
		float avg = calcAVGRating(ratings);
		return avg + 400 * (winPoints - losePoints) / nPlays;
	}
	
	public float calcRating(float myRating, float opponentRating, float winPoints, float losePoints, float nPlays) {
		// the rating hasn't been calculated yet
		if (myRating < 0) {
			myRating = calcInitialRating(winPoints, losePoints, nPlays);
		}
		
		return myRating + getK(myRating) * (winPoints - expectedPoints(myRating, opponentRating));
	}
	
	public void updateRatings(ArrayList<GameResultForRating> gameResults)	{
		for (GameResultForRating gameResult : gameResults) {
			float playerA_rating = ratings.get(gameResult.getPlayerA_id());
			float playerB_rating = ratings.get(gameResult.getPlayerB_id());
			float winPoints = gameResult.getWinPoints();
			float losePoints = gameResult.getLosePoints();
			float nPlays = gameResult.getNPlays();
			float res = calcRating(playerA_rating, playerB_rating, winPoints, losePoints, nPlays);
			DGSLogger.log("RES: " + res);
		}
	}

	private float getK(float rating) {
		if (rating <= 1900) return 45;
		else if (rating <= 2200) return 30;
		else if (rating <= 2350) return 15;
		else return 10;
	}
	
	private float expectedPoints(float ra, float rb) {
		return 0.5f * (1 + (float)Math.pow(10, (ra - rb) * 0.0025f));
	}

	public float calcAVGRating(HashMap<String, Float> existingRatings) {
		float total = 0;
		float numRatings = 0;
		Set<String> keys = existingRatings.keySet();
		for (String k : keys)  {
			Float f = existingRatings.get(k);
			if (f >= 0) {
				total += f;
				numRatings++;
			}
		}
		
		return numRatings / total;
	}
	
	public static void main(String args[]) {
		RatingElo elo = new RatingElo();
		
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			int e = 1500 + rnd.nextInt(800);
			elo.putRating("ID_" + i, (float) e);
			DGSLogger.log("ID: " + i + " Value: " + e);
		}
		
		ArrayList<GameResultForRating> gameResults = new ArrayList<GameResultForRating>();
		GameResultForRating gameRes = new GameResultForRating("ID_0", "ID_1", 0, 1, 0, 1);
		gameResults.add(gameRes);
		elo.updateRatings(gameResults);
	}
}