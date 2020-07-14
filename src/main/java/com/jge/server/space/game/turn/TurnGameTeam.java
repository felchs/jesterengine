/*
 * Dark Game Engine is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Project King of Tactics is distributed in the hope that it will be useful,
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

import java.util.ArrayList;

import com.jge.server.space.game.GameTeam;

public class TurnGameTeam extends GameTeam {
	
	private ArrayList<ScreenPositionInterface> screenPositions = new ArrayList<ScreenPositionInterface>();

	public TurnGameTeam(int handle, String name) {
		super(handle, name);
	}
	
	public void setScreenPositions(ArrayList<ScreenPositionInterface> positions) {
		this.screenPositions = positions;
	}

	public ArrayList<ScreenPositionInterface> getScreenPositions() {
		return screenPositions;
	}

	public boolean isTeamScreenPosition(ScreenPositionInterface screenPosition) {
		return screenPositions.contains(screenPosition);
	}
}