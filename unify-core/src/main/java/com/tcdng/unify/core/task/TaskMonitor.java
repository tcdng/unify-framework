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

import com.tcdng.unify.core.UnifyError;

/**
 * Used for monitoring the status of a task and also canceling or stopping its
 * execution.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface TaskMonitor {

    /**
     * Returns the name of the task being monitored.
     * 
     */
    String getTaskName();

    /**
     * Returns the task output object.
     * 
     * @param taskIndex
     *            the task index
     */
    TaskOutput getTaskOutput();

    /**
     * Adds a task message. Typically set by the executing task to give real-time
     * information on task's progress.
     */
    void addMessage(String message);

    /**
     * Adds an error message to the task monitor using information from supplied
     * error object.
     * 
     * @param unifyError
     *            the error object to use
     */
    void addErrorMessage(UnifyError unifyError);

    /**
     * Gets the task messages. Typically called by the monitoring process. For
     * instance a process that displays information about the progress of the task.
     */
    String[] getMessages();

    /**
     * Returns the last message added to task.
     */
    String getLastMessage();

    /**
     * Clears all task messages.
     */
    void clearMessages();

    /**
     * Adds a task exception. Exceptions are added by the task manager if any is
     * thrown during execution of the task.
     * 
     * @param e
     *            the exception to add
     */
    void addException(Exception e);

    /**
     * Gets all exceptions thrown during execution of the task.
     */
    Exception[] getExceptions();

    /**
     * Indicates begin.
     */
    void begin();

    /**
     * Indicates done.
     */
    void done();
    
    /**
     * Cancels task.
     */
    void cancel();

    /**
     * Returns true if task has been canceled.
     */
    boolean isCanceled();

    /**
     * Returns true if task has any exceptions.
     */
    boolean isExceptions();

    /**
     * Returns true if task is done running.
     */
    boolean isDone();

    /**
     * Returns true if task is running
     */
    boolean isRunning();
}