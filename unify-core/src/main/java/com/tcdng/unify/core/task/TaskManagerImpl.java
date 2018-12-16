/*
 * Copyright 2018 The Code Department
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.RequestContextManager;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyError;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Taskable;
import com.tcdng.unify.core.business.internal.ProxyBusinessModuleMethodRelay;
import com.tcdng.unify.core.system.ClusterManagerBusinessModule;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Default implementation of a task manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_TASKMANAGER)
public class TaskManagerImpl extends AbstractUnifyComponent implements TaskManager {

    @Configurable(ApplicationComponents.APPLICATION_PROXYBUSINESSMODULEGENERATOR)
    private ProxyBusinessModuleMethodRelay proxyMethodRelay;

    @Configurable(ApplicationComponents.APPLICATION_TASKSTATUSLOGGER)
    private TaskStatusLogger taskStatusLogger;

    @Configurable(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER)
    private RequestContextManager requestContextManager;

    @Configurable("128")
    private int maxThreads;

    @Configurable("256")
    private int maxMonitorMessages;

    private Set<String> uniqueTaskIDSet;

    private ScheduledExecutorService scheduledExecutorService;

    private Map<String, TaskableMethodConfig> taskConfigByNameMap;

    public TaskManagerImpl() {
        uniqueTaskIDSet = Collections.synchronizedSet(new HashSet<String>());
        taskConfigByNameMap = new HashMap<String, TaskableMethodConfig>();
    }

    @Override
    public TaskableMethodConfig getTaskableMethodConfig(String taskName) throws UnifyException {
        TaskableMethodConfig bmtc = taskConfigByNameMap.get(taskName);
        if (bmtc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_METHOD_UNKNOWN, taskName);
        }
        return bmtc;
    }

    @Override
    public Collection<TaskableMethodConfig> getAllTaskableMethodConfigs() throws UnifyException {
        return taskConfigByNameMap.values();
    }

    @Override
    public boolean isTaskableMethod(String taskName) throws UnifyException {
        return taskConfigByNameMap.containsKey(taskName);
    }

    @Override
    public TaskMonitor executeTask(String taskName, Map<String, Object> parameters, boolean logMessages,
            String taskStatusLoggerName) throws UnifyException {
        TaskMonitor taskMonitor = setExecution(Arrays.asList(taskName), parameters, logMessages, false, 0, 0, 0,
                taskStatusLoggerName);
        while (taskMonitor.isPending()) {
            Thread.yield();
        }
        return taskMonitor;
    }

    @Override
    public TaskMonitor executeTasks(List<String> taskNames, Map<String, Object> parameters, boolean logMessages,
            boolean dependent, String taskStatusLoggerName) throws UnifyException {
        TaskMonitor taskMonitor = setExecution(taskNames, parameters, logMessages, dependent, 0, 0, 0,
                taskStatusLoggerName);
        while (taskMonitor.isPending()) {
            Thread.yield();
        }
        return taskMonitor;
    }

    @Override
    public TaskMonitor startTask(String taskName, Map<String, Object> parameters, boolean logMessages,
            String taskStatusLoggerName) throws UnifyException {
        return setExecution(Arrays.asList(taskName), parameters, logMessages, false, 0, 0, 0, taskStatusLoggerName);
    }

    @Override
    public TaskMonitor startTasks(List<String> taskNames, Map<String, Object> parameters, boolean logMessages,
            boolean dependent, String taskStatusLoggerName) throws UnifyException {
        return setExecution(taskNames, parameters, logMessages, dependent, 0, 0, 0, taskStatusLoggerName);
    }

    @Override
    public TaskMonitor scheduleTaskToRunAfter(String taskName, Map<String, Object> parameters, boolean logMessages,
            long delayInMillSec, String taskStatusLoggerName) throws UnifyException {
        return setExecution(Arrays.asList(taskName), parameters, logMessages, false, delayInMillSec, 0, 0,
                taskStatusLoggerName);
    }

    @Override
    public TaskMonitor scheduleTasksToRunAfter(List<String> taskNames, Map<String, Object> parameters,
            boolean logMessages, boolean dependent, long delayInMillSec, String taskStatusLoggerName)
            throws UnifyException {
        return setExecution(taskNames, parameters, logMessages, dependent, delayInMillSec, 0, 0, taskStatusLoggerName);
    }

    @Override
    public TaskMonitor scheduleTaskToRunPeriodically(String taskName, Map<String, Object> parameters,
            boolean logMessages, long inDelayInMillSec, long periodInMillSec, int numberOfTimes,
            String taskStatusLoggerName) throws UnifyException {
        return setExecution(Arrays.asList(taskName), parameters, logMessages, false, inDelayInMillSec, periodInMillSec,
                numberOfTimes, taskStatusLoggerName);
    }

    @Override
    public TaskMonitor scheduleTasksToRunPeriodically(List<String> taskNames, Map<String, Object> parameters,
            boolean logMessages, boolean dependent, long inDelayInMillSec, long periodInMillSec, int numberOfTimes,
            String taskStatusLoggerName) throws UnifyException {
        return setExecution(taskNames, parameters, logMessages, dependent, inDelayInMillSec, periodInMillSec,
                numberOfTimes, taskStatusLoggerName);
    }

    @Override
    public TaskMonitor schedulePeriodicExecution(PeriodicType periodicType, String businessServiceName,
            String methodName, String taskStatusLoggerName, long inDelayInMillSec) throws UnifyException {
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            Method method = getComponentType(businessServiceName).getMethod(methodName, TaskMonitor.class);
            parameters.put(PeriodicExecutionTaskConstants.PERIODICEXECUTIONINFO,
                    new PeriodicExecutionInfo(businessServiceName, method));

            return scheduleTaskToRunPeriodically("periodicexecution-task", parameters, false, inDelayInMillSec,
                    periodicType.getPeriodInMillSec(), 0, taskStatusLoggerName);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(UnifyComponent.class)) {
            for (Method method : unifyComponentConfig.getType().getMethods()) {
                Taskable ta = method.getAnnotation(Taskable.class);
                if (ta == null) {
                    ta = proxyMethodRelay
                            .getTaskable(ReflectUtils.getMethodSignature(unifyComponentConfig.getName(), method));
                }

                if (ta != null) {
                    if (!unifyComponentConfig.isSingleton()) {
                        throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_METHOD_SINGLETON_ONLY,
                                unifyComponentConfig.getName(), ta.name());
                    }

                    TaskableMethodConfig tmc = taskConfigByNameMap.get(ta.name());
                    if (tmc != null) {
                        throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_METHOD_ALREADY_EXISTS, ta.name(),
                                unifyComponentConfig.getName(), tmc.getComponentName());
                    }

                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes.length == 0 || !TaskMonitor.class.equals(paramTypes[0])) {
                        throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_METHOD_MUST_HAVE_FIRST_TASKMONITOR,
                                ta.name(), unifyComponentConfig.getName(), method);
                    }

                    if (paramTypes.length != (ta.parameters().length + 1)) {
                        throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_METHOD_MISMATCHED_PARAMS, ta.name(),
                                unifyComponentConfig.getName());
                    }

                    if (void.class.equals(method.getReturnType())) {
                        throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_METHOD_RETURN_NON_VOID, ta.name(),
                                unifyComponentConfig.getName());
                    }

                    // Get parameter configuration
                    List<TaskableMethodConfig.ParamConfig> paramConfigList = new ArrayList<TaskableMethodConfig.ParamConfig>();
                    for (int i = 1; i < paramTypes.length; i++) {
                        Class<?> paramType = paramTypes[i];
                        Parameter pa = ta.parameters()[i - 1];
                        String paramName = AnnotationUtils.getAnnotationString(pa.name());
                        if (paramName == null) {
                            paramName = AnnotationUtils.getAnnotationString(pa.value());
                        }

                        String description = AnnotationUtils.getAnnotationString(pa.description());
                        if (description != null) {
                            description = resolveApplicationMessage(description);
                        }

                        if (!paramType.isAssignableFrom(pa.type())) {
                            throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_PARAMETER_TYPE_INCOMPATIBLE,
                                    paramName, pa.type(), ta.name(), paramType);
                        }

                        paramConfigList.add(new TaskableMethodConfig.ParamConfig(pa.type(), paramName, description,
                                pa.editor(), pa.mandatory()));
                    }

                    // Create configuration and store
                    String idGenerator = AnnotationUtils.getAnnotationString(ta.idGenerator());
                    tmc = new TaskableMethodConfig(ta.name(), resolveApplicationMessage(ta.description()),
                            unifyComponentConfig.getName(), method, Collections.unmodifiableList(paramConfigList),
                            ta.limit(), idGenerator, ta.schedulable());
                    taskConfigByNameMap.put(tmc.getName(), tmc);
                }
            }
        }

        // Initialize scheduler
        if (maxThreads <= 0) {
            maxThreads = 10;
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(maxThreads);
    }

    @Override
    protected void onTerminate() throws UnifyException {
        scheduledExecutorService.shutdownNow();
    }

    private TaskMonitor setExecution(List<String> taskNames, Map<String, Object> parameters, boolean logMessages,
            boolean dependent, long inDelayMillSec, long periodMillSec, int numberOfTimes, String taskStatusLoggerName)
            throws UnifyException {
        boolean periodic = periodMillSec > 0;
        TaskStatusLogger taskStatusLogger = null;
        if (taskStatusLoggerName != null) {
            taskStatusLogger = (TaskStatusLogger) getComponent(taskStatusLoggerName);
        }

        TaskMonitorImpl taskMonitor = new TaskMonitorImpl(taskStatusLogger, logMessages);
        TaskInfo prevTaskInfo = null;
        Task[] tasks = new Task[taskNames.size()];
        for (int i = 0; i < tasks.length; i++) {
            try {
                String origTaskName = taskNames.get(i);
                String actTaskName = origTaskName;
                TaskableMethodConfig tmc = null;
                if (isTaskableMethod(origTaskName)) {
                    tmc = getTaskableMethodConfig(origTaskName);
                    actTaskName = TaskableMethodConstants.TASKABLE_METHOD_TASK;
                }

                tasks[i] = (Task) getComponent(actTaskName);
                TaskInfo taskInfo = createTaskInfo(origTaskName, tasks[i], tmc, parameters, prevTaskInfo);

                prevTaskInfo = taskInfo;
                taskMonitor.addTaskInfo(taskInfo);
            } catch (UnifyException ex) {
                for (TaskInfo taskInfo : taskMonitor.getTaskInfoList()) {
                    uniqueTaskIDSet.remove(taskInfo.getTaskID());
                }
                throw ex;
            }
        }

        Runnable runnable = new TaskThread(getRequestContext(), taskMonitor, tasks, parameters, periodic, dependent,
                numberOfTimes);
        Future<?> future = null;
        if (periodic) {
            if (inDelayMillSec <= 0) {
                inDelayMillSec = 1;
            }
            future = scheduledExecutorService.scheduleAtFixedRate(runnable, inDelayMillSec, periodMillSec,
                    TimeUnit.MILLISECONDS);
        } else if (inDelayMillSec > 0) {
            future = scheduledExecutorService.schedule(runnable, inDelayMillSec, TimeUnit.MILLISECONDS);
        } else {
            future = scheduledExecutorService.schedule(runnable, 1, TimeUnit.MILLISECONDS);
        }

        taskMonitor.setFuture(future);

        return taskMonitor;
    }

    private synchronized TaskInfo createTaskInfo(String origTaskName, Task task, TaskableMethodConfig tmc,
            Map<String, Object> inputParameters, TaskInfo prevTaskInfo) throws UnifyException {
        TaskOutput prevTaskOutput = null;
        if (prevTaskInfo != null) {
            prevTaskOutput = prevTaskInfo.getTaskOutput();
        }

        TaskInput taskInput = new TaskInput(origTaskName, tmc, inputParameters, prevTaskOutput);
        TaskInstanceInfo taskInstanceInfo = task.getTaskInstanceInfo(taskInput);
        String executionId = taskInstanceInfo.getExecutionId();
        if (TaskExecLimit.ALLOW_SINGLE.equals(taskInstanceInfo.getLimit())) {
            // TODO the task ID set should be checked against execution IDs in cluster
            // cache!
            // To use grabLock();
            if (uniqueTaskIDSet.contains(executionId)) {
                throw new UnifyException(UnifyCoreErrorConstants.TASK_WITH_ID_ALREADY_RUNNING, executionId);
            }
            uniqueTaskIDSet.add(executionId);
        }

        return new TaskInfo(executionId, taskInput, new TaskOutput());
    }

    private void removeTask(TaskInfo taskInfo, TaskStatus taskStatus, boolean periodic) {
        if (!periodic) {
            uniqueTaskIDSet.remove(taskInfo.getTaskID());
        }

        if (taskStatus != null) {
            taskInfo.setTaskStatus(taskStatus);
        }
    }

    private class TaskThread extends Thread {

        private RequestContext requestContext;

        private TaskMonitorImpl taskMonitor;

        private Task[] tasks;

        private Map<String, Object> parameters;

        private String lockToRelease;

        private boolean periodic;

        private boolean dependent;

        private int numberOfTimes;

        public TaskThread(RequestContext requestContext, TaskMonitorImpl taskMonitor, Task[] tasks,
                Map<String, Object> parameters, boolean periodic, boolean dependent, int numberOfTimes) {
            this.requestContext = requestContext;
            this.taskMonitor = taskMonitor;
            this.tasks = tasks;
            this.parameters = parameters;
            this.periodic = periodic;
            this.dependent = dependent;
            this.numberOfTimes = numberOfTimes;
            lockToRelease = (String) parameters.get(TaskParameterConstants.LOCK_TO_RELEASE);
        }

        @Override
        public void run() {
            try {
                requestContextManager.loadRequestContext(requestContext);
                requestContext = null;
                runTasks();
            } catch (Exception e) {
            } finally {
                if (!StringUtils.isBlank(lockToRelease)) {
                    try {
                        ClusterManagerBusinessModule clusterManager = (ClusterManagerBusinessModule) getComponent(
                                ApplicationComponents.APPLICATION_CLUSTERMANAGER);
                        clusterManager.releaseSynchronizationLock(lockToRelease);
                    } catch (Exception e) {
                    }
                }

                try {
                    requestContextManager.unloadRequestContext();
                } catch (Exception e) {
                }
            }

        }

        private void runTasks() {
            taskMonitor.setRunning(true);
            boolean endPeriodic = false;
            boolean abort = false;
            int i = 0;
            for (; !isInterrupted() && !abort && i < tasks.length; i++) {
                taskMonitor.setCurrentTaskIndex(i);
                TaskInfo taskInfo = taskMonitor.getTaskInfo(i);

                try {
                    if (!taskMonitor.isCanceled()) {
                        taskInfo.setTaskStatus(TaskStatus.RUNNING);

                        if (taskMonitor.isTaskStatusLogger()) {
                            taskMonitor.getTaskStatusLogger().logTaskStatus(taskMonitor, parameters);
                        }

                        tasks[i].execute(taskMonitor, taskInfo.getTaskInput(), taskInfo.getTaskOutput());

                        if (!taskMonitor.isCanceled()) {
                            removeTask(taskInfo, TaskStatus.COMPLETED, periodic);
                        }
                    }

                    if (taskMonitor.isCanceled()) {
                        removeTask(taskInfo, TaskStatus.CANCELED, periodic);
                    }
                } catch (Exception e) {
                    if (taskMonitor.isTaskStatusLogger()) {
                        taskMonitor.getTaskStatusLogger().logTaskException(e);
                    } else {
                        taskStatusLogger.logTaskException(e);
                    }

                    removeTask(taskInfo, TaskStatus.FAILED, periodic);
                    taskInfo.setTaskStatus(TaskStatus.FAILED);
                    taskMonitor.addException(e);
                    try {
                        if (e instanceof UnifyException) {
                            UnifyError err = ((UnifyException) e).getUnifyError();
                            taskMonitor.addMessage(getUnifyComponentContext().getMessages()
                                    .getMessage(Locale.getDefault(), err.getErrorCode(), err.getErrorParams()));
                        } else {
                            taskMonitor.addMessage(e.getMessage());
                        }
                    } catch (UnifyException e1) {
                    }

                    if (dependent) {
                        abort = true;
                    }

                    logError(e);
                } finally {
                    if (taskMonitor.isTaskStatusLogger()) {
                        taskMonitor.getTaskStatusLogger().logTaskStatus(taskMonitor, parameters);
                    }
                }
            }

            for (; i < tasks.length; i++) {
                taskMonitor.setCurrentTaskIndex(i);
                TaskInfo taskInfo = taskMonitor.getTaskInfo(i);
                removeTask(taskInfo, TaskStatus.ABORTED, periodic);
                if (taskMonitor.isTaskStatusLogger()) {
                    taskMonitor.getTaskStatusLogger().logTaskStatus(taskMonitor, parameters);
                }
            }

            if (numberOfTimes > 0) {
                if ((--numberOfTimes) == 0) {
                    taskMonitor.cancelFuture();
                    endPeriodic = true;
                }
            }

            if (endPeriodic) {
                for (TaskInfo taskInfo : taskMonitor.getTaskInfoList()) {
                    removeTask(taskInfo, null, false);
                }
            }

            taskMonitor.setRunning(false);
        }
    }

    private class TaskInfo {

        private String taskID;

        private TaskInput taskInput;

        private TaskOutput taskOutput;

        private TaskStatus taskStatus;

        public TaskInfo(String taskID, TaskInput taskInput, TaskOutput taskOutput) {
            this.taskID = taskID;
            this.taskInput = taskInput;
            this.taskOutput = taskOutput;
            taskStatus = TaskStatus.INITIALISED;
        }

        public TaskStatus getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(TaskStatus taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getTaskID() {
            return taskID;
        }

        public TaskInput getTaskInput() {
            return taskInput;
        }

        public TaskOutput getTaskOutput() {
            return taskOutput;
        }
    }

    private class TaskMonitorImpl implements TaskMonitor {

        private TaskStatusLogger taskStatusLogger;

        private List<TaskInfo> taskInfoList;

        private List<String> messages;

        private List<Exception> exceptions;

        private Future<?> future;

        private int currentTaskIndex;

        private boolean logMessages;

        private boolean canceled;

        private boolean running;

        public TaskMonitorImpl(TaskStatusLogger taskStatusLogger, boolean logMessages) {
            this.taskStatusLogger = taskStatusLogger;
            this.logMessages = logMessages;
            taskInfoList = new ArrayList<TaskInfo>();
            if (this.logMessages) {
                messages = new ArrayList<String>();
                exceptions = new ArrayList<Exception>();
            }
        }

        @Override
        public String getTaskName(int taskIndex) {
            return taskInfoList.get(taskIndex).getTaskInput().getOrigTaskName();
        }

        @Override
        public void cancel() {
            canceled = true;
            cancelFuture();
        }

        @Override
        public boolean isExceptions() {
            return !exceptions.isEmpty();
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public boolean isPending() {
            if (!canceled) {
                TaskStatus taskStatus = taskInfoList.get(taskInfoList.size() - 1).getTaskStatus();
                return TaskStatus.INITIALISED.equals(taskStatus) || TaskStatus.RUNNING.equals(taskStatus);
            }

            return false;
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public boolean isDone() {
            TaskStatus taskStatus = taskInfoList.get(taskInfoList.size() - 1).getTaskStatus();
            return TaskStatus.COMPLETED.equals(taskStatus) || TaskStatus.CANCELED.equals(taskStatus)
                    || TaskStatus.FAILED.equals(taskStatus) || TaskStatus.ABORTED.equals(taskStatus);
        }

        @Override
        public String getTaskId(int taskIndex) {
            return taskInfoList.get(taskIndex).getTaskID();
        }

        @Override
        public TaskStatus getTaskStatus(int taskIndex) {
            return taskInfoList.get(taskIndex).getTaskStatus();
        }

        @Override
        public TaskOutput getTaskOutput(int taskIndex) {
            return taskInfoList.get(taskIndex).getTaskOutput();
        }

        @Override
        public int getTaskCount() {
            return taskInfoList.size();
        }

        @Override
        public int getCurrentTaskIndex() {
            return currentTaskIndex;
        }

        @Override
        public TaskStatus getCurrentTaskStatus() {
            return getTaskStatus(currentTaskIndex);
        }

        @Override
        public TaskOutput getCurrentTaskOutput() {
            return getTaskOutput(currentTaskIndex);
        }

        @Override
        public void addException(Exception exception) {
            if (logMessages) {
                if (exceptions.size() >= maxMonitorMessages) {
                    exceptions.remove(0);
                }

                exceptions.add(exception);
            }
        }

        @Override
        public Exception[] getExceptions() {
            if (logMessages) {
                return exceptions.toArray(new Exception[exceptions.size()]);
            }

            return new Exception[0];
        }

        @Override
        public void addMessage(String message) {
            if (logMessages) {
                if (messages.size() >= maxMonitorMessages) {
                    messages.remove(0);
                }

                messages.add(message);
                logDebug("Task Monitor ({0}): {1}", getTaskId(currentTaskIndex), message);
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

        public void cancelFuture() {
            if (future != null) {
                future.cancel(false);
            }
        }

        public void addTaskInfo(TaskInfo taskInfo) {
            taskInfoList.add(taskInfo);
        }

        public List<TaskInfo> getTaskInfoList() {
            return taskInfoList;
        }

        public TaskInfo getTaskInfo(int taskIndex) {
            return taskInfoList.get(taskIndex);
        }

        public void setCurrentTaskIndex(int currentTaskIndex) {
            this.currentTaskIndex = currentTaskIndex;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public TaskStatusLogger getTaskStatusLogger() {
            return taskStatusLogger;
        }

        public boolean isTaskStatusLogger() {
            return taskStatusLogger != null;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }
    }
}
