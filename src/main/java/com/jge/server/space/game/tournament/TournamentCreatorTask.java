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
package com.jge.server.space.game.tournament;

import java.util.Calendar;
import java.util.Vector;

import com.jge.server.net.AppContext;
import com.jge.server.net.Task;
import com.jge.server.space.TournamentInfo;

public class TournamentCreatorTask implements Task {
	@SuppressWarnings("unused")
	private Vector<TournamentInfo> tournamentsList = new Vector<TournamentInfo>();
	
	public TournamentCreatorTask() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.SECOND, 60);
		long nextTime = cal.getTimeInMillis();
		long delay = nextTime - Calendar.getInstance().getTimeInMillis();

		// each 5 minutes check start or finish a tournament
		long period = 5000 * 60;
		AppContext.getTaskManager().schedulePeriodicTask(this, delay, period);
	}
	
	public TournamentSpace createNewTournament(TournamentInfo ti) {
		return new TournamentSpace(ti.id, ti.initTime, ti.roundTime, ti.rounds, ti.playersPerGame, ti.playersPerTeam, ti.minPlayersToStart);
	}

	public void run() {
		//Calendar cal = Calendar.getInstance();
		//Date currDate = cal.getTime();

		/*
		ScalableList<TournamentInfo> list = tournamentsList.get();
		for (TournamentInfo ti : list) {
			ti.initTime
			
			TournamentSpace tournament = (TournamentSpace)SpaceIdMapping.getSpaceWithId(ti.id);
			
			if (tournament.isRunning()) {
				tournament.finish();
			}
			
			tournament.start();
		}
		*/
	}
}
