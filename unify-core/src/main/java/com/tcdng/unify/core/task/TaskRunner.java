/*
 * Copyright 2018-2025 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.core.task;

import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.PeriodicType;

/**
 * Task runner.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface TaskRunner extends UnifyComponent {

	/**
	 * Start runner with supplied parameter.
	 * 
	 * @param maxRunThread the maximum number of thread
	 * @param permitMultiple the value to set
	 * @return true if started otherwise false if already started
	 */
	boolean start(int maxRunThread, boolean permitMultiple);

	/**
	 * Stops runner and all its scheduled tasks.
	 */
	void stop();

	/**
	 * Checks if this runner is running.
	 * 
	 * @return true if running otherwise false
	 */
	boolean isRunning();

	/**
	 * Checks if task is scheduled on this runner.
	 * 
	 * @param taskName the task name
	 * @return true if scheduled otherwise false
	 */
	boolean isScheduled(String taskName);

	/**
	 * Adds a task to runner schedule.
	 * 
	 * @param periodicType     the periodic type
	 * @param taskName         the task name
	 * @param parameters       the task parameters
	 * @param logMessages      indicates if messages should be logged
	 * @param inDelayInMillSec initial delay in milliseconds
	 * @return the task monitor
	 * @throws UnifyException if an error occurs
	 */
	TaskMonitor schedule(PeriodicType periodicType, String taskName, Map<String, Object> parameters,
			boolean logMessages, long inDelayInMillSec) throws UnifyException;

	/**
	 * Adds a task to runner schedule.
	 * 
	 * @param taskName             the task name
	 * @param parameters           the task parameters
	 * @param logMessages          indicates if messages should be logged
	 * @param inDelayInMillSec     initial delay in milliseconds
	 * @param repeatDelayInMillSec repeat delay in milliseconds
	 * @param numberOfTimes        number of times task should run. Endless repeat
	 *                             if <= 0
	 * @return the task monitor
	 * @throws UnifyException if an error occurs
	 */
	TaskMonitor schedule(String taskName, Map<String, Object> parameters, boolean logMessages, long inDelayInMillSec,
			long periodInMillSec, int numberOfTimes) throws UnifyException;

	/**
	 * Adds a task to runner schedule.
	 * 
	 * @param tmc                  the taskable method configuration
	 * @param taskName             the task name
	 * @param parameters           the task parameters
	 * @param logMessages          indicates if messages should be logged
	 * @param inDelayInMillSec     initial delay in milliseconds
	 * @param repeatDelayInMillSec repeat delay in milliseconds
	 * @param numberOfTimes        number of times task should run. Endless repeat
	 *                             if <= 0
	 * @return the task monitor
	 * @throws UnifyException if an error occurs
	 */
	TaskMonitor schedule(TaskableMethodConfig tmc, String taskName, Map<String, Object> parameters, boolean logMessages,
			long inDelayInMillSec, long periodInMillSec, int numberOfTimes) throws UnifyException;
}
