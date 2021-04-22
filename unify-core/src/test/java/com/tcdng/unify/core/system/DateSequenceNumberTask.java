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
package com.tcdng.unify.core.system;

import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.task.AbstractTask;
import com.tcdng.unify.core.task.TaskInput;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskOutput;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Date sequence number task.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("datesequencenumber-task")
public class DateSequenceNumberTask extends AbstractTask {

    @Configurable
    private SequenceNumberService sequenceNumberService;

    public void setSequenceNumberService(SequenceNumberService sequenceNumberService) {
        this.sequenceNumberService = sequenceNumberService;
    }

    @Override
    public void execute(TaskMonitor taskMonitor, TaskInput input, TaskOutput output) throws UnifyException {
        int iterations = input.getParam(int.class, DateSequenceNumberTaskConstants.ITERATIONS);
        Date testDate = input.getParam(Date.class, DateSequenceNumberTaskConstants.DATE);
        String testSequenceName = input.getParam(String.class, DateSequenceNumberTaskConstants.SEQUENCENAME);

        Long prevSequenceNo = null;
        for (int i = 0; i < iterations; i++) {
            Long sequenceNo = sequenceNumberService.getNextSequenceNumber(testSequenceName, testDate);
            if (prevSequenceNo != null && (prevSequenceNo + 1) != sequenceNo) {
                throwOperationErrorException(null);
            }
            prevSequenceNo = sequenceNo;
            ThreadUtils.yield();
        }
    }
}
