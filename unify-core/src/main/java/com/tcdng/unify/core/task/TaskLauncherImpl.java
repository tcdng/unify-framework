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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;

/**
 * Default implementation of a task launcher.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_TASKLAUNCHER)
public class TaskLauncherImpl extends AbstractUnifyComponent implements TaskLauncher {

    @Configurable(ApplicationComponents.APPLICATION_TASKMANAGER)
    private TaskManager taskManager;

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public TaskMonitor launchTask(TaskSetup taskSetup) throws UnifyException {
        if (taskSetup.getTaskNames().isEmpty()) {
            throw new UnifyException(UnifyCoreErrorConstants.TASKSETUP_NO_TASK);
        }

        switch (taskSetup.getType()) {
            case RUN_AFTER:
                return taskManager.scheduleTasksToRunAfter(taskSetup.getTaskNames(), taskSetup.getParameters(),
                        taskSetup.isMessages(), taskSetup.isDependent(), taskSetup.getDelayInMillSec(),
                        taskSetup.getLogger());
            case RUN_PERIODIC:
                return taskManager.scheduleTasksToRunPeriodically(taskSetup.getTaskNames(), taskSetup.getParameters(),
                        taskSetup.isMessages(), taskSetup.isDependent(), taskSetup.getDelayInMillSec(),
                        taskSetup.getPeriodInMillSec(), taskSetup.getNumberOfTimes(), taskSetup.getLogger());
            case RUN_IMMEDIATE_BLOCK:
                return taskManager.executeTasks(taskSetup.getTaskNames(), taskSetup.getParameters(),
                        taskSetup.isMessages(), taskSetup.isDependent(), taskSetup.getLogger());
            case RUN_IMMEDIATE:
            default:
                return taskManager.startTasks(taskSetup.getTaskNames(), taskSetup.getParameters(),
                        taskSetup.isMessages(), taskSetup.isDependent(), taskSetup.getLogger());
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
