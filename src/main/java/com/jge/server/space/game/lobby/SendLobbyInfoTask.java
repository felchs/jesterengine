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

import java.io.Serializable;
import java.util.BitSet;

import com.jge.server.net.Task;

/**
 * This class should save data into non transactional database
 * for the client to recover the information about who is
 * connected on the lobby
 * 
 * @author orochimaster
 *
 */
public class SendLobbyInfoTask implements Task, Serializable {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private LobbySpace space;
	
	public SendLobbyInfoTask(LobbySpace space) {
		this.space = space;
	}
	
	public static void main(String[] args) {
		BitSet s = new BitSet();
		for (int i = 0; i < 10; i++) {
			s.set(i);
		}
	}
	
	public void run() {
/*		LobbySpace lobby = space.getForUpdate();

		ServerMessage info = new ServerMessage(LobbyProtocol.LOBBY_INFO);
		
		BitSet bitSet = new BitSet();
		int bitIndex;
		LobbyPlaceState[][] places = lobby.getPlaces();
		for (int i= 0; i < places.length; i++) {
			LobbyPlaceState[] sub = places[i];
			for (int j = 0; j < sub.length; j++) {
				boolean value = sub[j] == LobbyPlaceState.FREE;
				
				bitSet.set(bitIndex++, value);
			}
		}
		
		Channel eventsChannel = lobby.getEventsChannel();
		
		eventsChannel.send(null, buf);
*/
		
		
//		info.put(lobby.getID());
//		info.send(eventsChannel);
		
		
//
//		// if the lobby hasn't changed don't send anything
//		if (lobby.changed()) {
//			LobbyPlaceState[][] places = lobby.getPlaces();
//
//			// just send what changes
//			int length = places.length;
//			for (int i = 0; i < length; i++) {
//				LobbyPlaceState[] placeStates = places[i];
//				for (byte subPlace = 0; subPlace < placeStates.length; subPlace++) {
//					LobbyPlaceState currPlace = placeStates[subPlace];
//					if (currPlace.hasChanged()) {
////						objList.add(new InfoStruct(place, subPlace, currPlace));
//						currPlace.setChanged(true);
//					}
//				}
//			}
//			
////			info.put((short)objList.size()); // num of places that changed
////			for (InfoStruct is : objList) {
////				info.put(is.place);
////				info.put(is.subPlace);
////				info.put(is.placeState);
////				info.put(is.clientID);
////			}
////			objList.clear();
////			objList = null;
//		}
//
//		//info.send(lobby.getEventsChannel());
//		//Logger.getLogger("SOME").log(Level.INFO, "some...");
	}
}