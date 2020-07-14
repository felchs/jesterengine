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
package com.jge.server.space.game.turn;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.space.SpaceProtocol;
import com.jge.server.space.SpaceState;
import com.jge.server.space.game.AutomaticPlayerInterface;
import com.jge.server.space.game.GameProtocol;
import com.jge.server.space.game.GameSpace;
import com.jge.server.space.game.Player;
import com.jge.server.space.game.RobotInterface;
import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

public abstract class TurnBasedGame extends GameSpace {
	protected TurnNode currentTurnNode;

	protected CopyOnWriteArrayList<Byte> currentPlayersIndex = new CopyOnWriteArrayList<Byte>();

	// the order of the positions must be in accordance with the ScreenPositionInteface.getPositionIdx()
	protected CopyOnWriteArrayList<ScreenPositionInterface> screenPositions = new CopyOnWriteArrayList<ScreenPositionInterface>();

	protected Hashtable<Byte, HashSet<String>> playerByScreenPos = new Hashtable<Byte, HashSet<String>>();

	/**
	 * If automatic play == 0, there's no automatic play
	 */
	private final long automaticPlayTime;
	
	public TurnBasedGame(int id, byte gameEnum, int minPlayersToStart, int maxPlayers, long automaticPlayTime) {
		super(id, gameEnum, minPlayersToStart, maxPlayers);
		
		createTeams();
		
		createScreenPositions();
		
		this.automaticPlayTime = automaticPlayTime;
	}

	public ScreenPositionInterface getScreenPositionWithIndex(int index) {
		return screenPositions.get(index);
	}

	public void setupNewNode(TurnNode node) {
		DGSLogger.log("TurnBasedGame.setupNewNode");

		this.currentTurnNode = node;

		resetCurrentPlayer();
		
		node.updateBeforeTurnStart();
		
		node.turnStart();
		
		notifyCurrentPlayer();
		
		node.updateAfterTurnStart();
	}
	
	@Override
	public void start() {
		super.start();

		DGSLogger.log(Level.INFO, " TurnBasedGame.start() " + this.getName());
		
		updatePlayersTeams();
		
		sortPlayersOrderOnGameStart();

		TurnNode newturnNode = createTurnNode();
		setupNewNode(newturnNode);
	}
	
	@Override
	public void finish() {
		DGSLogger.log("TurnBasedGame.finish(), before invalidate");
		invalidateAutomaticPlays();
		
		super.finish();
		
		// fcs changed 281220012
		clearCurrentPlayers();		
	}
	
	@Override
	public void pause() {
	}

	public void addCurrentPlayerIndex(Byte currentPlayerIndex) {
		DGSLogger.log("TurnBasedGame.addCurrentPlayerIndex: " + currentPlayerIndex);
		if (currentPlayersIndex.contains(currentPlayerIndex)) {
			throw new RuntimeException("Player index already " + currentPlayerIndex + " exists on list of current players index");
		}

		currentPlayersIndex.add(currentPlayerIndex);
		DGSLogger.log("TurnBasedGame.addCurrentPlayerIndexsz: " + currentPlayersIndex.size());
		DGSLogger.log("TurnBasedGame.addCurrentPlayerIndex_: " + currentPlayersIndex.get(0));
	}

	public void clearCurrentPlayers() {
		DGSLogger.log("TurnBasedGame.clearCurrentPlayers()");
		currentPlayersIndex.clear();
	}
	
	@Override
	public void addPlayer(Player player) {
		super.addPlayer(player);

		TurnPlayer turnPlayer = (TurnPlayer)player;
		
		String playerName = player.getPlayerName();
		
		byte screenPos = turnPlayer.getScreenPosition().getPositionIdx();
		HashSet<String> playersSet = playerByScreenPos.get(screenPos);
		DGSLogger.log("TurnBasedGame.addPlayer(), screenPos: " + screenPos + ", playersSet: " + playersSet);
		if (playersSet == null) {
			playersSet = new HashSet<String>();
			playerByScreenPos.put(screenPos, playersSet);
			DGSLogger.log("TurnBasedGame.addPlayer(), screenPos: " + screenPos + ", playersSet: " + playersSet.size());
		}
		playersSet.add(playerName);
	}
		
