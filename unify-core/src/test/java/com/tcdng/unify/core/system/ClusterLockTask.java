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
package com.tcdng.unify.core.system;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.task.AbstractTask;
import com.tcdng.unify.core.task.TaskInput;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskOutput;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Cluster synchronization test task.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("clustershareddata-test")
public class ClusterLockTask extends AbstractTask {

    @Configurable("40")
    private int sharedTestCount;

    @Configurable
    private ClusterService clusterManager;

    private static double sharedValue;

    public void setSharedTestCount(int sharedTestCount) {
        this.sharedTestCount = sharedTestCount;
    }

    public void setClusterManager(ClusterService clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Override
    public void execute(TaskMonitor taskMonitor, TaskInput input, TaskOutput output) throws UnifyException {
        for (int i = 0; i < sharedTestCount; i++) {
            double testValue = Math.random();
            clusterManager.beginSynchronization("sharedSync");
            try {
                sharedValue = testValue;
                ThreadUtils.yield();
                if (sharedValue != testValue) {
                    throwOperationErrorException(new Exception("Shared value corrupted!"));
                }
            } finally {
                clusterManager.endSynchronization("sharedSync");
            }
        }
    }
}
