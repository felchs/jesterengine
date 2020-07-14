package com.jge.server.space.game.achievements;

import java.io.Serializable;

public class GameStatisticsStruct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int wins;
	
	public int loses;
	
	public int draws;
	
	public int abandon;
	
	public byte gameId;
	
	public String email;
}