	@Override
	public void removePlayer(Player player) {
		super.removePlayer(player);
		
		ScreenPositionInterface screenPos = ((TurnPlayer)player).getScreenPosition();

		// it is in order so get the ordinal
		ScreenPositionInterface screenPos_ = screenPositions.get(screenPos.getPositionIdx());
		String playerName = player.getPlayerName();
		DGSLogger.log("TurnBasedGame.removePlayer(), playerName: " + playerName + ", screenPos: " + screenPos_.getPositionIdx());
		HashSet<String> playersSet = playerByScreenPos.get(screenPos_.getPositionIdx());
		DGSLogger.log("TurnBasedGame.removePlayer(), playersSet: " + playersSet);
		playersSet.remove(playerName);
		
		if (playersSet.size() > 0) {
			for (String pl : playersSet) {
				DGSLogger.log("pl: " + pl);
			}
			throw new RuntimeException("Player name: " + playerName);
		}
	}
		
	public boolean hasPlayersOnScreenPos(byte screenPos) {
		HashSet<String> playersSet = playerByScreenPos.get(screenPos);
		return playersSet != null && playersSet.size() > 0;	
	}
	
	/**
	 * Get the first player. To get a list of players retrieve 
	 * @param screenPos
	 * @return the next {@link TurnPlayer}
	 */
	public TurnPlayer getPlayerWithScreenPos(byte screenPos) {
		DGSLogger.log("TurnBasedGame.getPlayerWithScreenPos: " + screenPos);
		HashSet<String> playersSet = playerByScreenPos.get(screenPos);
		Iterator<String> iterator = playersSet.iterator();
		String playerName = iterator.next();
		if (playerName == null) {
			Iterator<String> it = playersSet.iterator();
			while (it.hasNext()) {
				DGSLogger.log("TurnBasedGame.getPlayerWithScreenPos, playerName: " + it.next());
			}
			throw new RuntimeException();
		}
		DGSLogger.log("TurnBasedGame.getPlayerWithScreenPos, playerName: " + playerName);
		return (TurnPlayer) MappingUtil.getObject(playerName);
	}
	
	public HashSet<TurnPlayer> getPlayerNamesWithScreenPos(byte screenPos) {
		HashSet<TurnPlayer> playersSet = new HashSet<TurnPlayer>();
		HashSet<String> playerNamesSet = playerByScreenPos.get(screenPos);
		for (String playerName : playerNamesSet) {
			playersSet.add((TurnPlayer)MappingUtil.getObject(playerName));
		}
		return playersSet;
	}
	
	public TurnPlayer getCurrentPlayer() {
		return getCurrentPlayerByScreenPos();
	}
	
	public TurnPlayer getCurrentPlayerByScreenPos() {
		if (currentPlayersIndex.size() > 1) {
			DGSLogger.log("There is more than one current player, it will return the first one");
		}

		// the current player index is used as a handle on turn based games
		Byte screenPos = currentPlayersIndex.get(0);

		return getPlayerWithScreenPos(screenPos);
	}

	public TurnPlayer getCurrentPlayerByEnteringOrder() {
		if (currentPlayersIndex.size() > 1) {
			DGSLogger.log("There is more than one current player, it will return the first one");
		}

		// here the current player index is used 
		// as the player entering order
		Byte playerIndex = currentPlayersIndex.get(0);

		return (TurnPlayer) super.getPlayerWithIndex(playerIndex);
	}

	public void setCurrentPlayerIndex(Byte currentPlayerIndex) {
		DGSLogger.log("TurnBasedGame.setCurrentPlayerIndex: " + currentPlayerIndex);
		clearCurrentPlayers();

		addCurrentPlayerIndex(currentPlayerIndex);
	}
	
	public void sortPlayersOrderOnGameStart() {
		addCurrentPlayerIndex((byte)0);
	}
	
	public void setupNextPlayers() {
		DGSLogger.log("TurnBasedGame.setupNextPlayers, currPlayerIndex sz: " + currentPlayersIndex.size());
		Byte currPlayerIndex = currentPlayersIndex.get(0);
		currPlayerIndex = ++currPlayerIndex >= MAX_PLAYERS ? (byte)0 : currPlayerIndex;
		DGSLogger.log("TurnBasedGame.setupNextPlayers, currPlayerIndex: " + currPlayerIndex);
		setCurrentPlayerIndex(currPlayerIndex);
	}
	
