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

import java.util.concurrent.CopyOnWriteArrayList;

import com.jge.server.ServerMessage;
import com.jge.server.client.Client;
import com.jge.server.space.Space;
import com.jge.server.space.SpaceState;
import com.jge.server.space.game.achievements.Achievements;
import com.jge.server.space.game.ranking.Ranking;
import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

public abstract class GameSpace extends Space {
	public static transient final String ROBOT_PREFIX = "ROBOT:";
	
	private final byte gameEnum;

	protected final String PLAYER_PREFIX;

	private final int MIN_PLAYERS_TO_START;

	protected final int MAX_PLAYERS;
	
	protected Ranking ranking;
	
	private int restartCount;
	
	// the order is important
	protected CopyOnWriteArrayList<String> playerNames = new CopyOnWriteArrayList<String>();
	
	protected CopyOnWriteArrayList<String> automaticPlayerNames = new CopyOnWriteArrayList<String>();

	protected CopyOnWriteArrayList<String> teamNames = new CopyOnWriteArrayList<String>();
	
	protected GameScore gameScore;
	
	protected int numHumanPlayers;
	
	protected int numHumanPlayersEnteredGame;
	
	protected byte numRobots;

	private boolean canFillWithRobots;

	public GameSpace(int id, byte gameEnum, int minPlayersToStart, int maxPlayers) {
		super(id);
		
		this.gameEnum = gameEnum;

		this.PLAYER_PREFIX = this.getClass().getSimpleName() + id  + ":PLAYER:";

		this.MIN_PLAYERS_TO_START = minPlayersToStart;

		this.MAX_PLAYERS = maxPlayers;
	}
	
	public byte getGameEnum() {
		return gameEnum;
	}
	
	public int getMAX_PLAYERS() {
		return MAX_PLAYERS;
	}
	
	public CopyOnWriteArrayList<String> getAutomaticPlayerNames() {
		return automaticPlayerNames;
	}
	
	public int getNumPlayers() {
		return playerNames.size();
	}

	public int getNumRobots() {
		return numRobots;
	}

	public int getMinPlayersToStart() {
		return MIN_PLAYERS_TO_START;
	}

	public void restartCall() {
		DGSLogger.log("GameSpace.restartCall, spaceState: " + getSpaceState());
		if (getSpaceState() == SpaceState.FINISHED) {
			DGSLogger.log("GameSpace.restartCall, restartCount: " + restartCount + " MAX_PLAYERS: " + MAX_PLAYERS);
			int totalValidPlayers = MAX_PLAYERS - getNumRobots();
			if (++restartCount == totalValidPlayers) {
				onPreStartClear();
				DGSLogger.log("GameSpace.restartCall, before call start()");
				start();
			}
		}
	}
	
	public void onPreStartClear() {
		if (ranking != null) {
			Ranking ranking_ = ranking;
			ranking_.checkCleanPoints();
		}
		restartCount = 0;
	}

	public void start() {
		DGSLogger.log("GameSpace.start()");

		setSpaceState(SpaceState.STARTED);
		
		notifyParentGameStarted();
		
		sendGameStartEvent();

		createAutomaticPlayers();
		
		createGameScore();
	}
	
	public void pause() {
	}
	
	public void finish() {
		boolean hasHumanPlayers = hasHumanPlayers();
		
		DGSLogger.log("GameSpace.finish, hasHumanPlayers: " + hasHumanPlayers);

		if (hasHumanPlayers) {
			if (hadPlayedWithOtherHumans()) {
				updateStatisticsOnGameFinish();
			}
			
			sendGameFinished();
			
			sendGameResults();
			
			if (getAutomaticRestartState()) {
				scheduleRestartCall();
			} else {
				sendCanRestart(getCanRestartFlag());
			}
		}
		else {
			DGSLogger.log("GameSpace.finish(), canRestartGame, else");
			removePlayersAndSendExit(false);
		}
		
		setSpaceState(SpaceState.FINISHED);
	}
	
	protected boolean getCanRestartFlag() {
		return true;
	}

	private void scheduleRestartCall() {
		restartCall();
	}

	public void addPlayer(Player player) {
		String playerName = player.getPlayerName();
		DGSLogger.log("GameSpace.addPlayer(): " + playerName);

		playerNames.add(playerName);
		MappingUtil.addObject(playerName, player);
		
		if (player.isRobot()) {
			notifyParentRobotEnter((RobotInterface)player);
		} else {
			numHumanPlayers++;
			numHumanPlayersEnteredGame++;
		}
	}
	
