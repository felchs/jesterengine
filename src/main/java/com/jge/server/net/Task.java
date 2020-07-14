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
 * Represents the interface to a task that must be executed by
 * {@link TaskManager}.
 * 
 * To execute a task call: 
 * {@link TaskManager#schedulePeriodicTask(Task, long, long)}
 * {@link TaskManager#schedulePeriodicTask(Task, long, long, long)}
 * {@link TaskManager#scheduleTask(Task, long)}
 * {@link TaskManager#startTask(Task)}
 * 
 */
public interface Task extends Runnable {
	/**
	 * The execution of the task must be implemented into this method
	 * It's called by {@link TaskManager} to run this {@link Task}
	 */
	public void run();
}
