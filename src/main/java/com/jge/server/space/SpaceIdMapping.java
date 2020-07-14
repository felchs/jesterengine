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
package com.jge.server.space;

import java.util.Vector;
import java.util.logging.Level;

import com.jge.server.utils.DGSLogger;
import com.jge.server.utils.MappingUtil;

/**
 * When a new {@link Space} is created is is mapped in this class
 *  
 */
public class SpaceIdMapping {
	/**
	 * All {@link Space}'s created have this String prefix
	 */
	public static final String SPACE_PREFIX = "SPC_";

	/**
	 * Predefined available spaces ids to use
	 */
	private Vector<Integer> availableSpacesId = new Vector<Integer>();
	
	/**
	 * When a {@link Space} is created the id used is set here
	 */
	private Vector<Integer> usedSpacesId = new Vector<Integer>();
	
	/**
	 * Empty constructor
	 */
	public SpaceIdMapping() { }
	
	/**
	 * Adds a new predefined space id to the list
	 * @param id the {@link Space} id
	 */
	public void addAvailableSpaceId(Integer id) {
		availableSpacesId.add(id);
	}
	
	/**
	 * Gets a new space id, and set it to used
	 * @return the {@link Space} id
	 */
	public Integer getFreeSpaceIdAndMoveToUsed() {
		Integer id = availableSpacesId.remove(availableSpacesId.size() - 1);
		usedSpacesId.add(id);
		return id;
	}
	
	/**
	 * Check whether there is an available {@link Space} id or not
	 * @return whether there is an available {@link Space} id or not
	 */
	public boolean hasAvailableId() {
		return !availableSpacesId.isEmpty();
	}
		
	/**
	 * Frees a used space it, remove the used id from {@link SpaceIdMapping#usedSpacesId} to {@link SpaceIdMapping#availableSpacesId} list 
	 * @param id the id to move
	 */
	public void freeSpace(Integer id) {
		usedSpacesId.remove(id);
		availableSpacesId.add(id);
	}
	
	/**
	 * Moves a {@link Space}'s id to used list
	 * @param space the {@link Space} to get the id from
	 */
	public void moveToUsed(Space space) {
		moveToUsed(space.getId());
	}
	
	/**
	 * Moves a id from from {@link SpaceIdMapping#usedSpacesId} to {@link SpaceIdMapping#availableSpacesId} list
	 * @param spaceId the {@link Space} id to move
	 */
	public void moveToUsed(Integer spaceId) {
		if (!availableSpacesId.remove(spaceId)) {
			DGSLogger.log(Level.SEVERE, "The space ID: " + spaceId + " could not be removed from id list.");
		} else {
			usedSpacesId.add(spaceId);
		}
	}

	/**
	 * Maps a new {@link Space} 
	 * @param space the {@link Space} to be mapped
	 */
	public static void mapSpace(Space space) {
		String objectName = SpaceIdMapping.SPACE_PREFIX + space.getId();
		DGSLogger.log("SpaceIdMapping.addObject, objectName: " + objectName + ", space: " + space);
		MappingUtil.addObject(objectName, space);
	}

	/**
	 * Gets a mapped {@link Space} with id
	 * @param id the id of the mapped {@link Space}
	 * @return the mapped {@link Space}
	 */
	public static Space getSpaceWithId(int id) {
		String objectName = SPACE_PREFIX + id;
		return (Space)MappingUtil.getObject(objectName);
	}
}