	public void addAutomaticPlayer(AutomaticPlayerInterface player) {
		final String playerName = player.getName();
		automaticPlayerNames.add(playerName);
		
		MappingUtil.addObject(playerName, player);
	}
	
	public void removePlayer(Player player) {
		String playerName = player.getPlayerName();
		DGSLogger.log("GameSpace.removePlayer(): " + playerName);
		
		removePlayerFromTeam(player);

		MappingUtil.removeObject(playerName);
		
		if (player.isRobot()) {
			numRobots--;
		} else {
			numHumanPlayers--;
		}
		
		notifyParentPlayerExit(player);
	}
	
	public void removePlayerName(String playerName) {
		DGSLogger.log("GameSpace.removePlayerName: " + playerName);
		playerNames.remove(playerName);
	}
	
	public void removePlayerFromTeam(Player player) {
		String teamName = player.getTeamName();
		GameTeam gameTeam = getTeam(teamName);
		if (gameTeam != null) {
			gameTeam.removePlayer(player);
		}
	}
	
	public void removePlayerFromTeam(String playerName) {
		Player player = (Player)MappingUtil.getObject(playerName);
		String teamName = player.getTeamName();
		GameTeam gameTeam = getTeam(teamName);
		gameTeam.removePlayerWithName(playerName);
	}
	
	public Player[] getPlayers() {
		CopyOnWriteArrayList<String> playerNames = getPlayerNames();
		int numPlayers = playerNames.size();
		Player[] playersList = new Player[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			String playerName = playerNames.get(i);
			Player player = getPlayerWithPlayerName(playerName);
			playersList[i] = player;
		}
		return playersList;
	}

	public Player getPlayerWithIndex(int index) {
		String playerName = playerNames.get(index);
		return getPlayerWithPlayerName(playerName);
	}
	
	public String getPlayerName(String clientName) {
		return PLAYER_PREFIX + clientName;
	}

	public Player getPlayerWithClientName(String clientName) {
		String playerNameWithPrefix = getPlayerName(clientName);
		return getPlayerWithPlayerName(playerNameWithPrefix);
	}

	public Player getPlayerWithPlayerName(String playerName) {
		DGSLogger.log("Player___: "+ playerName + " SZ: " + playerNames.size());
		for (String player : playerNames) {
			DGSLogger.log("Player: " + player);
		}
		if (playerNames.contains(playerName)) {
			return (Player) MappingUtil.getObject(playerName);
		}

		return null;
	}
	
	public AutomaticPlayerInterface getAutomaticPlayerWithPlayerName(String playerName) {
		DGSLogger.log("AutomticPlayer___: "+ playerName + " sz: " + automaticPlayerNames.size());
		for (String player : automaticPlayerNames) {
			DGSLogger.log("Player: " + player);
		}
		if (automaticPlayerNames.contains(playerName)) {
			return (AutomaticPlayerInterface) MappingUtil.getObject(playerName);
		}
		return null;
	}
	
	public Client getClientWithPlayerName(String playerName) {
		String clientName = playerName.replace(PLAYER_PREFIX, "");
		return (Client)MappingUtil.getObject(clientName);
	}
	
	public Client getClient(String clientName) {
		return (Client)MappingUtil.getObject(clientName);
	}

	public CopyOnWriteArrayList<String> getPlayerNames() {
		return playerNames;
	}
	
	public String getOpponentTeamName(Player player) {
		String playerTeamName = player.getTeamName();
		int sz = teamNames.size();
		for (int i = 0; i < sz; i++) {
			String teamName = teamNames.get(i);
			if (!teamName.equals(playerTeamName)) {
				return teamName;
			}
		}

		return null;
	}

	protected void addTeam(GameTeam team) {
		String teamName = team.getName();

		teamNames.add(teamName);
		
		MappingUtil.addObject(teamName, team);
	}

	public GameTeam getTeam(String name) {
		if (teamNames.contains(name)) {
			return (GameTeam) MappingUtil.getObject(name);
		}

		return null;
	}
	
	public CopyOnWriteArrayList<String> getTeamNames() {
		return teamNames;
	}

