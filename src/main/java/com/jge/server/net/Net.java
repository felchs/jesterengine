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
 * This class is the abstraction for client x server message sending
 * It can be implemented as any net engine 
 */
public abstract class Net {
	/**
	 * Entry point of message receiving it's the net processor 
	 */
	private NetProcessor netProcessor;

	/**
	 * Constructor passing parameters
	 * @param netProcessor the entry point of message receiving
	 */
	public Net(NetProcessor netProcessor) {
		this.netProcessor = netProcessor;
	}
	
	/**
	 * Gets the net processor
	 * @return the {@link SessionMessageReceiver} that is the net processor of this {@link Net} class
	 */
	public NetProcessor getNetProcessor() {
		return netProcessor;
	}
}