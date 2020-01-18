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
import com.tcdng.unify.core.annotation.Component;

/**
 * Periodic execution task.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = "periodicexecution-task")
public class PeriodicExecutionTask extends AbstractTask {

    @Override
    public void execute(TaskMonitor taskMonitor, TaskInput input, TaskOutput output) throws UnifyException {
        try {
            if (!taskMonitor.isCanceled()) {
                PeriodicExecutionInfo periodicExecutionInfo = input.getParam(PeriodicExecutionInfo.class,
                        PeriodicExecutionTaskConstants.PERIODICEXECUTIONINFO);
                UnifyComponent unifyComponent = getComponent(periodicExecutionInfo.getComponentName());
                periodicExecutionInfo.getMethod().invoke(unifyComponent, taskMonitor);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
    }
}
