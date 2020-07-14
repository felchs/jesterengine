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
package com.jge.server.utils;

import java.util.Vector;

public class IdList  {
	private Vector<Short> availableNames = new Vector<Short>();
	private Vector<Short> usedIds = new Vector<Short>();
	private String namePrefix;
	
	public IdList() {
		this("");
	}
	
	public IdList(String namePrefix) {
		this.namePrefix = namePrefix;
	}
	
	public String getNextNameId() {
		return namePrefix + getNextId();
	}
	
	public short getNextId() {
		if (!availableNames.isEmpty()) {
			Short id = availableNames.remove(0);
			usedIds.add(id);
			return id;
		} else if (usedIds.isEmpty()) {
			return getRandomId(1000);
		} else {
			Short id = usedIds.get(usedIds.size() - 1);
			return getRandomId(id);
		}
	}

	private Short getRandomId(long xor) {
		Short newPseudoRandom = LFSR.getNextShort("IdList");
		if (availableNames.contains(newPseudoRandom) || usedIds.contains(newPseudoRandom)) {
//			availableNames.add(newPseudoRandom);
			throw new RuntimeException();
		}
		return newPseudoRandom;
	}

	public void freeName(String clientName) {
		if (!namePrefix.contains(clientName)) {
			throw new RuntimeException();
		}
		String idStr = clientName.substring(namePrefix.length() - 1);
		short id = Short.parseShort(idStr);
		freeWithId(id);
	}
	
	public void freeWithId(short id) {
		usedIds.remove(id);
		availableNames.add(id);
	}
}