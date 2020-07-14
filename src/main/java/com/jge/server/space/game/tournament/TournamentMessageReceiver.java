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

import java.nio.ByteBuffer;

import com.jge.server.client.Client;
import com.jge.server.client.MessageSender;
import com.jge.server.net.Channel;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.utils.DGSLogger;

public class TournamentMessageReceiver extends SpaceMessageReceiver {
	protected SpaceIdMapping spaceIdMapping;
	
	public TournamentMessageReceiver(Space space, SpaceIdMapping spaceIdMapping) {
		super(space);
		this.spaceIdMapping = spaceIdMapping;
	}

	protected boolean processEvent(Channel channel, MessageSender sender, byte event, ByteBuffer msg) {
		if (super.processEvent(channel, sender, event, msg)) {
			return true;
		}
		
		if (!sender.isHuman()) {
			return false;
		}
		
		Client client = (Client) sender;
		DGSLogger.log("TournamentMessage received, event: " + event);

		if (TournamentProtocol.CHECK_CAN_ENTER.getId() == event) {
			TournamentSpace tournament = (TournamentSpace)getSpace();
			tournament.sendCanEnterTournament(client);
			return true;
		}
		else if (TournamentProtocol.GIVE_UP.getId() == event) {
			TournamentSpace tournament = (TournamentSpace)getSpace();
			tournament.sendPlayerGiveUp(client);
			return true;
		}
		
		return false;
	}
}