	@Override
	protected Player createGamePlayer(Client client, Object initInfo) {
		byte playerPos = (Byte)initInfo;
		DGSLogger.log("TurnBasedGame.createGamePlayer(): " + playerPos);
		ScreenPositionInterface screenPosition = screenPositions.get(playerPos);
		if (screenPosition == null) {
			throw new RuntimeException("Screen pos null, player pos: " + playerPos);
		}
		return new TurnPlayer(client, PLAYER_PREFIX + client.getName(), screenPosition);
	}
	
	protected boolean reachedMaxPlayers() {
		int maxPlayers = getMAX_PLAYERS();
		return maxPlayers > 0 && playerNames.size() - automaticPlayerNames.size() >= maxPlayers;
	}

	protected boolean checkCanStartGame() {
		final int MIN_PLAYERS_TO_START = getMinPlayersToStart();
		
		final int minPlayers = playerNames.size();
		boolean canStartGame = getSpaceState() == SpaceState.STARTED || MIN_PLAYERS_TO_START == -1 || minPlayers >= MIN_PLAYERS_TO_START;
		
		DGSLogger.log("GameSpace.checkCanStartGame, spaceState: " + getSpaceState() + ", MIN_PLAYERS_TO_START: " + MIN_PLAYERS_TO_START +  " playerNamesSz: " + playerNames.size());
		
		if (canStartGame) {
			onPreStartClear();

			start();
		}
	  
		return canStartGame;
	}
	
	@Override
	protected Player createRobotWithExistingPlayer(Player player, Object initInfo) {
		TurnPlayer turnPlayer = (TurnPlayer)player;
		ScreenPositionInterface screenPos = turnPlayer.getScreenPosition();
		byte robotIndex = (Byte)initInfo;
		TurnPlayer robotPlayer = (TurnPlayer) createRobot(robotIndex, initInfo);
		robotPlayer.setTeamName(player.getTeamName());
		robotPlayer.setScreenPosition(screenPos);
		return robotPlayer;
	}
	
	public TurnNode getCurrentTurnNode() {
		return currentTurnNode;
	}
	
	public boolean isClientTurn(Client client) {
		TurnPlayer currPlayer = getCurrentPlayer();
		if (currPlayer.isRobot()) {
			return false;
		}
		
		String currClientName = currPlayer.getClientName();
		String clientName = client.getName();
		return currClientName.equalsIgnoreCase(clientName);
	}
	
	public boolean isCurrentPlayer(Player player) {
		short playerHandle = getCurrentPlayer().getPlayerHandle();
		short playerHandle2 = player.getPlayerHandle();
		DGSLogger.log("TurnBasedGame.isCurrentPlayer() currPlayerHandle: " + playerHandle + ", playerHandle: " + playerHandle2);
		return playerHandle == playerHandle2;
	}
	
	@Override
	public void fillWithRobots() {
		Iterator<ScreenPositionInterface> it = screenPositions.iterator();
		while (it.hasNext()) {
			ScreenPositionInterface screenPos = it.next();
			DGSLogger.log("TurnBasedGame.fillWithRobots(), robotindex: " + numRobots);
			if (!hasPlayersOnScreenPos(screenPos.getPositionIdx())) {
				Player newRobot = createRobot(numRobots, screenPos);
				addPlayer(newRobot);

				sendRobotEnter((RobotInterface)newRobot);
				numRobots++;
			}
			
			if (checkCanStartGame()) {
				DGSLogger.log("TurnBasedGame.fillWithRobots(), checkCanStartGame true, robotIndex: " + numRobots);
				break;
			}
		}
	}
	
	public void resetCurrentPlayer() {
		DGSLogger.log("TurnBasedGame.resetCurrentPlayer()");
		setCurrentPlayerIndex((byte)0);
	}
	
	public ScreenPositionInterface getCurrentScreenPos() {
		TurnPlayer currentPlayer = getCurrentPlayer();
		return currentPlayer.getScreenPosition();
	}
	