	/**
	 * Removes the players proxies and send the client from this GameSpace to a
	 * parent space
	 * @param hasHumanPlayers 
	 */
	protected void removePlayersAndSendExit(boolean hasHumanPlayers) {
		DGSLogger.log("GameSpace.removePlayersAndSendExit()");
		int sz = playerNames.size();
		for (int i = 0; i < sz; i++) {
			String gamePlayerName = playerNames.get(i);

			Player gamePlayer = getPlayerWithPlayerName(gamePlayerName);
			
			
			removePlayer(gamePlayer);

			if (gamePlayer.isAutomaticPlayer()) {
				continue;
			}

			if (gamePlayer.isRobot()) {
				sendRobotExit(((RobotInterface)gamePlayer).getRobotIndex());
			} else {
				Client client = gamePlayer.getClient();
				sendClientExit(client);
			}
		}
		
		playerNames.clear();
		
		if (numRobots != 0) {
			throw new RuntimeException();
		}
		
		if (numHumanPlayers != 0) {
			throw new RuntimeException();
		}
	}
	
	public GameScore getGameScore() {
		return gameScore;
	}

	@Override
	public boolean putClient(Client client, Object initInfo) {
		updateAchievements(client);
		
		if (reachedMaxPlayers()) {
			DGSLogger.log("GameSpace.putClient, client name: " + client.getName() + " could not be put. Max players reached!");
			return false;
		}
		
		if (isClientInSpace(client)) {
			DGSLogger.log("GameSpace.putClient, client name: " + client.getName() + " could not be put. The client was already on this game!");
			return false;
		}
		
		// do game player creation before putting client on space
		Player gamePlayer = createGamePlayer(client, initInfo);
		addPlayer(gamePlayer);

		super.putClient(client, initInfo);

		checkCanStartGame();
		
		return true;
	}
		
	@Override
	public boolean onExit(Client client, boolean disconnected) {
		if (!super.onExit(client, disconnected)) {
			return false;
		}
		
		DGSLogger.log("GameSpace.removePlayerWithClient, name: " + client.getName());
		Player player = client.getPlayer();
		String playerName = player.getPlayerName();
		
		if (player != null) {
			if (!player.isRobot()) {
				updateRankingOnExit(player, disconnected);
			}
	
			removePlayer(player);
		}
		removePlayerName(playerName);
		
		boolean gameIsRunning = isGameRunning();
		DGSLogger.log("GameSpace.removePlayerWithClient(), gameIsRunning: " + gameIsRunning);
		boolean hasHumanPlayers = hasHumanPlayers();
		DGSLogger.log("GameSpace.removePlayerWithClient, hasHumanPlayers: " + hasHumanPlayers);
		
		if (gameIsRunning) {
			if (hasHumanPlayers) {
				if (doPutBot()) {
					numRobots++;
					Player newRobot = createRobotWithExistingPlayer(player, numRobots);
					addPlayer(newRobot);
					short clientToReplace = client.getId();
					RobotInterface robotInterface = (RobotInterface)newRobot;
					byte robotIndex = robotInterface.getRobotIndex();
					sendRobotReplacement(clientToReplace, robotIndex);
					sendRobotEnter(robotInterface);
					
					checkDoRobotPlay();
				}
			} else {
				DGSLogger.log("GameSpace.removePlayerWithClient, finish");
				finish();
			}
		} else if (!hasHumanPlayers) {
			DGSLogger.log("GameSpace.removePlayerWithClient(), !hasHumanPlayers");
			removePlayersAndSendExit(false);
		}
		
		return true;
	}

	public boolean isGameRunning() {
		return getSpaceState() == SpaceState.STARTED || getSpaceState() == SpaceState.PAUSED;
	}

	protected boolean hasHumanPlayers() {
		return numHumanPlayers > 0;
	}
	
	protected boolean hadPlayedWithOtherHumans() {
		return numHumanPlayersEnteredGame > 1;
	}
	
	protected boolean doPutBot() {
		return true;
	}
	
	protected boolean reachedMaxPlayers() {
		int maxPlayers = getMAX_PLAYERS();
		int numPlayers = getNumPlayers();
		DGSLogger.log("reachedMaxPlayers, maxPlayers: " + maxPlayers + ", numPlayers: " + numPlayers);
		return maxPlayers > 0 && numPlayers >= maxPlayers;
	}
	
	protected boolean checkCanStartGame() {
 		boolean canStartGame = getSpaceState() == SpaceState.STARTED || 
 												  MIN_PLAYERS_TO_START == -1 || 
 												  getNumPlayers() >= MIN_PLAYERS_TO_START;
 		DGSLogger.log("GameSpace.checkCanStartGame, spaceState: " + getSpaceState() + ", MIN_PLAYERS_TO_START: " + MIN_PLAYERS_TO_START +  " playerNamesSz: " + getNumPlayers() + " Can start: "+ canStartGame); 
		
		if (canStartGame) {
			onPreStartClear();

			start();
		}
	  
		return canStartGame;
	}
	
