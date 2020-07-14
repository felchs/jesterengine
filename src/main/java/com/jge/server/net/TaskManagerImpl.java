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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link TaskManager}
 * This is a simple implementation of task manager using the common java fixed thread pool executor service
 * Check out the {@link Executors#newFixedThreadPool(int)}
 * It uses by default threads the number or available processors on the machine.
 * It's recommended to use the default, but in case you have a specific machine configuration you can set it.
 * To change the number of threads on the config.properties by the variable: com.jge.server.net.nthreads_taskmanager
 * 
 * @see TaskManager
 *
 */
public class TaskManagerImpl implements TaskManager {
	/**
	 * The {@link ExecutorService} of this {@link TaskManagerImpl}
	 */
	private final ExecutorService newFixedThreadPool;
	
	/**
	 * The delay to check for new task to be executed
	 */
	private long delayCheckerNewTasksExecution = 1000;
	
	/**
	 * <pre>
	 * A {@link SortedMap} of {@link TaskHandle}
	 *  key -> timeStamp the task must be triggered
	 *  value -> the task itself
	 * </pre>
	 */
	private SortedMap<Long, Set<TaskHandle>> taskHandleMap = Collections.synchronizedSortedMap(new TreeMap<Long, Set<TaskHandle>>());
	
	/**
	 * Empty Constructor
	 * It loads the {@link Executor}
	 */
	public TaskManagerImpl() {
		int nThreads = Runtime.getRuntime().availableProcessors();
		String nThreadsProperty = AppContext.getProperty("com.jge.server.net.nthreads_taskmanager");
		if (!nThreadsProperty.equalsIgnoreCase("default")) {
			try {
				nThreads = Integer.parseInt(nThreadsProperty);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.newFixedThreadPool = Executors.newFixedThreadPool(nThreads);
		
		checkExecutionThread();
	}

	/**
	 * It creates a new thread to check every {@link TaskManagerImpl#delayCheckerNewTasksExecution} milliseconds
	 */
	private void checkExecutionThread() {
		new Thread() {
			private Set<TaskHandle> tasksToRemove = new HashSet<TaskHandle>();
			
			private boolean active = true;
			
			@Override
			public void run() {
				while (active) {
					// each second look for tasks to be executed
					try {
						Thread.sleep(delayCheckerNewTasksExecution);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Set<Long> keysToRemove = new HashSet<Long>();
					Set<Long> keys = taskHandleMap.keySet();
					for (Long key : keys) {
						Set<TaskHandle> taskHandleSet = taskHandleMap.get(key);
						for (TaskHandle taskHandle : taskHandleSet) {
							Task task = taskHandle.getTask();
							
							long currTime = System.currentTimeMillis();
							
							if (!taskHandle.isPeriodic() ||
								(taskHandle.isPeriodic() && 
									(taskHandle.isInfinitePeriodicTask() || taskHandle.decreaseNumTimesToExecute() > 0) &&
									taskHandle.canExecuteByTime(currTime))) {
								
								taskHandle.decreaseNumTimesToExecute();
								newFixedThreadPool.execute(task);
								
								tasksToRemove.add(taskHandle);
							}
						}
						
						// just removing the unused tasks
						taskHandleSet.removeAll(tasksToRemove);
						
						if (taskHandleSet.isEmpty()) {
							keysToRemove.add(key);
						}
					}
					
					for (Long key : keysToRemove) {
						taskHandleMap.remove(key);
					}
				}
			}
		}.start();
	}
	
	/**
	 * Puts a {@link TaskHandle} on the {@link TaskManagerImpl#taskHandleMap}
	 * @param timeToStart the time to start the {@link Task} handled by the {@link TaskHandle}
	 * @param taskHandle the {@link TaskHandle} to be inserted into {@link TaskManagerImpl#taskHandleMap}
	 */
	private void putOnTaskHandleMap(long timeToStart, TaskHandle taskHandle) {
		Set<TaskHandle> taskHandleSet = taskHandleMap.get(timeToStart);
		if (taskHandleSet == null) {
			taskHandleSet = new HashSet<TaskHandle>();
			taskHandleMap.put(timeToStart, taskHandleSet);
		}
		taskHandleSet.add(taskHandle);
	}

	/**
	 * {@inheritDoc}
	 */
	public TaskHandle startTask(Task task) {
		return scheduleTask(task, 0);
	}

	/**
	 * {@inheritDoc}
	 */	
	public TaskHandle scheduleTask(Task task, long delayToStart) {
		TaskHandle taskHandle = new TaskHandle(task, delayToStart);
		long timeToStart = System.currentTimeMillis() + delayToStart;
		putOnTaskHandleMap(timeToStart, taskHandle);
		return taskHandle;
	}
	
	/**
	 * {@inheritDoc}
	 */	
	public TaskHandle schedulePeriodicTask(Task task, long delayToStart, long period) {
		return schedulePeriodicTask(task, delayToStart, period, -1);
	}

	/**
	 * {@inheritDoc}
	 */	
	public TaskHandle schedulePeriodicTask(Task task, long delayToStart, long period, long numTimesToExecute) {
		TaskHandle taskHandle = new TaskHandle(task, delayToStart, period, numTimesToExecute);
		long timeToStart = System.currentTimeMillis() + delayToStart;
		putOnTaskHandleMap(timeToStart, taskHandle);
		return taskHandle;
	}
}