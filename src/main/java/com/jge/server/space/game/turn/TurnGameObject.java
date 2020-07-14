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

import com.jge.server.space.game.GameObject;

public class TurnGameObject extends GameObject<Object> {
	private static final long serialVersionUID = 1L;

	protected int ownerScreenPosIndex = -1;

	public TurnGameObject() {
	}

	public TurnGameObject(Object id) {
		super(id);
	}

	public void setScreenOwnerIndex(int ownerScreenIndexPos) {
		this.ownerScreenPosIndex = ownerScreenIndexPos;
	}

	public int getOwnerScreenPosIndex() {
		return ownerScreenPosIndex;
	}

	@Override
	public void reset() {
		super.reset();

		this.ownerScreenPosIndex = -1;
	}
}