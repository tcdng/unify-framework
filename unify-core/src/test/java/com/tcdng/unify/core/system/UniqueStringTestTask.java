/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.core.system;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.task.AbstractTask;
import com.tcdng.unify.core.task.TaskInput;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskOutput;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Unique string test task.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("uniquestringtest-task")
public class UniqueStringTestTask extends AbstractTask {

    @Configurable
    private SequenceNumberService sequenceNumberService;

    @Override
    public void execute(TaskMonitor taskMonitor, TaskInput taskInput, TaskOutput taskOutput) throws UnifyException {
        Map<String, Long> resultMap = new HashMap<String, Long>();
        int iterations = taskInput.getParam(int.class, UniqueStringTestTaskConstants.ITERATIONS);
        for (int i = 0; i < iterations; i++) {
            for (String string : taskInput.getParam(String[].class, UniqueStringTestTaskConstants.UNIQUESTRINGLIST)) {
                Long id = sequenceNumberService.getUniqueStringId(string);
                if (resultMap.containsKey(string)) {
                    if (!id.equals(resultMap.get(string))) {
                        throwOperationErrorException(null);
                    }
                } else {
                    resultMap.put(string, id);
                }
                ThreadUtils.yield();
            }
        }
    }
}
