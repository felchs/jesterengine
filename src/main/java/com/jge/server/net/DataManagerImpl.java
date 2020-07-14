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
package com.jge.server.net;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link DataManager}
 * It uses a {@link ConcurrentHashMap} to bind the objects
 */
public class DataManagerImpl implements DataManager {

	/**
	 * Data mapping with {@link ConcurrentHashMap}
	 */
	private ConcurrentHashMap<String, Object> dataMap = new ConcurrentHashMap<String, Object>();	
	
	/**
	 * {@inheritDoc}
	 */
	public Object getBinding(String objectName) {
		return dataMap.get(objectName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBinding(String bindingName, Object managedObject) {
		dataMap.put(bindingName, managedObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeBinding(String bindingName) {
		dataMap.remove(bindingName);
	}

}
