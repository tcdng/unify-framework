/*
 * Copyright 2014 The Code Department
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
 * Cluster manager test case.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ClusterManagerBusinessModuleTest extends AbstractUnifyComponentTest {

	public ClusterManagerBusinessModuleTest() {
		super(true); // Cluster mode
	}

	@Test
	public void testSingleMemberSync() throws Exception {
		ClusterManagerBusinessModule cmModule = (ClusterManagerBusinessModule) getComponent(
				ApplicationComponents.APPLICATION_CLUSTERMANAGER);
		String lockOwnerId = cmModule.getLockOwnerId(false);
		cmModule.beginSynchronization("computeSalaryLock");
		List<ClusterLock> clusterSyncList = cmModule
				.findClusterLocks(new ClusterLockQuery().lockName("computeSalaryLock"));
		assertEquals(1, clusterSyncList.size());
		ClusterLock clusterLock = clusterSyncList.get(0);
		assertEquals("computeSalaryLock", clusterLock.getLockName());
		assertEquals(lockOwnerId, clusterLock.getCurrentOwner());
		assertEquals(Integer.valueOf(1), clusterLock.getLockCount());

		cmModule.endSynchronization("computeSalaryLock");
		clusterSyncList = cmModule.findClusterLocks(new ClusterLockQuery().lockName("computeSalaryLock"));
		assertEquals(1, clusterSyncList.size());
		clusterLock = clusterSyncList.get(0);
		assertNull(clusterLock.getCurrentOwner());
		assertEquals(Integer.valueOf(0), clusterLock.getLockCount());
	}

	@Test
	public void testSingleMemberWithRecursiveSync() throws Exception {
		ClusterManagerBusinessModule cmModule = (ClusterManagerBusinessModule) getComponent(
				ApplicationComponents.APPLICATION_CLUSTERMANAGER);
		String lockOwnerId = cmModule.getLockOwnerId(false);
		cmModule.beginSynchronization("generateResultLock");
		cmModule.beginSynchronization("generateResultLock");
		cmModule.beginSynchronization("generateResultLock");
		List<ClusterLock> clusterSyncList = cmModule
				.findClusterLocks(new ClusterLockQuery().lockName("generateResultLock"));
		assertEquals(1, clusterSyncList.size());
		ClusterLock clusterLock = clusterSyncList.get(0);
		assertEquals("generateResultLock", clusterLock.getLockName());
		assertEquals(lockOwnerId, clusterLock.getCurrentOwner());
		assertEquals(Integer.valueOf(3), clusterLock.getLockCount());

		cmModule.endSynchronization("generateResultLock");
		cmModule.endSynchronization("generateResultLock");
		cmModule.endSynchronization("generateResultLock");
		clusterSyncList = cmModule.findClusterLocks(new ClusterLockQuery().lockName("generateResultLock"));
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
