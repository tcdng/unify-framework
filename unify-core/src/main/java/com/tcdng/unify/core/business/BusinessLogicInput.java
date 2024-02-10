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
package com.tcdng.unify.core.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Business logic input.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class BusinessLogicInput {

    private TaskMonitor taskMonitor;

    private Map<String, Object> parameters;

    public BusinessLogicInput() {
        this.parameters = new HashMap<String, Object>();
    }

    public BusinessLogicInput(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
        this.parameters = new HashMap<String, Object>();
    }

    public TaskMonitor getTaskMonitor() {
        return taskMonitor;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
    }

    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    public Set<String> getParameterNames() {
        return parameters.keySet();
    }

    public <T> T getParameter(Class<T> valueType, String name) throws UnifyException {
        return DataUtils.convert(valueType, parameters.get(name));
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