	public void onPrePlayEvent(byte event) {
		DGSLogger.log("TurnBasedGame.onPrePlayEvent(), before invalidate");
		invalidateAutomaticPlays();

		DGSLogger.log("TurnBasedGame.onPrePlayEvent()");
		TurnNode currNode = currentTurnNode;
		
		currNode.onPrePlayEvent(event);
	}
	
	public void onPosPlayEvent(byte event) {
		DGSLogger.log("TurnBasedGame.onPosPlayEvent()");
		TurnNode currNode = currentTurnNode;
		currNode.onPosPlayEvent(event);
	}
	
	public long getAutomaticPlayTime() {
		return automaticPlayTime;
	}
	
	/**
	 * If time is zero there is no automatic play
	 * If automaticPlayTime > 0, schedule the time with this time
	 * @return
	 */
	public boolean hasAutomaticPlay() {
		return automaticPlayTime > 0;
	}
	
	public void checkScheduleAutomaticPlay() {
		DGSLogger.log("TurnBasedGame.checkLaunchAutomaticPlay(), automaticPlayTime: " + automaticPlayTime);

		if (!hasAutomaticPlay()) {
			return;
		}

		DGSLogger.log("TurnBasedGame.automaticPlayNames, sz: " + automaticPlayerNames.size());

		Iterator<String> it = automaticPlayerNames.iterator();
		while (it.hasNext()) {
			String automaticPlayerName = it.next();
			AutomaticPlayerInterface automaticPlayerInterface = (AutomaticPlayerInterface)getAutomaticPlayerWithPlayerName(automaticPlayerName);
			automaticPlayerInterface.scheduleAutomaticPlay(automaticPlayTime);
		}
	}
	
	public void invalidateAutomaticPlays() {
		Iterator<String> it = automaticPlayerNames.iterator();
		while (it.hasNext()) {
			String automaticPlayerName = it.next();
			AutomaticPlayerInterface automaticPlayerInterface = (AutomaticPlayerInterface)getAutomaticPlayerWithPlayerName(automaticPlayerName);
			automaticPlayerInterface.invalidateCurrentAutomaticPlays();
		}
	}
	
	public boolean isAutomaticPlayInvalidated(byte automaticPlayIndex, long automaticPlayKey) {
		String automaticPlayerName = automaticPlayerNames.get(automaticPlayIndex);
		AutomaticPlayerInterface automaticPlayerInterface = (AutomaticPlayerInterface)getAutomaticPlayerWithPlayerName(automaticPlayerName);
		return automaticPlayerInterface.isAutomaticPlayActive(automaticPlayKey);
	}
	
	public boolean removeAutomaticPlayer(byte automaticPlayIndex, long automaticPlayKey) {
		String automaticPlayerName = automaticPlayerNames.get(automaticPlayIndex);
		AutomaticPlayerInterface automaticPlayerInterface = (AutomaticPlayerInterface)getAutomaticPlayerWithPlayerName(automaticPlayerName);
		return automaticPlayerInterface.removeAutomaticPlay(automaticPlayKey);		
	}
	
	// ------------------------------------------------------------------------

	public abstract TurnNode createTurnNode();

	public abstract void createScreenPositions();
	
	public abstract void updatePlayersTeams();
	
	// Output events ----------------------------------------------------------
	
	public void sendTurnStarted(TurnNodeInfo info) {
		ServerMessage serverMessage = new ServerMessage(getId(), TurnGameProtocol.TURN_STARTED);
		DGSLogger.log("TurnBasedGame.sendTurnStarted id: " + id);
		serverMessage.sendToPlayers(this);
	}

	public void sendTurnFinished(TurnNodeInfo info) {
		ServerMessage serverMessage = new ServerMessage(getId(), TurnGameProtocol.TURN_FINISHED);
		DGSLogger.log("TurnBasedGame.sendTurnFinished id: " + id);
		serverMessage.sendToPlayers(this);
	}

