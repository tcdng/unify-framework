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
package com.tcdng.unify.web.ui.widget.panel;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.widget.AbstractPanel;
import com.tcdng.unify.web.ui.widget.data.TaskMonitorInfo;

/**
 * Represents a task monitor box panel.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-taskmonitorpanel")
@UplBinding("web/panels/upl/taskmonitorpanel.upl")
public class TaskMonitorPanel extends AbstractPanel {

    @Override
    @Action
    public void switchState() throws UnifyException {
        super.switchState();
        TaskMonitorInfo taskMonitorInfo = getValue(TaskMonitorInfo.class);
        if (taskMonitorInfo != null) {
            boolean isBusy = taskMonitorInfo.getTaskState() == TaskMonitorInfo.BUSY;
            getWidgetByShortName("cancelBtn").setVisible(isBusy);
            getWidgetByShortName("doneBtn").setVisible(!isBusy);
            setAllowRefresh(isBusy);
        }
    }

    @Action
    public void cancelTask() throws UnifyException {
        TaskMonitorInfo taskMonitorInfo = getValue(TaskMonitorInfo.class);
        taskMonitorInfo.cancelTask();
        commandHidePopup();
    }

    @Action
    public void taskDone() throws UnifyException {
        TaskMonitorInfo taskMonitorInfo = getValue(TaskMonitorInfo.class);
        String taskDonePath = null;
        if (taskMonitorInfo.getTaskState() == TaskMonitorInfo.PASS) {
            taskDonePath = taskMonitorInfo.getOnSuccessPath();
        } else if (taskMonitorInfo.getTaskState() == TaskMonitorInfo.FAIL
                || taskMonitorInfo.getTaskState() == TaskMonitorInfo.ERROR) {
            taskDonePath = taskMonitorInfo.getOnFailurePath();
        }

        if (StringUtils.isNotBlank(taskDonePath)) {
            commandPost(taskDonePath);
        } else {
            commandHidePopup();
        }
    }

}
