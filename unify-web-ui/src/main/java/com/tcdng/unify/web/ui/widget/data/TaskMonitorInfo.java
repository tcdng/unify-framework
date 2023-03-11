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
package com.tcdng.unify.web.ui.widget.data;

import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskOutput;

/**
 * Taskable monitor data object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TaskMonitorInfo {

    public static final int BUSY = 0;

    public static final int PASS = 1;

    public static final int FAIL = 2;

    public static final int ERROR = 3;

    private TaskMonitor taskMonitor;

    private String caption;

    private String onSuccessPath;

    private String onFailurePath;

    public TaskMonitorInfo(TaskMonitor taskMonitor, String caption) {
        this(taskMonitor, caption, null, null);
    }

    public TaskMonitorInfo(TaskMonitor taskMonitor, String caption, String onSuccessPath, String onFailurePath) {
        this.taskMonitor = taskMonitor;
        this.caption = caption;
        this.onSuccessPath = onSuccessPath;
        this.onFailurePath = onFailurePath;
    }

    public String getCaption() {
        return caption;
    }

    public String getOnSuccessPath() {
        return onSuccessPath;
    }

    public String getOnFailurePath() {
        return onFailurePath;
    }

    public String[] getMessages() {
        return taskMonitor.getMessages();
    }

    public String getLastMessage() {
        return taskMonitor.getLastMessage();
    }

    public TaskOutput getTaskOutput(int taskIndex) {
        return taskMonitor.getTaskOutput(taskIndex);
    }

    public int getTaskState() {
        switch (taskMonitor.getCurrentTaskStatus()) {
            case ABORTED:
            case CANCELED:
            case FAILED:
                return FAIL;
            case SUCCESSFUL:
                if (taskMonitor.isExceptions()) {
                    return ERROR;
                }
                return PASS;
            case INITIALIZED:
            default:
                return BUSY;
        }
    }

    public void cancelTask() {
        this.taskMonitor.cancel();
    }

}
