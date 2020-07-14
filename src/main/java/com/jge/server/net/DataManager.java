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

/**
 * Interface to manage instances on server
 * Any moment it is possible to bind or to remove the bind of a given Object
 *
 */
public interface DataManager {
	/**
	 * Gets a binded object
	 * @param objectName the given name for object bind
	 * @return the object binded or or null if not binded
	 */
	Object getBinding(String objectName);

	/**
	 * Sets a bind to an object
	 * @param bindingName an object name for binding
	 * @param managedObject the object to be binded
	 */
	void setBinding(String bindingName, Object managedObject);

	/**
	 * Removes a bind to an object
	 * @param bindingName an object name of the binding
	 */
	void removeBinding(String bindingName);
}
