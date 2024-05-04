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
package com.tcdng.unify.core;

import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskOutput;

/**
 * Test task monitor.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TestTaskMonitor implements TaskMonitor {

    @Override
	public String getTaskName() {
		return null;
	}

	@Override
	public TaskOutput getTaskOutput() {
		return new TaskOutput();
	}

	@Override
	public int expectedRuns() {
		return 0;
	}

	@Override
	public int actualRuns() {
		return 0;
	}

	@Override
	public boolean isNotPermitted() {
		return false;
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
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isExceptions() {
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

	@Override
	public boolean isExited() {
		return isNotPermitted() || isCancelled() || isDone();
	}

}
