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
 * When a {@link Task} is set to be executed by {@link TaskManager} it generates a
 * {@link TaskHandle} that is a class to save {@link Task}'s specifications
 * These specifications can be used by the {@link TaskManager} to handle task scheduling,
 * executions or even task canceling
 *
 */
public class TaskHandle {
	/**
	 * A delay t start a given {@link Task}
	 */
	private long delayToStart;
	
	/**
	 * If the {@link Task} handled by this {@link TaskHandle} is periodic, the period of delay between execution of
	 * this same {@link Task} 
	 */
	private long period;
	
	/**
	 * If this {@link Task} is period it will execute N times -> {@link TaskHandle#numTimesToExecute}
	 */
	private long numTimesToExecute = 1;
	
	/**
	 * The last time this {@link TaskHandle} executed
	 * Returns 0 if net set
	 */
	private long lastExecution;

	/**
	 * The {@link Task} which this {@link TaskHandle} refers to
	 */
	private Task task;
	
	/**
	 * Whether the {@link Task} was signed to be canceled or not
	 * {@link TaskManager} will check this flag when scheduling or executing this {@link Task} 
	 */
	private boolean canceled;
	
	/**
	 * Constructor passing parameters
	 * @param task the {@link Task} to be handled
	 * @param delayToStart a delay to start the {@link Task}
	 */
	public TaskHandle(Task task, long delayToStart) {
		this(task, delayToStart, -1, 1);
	}
	
	/**
	 * Constructor passing parameters 
	 * @param task the {@link Task} to be handled
	 * @param delayToStart a delay to start the {@link Task}
	 * @param period if this {@link Task} is periodic, the period of delay between execution of this same {@link Task}
	 * @param numTimesToExecute if this {@link Task} is period it will execute N times -> {@link TaskHandle#numTimesToExecute}
	 */
	public TaskHandle(Task task, long delayToStart, long period, long numTimesToExecute) {
		this.task = task;
		this.delayToStart = delayToStart;
		this.period = period;
		this.numTimesToExecute = numTimesToExecute;
	}
	
	/**
	 * Gets the delay to start the {@link Task}
	 * @return the delay to start the {@link Task}
	 */
	public long getDelay() {
		return delayToStart;
	}
	
	/**
	 * Gets the period of delay between {@link Task} execution
	 * If {@link Task} is periodic, the period of delay between execution of
	 * this same {@link Task}
	 * @return the period of delay between {@link Task} execution
	 */
	public long getPeriod() {
		return period;
	}
	
	/**
	 * Whether the {@link Task} handled is period or not
	 * @return whether the {@link Task} handled is period or not
	 */
	public boolean isPeriodic() {
		return period > -1;
	}
	
	/**
	 * Gets the number of times to execute the {@link Task} handled by this {@link TaskHandle
	 * If this {@link Task} is period it will execute N times -> {@link TaskHandle#numTimesToExecute}
	 * @return the number of times to execute the {@link Task} handled by this {@link TaskHandle}
	 */
	public long getNumTimesToExecute() {
		return numTimesToExecute;
	}
	
	/**
	 * Whether this task will always be executed periodically
	 * @return whether this task will always be executed periodically
	 */
	public boolean isInfinitePeriodicTask() {
		return numTimesToExecute < 0;
	}
	
	/**
	 * Decreases the number of times of the {@link Task} handled by this {@link TaskHandle} to be executed
	 * @return the number of times to execute the {@link Task} handled by this {@link TaskHandle}
	 */
	public long decreaseNumTimesToExecute() {
		if (isInfinitePeriodicTask()) {
			return numTimesToExecute;
		}
		
		numTimesToExecute--;
		
		if (numTimesToExecute < 0) {
			numTimesToExecute = 0;
		}
		
		return numTimesToExecute;
	}
	
	/**
	 * Gets the last execution time of the {@link Task} handled by this {@link TaskHandle}
	 * @return the last execution time of the {@link Task} handled by this {@link TaskHandle}
	 */
	public long getLastExecution() {
		return lastExecution;
	}
	
	/**
	 * Sets the the last execution time of the {@link Task} handled by this {@link TaskHandle
	 * @param lastExecution the the last execution time of the {@link Task} handled by this {@link TaskHandle}
	 */
	public void setLastExecution(long lastExecution) {
		this.lastExecution = lastExecution;
	}
	
	/**
	 * Gets the {@link Task} handled by this {@link TaskHandle
	 * @return the {@link Task} handled by this {@link TaskHandle
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Signs the {@link Task} handled by this {@link TaskHandle} to be canceled
	 */
	public void cancel() {
		this.canceled = true;
	}
	
	/**
	 * Gets whether the {@link Task} handled by this {@link TaskHandle} can be executed based on the time it was set to be executed 
	 * @param currTime the current time to check for execution
	 * @return whether can execute the task by time or not
	 */
	public boolean canExecuteByTime(long currTime) {
		return currTime > getLastExecution() + period;
	}

	/**
	 * Gets whether the {@link Task} handled by this {@link TaskHandle} is active or not
	 * @return whether the {@link Task} handled by this {@link TaskHandle} is active or not
	 */
	public boolean isActive() {
		return !canceled && !isInfinitePeriodicTask() && getNumTimesToExecute() <= 0;
	}
}
