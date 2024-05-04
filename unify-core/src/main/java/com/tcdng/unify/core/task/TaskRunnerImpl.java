/*
 * Copyright 2018-2024 The Code Department.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.RequestContextManager;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyError;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.UserTokenProvider;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * Task runner implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Singleton(false)
@Component("task-runner")
public class TaskRunnerImpl extends AbstractUnifyComponent implements TaskRunner {

	private static final long DEFAULT_SHUTDOWN_WAIT_MILLISECONDS = 60000L;

	private static final int DEFAULT_MAX_MONITOR_MESSAGES = 100;

	@Configurable(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER)
	private RequestContextManager requestContextManager;

	@Configurable
	private UserTokenProvider userTokenProvider;

	private ExecutorService processingExecutor;

	private Set<String> tasks;

	private long shutdownWaitMilliSecs;

	private int maxMonitorMessages;

	@Override
	public boolean start(int maxRunThread) {
		if (processingExecutor == null) {
			synchronized (this) {
				if (processingExecutor == null) {
					processingExecutor = Executors.newFixedThreadPool(maxRunThread <= 0 ? 1 : maxRunThread);
					tasks = new HashSet<String>();
				}
			}
		}

		return false;
	}

	@Override
	public void stop() {
		if (processingExecutor != null) {
			synchronized (this) {
				if (processingExecutor != null) {
					processingExecutor.shutdown();
					try {
						processingExecutor.awaitTermination(shutdownWaitMilliSecs, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						logSevere(e);
					} finally {
						processingExecutor = null;
						tasks = null;
					}
				}
			}
		}
	}

	@Override
	public void restart(int maxRunThread) {
		stop();
		start(maxRunThread);
	}

	@Override
	public boolean isRunning() {
		return processingExecutor != null && tasks != null;
	}

	@Override
	public boolean isScheduled(String taskName) {
		if (tasks != null) {
			synchronized (this) {
				if (tasks != null) {
					return tasks.contains(taskName);
				}
			}
		}

		return false;
	}

	@Override
	public boolean schedule(String taskName, Map<String, Object> parameters, boolean permitMultiple,
			boolean logMessages, long inDelayInMillSec, long periodInMillSec, int numberOfTimes) throws UnifyException {
		if (isRunning()) {
			synchronized (this) {
				if (isRunning()) {
					if (!permitMultiple && isScheduled(taskName)) {
						return false;
					}

					TaskableMethodConfig tmc = null; // TODO Resolve
					TaskRunParams params = new TaskRunParams(taskName, tmc, parameters, logMessages, inDelayInMillSec,
							periodInMillSec, numberOfTimes);
					schedule(params);
					return true;
				} else {
					throwOperationErrorException(new IllegalStateException("Task runner is not started."));
				}
			}
		}

		return false;
	}

	@Override
	protected void onInitialize() throws UnifyException {
		shutdownWaitMilliSecs = getContainerSetting(long.class,
				UnifyCorePropertyConstants.APPLICATION_TASKRUNNER_SHUTDOWN_MILLISECONDS,
				DEFAULT_SHUTDOWN_WAIT_MILLISECONDS);
		maxMonitorMessages = getContainerSetting(int.class,
				UnifyCorePropertyConstants.APPLICATION_MAX_TASKMONITOR_MESSAGES, DEFAULT_MAX_MONITOR_MESSAGES);
	}

	@Override
	protected void onTerminate() throws UnifyException {
		if (processingExecutor != null) {
			processingExecutor.shutdown();
		}
	}

	private void schedule(TaskRunParams params) {
		tasks.add(params.getTaskName());

		if (params.isWithInDelayInMillSec()) {
			new WaitThread(params, params.getInDelayInMillSec()).start();
		} else {
			processingExecutor.execute(new TaskRunnable(params));
		}
	}

	private boolean scheduleRepeatIfNecessary(TaskRunParams params) {
		if (params.incRunCounterAndCheckRepeat()) {
			if (params.isWithPeriodInMillSec()) {
				new WaitThread(params, params.getPeriodInMillSec()).start();
			} else {
				processingExecutor.execute(new TaskRunnable(params));
			}

			return true;
		}

		return false;
	}

	private class WaitThread extends Thread {

		private final TaskRunParams params;

		private final long waitMilliSecs;

		public WaitThread(TaskRunParams params, long waitMilliSecs) {
			this.params = params;
			this.waitMilliSecs = waitMilliSecs;
		}

		@Override
		public void run() {
			ThreadUtils.sleep(waitMilliSecs);
			processingExecutor.execute(new TaskRunnable(params));
		}

	}

	private class TaskRunnable implements Runnable {

		private final TaskRunParams params;

		private final String lockToRelease;

		private final Long tenantId;

		private final String userLoginId;

		public TaskRunnable(TaskRunParams params) {
			this.params = params;
			this.tenantId = (Long) params.getParameter(TaskParameterConstants.TENANT_ID);
			this.userLoginId = (String) params.getParameter(TaskParameterConstants.USER_LOGIN_ID);
			this.lockToRelease = (String) params.getParameter(TaskParameterConstants.LOCK_TO_RELEASE);
		}

		@Override
		public void run() {
			final boolean lock = !StringUtils.isBlank(lockToRelease);
			if (!lock || beginClusterLock(lockToRelease)) {
				try {
					RequestContext requestContext = getRequestContext();
					requestContextManager.loadRequestContext(requestContext);
					if (requestContext.getSessionContext().getUserToken() == null) {
						if (userTokenProvider != null && !StringUtils.isBlank(userLoginId)) {
							UserToken userToken = userTokenProvider.getUserToken(userLoginId, tenantId);
							requestContext.getSessionContext().setUserToken(userToken);
						}
					}

					TaskMonitorImpl taskMonitor = new TaskMonitorImpl(params.getTaskName(), params.isLogMessages());

					TaskInput input = params.isWithTmc()
							? new TaskInput(params.getTaskName(), params.getTmc(), params.getParameters())
							: new TaskInput(params.getTaskName(), params.getParameters());
					Task task = (Task) getComponent(params.getTaskName());
					task.execute(taskMonitor, input);
				} catch (Exception e) {
					logError(e);
				} finally {
					if (lock) {
						releaseClusterLock(lockToRelease);
					}

					try {
						requestContextManager.unloadRequestContext();
					} catch (Exception e) {
						logError(e);
					}
				}
			}

			scheduleRepeatIfNecessary(params);
		}

	}

	private class TaskRunParams {

		private final String taskName;

		private final TaskableMethodConfig tmc;

		private final Map<String, Object> parameters;

		private final boolean logMessages;

		private final long inDelayInMillSec;

		private final long periodInMillSec;

		private final int numberOfTimes;

		private long runCounter;

		public TaskRunParams(String taskName, TaskableMethodConfig tmc, Map<String, Object> parameters,
				boolean logMessages, long inDelayInMillSec, long periodInMillSec, int numberOfTimes) {
			this.taskName = taskName;
			this.tmc = tmc;
			this.parameters = parameters;
			this.logMessages = logMessages;
			this.inDelayInMillSec = inDelayInMillSec;
			this.periodInMillSec = periodInMillSec;
			this.numberOfTimes = numberOfTimes;
			this.runCounter = 0L;
		}

		public String getTaskName() {
			return taskName;
		}

		public TaskableMethodConfig getTmc() {
			return tmc;
		}

		public boolean isWithTmc() {
			return tmc != null;
		}

		public Map<String, Object> getParameters() {
			return parameters;
		}

		public Object getParameter(String name) {
			return parameters != null ? parameters.get(name) : null;
		}

		public boolean isLogMessages() {
			return logMessages;
		}

		public long getInDelayInMillSec() {
			return inDelayInMillSec;
		}

		public boolean isWithInDelayInMillSec() {
			return inDelayInMillSec > 0;
		}

		public long getPeriodInMillSec() {
			return periodInMillSec;
		}

		public boolean isWithPeriodInMillSec() {
			return periodInMillSec > 0;
		}

		public boolean incRunCounterAndCheckRepeat() {
			++runCounter;
			return numberOfTimes <= 0 || runCounter < numberOfTimes;
		}

	}

	private class TaskMonitorImpl implements TaskMonitor {

		private static final int CANCELLED = -1;

		private static final int PENDING = 0;

		private static final int RUNNING = 1;

		private static final int DONE = 2;

		private String taskName;

		private List<String> messages;

		private List<Exception> exceptions;

		private TaskOutput output;

		private boolean logMessages;

		private int running;

		public TaskMonitorImpl(String taskName, boolean logMessages) {
			this.taskName = taskName;
			this.logMessages = logMessages;
			this.exceptions = new ArrayList<Exception>();
			if (this.logMessages) {
				messages = new ArrayList<String>();
			}

			this.output = new TaskOutput();
			this.running = PENDING;
		}

		@Override
		public String getTaskName() {
			return taskName;
		}

		@Override
		public void begin() {
			if (running == PENDING) {
				running = RUNNING;
			}
		}

		@Override
		public void done() {
			if (running == RUNNING) {
				running = DONE;
			}
		}

		@Override
		public void cancel() {
			if (running == PENDING || running == RUNNING) {
				running = CANCELLED;
			}
		}

		@Override
		public boolean isExceptions() {
			return !exceptions.isEmpty();
		}

		@Override
		public boolean isCanceled() {
			return running < 0;
		}

		@Override
		public boolean isRunning() {
			return running == 1;
		}

		@Override
		public boolean isDone() {
			return running == 2;
		}

		@Override
		public TaskOutput getTaskOutput() {
			return output;
		}

		@Override
		public void addException(Exception exception) {
			exceptions.add(exception);
		}

		@Override
		public Exception[] getExceptions() {
			return exceptions.toArray(new Exception[exceptions.size()]);
		}

		@Override
		public void addMessage(String message) {
			if (logMessages) {
				if (messages.size() >= maxMonitorMessages) {
					messages.remove(0);
				}

				messages.add(message);
			}
		}

		@Override
		public void addErrorMessage(UnifyError unifyError) {
			if (logMessages) {
				try {
					addMessage(getSessionMessage(unifyError.getErrorCode(), unifyError.getErrorParams()));
				} catch (UnifyException e) {
					addException(e);
				}
			}
		}

		@Override
		public String[] getMessages() {
			if (logMessages) {
				return messages.toArray(new String[messages.size()]);
			}

			return DataUtils.ZEROLEN_STRING_ARRAY;
		}

		@Override
		public String getLastMessage() {
			if (logMessages) {
				if (!messages.isEmpty()) {
					return messages.get(messages.size() - 1);
				}
			}

			return null;
		}

		@Override
		public void clearMessages() {
			messages.clear();
		}

	}

}
