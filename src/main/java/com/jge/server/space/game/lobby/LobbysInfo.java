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
package com.jge.server.space.game.lobby;

import java.util.Collections;
import java.util.LinkedList;

public class LobbysInfo  {
	private static String getKey(int lobbyId, byte place, byte subplace) {
		return lobbyId + "_" + place + "_" + subplace;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	private LinkedList<String> freePlaces = new LinkedList<String>();
	
	private LinkedList<String> busyPlaces = new LinkedList<String>();
	
	public void addPlaces(int lobbyId, byte numPlaces, byte numSubplaces) {
		for (byte j = 0; j < numPlaces; j++) {
			for (byte k = 0; k < numSubplaces; k++) {
				String key = getKey(lobbyId, j, k);
				if (freePlaces.contains(key)) {
					throw new RuntimeException();
				}
				freePlaces.add(key);
			}
		}
	}

	public void setPlaceFree(int lobbyId, byte place, byte subplace) {
		String key = getKey(lobbyId, place, subplace);
		if (busyPlaces.remove(key)) {
			freePlaces.add(key);
			Collections.sort(freePlaces);
		}
	}

	public void setPlaceBusy(int lobbyId, byte place, byte subplace) {
		String key = getKey(lobbyId, place, subplace);
		if (freePlaces.remove(key)) {
			busyPlaces.add(key);
			Collections.sort(freePlaces);
		}
	}
	
	public int[] getFreePlace() {
		if (freePlaces.size() == 0) {
			return null;
		}
		
		String firstKey = freePlaces.remove();
		busyPlaces.add(firstKey);
		Collections.sort(busyPlaces);
		
		int sizeBusyPlaces = busyPlaces.size();
		
		String[] split = firstKey.split("_");
		return new int[] { Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), sizeBusyPlaces };
	}
}
