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

/**
 * Task execution information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TaskExecutionInfo {

	private String executionId;

	private TaskExecLimit permission;

	public TaskExecutionInfo(String executionId) {
		this(executionId, TaskExecLimit.ALLOW_MULTIPLE);
	}

	public TaskExecutionInfo(String executionId, TaskExecLimit permission) {
		this.executionId = executionId;
		this.permission = permission;
	}

	public String getExecutionId() {
		return executionId;
	}

	public TaskExecLimit getPermission() {
		return permission;
	}
}
