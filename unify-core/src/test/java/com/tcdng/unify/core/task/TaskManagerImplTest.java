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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;

/**
 * Default task manager implementation tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TaskManagerImplTest extends AbstractUnifyComponentTest {

	private TaskManager taskManager;

	private Map<String, Object> parameters;

	@Before
	public void setup() throws Exception {
		taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
		parameters = new HashMap<String, Object>();
	}

	@Test
	public void testIsTaskableMethod() throws Exception {
		assertTrue(taskManager.isTaskableMethod("computegalactic-task"));
		assertFalse(taskManager.isTaskableMethod("alphanumeric-task"));
	}

	@Test
	public void testGetTaskableMethodConfig() throws Exception {
		TaskableMethodConfig tmc = taskManager.getTaskableMethodConfig("computegalactic-task");
		assertNotNull(tmc);
		assertEquals("computegalactic-task", tmc.getName());
		assertEquals("test-taskablecomponent", tmc.getComponentName());
		assertEquals("Galactic Calculator", tmc.getDescription());
		assertNotNull(tmc.getMethod());
		assertEquals(TestTaskableMethodComponent.class, tmc.getMethod().getDeclaringClass());
		assertEquals(2, tmc.getParamCount());
		assertFalse(tmc.isSchedulable());

		List<TaskableMethodConfig.ParamConfig> paramConfigList = tmc.getParamConfigList();
		assertNotNull(paramConfigList);
		assertEquals(2, paramConfigList.size());

		TaskableMethodConfig.ParamConfig pc1 = paramConfigList.get(0);
		assertNotNull(pc1);
		assertEquals("name", pc1.getParamName());
		assertEquals(String.class, pc1.getType());

		TaskableMethodConfig.ParamConfig pc2 = paramConfigList.get(1);
		assertNotNull(pc2);
		assertEquals("factor", pc2.getParamName());
		assertEquals(Double.class, pc2.getType());
	}

	@Test(expected = UnifyException.class)
	public void testGetTaskableMethodConfigUnknown() throws Exception {
		taskManager.getTaskableMethodConfig("alphanumeric-task");
	}

	@Test
	public void testGetTaskableMethodConfigNoParameters() throws Exception {
		TaskableMethodConfig tmc = taskManager.getTaskableMethodConfig("getone-task");
		assertNotNull(tmc);
		assertEquals("getone-task", tmc.getName());
		assertEquals("test-taskablecomponent", tmc.getComponentName());
		assertEquals("Get One Task", tmc.getDescription());
		assertNotNull(tmc.getMethod());
		assertEquals(TestTaskableMethodComponent.class, tmc.getMethod().getDeclaringClass());
		assertEquals(0, tmc.getParamCount());
		assertFalse(tmc.isSchedulable());
	}

	@Test
	public void testGetSchedulableTaskableMethodConfig() throws Exception {
		TaskableMethodConfig tmc = taskManager.getTaskableMethodConfig("generatedailyrpt-task");
		assertNotNull(tmc);
		assertEquals("generatedailyrpt-task", tmc.getName());
		assertEquals("test-taskablecomponent", tmc.getComponentName());
		assertEquals("Generate Daily Report", tmc.getDescription());
		assertNotNull(tmc.getMethod());
		assertEquals(TestTaskableMethodComponent.class, tmc.getMethod().getDeclaringClass());
		assertEquals(2, tmc.getParamCount());
		assertTrue(tmc.isSchedulable());

		List<TaskableMethodConfig.ParamConfig> paramConfigList = tmc.getParamConfigList();
		assertNotNull(paramConfigList);
		assertEquals(2, paramConfigList.size());

		TaskableMethodConfig.ParamConfig pc1 = paramConfigList.get(0);
		assertNotNull(pc1);
		assertEquals("reportBase", pc1.getParamName());
		assertEquals("Report Base", pc1.getParamDesc());
		assertEquals("!ui-text", pc1.getEditor());
		assertEquals(String.class, pc1.getType());

		TaskableMethodConfig.ParamConfig pc2 = paramConfigList.get(1);
		assertNotNull(pc2);
		assertEquals("workingDt", pc2.getParamName());
		assertEquals("Working Date", pc2.getParamDesc());
		assertEquals("!ui-date", pc2.getEditor());
		assertEquals(Date.class, pc2.getType());
	}

	@Test(timeout = 4000)
	public void testExecuteTask() throws Exception {
		parameters.put("paramA", "Hello World!");
		TaskMonitor taskMonitor = taskManager.executeTask("test-taska", parameters, false, null);
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Hello World!", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testExecuteTasks() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.executeTasks(Arrays.asList("test-taska", "test-taskb"), parameters, false,
				true, null);
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(1));
		assertEquals("Tiger", taskMonitor.getTaskOutput(1).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testExecuteIndependentTasksWithBadTask() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.executeTasks(Arrays.asList("test-taska", "test-taskc", "test-taskb"),
				parameters, false, false, null);
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.FAILED, taskMonitor.getTaskStatus(1));
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(2));
		assertEquals("Tiger", taskMonitor.getTaskOutput(2).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testExecuteDependentTasksWithBadTask() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.executeTasks(Arrays.asList("test-taska", "test-taskc", "test-taskb"),
				parameters, false, true, null);
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.FAILED, taskMonitor.getTaskStatus(1));
		assertEquals(TaskStatus.ABORTED, taskMonitor.getTaskStatus(2));
		assertNull(taskMonitor.getTaskOutput(2).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testStartTask() throws Exception {
		parameters.put("paramA", "Hello World!");
		TaskMonitor taskMonitor = taskManager.startTask("test-taska", parameters, false, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Hello World!", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testStartTasks() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.startTasks(Arrays.asList("test-taska", "test-taskb"), parameters, false,
				true, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(1));
		assertEquals("Tiger", taskMonitor.getTaskOutput(1).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testStartIndependentTasksWithBadTask() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.startTasks(Arrays.asList("test-taska", "test-taskc", "test-taskb"),
				parameters, false, false, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.FAILED, taskMonitor.getTaskStatus(1));
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(2));
		assertEquals("Tiger", taskMonitor.getTaskOutput(2).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testStartDependentTasksWithBadTask() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.startTasks(Arrays.asList("test-taska", "test-taskc", "test-taskb"),
				parameters, false, true, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.FAILED, taskMonitor.getTaskStatus(1));
		assertEquals(TaskStatus.ABORTED, taskMonitor.getTaskStatus(2));
		assertNull(taskMonitor.getTaskOutput(2).getResult(String.class, "message"));
	}

	// ScheduleTaskToRunAfter

	@Test(timeout = 4000)
	public void testScheduleTaskToRunAfter() throws Exception {
		parameters.put("paramA", "Hello World!");
		TaskMonitor taskMonitor = taskManager.scheduleTaskToRunAfter("test-taska", parameters, false, 100, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Hello World!", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testScheduleTasksToRunAfter() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.scheduleTasksToRunAfter(Arrays.asList("test-taska", "test-taskb"),
				parameters, false, true, 100, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(1));
		assertEquals("Tiger", taskMonitor.getTaskOutput(1).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testScheduleIndependentTasksToRunAfterWithBadTask() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.scheduleTasksToRunAfter(
				Arrays.asList("test-taska", "test-taskc", "test-taskb"), parameters, false, false, 100, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.FAILED, taskMonitor.getTaskStatus(1));
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(2));
		assertEquals("Tiger", taskMonitor.getTaskOutput(2).getResult(String.class, "message"));
	}

	@Test(timeout = 4000)
	public void testScheduleDependentTasksToRunAfterWithBadTask() throws Exception {
		parameters.put("paramA", "Lion");
		parameters.put("paramB", "Tiger");
		TaskMonitor taskMonitor = taskManager.scheduleTasksToRunAfter(
				Arrays.asList("test-taska", "test-taskc", "test-taskb"), parameters, false, true, 100, null);
		while (!taskMonitor.isDone()) {
			Thread.yield();
		}
		assertEquals(TaskStatus.COMPLETED, taskMonitor.getTaskStatus(0));
		assertEquals("Lion", taskMonitor.getTaskOutput(0).getResult(String.class, "message"));
		assertEquals(TaskStatus.FAILED, taskMonitor.getTaskStatus(1));
		assertEquals(TaskStatus.ABORTED, taskMonitor.getTaskStatus(2));
		assertNull(taskMonitor.getTaskOutput(2).getResult(String.class, "message"));
	}

	@Override
	protected void onSetup() throws Exception {

	}

	@Override
	protected void onTearDown() throws Exception {

	}
}
