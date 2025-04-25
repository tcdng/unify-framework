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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.list.AbstractZeroParamsListCommand;
import com.tcdng.unify.core.list.ZeroParams;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Task list command.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("tasklist")
public class TaskListCommand extends AbstractZeroParamsListCommand {

    @Configurable
    private TaskManager taskManager;

    private FactoryMap<Locale, List<Listable>> taskListMap;

    public TaskListCommand() {
        taskListMap = new FactoryMap<Locale, List<Listable>>() {
            @Override
            protected List<Listable> create(Locale key, Object... params) throws Exception {
                List<Listable> list = new ArrayList<Listable>();
                for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(Task.class)) {
                    list.add(new ListData(unifyComponentConfig.getName(),
                            resolveSessionMessage(unifyComponentConfig.getDescription())));
                }

                for (TaskableMethodConfig tmc : taskManager.getAllTaskableMethodConfigs()) {
                    list.add(new ListData(tmc.getName(), resolveSessionMessage(tmc.getDescription())));
                }

                DataUtils.sortAscending(list, Listable.class, "listDescription");
                return list;
            }
        };
    }

    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
        return taskListMap.get(locale);
    }
}
