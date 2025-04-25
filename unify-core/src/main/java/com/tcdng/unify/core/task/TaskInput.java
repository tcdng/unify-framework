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

import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Taskable input data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class TaskInput {

    private String taskName;

    private TaskableMethodConfig tmc;

    private Map<String, Object> parameters;

    public TaskInput(String taskName, TaskableMethodConfig tmc, Map<String, Object> parameters) {
        this.taskName = taskName;
        this.tmc = tmc;
        this.parameters = parameters;
    }

    public TaskInput(String taskName, Map<String, Object> parameters) {
        this.taskName = taskName;
        this.parameters = parameters;
    }

    public String getTaskName() {
        return taskName;
    }

    public TaskableMethodConfig getTmc() {
        return tmc;
    }

    public boolean isWithTmc() {
    	return tmc != null;
    }
    
    public Set<String> getParamNames() {
        return parameters.keySet();
    }

    public <T> T getParam(Class<T> valueType, String name) throws UnifyException {
        return DataUtils.convert(valueType, parameters.get(name));
    }
}
