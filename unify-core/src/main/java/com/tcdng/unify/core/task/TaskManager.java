/*
 * Copyright 2018-2023 The Code Department.
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.data.ParamConfig;

/**
 * Task manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TaskManager extends UnifyComponent {

    /**
     * Returns the task configuration for a taskable method.
     * 
     * @param taskName
     *            the task name
     * 
     * @throws UnifyException
     *             if task with name is unknown. If an error occurs.
     */
    TaskableMethodConfig getTaskableMethodConfig(String taskName) throws UnifyException;

    /**
     * Returns all taskable method configurations.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    Collection<TaskableMethodConfig> getAllTaskableMethodConfigs() throws UnifyException;

    /**
     * Checks if taskable method with name exists in manager's scope.
     * 
     * @param taskName
     *            the task name
     * @return a true value if found otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    boolean isTaskableMethod(String taskName) throws UnifyException;

    /**
     * Gets task annotated parameters.
     * 
     * @param taskName
     *                 the task name
     * @return list of task parameters
     * @throws UnifyException
     *                        if task with name is unknown
     */
    List<ParamConfig> getTaskParameters(String taskName) throws UnifyException;
    
    /**
     * Executes a task with specified name. Blocks until task completes execution or
     * fails. Task will still run in a separate thread.
     * 
     * @param taskName
     *            the task name
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor executeTask(String taskName, Map<String, Object> parameters, boolean logMessages,
            String taskStatusLoggerName) throws UnifyException;

    /**
     * Executes a sequence of tasks which are executed in sequence. Blocks until all
     * tasks complete execution or one fails depending on the dependent flag. Tasks
     * will still run in a separate thread.
     * 
     * @param taskNames
     *            the task names
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param dependent
     *            the dependent flag. Determines if next task should run if current
     *            task fails.
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor executeTasks(List<String> taskNames, Map<String, Object> parameters, boolean logMessages,
            boolean dependent, String taskStatusLoggerName) throws UnifyException;

    /**
     * Starts a task with specified name. Does not block and returns immediately
     * with a task monitor. Task is setup to run in some other thread.
     * 
     * @param taskName
     *            the task name
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor startTask(String taskName, Map<String, Object> parameters, boolean logMessages,
            String taskStatusLoggerName) throws UnifyException;

    /**
     * Starts tasks which are executed in sequence. Does not block and returns
     * immediately with task monitors. Tasks are setup to run sequentially in some
     * other thread.
     * 
     * @param taskNames
     *            the task names
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param dependent
     *            the dependent flag. Determines if next task should run if current
     *            task fails.
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor startTasks(List<String> taskNames, Map<String, Object> parameters, boolean logMessages,
            boolean dependent, String taskStatusLoggerName) throws UnifyException;

    /**
     * Schedules a task with specified name to run after a delay. Does not block and
     * returns immediately with a task monitor. Taska is setup to run in some other
     * thread after a period.
     * 
     * @param taskName
     *            the task name
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param delayInMillSec
     *            the delay in milliseconds
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return TaskMonitor - the task monitor
     * @throws UnifyException
     *             - If an error occurs
     */
    TaskMonitor scheduleTaskToRunAfter(String taskName, Map<String, Object> parameters, boolean logMessages,
            long delayInMillSec, String taskStatusLoggerName) throws UnifyException;

    /**
     * Schedules tasks which are executed in sequence after a delay. Does not block
     * and returns immediately with task monitors. Tasks are setup to run
     * sequentially in some other thread after a period.
     * 
     * @param taskNames
     *            the task names
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param dependent
     *            the dependent flag. Determines if next task should run if current
     *            task fails.
     * @param delayInMillSec
     *            the delay in milliseconds
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor scheduleTasksToRunAfter(List<String> taskNames, Map<String, Object> parameters, boolean logMessages,
            boolean dependent, long delayInMillSec, String taskStatusLoggerName) throws UnifyException;

    /**
     * Schedules a task with specified name to run periodically. Does not block and
     * returns immediately with a task monitor. Taskable is setup to run
     * periodically in some other thread after a delay.
     * 
     * @param taskName
     *            the task name
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param inDelayInMillSec
     *            the initial delay in milliseconds
     * @param periodInMillSec
     *            the period in milliseconds
     * @param numberOfTimes
     *            the number of times. Ignored if less or equal to zero
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor scheduleTaskToRunPeriodically(String taskName, Map<String, Object> parameters, boolean logMessages,
            long inDelayInMillSec, long periodInMillSec, int numberOfTimes, String taskStatusLoggerName)
            throws UnifyException;

    /**
     * Schedules tasks which are executed in sequence periodically. Does not block
     * and returns immediately with task monitors. Tasks are setup to run
     * periodically in some other thread after a delay.
     * 
     * @param taskNames
     *            the task names
     * @param parameters
     *            the execution parameters
     * @param logMessages
     *            the log messages flag that indicates if messages should be logged
     *            to task monitor
     * @param dependent
     *            the dependent flag. Determines if next task should run if current
     *            task fails.
     * @param inDelayInMillSec
     *            the initial delay in milliseconds
     * @param periodInMillSec
     *            the period in milliseconds
     * @param numberOfTimes
     *            the number of times. Ignored if less or equal to zero
     * @param taskStatusLoggerName
     *            the task status logger. Can be null
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor scheduleTasksToRunPeriodically(List<String> taskNames, Map<String, Object> parameters,
            boolean logMessages, boolean dependent, long inDelayInMillSec, long periodInMillSec, int numberOfTimes,
            String taskStatusLoggerName) throws UnifyException;

    /**
     * Schedule periodic execution
     * 
     * @param periodicType
     *            the periodic type
     * @param businessServiceName
     *            the business service name
     * @param methodName
     *            the method name
     * @param taskStatusLoggerNameName
     *            the task logger if any
     * @param inDelayInMillSec
     *            the initial delay in milliseconds
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    TaskMonitor schedulePeriodicExecution(PeriodicType periodicType, String businessServiceName, String methodName,
            String taskStatusLoggerNameName, long inDelayInMillSec) throws UnifyException;
}
