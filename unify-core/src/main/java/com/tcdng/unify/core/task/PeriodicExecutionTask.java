/*
 * Copyright 2018-2025 The Code Department.
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
 * @author The Code Department
 * @since 4.1
 */
@Component(PeriodicExecutionTaskConstants.PERIODIC_METHOD_TASK)
public class PeriodicExecutionTask extends AbstractTask {

	@Override
	public void execute(TaskMonitor tm, TaskInput input) throws UnifyException {
		try {
			if (!tm.isCancelled()) {
				PeriodicExecutionInfo periodicExecutionInfo = input.getParam(PeriodicExecutionInfo.class,
						PeriodicExecutionTaskConstants.PERIODICEXECUTIONINFO);
				logDebug("Invoking periodic method [{0}] on component [{1}]...",
						periodicExecutionInfo.getMethod().getName(), periodicExecutionInfo.getComponentName());
				UnifyComponent unifyComponent = getComponent(periodicExecutionInfo.getComponentName());
				periodicExecutionInfo.getMethod().invoke(unifyComponent, tm);
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throwOperationErrorException(e);
		}
	}
}
