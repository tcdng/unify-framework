/*
 * Copyright 2018-2025 The Code Department.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.data.LockInfo;
import com.tcdng.unify.core.task.TaskManager;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Lock manager test case.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class LockManagerTest extends AbstractUnifyComponentTest {

	public LockManagerTest() {
		super(true); // Cluster mode
	}

	@Test(timeout = 5000)
	public void testIsLocked() throws Exception {
		LockManager lockManager = (LockManager) getComponent(ApplicationComponents.APPLICATION_LOCKMANAGER);
		assertFalse(lockManager.isLocked("freeLock"));

		lockManager.grabLock("freeLock");
		assertTrue(lockManager.isLocked("freeLock"));

		lockManager.releaseLock("freeLock");
		assertFalse(lockManager.isLocked("freeLock"));
	}

	@Test(timeout = 5000)
	public void testSingleMemberSync() throws Exception {
		LockManager lockManager = (LockManager) getComponent(ApplicationComponents.APPLICATION_LOCKMANAGER);
		String threadId = String.valueOf(ThreadUtils.currentThreadId());
		String lockOwnerId = lockManager.getNodeId();
		lockManager.grabLock("computeSalaryLock");
		LockInfo lockInfo = lockManager.getLockInfo("computeSalaryLock");
		assertNotNull(lockInfo);
		assertEquals("computeSalaryLock", lockInfo.getLockName());
		assertEquals(lockOwnerId, lockInfo.getCurrentOwner());
		assertEquals(threadId, lockInfo.getThreadId());
		assertEquals(Integer.valueOf(1), lockInfo.getLockCount());
		assertNotNull(lockInfo.getExpiryTime());

		lockManager.releaseLock("computeSalaryLock");
		lockInfo = lockManager.getLockInfo("computeSalaryLock");
		assertNull(lockInfo);
	}

	@Test(timeout = 5000)
	public void testSingleMemberWithRecursiveSync() throws Exception {
		LockManager lockManager = (LockManager) getComponent(ApplicationComponents.APPLICATION_LOCKMANAGER);
		String threadId = String.valueOf(ThreadUtils.currentThreadId());
		String lockOwnerId = lockManager.getNodeId();
		lockManager.grabLock("generateResultLock");
		lockManager.grabLock("generateResultLock");
		lockManager.grabLock("generateResultLock");
		LockInfo lockInfo = lockManager.getLockInfo("generateResultLock");
		assertNotNull(lockInfo);
		assertEquals("generateResultLock", lockInfo.getLockName());
		assertEquals(lockOwnerId, lockInfo.getCurrentOwner());
		assertEquals(threadId, lockInfo.getThreadId());
		assertEquals(Integer.valueOf(3), lockInfo.getLockCount());
		assertNotNull(lockInfo.getExpiryTime());

		lockManager.releaseLock("generateResultLock");
		lockInfo = lockManager.getLockInfo("generateResultLock");
		assertNotNull(lockInfo);
		assertEquals("generateResultLock", lockInfo.getLockName());
		assertEquals(lockOwnerId, lockInfo.getCurrentOwner());
		assertEquals(threadId, lockInfo.getThreadId());
		assertEquals(Integer.valueOf(2), lockInfo.getLockCount());
		assertNotNull(lockInfo.getExpiryTime());

		lockManager.releaseLock("generateResultLock");
		lockInfo = lockManager.getLockInfo("generateResultLock");
		assertNotNull(lockInfo);
		assertEquals("generateResultLock", lockInfo.getLockName());
		assertEquals(lockOwnerId, lockInfo.getCurrentOwner());
		assertEquals(threadId, lockInfo.getThreadId());
		assertEquals(Integer.valueOf(1), lockInfo.getLockCount());
		assertNotNull(lockInfo.getExpiryTime());

		lockManager.releaseLock("generateResultLock");
		lockInfo = lockManager.getLockInfo("generateResultLock");
		assertNull(lockInfo);
	}

	@Test
	public void testClusterSynchronizationWithSharedData() throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
		TaskMonitor[] taskMonitor = new TaskMonitor[4];
		for (int i = 0; i < taskMonitor.length; i++) {
			taskMonitor[i] = taskManager.startTask("clustershareddata-test", parameters, true);
		}

		boolean done = false;
		do {
			ThreadUtils.sleep(30);
			done = true;
			for (int i = 0; i < taskMonitor.length; i++) {
				done &= taskMonitor[i].isDone();
			}
		} while (!done);

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
