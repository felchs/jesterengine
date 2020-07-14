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
package com.jge.server.net.netty;

/**
 * Helper thread used to start the Netty engine
 */
public class NettyStarterThread extends Thread {
	/**
	 * Time to sleep this thread until {@link Netty} to be started
	 */
	private static final int SLEEP_TIME = 100;

	/**
	 * {@link Netty} instance
	 */
	private Netty netty;
	
	/**
	 * Whether it started or not
	 */
	private boolean started;
	
	/**
	 * Constructor passing {@link Netty} instance
	 * @param netty the {@link Netty} instance
	 */
	public NettyStarterThread(Netty netty) {
		this.netty = netty;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		netty.init(this);
	}

	/**
	 * Waits the {@link Netty} to be started to release the thread
	 */
	public void waitUntilStart() {
		this.start();
		
		while (!started) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Notifies this thread when {@link Netty} started, so this instance can be released
	 */
	public void notifyStart() {
		this.started = true;
	}
}
