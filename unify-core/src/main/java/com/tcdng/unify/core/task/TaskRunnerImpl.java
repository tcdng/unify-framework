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
import com.tcdng.unify.core.annotation.PeriodicType;
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

	private final Set<String> tasks;

	private long shutdownWaitMilliSecs;

	private int maxMonitorMessages;

	private boolean started;

	private boolean permitMultiple;
	
	public TaskRunnerImpl() {
		this.tasks = new HashSet<String>();
	}
	
	@Override
	public boolean start(int maxRunThread, boolean permitMultiple) {
		logDebug("Starting task runner [{0}] with maximum run threads [{1}]...", this, maxRunThread);
		if (!this.started) {
			synchronized (this) {
				if (!this.started) {
					this.processingExecutor = Executors.newFixedThreadPool(maxRunThread <= 0 ? 1 : maxRunThread);
					this.permitMultiple = permitMultiple;
					this.started = true;
					logDebug("Task runner [{0}] is successfully started.", this);
					return true;
				}
			}
		}

		logDebug("Task runner [{0}] is already started.", this);
		return false;
	}

	@Override
	public void stop() {
		logDebug("Stopping task runner [{0}] ...", this);
		if (started) {
			synchronized (this) {
				if (started) {
					processingExecutor.shutdown();
					started = false;
					tasks.clear();
					
					try {
						processingExecutor.awaitTermination(shutdownWaitMilliSecs, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						logSevere(e);
					}

					logDebug("Task runner [{0}] is successfully stopped.", this);
					return;
				}
			}
		}

		logDebug("Task runner [{0}] is already stopped.", this);
	}

	@Override
	public boolean isRunning() {
		return started;
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
	public TaskMonitor schedule(PeriodicType periodicType, String taskName, Map<String, Object> parameters,
			boolean logMessages, long inDelayInMillSec) throws UnifyException {
		return schedule(null, taskName, parameters, logMessages, inDelayInMillSec, periodicType.getPeriodInMillSec(),
				0);
	}

	@Override
	public TaskMonitor schedule(String taskName, Map<String, Object> parameters, boolean logMessages,
			long inDelayInMillSec, long periodInMillSec, int numberOfTimes) throws UnifyException {
		return schedule(null, taskName, parameters, logMessages, inDelayInMillSec, periodInMillSec, numberOfTimes);
	}

	@Override
	public TaskMonitor schedule(TaskableMethodConfig tmc, String taskName, Map<String, Object> parameters,
			boolean logMessages, long inDelayInMillSec, long periodInMillSec, int numberOfTimes) throws UnifyException {
		boolean _permitMultiple = permitMultiple;
		String actualTaskName = taskName;
		if (tmc != null) {
			_permitMultiple &= tmc.isPermitMultiple();
			actualTaskName = TaskableMethodConstants.TASKABLE_METHOD_TASK;
		}

		return internalSchedule(tmc, actualTaskName, taskName, parameters, _permitMultiple, logMessages,
				inDelayInMillSec, periodInMillSec, numberOfTimes);
	}

	private TaskMonitor internalSchedule(TaskableMethodConfig tmc, String actualTaskName, String taskName,
			Map<String, Object> parameters, boolean _permitMultiple, boolean logMessages, long inDelayInMillSec,
			long periodInMillSec, int numberOfTimes) throws UnifyException {
		if (numberOfTimes > 0) {
			logDebug(
					"Scheduling task [{0} - {1}] for execution [{2}] time(s) with initial delay [{3}ms] and repeat period [{4}ms]...",
					actualTaskName, taskName, numberOfTimes, inDelayInMillSec, periodInMillSec);
		} else {
			logDebug(
					"Scheduling task [{0} - {1}] for continuous execution with initial delay [{2}ms] and repeat period [{3}ms]...",
					actualTaskName, taskName, inDelayInMillSec, periodInMillSec);
		}

		TaskMonitorImpl tm = new TaskMonitorImpl(actualTaskName, logMessages, numberOfTimes);
		if (isRunning()) {
			synchronized (this) {
				if (isRunning()) {
					if (_permitMultiple || !isScheduled(taskName)) {
						TaskRunParams params = new TaskRunParams(taskName, tm, tmc, parameters, inDelayInMillSec, periodInMillSec,
								numberOfTimes, _permitMultiple);
						schedule(params);
					} else {
						tm.notPermitted();
					}
				} else {
					throwOperationErrorException(new IllegalStateException("Task runner is not started."));
				}
			}
		} else {
			throwOperationErrorException(new IllegalStateException("Task runner is not started."));
		}

		logDebug("Scheduling of task [{0}] completed with permitted [{1}].", actualTaskName, !tm.isNotPermitted());
		return tm;
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
		stop();
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
		if (params.incRunCounterAndCheckRepeat() && !params.getTm().isCancelled()) {
			if (params.isWithPeriodInMillSec()) {
				new WaitThread(params, params.getPeriodInMillSec()).start();
			} else {
				processingExecutor.execute(new TaskRunnable(params));
			}

			// Repeat
			return true;
		}

		// Done
		if (!params.isPermitMultiple()) {
			tasks.remove(params.getTaskName());
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

		private final String lockToTry;

		private final Long tenantId;

		private final String userLoginId;

		public TaskRunnable(TaskRunParams params) {
			this.params = params;
			this.tenantId = (Long) params.getParameter(TaskParameterConstants.TENANT_ID);
			this.userLoginId = (String) params.getParameter(TaskParameterConstants.USER_LOGIN_ID);
			this.lockToTry = (String) params.getParameter(TaskParameterConstants.LOCK_TO_TRY);
		}

		@Override
		public void run() {
			final boolean lock = !StringUtils.isBlank(lockToTry);
			final TaskMonitorImpl tm = params.getTm();
			tm.begin();
			try {
				if (!lock || tryGrabLock(lockToTry)) {
					try {
						RequestContext requestContext = getRequestContext();
						requestContextManager.loadRequestContext(requestContext);
						if (requestContext.getSessionContext().getUserToken() == null) {
							if (userTokenProvider != null && !StringUtils.isBlank(userLoginId)) {
								UserToken userToken = userTokenProvider.getUserToken(userLoginId, tenantId);
								requestContext.getSessionContext().setUserToken(userToken);
							}
						}

						TaskInput input = params.isWithTaskableMethodConfig()
								? new TaskInput(params.getActualTaskName(), params.getTaskableMethodConfig(),
										params.getParameters())
								: new TaskInput(params.getActualTaskName(), params.getParameters());
						Task task = getComponent(Task.class, params.getActualTaskName());
						task.execute(tm, input);
					} catch (Exception e) {
						tm.addException(e);
						logError(e);
					} finally {
						if (lock) {
							releaseLock(lockToTry);
						}

						try {
							requestContextManager.unloadRequestContext();
						} catch (Exception e) {
							logError(e);
						}
					}
				}
			} catch (Exception e) {
				tm.addException(e);
				logError(e);
			} finally {
				tm.done();
			}

			scheduleRepeatIfNecessary(params);
		}

	}

	private class TaskRunParams {

		private final String taskName;
		
		private final TaskMonitorImpl tm;

		private final TaskableMethodConfig tmc;

		private final Map<String, Object> parameters;

		private final long inDelayInMillSec;

		private final long periodInMillSec;

		private final int numberOfTimes;

		private final boolean permitMultiple;
		
		private long runCounter;

		public TaskRunParams(String taskName, TaskMonitorImpl tm, TaskableMethodConfig tmc, Map<String, Object> parameters,
				long inDelayInMillSec, long periodInMillSec, int numberOfTimes, boolean permitMultiple) {
			this.taskName = taskName;
			this.tm = tm;
			this.tmc = tmc;
			this.parameters = parameters;
			this.inDelayInMillSec = inDelayInMillSec;
			this.periodInMillSec = periodInMillSec;
			this.numberOfTimes = numberOfTimes;
			this.permitMultiple = permitMultiple;
			this.runCounter = 0L;
		}

		public TaskMonitorImpl getTm() {
			return tm;
		}

		public String getTaskName() {
			return taskName;
		}

		public String getActualTaskName() {
			return tm.getTaskName();
		}

		public TaskableMethodConfig getTaskableMethodConfig() {
			return tmc;
		}

		public boolean isWithTaskableMethodConfig() {
			return tmc != null;
		}

		public Map<String, Object> getParameters() {
			return parameters;
		}

		public Object getParameter(String name) {
			return parameters != null ? parameters.get(name) : null;
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

		public boolean isPermitMultiple() {
			return permitMultiple;
		}

	}

	private class TaskMonitorImpl implements TaskMonitor {

		private static final int CANCELLED = -1;

		private static final int PENDING = 0;

		private static final int RUNNING = 1;

		private static final int DONE = 2;

		private static final int NOT_PERMITTED = 3;

		private String taskName;

		private List<String> messages;

		private List<Exception> exceptions;

		private final TaskOutput output;

		private final boolean logMessages;

		private final int expectedRuns;

		private int actualRuns;

		private int running;

		public TaskMonitorImpl(String taskName, boolean logMessages, int expectedRuns) {
			this.output = new TaskOutput();
			this.taskName = taskName;
			this.logMessages = logMessages;
			this.expectedRuns = expectedRuns;
			this.exceptions = new ArrayList<Exception>();
			if (this.logMessages) {
				messages = new ArrayList<String>();
			}

			this.running = PENDING;
			this.actualRuns = 0;
		}

		@Override
		public String getTaskName() {
			return taskName;
		}

		public void begin() {
			if (running == PENDING) {
				running = RUNNING;
			}
		}

		public void done() {
			if (running == RUNNING) {
				actualRuns++;
				if (expectedRuns > 0 && actualRuns >= expectedRuns) {
					running = DONE;
				}
			}
		}

		public void notPermitted() {
			if (running == PENDING) {
				running = NOT_PERMITTED;
			}
		}

		@Override
		public void cancel() {
			if (running == PENDING || running == RUNNING) {
				running = CANCELLED;
			}
		}

		@Override
		public int expectedRuns() {
			return expectedRuns;
		}

		@Override
		public int actualRuns() {
			return actualRuns;
		}

		@Override
		public TaskStatus getTaskStatus() {
			if (isDone()) {
				return isExceptions() ? TaskStatus.FAILED:  TaskStatus.SUCCESSFUL;
			}

			if (isRunning()) {
				return TaskStatus.RUNNING;
			}	
			
			if (isCancelled()) {
				return TaskStatus.CANCELED;
			}
			
			if (isNotPermitted()) {
				return TaskStatus.ABORTED;
			}

			return TaskStatus.INITIALIZED;
		}

		@Override
		public boolean isNotPermitted() {
			return running == NOT_PERMITTED;
		}

		@Override
		public boolean isExceptions() {
			return !exceptions.isEmpty();
		}

		@Override
		public boolean isCancelled() {
			return running == CANCELLED;
		}

		@Override
		public boolean isRunning() {
			return running == RUNNING;
		}

		@Override
		public boolean isDone() {
			return running == DONE;
		}

		@Override
		public boolean isExited() {
			return isNotPermitted() || isCancelled() || isDone();
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
