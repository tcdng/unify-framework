/*
 * Copyright 2018-2024 The Code Department.
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

/**
 * Task runner.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TaskRunner extends UnifyComponent {

	/**
	 * Start runner with supplied parameter.
	 * 
	 * @param maxRunThread the maximum number of thread
	 * @return true if started otherwise false if already started
	 */
	boolean start(int maxRunThread);

	/**
	 * Stops runner and all its scheduled tasks.
	 */
	void stop();

	/**
	 * Restarts runner with supplied parameter.
	 * 
	 * @param maxRunThread the maximum number of thread
	 */
	void restart(int maxRunThread);

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
	 * @param taskName             the task name
	 * @param parameters           the task parameters
	 * @param permitMultiple       permits task to be scheduled multiple times
	 * @param logMessages          indicates if messages should be logged
	 * @param inDelayInMillSec     initial delay in milliseconds
	 * @param repeatDelayInMillSec repeat delay in milliseconds
	 * @param numberOfTimes        number of times task should run. Endless repeat
	 *                             if <= 0
	 * @return true if scheduled otherwise false
	 * @throws UnifyException if an error occurs
	 */
	boolean schedule(String taskName, Map<String, Object> parameters, boolean permitMultiple, boolean logMessages,
			long inDelayInMillSec, long periodInMillSec, int numberOfTimes) throws UnifyException;
}
