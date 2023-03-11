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

import java.lang.reflect.Method;
import java.util.List;

import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.data.ParamConfig;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Taskable method configuration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TaskableMethodConfig implements Listable {

    private String name;

    private String description;

    private String componentName;

    private Method method;

    private List<ParamConfig> paramConfigList;

    private TaskExecLimit taskExecLimit;

    private String idGenerator;

    private boolean schedulable;

    public TaskableMethodConfig(String name, String description, String componentName, Method method,
            List<ParamConfig> paramConfigList, TaskExecLimit taskExecLimit, String idGenerator, boolean schedulable) {
        this.name = name;
        this.description = description;
        this.componentName = componentName;
        this.method = method;
        this.paramConfigList = DataUtils.unmodifiableList(paramConfigList);
        this.taskExecLimit = taskExecLimit;
        this.idGenerator = idGenerator;
        this.schedulable = schedulable;
    }

    @Override
    public String getListKey() {
        return name;
    }

    @Override
    public String getListDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getComponentName() {
        return componentName;
    }

    public Method getMethod() {
        return method;
    }

    public List<ParamConfig> getParamConfigList() {
        return paramConfigList;
    }

    public TaskExecLimit getTaskExecLimit() {
        return taskExecLimit;
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public int getParamCount() {
        return paramConfigList.size();
    }

    public boolean isSchedulable() {
        return schedulable;
    }
}
