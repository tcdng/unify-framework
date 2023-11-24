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
package com.tcdng.unify.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.system.entities.ClusterLock;
import com.tcdng.unify.core.system.entities.ClusterLockQuery;
import com.tcdng.unify.core.task.TaskManager;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Cluster service test case.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ClusterServiceTest extends AbstractUnifyComponentTest {

    public ClusterServiceTest() {
        super(true); // Cluster mode
    }

    @Test
    public void testSingleMemberSync() throws Exception {
        ClusterService clusterService = (ClusterService) getComponent(ApplicationComponents.APPLICATION_CLUSTERSERVICE);
        String lockOwnerId = clusterService.getLockOwnerId(false);
        clusterService.beginSynchronization("computeSalaryLock");
        List<ClusterLock> clusterSyncList =
                clusterService.findClusterLocks(new ClusterLockQuery().lockName("computeSalaryLock"));
        assertEquals(1, clusterSyncList.size());
        ClusterLock clusterLock = clusterSyncList.get(0);
        assertEquals("computeSalaryLock", clusterLock.getLockName());
        assertEquals(lockOwnerId, clusterLock.getCurrentOwner());
        assertEquals(Integer.valueOf(1), clusterLock.getLockCount());

        clusterService.endSynchronization("computeSalaryLock");
        clusterSyncList = clusterService.findClusterLocks(new ClusterLockQuery().lockName("computeSalaryLock"));
        assertEquals(1, clusterSyncList.size());
        clusterLock = clusterSyncList.get(0);
        assertNull(clusterLock.getCurrentOwner());
        assertEquals(Integer.valueOf(0), clusterLock.getLockCount());
    }

    @Test
    public void testSingleMemberWithRecursiveSync() throws Exception {
        ClusterService clusterService = (ClusterService) getComponent(ApplicationComponents.APPLICATION_CLUSTERSERVICE);
        String lockOwnerId = clusterService.getLockOwnerId(false);
        clusterService.beginSynchronization("generateResultLock");
        clusterService.beginSynchronization("generateResultLock");
        clusterService.beginSynchronization("generateResultLock");
        List<ClusterLock> clusterSyncList =
                clusterService.findClusterLocks(new ClusterLockQuery().lockName("generateResultLock"));
        assertEquals(1, clusterSyncList.size());
        ClusterLock clusterLock = clusterSyncList.get(0);
        assertEquals("generateResultLock", clusterLock.getLockName());
        assertEquals(lockOwnerId, clusterLock.getCurrentOwner());
        assertEquals(Integer.valueOf(3), clusterLock.getLockCount());

        clusterService.endSynchronization("generateResultLock");
        clusterService.endSynchronization("generateResultLock");
        clusterService.endSynchronization("generateResultLock");
        clusterSyncList = clusterService.findClusterLocks(new ClusterLockQuery().lockName("generateResultLock"));
        assertEquals(1, clusterSyncList.size());
        clusterLock = clusterSyncList.get(0);
        assertNull(clusterLock.getCurrentOwner());
        assertEquals(Integer.valueOf(0), clusterLock.getLockCount());
    }

    @Test
    public void testClusterSynchronizationWithSharedData() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
        TaskMonitor[] taskMonitor = new TaskMonitor[4];
        for (int i = 0; i < taskMonitor.length; i++) {
            taskMonitor[i] = taskManager.startTask("clustershareddata-test", parameters, true, null);
        }

        boolean pending = true;
        do {
            ThreadUtils.sleep(30);
            pending = false;
            for (int i = 0; i < taskMonitor.length; i++) {
                pending |= taskMonitor[i].isPending();
            }
        } while (pending);

        for (int i = 0; i < taskMonitor.length; i++) {
            if (taskMonitor[i].isExceptions()) {
                throw taskMonitor[i].getExceptions()[0];
            }
        }
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
