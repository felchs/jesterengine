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
 * Interface to a Task Management
 * 
 */
public interface TaskManager {
	/**
	 * Starts a {@link Task}
	 * @param task the {@link Task} to be started
	 * @return a {@link TaskHandle} for {@link Task} management
	 */
	TaskHandle startTask(Task task);
	
	/**
	 * Schedules a {@link Task} to be executed in future
	 * @param task the {@link Task} to be started
	 * @param delayToStart
	 * @return a {@link TaskHandle} for {@link Task} management
	 */
	TaskHandle scheduleTask(Task task, long delayToStart);
	
	/**
	 * Schedules a periodic {@link Task} to be executed in future
	 * @param task the {@link Task} to be started
	 * @param delayToStart
	 * @param period
	 * @return a {@link TaskHandle} for {@link Task} management
	 */
	TaskHandle schedulePeriodicTask(Task task, long delayToStart, long period);
	
	/**
	 * Schedules a periodic {@link Task} to be executed in future
	 * @param task the {@link Task} to be started
	 * @param delayToStart
	 * @param period
	 * @param numTimesToExecute
	 * @return a {@link TaskHandle} for {@link Task} management
	 */
	TaskHandle schedulePeriodicTask(Task task, long delayToStart, long period, long numTimesToExecute);
}
