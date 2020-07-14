package com.jge.server.space.game.achievements;

import java.io.Serializable;

public class AchievementStruct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public byte gameId;
	
	public int achievementId;
	
	public int quantityAchieved;
	
	public int quantityRequired;
	
	public long achievemntDate;
	
	public String email;
}
