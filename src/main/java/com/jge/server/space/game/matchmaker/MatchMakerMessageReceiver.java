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
package com.jge.server.space.game.matchmaker;

import java.nio.ByteBuffer;

import com.jge.server.client.Client;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.utils.DGSLogger;

public class MatchMakerMessageReceiver extends SpaceMessageReceiver {
	public MatchMakerMessageReceiver(Space space) {
		super(space);
	}

	protected boolean processEvent(Channel channel, Client client, byte event, ByteBuffer msg) {
		if (super.processEvent(channel, client, event, msg)) {
			return true;
		}
		
		if (!client.isHuman()) {
			return false;
		}

		if (MatchProtocol.PLAY_NOW.getId() == event) {
			playNow(channel, client, msg, false);
			return true;
		}
		if (MatchProtocol.PLAY_NOW_WITH_ROBOTS.getId() == event) {
			playNow(channel, client, msg, true);
			return true;
		}

		return false;
	}

	protected void playNow(Channel channel, Client client, ByteBuffer msg, boolean fillWithRobots) {
		byte matchType = msg.get();
		byte gameEnum = msg.get();
		DGSLogger.log("LobbyMessageReceiver.playNow() matchType: " + matchType);
		
		MatchMakerSpace matchMakder = (MatchMakerSpace)getSpace();
		matchMakder.wantToPlayNow(client, matchType, gameEnum, fillWithRobots);
		
		/*
		if (matchMakder.putClient(client)) {
			DGSLogger.log("MatchMakerMessageReceiver.playNow() canPutOneMoreClient true");
			
			matchMakder.wantToPlayNow(client, matchType, gameEnum, fillWithRobots);
		} else {
			DGSLogger.log("LobbyMessageReceiver.playNow() !hasAvailableID");
			matchMakder.sendCannotEnter(client);
		}
		*/
	}


}