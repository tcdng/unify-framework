/*
 * Copyright (c) 2018-2025 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.Parameters;

/**
 * Bad test task.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("test-taskc")
@Parameters({ @Parameter(type = int.class, name = "magnitude", description = "Magnitude",
        editor = "!ui-integer precision:2") })
public class TestTaskC extends AbstractTask {

    @Override
    public void execute(TaskMonitor taskMonitor, TaskInput input) throws UnifyException {
        throw new UnifyOperationException();
    }
}
