/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core;

import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskOutput;
import com.tcdng.unify.core.task.TaskStatus;

/**
 * Test task monitor.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TestTaskMonitor implements TaskMonitor {

    @Override
    public String getTaskName(int taskIndex) {
        return null;
    }

    @Override
    public TaskStatus getTaskStatus(int taskIndex) {
        return null;
    }

    @Override
    public TaskStatus getCurrentTaskStatus() {
        return null;
    }

    @Override
    public String getTaskId(int taskIndex) {
        return null;
    }

    @Override
    public TaskOutput getTaskOutput(int taskIndex) {
        return null;
    }

    @Override
    public TaskOutput getCurrentTaskOutput() {
        return null;
    }

    @Override
    public int getTaskCount() {
        return 0;
    }

    @Override
    public int getCurrentTaskIndex() {
        return 0;
    }

    @Override
    public void addMessage(String message) {

    }

    @Override
    public void addErrorMessage(UnifyError unifyError) {

    }

    @Override
    public String[] getMessages() {
        return null;
    }

    @Override
    public String getLastMessage() {
        return null;
    }

    @Override
    public void clearMessages() {

    }

    @Override
    public void addException(Exception e) {

    }

    @Override
    public Exception[] getExceptions() {
        return null;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public boolean isExceptions() {
        return false;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

}
