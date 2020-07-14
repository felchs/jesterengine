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

import java.io.Serializable;

public class GameObject<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected T id;
	
	protected boolean enabled = true;
	
	protected boolean selectable = true;
		
	public GameObject() {
	}
	
	public GameObject(T id) {
		this.id = id;
	}
	
	public T getId() {
		return id;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	public boolean isSelectable() {
		return selectable;
	}
	
	public void reset() {
		this.enabled = true;
		this.selectable = true;
	}
}