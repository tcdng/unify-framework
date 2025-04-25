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
package com.tcdng.unify.core.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.util.DataUtils;

/**
 * Task setup.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class TaskSetup {

	private TaskExecType type;

	private String taskName;

	private Map<String, Object> parameters;

	private String eventCode;

	private List<String> eventDetails;

	private String logger;

	private long delayInMillSec;

	private long periodInMillSec;

	private int numberOfTimes;

	private boolean messages;

	private TaskSetup(TaskExecType type, String taskName, Map<String, Object> parameters, String eventCode,
			List<String> eventDetails, String logger, long delayInMillSec, long periodInMillSec, int numberOfTimes,
			boolean messages) {
		this.type = type;
		this.taskName = taskName;
		this.parameters = parameters;
		this.eventCode = eventCode;
		this.eventDetails = eventDetails;
		this.logger = logger;
		this.delayInMillSec = delayInMillSec;
		this.periodInMillSec = periodInMillSec;
		this.numberOfTimes = numberOfTimes;
		this.messages = messages;
	}

	public TaskExecType getType() {
		return type;
	}

	public String getTaskName() {
		return taskName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getEventCode() {
		return eventCode;
	}

	public List<String> getEventDetails() {
		return eventDetails;
	}

	public String getLogger() {
		return logger;
	}

	public long getDelayInMillSec() {
		return delayInMillSec;
	}

	public long getPeriodInMillSec() {
		return periodInMillSec;
	}

	public int getNumberOfTimes() {
		return numberOfTimes;
	}

	public boolean isMessages() {
		return messages;
	}

	public static Builder newBuilder(String taskName) {
		return new Builder(taskName);
	}

	public static Builder newBuilder(TaskExecType type, String taskName) {
		return new Builder(type, taskName);
	}

	public static class Builder {

		private TaskExecType type;

		private String taskName;

		private Map<String, Object> parameters;

		private String eventCode;

		private String[] eventDetails;

		private String logger;

		private long delayInMillSec;

		private long periodInMillSec;

		private int numberOfTimes;

		private boolean messages;

		private Builder(String taskName) {
			this(TaskExecType.RUN_IMMEDIATE, taskName);
		}

		private Builder(TaskExecType type, String taskName) {
			this.type = type;
			this.taskName = taskName;
		}

		public Builder setParams(Map<String, Object> params) {
			if (parameters == null) {
				parameters = new HashMap<String, Object>();
			}

			parameters.putAll(params);
			return this;
		}

		public Builder setParam(String name, Object value) {
			if (parameters == null) {
				parameters = new HashMap<String, Object>();
			}

			parameters.put(name, value);
			return this;
		}

		public Builder delayInMillSec(long delayInMillSec) {
			this.delayInMillSec = delayInMillSec;
			return this;
		}

		public Builder delayUntil(Date time) {
			this.delayInMillSec = time.getTime() - new Date().getTime();
			return this;
		}

		public Builder periodInMillSec(long periodInMillSec) {
			this.periodInMillSec = periodInMillSec;
			return this;
		}

		public Builder numberOfTimes(int numberOfTimes) {
			this.numberOfTimes = numberOfTimes;
			return this;
		}

		public Builder useStatusLogger(String logger) {
			this.logger = logger;
			return this;
		}

		public Builder logEvent(String eventCode, String... eventDetails) {
			this.eventCode = eventCode;
			this.eventDetails = eventDetails;
			return this;
		}

		public Builder logMessages() {
			this.messages = true;
			return this;
		}

		public TaskSetup build() {
			return new TaskSetup(type, taskName, DataUtils.unmodifiableMap(parameters), eventCode,
					DataUtils.unmodifiableList(eventDetails), logger, delayInMillSec, periodInMillSec, numberOfTimes,
					messages);
		}

	}
}