	public void sendMatchStarted(TurnNodeInfo info) {
		ServerMessage serverMessage = new ServerMessage(getId(), TurnGameProtocol.MATCH_STARTED);
		DGSLogger.log("TurnBasedGame.sendMatchStartyed id: " + id);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendMatchFinished() {
		ServerMessage serverMessage = new ServerMessage(getId(), TurnGameProtocol.MATCH_FINISHED);
		DGSLogger.log("TurnBasedGame.sendMatchFinished id: " + id);
		serverMessage.sendToPlayers(this);
	}

	public TurnPlayer notifyCurrentPlayer() {
		TurnPlayer currPlayer = getCurrentPlayer();
		
		DGSLogger.log("TurnBasedGame.notifyCurrentPlayer(), before invalidate");
		invalidateAutomaticPlays();
		
		if (!currPlayer.isRobot()) {
			checkScheduleAutomaticPlay();
		}

		sendCurrentPlayer(currPlayer);
		
		return currPlayer;
	}
	
	protected void updateServerMessageOnConnectedClient(ServerMessage serverMessage, Client client) {
		short handle = client.getId();
		serverMessage.put(handle);
		serverMessage.put(client.getSessionName());
		serverMessage.put((byte)client.getClientStatus().ordinal());
		TurnPlayer player = (TurnPlayer) client.getPlayer();
		byte screenPos = player.getScreenPosition().getPositionIdx();
		serverMessage.put(screenPos);
	}
	
	public void checkDoRobotPlay() {
		DGSLogger.log("TurnBasedGame.checkDoRobotPlay()");
		
		if (!isGameRunning() || getCurrentTurnNode().hasTurnFinished()) {
			DGSLogger.log("TurnBasedGame.checkDoRobotPlay: spaceState == SpaceState.FINISHED");
			return;
		}

		TurnPlayer currPlayer = getCurrentPlayer();
		if (currPlayer.isRobot()) {
			notifyCurrentPlayer();
		}
	}
	
	public void sendClientEnter(Client client) {
		ServerMessage serverMessage = new ServerMessage(getId(), SpaceProtocol.ENTER);
		serverMessage.put(client.getId());
		serverMessage.put(client.getSessionName());
		serverMessage.put((byte)client.getClientStatus().ordinal());
		byte screenPos = ((TurnPlayer)client.getPlayer()).getScreenPosition().getPositionIdx();
		serverMessage.put(screenPos); // screen pos
		serverMessage.sendToPlayers(this);
		DGSLogger.log("Space.sendClientEnter: " + id + " ScreenPos: " + screenPos);
	}
	
	public void sendRobotEnter(RobotInterface robot) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_ROBOT_ENTER);
		byte robotIndex = robot.getRobotIndex();
		serverMessage.put(robotIndex);
		byte screenPos = ((TurnRobotInterface)robot).getRobotScreenPos();
		serverMessage.put(screenPos);
		serverMessage.sendToPlayers(this);
		DGSLogger.log("GameSpace.sendRobotEnter, id: " + id + " robotIndex: " + robotIndex);		
	}
	
	public void sendSelectables(Player player) {
		ServerMessage serverMessage = new ServerMessage(getId(), TurnGameProtocol.SET_THIS_PLAYER_SELECTABLES);
		serverMessage.put(true);
		DGSLogger.log("TurnBasedGame.sendSelectables playerName: " + player.getPlayerName());
		serverMessage.sendToPlayer(player);
	}
	
	public void sendCurrentPlayer(TurnPlayer currPlayer) {
		DGSLogger.log("TurnBasedGame.notifyCurrentPlayer(), currPlayer: " + currPlayer.getPlayerName() + ", screenPos: " + currPlayer.getScreenPosition());
		ServerMessage serverMessage = null;
		if (currPlayer.isRobot()) {
			byte robotIndex = ((RobotInterface)currPlayer).getRobotIndex();
			serverMessage = new ServerMessage(getId(), TurnGameProtocol.NOTIFY_CURRENT_PLAYER_ROBOT);
			serverMessage.put(robotIndex);
			DGSLogger.log("TurnBasedGame.notifyCurrentPlayer name: " + currPlayer.getPlayerName() + " id: " + id + " robotIndex: " + robotIndex);
		} else {
			serverMessage = new ServerMessage(getId(), TurnGameProtocol.NOTIFY_CURRENT_PLAYER);
			short handle = currPlayer.getPlayerHandle();
			serverMessage.put(handle);
			DGSLogger.log("TurnBasedGame.notifyCurrentPlayer name: " + currPlayer.getPlayerName() + " id: " + id + " handle: " + handle);
		}
		
		serverMessage.sendToPlayers(this);
	}
}