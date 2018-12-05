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
package com.tcdng.unify.core.task;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

public class TaskLauncherTest extends AbstractUnifyComponentTest {

	@Test
	public void testLaunchTaskComponent() throws Exception {
		TaskLauncher launcher = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);
		TaskSetup setup = TaskSetup.newBuilder().addTask("test-taska").setParam("paramA", "Elmer Fudd").build();
		TaskMonitor monitor = launcher.launchTask(setup);
		while (!monitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, monitor.getTaskStatus(0));
		assertEquals("Elmer Fudd", monitor.getTaskOutput(0).getResult(String.class, "message"));
	}

	@Test
	public void testLaunchTaskableMethod() throws Exception {
		TaskLauncher launcher = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);
		TaskSetup setup = TaskSetup.newBuilder().addTask("computegalactic-task").setParam("name", "Earth")
				.setParam("factor", 0.25).build();
		TaskMonitor monitor = launcher.launchTask(setup);
		while (!monitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, monitor.getTaskStatus(0));
		assertEquals(Integer.valueOf(250000),
				monitor.getTaskOutput(0).getResult(Integer.class, TaskableMethodConstants.TASK_RESULT));
	}

	@Test
	public void testLaunchTaskableMethodNullParameter() throws Exception {
		TaskLauncher launcher = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);
		TaskSetup setup = TaskSetup.newBuilder().addTask("computegalactic-task").setParam("factor", 0.25).build();
		TaskMonitor monitor = launcher.launchTask(setup);
		while (!monitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, monitor.getTaskStatus(0));
		assertEquals(Integer.valueOf(0),
				monitor.getTaskOutput(0).getResult(Integer.class, TaskableMethodConstants.TASK_RESULT));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}

}
