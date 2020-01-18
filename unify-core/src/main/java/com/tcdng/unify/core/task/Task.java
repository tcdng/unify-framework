/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Represents a task to be executed. Tasks run in separate threads.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface Task extends UnifyComponent {

    /**
     * Returns task instance information object based on supplied input.
     * 
     * @param input
     *            the task input object
     * @return a task instance information object
     * @throws UnifyException
     *             if an error occurs
     */
    TaskInstanceInfo getTaskInstanceInfo(TaskInput input) throws UnifyException;

    /**
     * Executes task with specified parameters and is expected to block until
     * execution is completed or cancelled.
     * 
     * @param taskMonitor
     *            the task monitor object
     * @param input
     *            the task input object
     * @param output
     *            the task output object
     * @throws UnifyException
     *             if an error occurs
     */
    void execute(TaskMonitor taskMonitor, TaskInput input, TaskOutput output) throws UnifyException;
}
