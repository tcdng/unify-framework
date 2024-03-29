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

/**
 * Used for logging the status of task during execution.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TaskStatusLogger extends UnifyComponent {

    /**
     * Logs the status of a task using supplied task monitor.
     * 
     * @param taskMonitor
     *            the task monitor
     * @param parameters
     *            the task parameters
     */
    void logTaskStatus(TaskMonitor taskMonitor, Map<String, Object> parameters);

    /**
     * Logs and exception that occurs when task is running.
     * 
     * @param e
     *            the exception to log
     */
    void logTaskException(Exception e);
}
