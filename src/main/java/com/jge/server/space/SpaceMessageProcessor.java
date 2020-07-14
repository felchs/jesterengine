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

import java.nio.ByteBuffer;

import com.jge.server.utils.DGSLogger;

public class SpaceMessageProcessor {
	protected SpaceMessageAdapter spaceOutMessageInterface;
	
	public SpaceMessageProcessor(SpaceMessageAdapter spaceOutMessageInterface) {
		this.spaceOutMessageInterface = spaceOutMessageInterface;
	}
	
	public SpaceMessageAdapter getSpaceOut() {
		return spaceOutMessageInterface;
	}

	public boolean callFunction(byte event, ByteBuffer message) {
		DGSLogger.log("SpaceMessageProcessor.callFunction, event: " + event);
		if (SpaceProtocol.INIT.getId() == event) {
			getSpaceOut().init(message);
			return true;
		}
		else if (SpaceProtocol.ENTER.getId() == event) {
			getSpaceOut().enter(message);
			return true;
		}
		else if (SpaceProtocol.EXIT.getId() == event) {
			getSpaceOut().onExit(message);
			return true;
		}
		else if (SpaceProtocol.INFO.getId() == event) {
			getSpaceOut().info(message);
			return true;
		}
		else if (SpaceProtocol.UPDATE_CONNECTED_CLIENTS.getId() == event) {
			getSpaceOut().updateConnectedClients(message);
			return true;
		}
		else if (SpaceProtocol.BYTE_ENABLE.getId() == event) {
			byte objectId = message.get();
			boolean enabled_ = message.get() == 1;
			getSpaceOut().setByteEnabled(objectId, enabled_);
			return true;
		}
		else if (SpaceProtocol.BYTE_ENABLES.getId() == event) {
			byte len = message.get();
//			if (len == 0) {
//				boolean enabled_ = message.get() == 1;
//				getSpaceOut().setAllEnabled(enabled_);
//			} else {
				for (int i = 0; i < len; i++) {
					byte objectId = message.get();
					boolean enabled_ = message.get() == 1;
					getSpaceOut().setByteEnabled(objectId, enabled_);
//				}
			}
			return true;
		}
		else if (SpaceProtocol.SHORT_ENABLE.getId() == event) {
			short objectId = message.getShort();
			boolean enabled_ = message.getShort() == 1;
			getSpaceOut().setShortEnabled(objectId, enabled_);
			return true;
		}
		else if (SpaceProtocol.SHORT_ENABLES.getId() == event) {
			short len = message.getShort();
			for (int i = 0; i < len; i++) {
				short objectId = message.getShort();
				boolean enabled_ = message.get() == 1;
				getSpaceOut().setShortEnabled(objectId, enabled_);
			}
			return true;
		}
		else if (SpaceProtocol.BYTE_VISIBLE.getId() == event) {
			byte objectId = message.get();
			boolean visible_ = message.get() == 1;
			getSpaceOut().setByteVisible(objectId, visible_);
			return true;
		}
		else if (SpaceProtocol.BYTE_VISIBLES.getId() == event) {
			byte len = message.get();
//			if (len == 0) {
//				boolean visible_ = message.get() == 1;
//				getSpaceOut().setAllVisible(visible_);
//			} else {
				for (int i = 0; i < len; i++) {
					byte objectId = message.get();
					boolean visible_ = message.get() == 1;
					getSpaceOut().setByteVisible(objectId, visible_);
				}
//			}
			return true;
		}
		else if (SpaceProtocol.SHORT_VISIBLE.getId() == event) {
			short objectId = message.getShort();
			boolean visible_ = message.get() == 1;
			getSpaceOut().setShortVisible(objectId, visible_);
			return true;
		}
		else if (SpaceProtocol.SHORT_VISIBLES.getId() == event) {
			short len = message.getShort();
			for (int i = 0; i < len; i++) {
				short objectId = message.get();
				boolean visible_ = message.get() == 1;
				getSpaceOut().setShortVisible(objectId, visible_);
			}
			return true;
		}
		else if (SpaceProtocol.BYTE_SELECTION.getId() == event) {
			byte objectId = message.get();
			boolean selected_ = message.get() == 1;
			getSpaceOut().setByteSelected(objectId, selected_);
			return true;
		}
		else if (SpaceProtocol.BYTE_SELECTIONS.getId() == event) {
			byte len = message.get();
//			if (len == 0) {
//				boolean selected_ = message.get() == 1;
//				getSpaceOut().setAllSelected(selected_);
//			} else {
				for (byte i = 0; i < len; i++) {
					byte objectId = message.get();
					boolean selected_ = message.get() == 1;
					getSpaceOut().setByteSelected(objectId, selected_);
				}
//			}
			return true;
		}
		else if (SpaceProtocol.SHORT_SELECTION.getId() == event) {
			short objectId = message.getShort();
			boolean selected_ = message.get() == 1;
			getSpaceOut().setShortSelected(objectId, selected_);
			return true;
		}
		else if (SpaceProtocol.SHORT_SELECTIONS.getId() == event) {
			short len = message.getShort();
			for (byte i = 0; i < len; i++) {
				short objectId = message.getShort();
				boolean selected_ = message.get() == 1;
				getSpaceOut().setShortSelected(objectId, selected_);
			}
			return true;
		}
		else if (SpaceProtocol.BYTE_SELECTABLE.getId() == event) {
			byte objectId = message.get();
			boolean selected_ = message.get() == 1;
			getSpaceOut().setByteSelectable(objectId, selected_);
			return true;
		}
		else if (SpaceProtocol.BYTE_SELECTABLES.getId() == event) {
			byte len = message.get();
//			if (len == 0) {
//				boolean selected_ = message.get() == 1;
//				getSpaceOut().setAllSelectable(selected_);
//			} else {
				for (byte i = 0; i < len; i++) {
					byte objectId = message.get();
					boolean selected_ = message.get() == 1;
					getSpaceOut().setByteSelectable(objectId, selected_);
				}
//			}
			return true;
		}
		else if (SpaceProtocol.SHORT_SELECTABLE.getId() == event) {
			short objectId = message.getShort();
			boolean selected_ = message.get() == 1;
			getSpaceOut().setShortSelectable(objectId, selected_);
			return true;
		}
		else if (SpaceProtocol.SHORT_SELECTABLES.getId() == event) {
			short len = message.get();
			for (byte i = 0; i < len; i++) {
				short objectId = message.getShort();
				boolean selected_ = message.get() == 1;
				getSpaceOut().setShortSelectable(objectId, selected_);
			}
			return true;
		}
		
		return false;
	}
}
