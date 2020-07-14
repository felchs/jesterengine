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
package com.jge.server.space.game.achievements;

public abstract class Achievement {
	private byte gameType;

	private int achievementId;
	
	private int quantityAchieved;
	
	private int quantityRequired;
	
	private long achievementDate;
	
	private boolean needExport;
	
	public Achievement(byte gameType, int achievementId, int quantityAchived, int quantityRequired) {
		this.gameType = gameType;
		this.achievementId = achievementId;
		this.quantityAchieved = quantityAchived;
		this.quantityRequired = quantityRequired;
	}
	
	public byte getGameId() {
		return gameType;
	}
	
	public int getAchievementId() {
		return achievementId;
	}
	
	public void setQuantityAchieved(int quantityAchieved) {
		this.quantityAchieved = quantityAchieved;
	}
	
	public int getQuantityAchieved() {
		return quantityAchieved;
	}
	
	public void setQuantityRequired(int quantityRequired) {
		this.quantityRequired = quantityRequired;
	}
	
	public int getQuantityRequired() {
		return quantityRequired;
	}
	
	public long getAchievementDate() {
		return achievementDate;
	}
	
	public void setAchievementDate(long achievementDate) {
		this.achievementDate = achievementDate;
	}
		
	public boolean isExported() {
		return needExport;
	}
	
	public void setNeedExport(boolean needExport) {
		this.needExport = needExport;
	}
	
	public abstract boolean didAchieve();
}