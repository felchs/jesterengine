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
package com.jge.server.space.game.rating;


public class ClientRating {
	private static final long serialVersionUID = 1L;
	
	private float rating;
	
	private float tournamentRating;
	
	private int stars;
	
	private int fidelity;

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public float getTournamentRating() {
		return tournamentRating;
	}

	public void setTournamentRating(float tournamentRating) {
		this.tournamentRating = tournamentRating;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public int getFidelity() {
		return fidelity;
	}

	public void setFidelity(int fidelity) {
		this.fidelity = fidelity;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}