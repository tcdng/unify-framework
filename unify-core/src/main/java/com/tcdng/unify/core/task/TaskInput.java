/*
 * Copyright 2018-2022 The Code Department.
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

import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Taskable input data object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TaskInput {

    private String origTaskName;

    private TaskableMethodConfig tmc;

    private Map<String, Object> parameters;

    private TaskOutput prevTaskOutput;

    public TaskInput(String origTaskName, TaskableMethodConfig tmc, Map<String, Object> parameters,
            TaskOutput prevTaskOutput) {
        this.origTaskName = origTaskName;
        this.tmc = tmc;
        this.parameters = parameters;
        this.prevTaskOutput = prevTaskOutput;
    }

    public String getOrigTaskName() {
        return origTaskName;
    }

    public TaskOutput getPrevTaskOutput() {
        return prevTaskOutput;
    }

    public TaskableMethodConfig getTmc() {
        return tmc;
    }

    public Set<String> getParamNames() {
        return parameters.keySet();
    }

    public <T> T getParam(Class<T> valueType, String name) throws UnifyException {
        return DataUtils.convert(valueType, parameters.get(name));
    }
}
