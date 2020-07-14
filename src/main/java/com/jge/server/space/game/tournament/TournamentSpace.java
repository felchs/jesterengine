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

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.net.AppContext;
import com.jge.server.net.Channel;
import com.jge.server.net.ChannelManager;
import com.jge.server.net.Delivery;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.SpaceChannelReceiver;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceIdMapping;
import com.jge.server.space.SpaceMessageReceiver;
import com.jge.server.utils.DGSLogger;

public class TournamentSpace extends Space {
	private String tournamentChatChannelName;
	
	protected SpaceIdMapping spaceIdMapping;
	
	@SuppressWarnings("unused")
	private long initTime;
	@SuppressWarnings("unused")
	private long roundTime;
	
	protected int rounds;
	protected int playersPerGame;
	protected int playersPerTeam;
	protected int minPlayersToStart;
	
	protected boolean running;

	public TournamentSpace(int id, long time, int maxPlayers, SpaceIdMapping spaceIDMapping) {
		super(id);
	}
	
	public TournamentSpace(int id, long initTime, long roundTime, int rounds, int playersPerGame, int playersPerTeam, int minPlayersToStart) {
		super(id);
		this.initTime = initTime;
		this.roundTime = roundTime;
		this.rounds = rounds;
		this.playersPerGame = playersPerGame;
		this.playersPerTeam = playersPerTeam;
		this.minPlayersToStart = minPlayersToStart;
		this.spaceIdMapping = new SpaceIdMapping();
	}

	public void setTournamentChatChannelName(String lobbyChatChannelName) {
		this.tournamentChatChannelName = lobbyChatChannelName;
	}

	public Channel getTournamentChatChannel() {
		return AppContext.getChannelManager().getChannel(tournamentChatChannelName);
	}

	@Override
	public void joinChannels(ClientSession clientSession) {
		getTournamentChatChannel().join(clientSession);
		Channel chatChannel = AppContext.getChannelManager().getChannel(tournamentChatChannelName);
		if (chatChannel != null) {
			chatChannel.join(clientSession);
		}
	}
	
	protected String getEventsChannelName() {
		return getName() + "ChEvents";
	}

	@Override
	protected void createChannels() {
		ChannelManager cm = AppContext.getChannelManager();
		cm.createChannel(getEventsChannelName(), new SpaceChannelReceiver(createMessageReceiver()), Delivery.RELIABLE);
	}

	@Override
	public SpaceMessageReceiver createMessageReceiver() {
		return new TournamentMessageReceiver(this, spaceIdMapping);
	}
	
	@Override
	public boolean putClient(Client client) {
		return super.putClient(client);
	}
	
	@Override
	public boolean canEnterSpace() {
		return super.canEnterSpace() && !running;
	}
	
	public void start() {
		running = true;
		
	}

	public void finish() {
		// clean up stuff
		// remove players
		
		running = false;
	}
	
	@Override
	protected void sendAdditionalInitInfo(ServerMessage serverMessage) { }
	
	public boolean isRunning() {
		return running;
	}

	// OUTPUT EVENTS ----------------------------------------------------------
	
	public void sendPlayerGiveUp(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), TournamentProtocol.ON_PLAYER_GIVE_UP);
		DGSLogger.log("Send player give up TournamentSpace: " + id);
		serverMessage.sendClientMessage(getClientNames());
	}

	public void sendCanEnterTournament(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), TournamentProtocol.SEND_CAN_ENTER);
		DGSLogger.log("Send can enter tournament, tournament id: " + id);
		serverMessage.sendClientMessage(getClientNames());		
	}	
}