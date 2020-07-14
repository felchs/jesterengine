package com.jge.server.space.game;

import java.util.ArrayList;

public abstract class GameFactory {
	
	private static GameFactory instance;
	
	public static void setInstance(GameFactory spaceFactory) {
		if (GameFactory.instance != null) {
			throw new RuntimeException("This instance is global, you shoule set this instance just once.");
		}
		GameFactory.instance = spaceFactory;
	}
	
	public static GameFactory getInstance() {
		return instance;
	}

	///////////////////////////////////////////////////////////////////////////
	
	public abstract GameSpace createGame(byte gameEnum, int numPlayers, boolean fillWithRobots);
	
	public abstract int getNumPlayers(byte gameEnum);

	public abstract ArrayList<Byte> getGameTypes();
}
