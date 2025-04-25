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

/**
 * Task status logger.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface TaskStatusLogger extends UnifyComponent {

	/**
	 * Logs task status.
	 * 
	 * @param tm         the task monitor
	 * @param parameters the task parameters
	 * @throws UnifyException if an error occurs
	 */
	void logStatus(TaskMonitor tm, Map<String, Object> parameters) throws UnifyException;
}