	public boolean hasAchievements() {
		return true;
	}

	public void updateAchievements(Client client) {
		if (!hasAchievements()) {
			return;
		}
		
		Achievements clientAchievements = client.getAchievements(getGameEnum());
		if (clientAchievements == null) {
			Achievements newAchievements = createAchievements();
			client.putAchievements(getGameEnum(), newAchievements);
		}
	}

	protected Player createGamePlayer(Client client, Object initInfo) {
		return new Player(client, PLAYER_PREFIX + client.getName());
	}

	public void setRanking(Ranking ranking) {
		if (ranking == null) {
			throw new NullPointerException();
		}
		this.ranking = ranking;
	}
	
	public boolean hasRanking() {
		return ranking != null;
	}
	
	public void setCanFillWithRobots(boolean canFillWithRobots) {
		this.canFillWithRobots = canFillWithRobots;
	}
	
	public boolean canFillWithRobots() {
		return canFillWithRobots;
	}
	
	protected void createGameScore() {
		this.gameScore = new GameScore();
		DGSLogger.log("GameSpace.createGameScore(), gameScore: " + gameScore);
	}

	protected abstract Player createRobotWithExistingPlayer(Player player, Object initInfo);
	
	protected abstract Player createRobot(byte robotIndex, Object initInfo);

	protected abstract void createTeams();
	
	public abstract void fillWithRobots();
	
	protected abstract Achievements createAchievements();
	
	protected abstract void updateStatisticsOnGameFinish();
	
	protected abstract void notifyParentRobotEnter(RobotInterface robot);
	
	protected abstract void notifyParentPlayerExit(Player player);
	
	protected abstract void notifyParentGameStarted();
	
	// Output Events ----------------------------------------------------------

	public void sendGamePlayerGiveUp(short handle) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_PLAYER_GIVE_UP);
		serverMessage.put(handle);
		DGSLogger.log("Space.sendGamePlayerGiveUp id: " + id + " handle: " + handle);
		serverMessage.sendToPlayers(this);
	}

	public void sendGamePlayerFall(short handle) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_PLAYER_FALL);
		serverMessage.put(handle);
		DGSLogger.log("GameSpace.sendGamePlayerFall id: " + id + " handle: " + handle);
		serverMessage.sendToPlayers(this);
	}

	public void sendGameStartEvent() {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_STARTED);
		DGSLogger.log("Space.sendGameStartEvent id: " + id + " fnc: " + GameProtocol.GAME_STARTED);
		serverMessage.sendToPlayers(this);
	}

	public void sendGameFinished() {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_FINISHED);
		DGSLogger.log("GameSpace.sendGameFinished id: " + id);
		serverMessage.sendToPlayers(this);
	} 

	public void sendCanRestart(boolean canRestartFlag) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_CAN_RESTART);
		DGSLogger.log("GameSpace.sendCheckCanRestart id: " + id);
		serverMessage.put(canRestartFlag);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendRobotEnter(RobotInterface robotInterface) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_ROBOT_ENTER);
		byte robotIndex = robotInterface.getRobotIndex();
		serverMessage.put(robotIndex);
		serverMessage.sendToPlayers(this);
		DGSLogger.log("GameSpace.sendRobotEnter, id: " + id + " robotIndex: " + robotIndex);
	}
	
	public void sendRobotExit(byte robotIndex) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_ROBOT_EXIT);
		serverMessage.put(robotIndex);
		DGSLogger.log("GameSpace.sendRobotExit id: " + id + " robotIndex: " + robotIndex);
		serverMessage.sendToPlayers(this);
	}
	
	public void sendRobotReplacement(short clientToReplace, byte robotIndex) {
		ServerMessage serverMessage = new ServerMessage(getId(), GameProtocol.GAME_ROBOT_REPLACEMENT);
		serverMessage.put(clientToReplace);
		serverMessage.put(robotIndex);
		serverMessage.sendToPlayers(this);
		DGSLogger.log("GameSpace.sendRobotEnter, id: " + id + " robotIndex: " + robotIndex);		
	}
	
	protected abstract void sendGameResults();

	public abstract void sendScore(GameScore score);
	
	protected abstract void updateRankingOnExit(Player player, boolean disconnected);
	
	protected abstract boolean getAutomaticRestartState();
	
	protected abstract void checkDoRobotPlay();
	
	protected abstract void createAutomaticPlayers();
}