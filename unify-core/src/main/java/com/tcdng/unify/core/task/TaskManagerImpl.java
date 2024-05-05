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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.RequestContextManager;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserTokenProvider;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Taskable;
import com.tcdng.unify.core.business.internal.ProxyBusinessServiceMethodRelay;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ParamConfig;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Default implementation of a task manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_TASKMANAGER)
public class TaskManagerImpl extends AbstractUnifyComponent implements TaskManager {

	private static final int DEFAULT_TASKRUNNER_MAXTHREADS = 128;

	@Configurable(ApplicationComponents.APPLICATION_PROXYBUSINESSSERVICEGENERATOR)
	private ProxyBusinessServiceMethodRelay proxyMethodRelay;

	@Configurable(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER)
	private RequestContextManager requestContextManager;

	@Configurable
	private UserTokenProvider userTokenProvider;

	@Configurable
	private TaskRunner periodicRunner;

	@Configurable
	private TaskRunner scheduledRunner;

	@Configurable
	private TaskRunner taskableRunner;

	private Map<String, TaskableMethodConfig> taskConfigByNameMap;

	private FactoryMap<String, List<ParamConfig>> taskParamConfigByTypeMap;

	public TaskManagerImpl() {
		taskConfigByNameMap = new HashMap<String, TaskableMethodConfig>();
		taskParamConfigByTypeMap = new FactoryMap<String, List<ParamConfig>>() {

			@Override
			protected List<ParamConfig> create(String taskName, Object... params) throws Exception {
				return DataUtils.unmodifiableList(getComponentParamConfigs(Task.class, taskName));
			}

		};
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
	public List<ParamConfig> getTaskParameters(String taskName) throws UnifyException {
		if (isTaskableMethod(taskName)) {
			return getTaskableMethodConfig(taskName).getParamConfigList();
		}

		return taskParamConfigByTypeMap.get(taskName);
	}

	@Override
	public TaskMonitor executeTask(String taskName, Map<String, Object> parameters, boolean logMessages)
			throws UnifyException {
		TaskMonitor tm = taskableRunner.schedule(taskConfigByNameMap.get(taskName), taskName, parameters, logMessages,
				0, 0, 1);
		while (!tm.isDone() && !tm.isCancelled()) {
			Thread.yield();
		}

		return tm;
	}

	@Override
	public TaskMonitor startTask(String taskName, Map<String, Object> parameters, boolean logMessages)
			throws UnifyException {
		return taskableRunner.schedule(taskConfigByNameMap.get(taskName), taskName, parameters, logMessages, 0, 0, 1);
	}

	@Override
	public TaskMonitor scheduleTaskToRunAfter(String taskName, Map<String, Object> parameters, boolean logMessages,
			long delayInMillSec) throws UnifyException {
		return scheduledRunner.schedule(taskConfigByNameMap.get(taskName), taskName, parameters, logMessages,
				delayInMillSec, 0, 1);
	}

	@Override
	public TaskMonitor scheduleTaskToRunPeriodically(String taskName, Map<String, Object> parameters,
			boolean logMessages, long inDelayInMillSec, long periodInMillSec, int numberOfTimes) throws UnifyException {
		return scheduledRunner.schedule(taskConfigByNameMap.get(taskName), taskName, parameters, logMessages,
				inDelayInMillSec, periodInMillSec, numberOfTimes);
	}

	@Override
	public TaskMonitor schedulePeriodicExecution(PeriodicType periodicType, String businessServiceName,
			String methodName, long inDelayInMillSec) throws UnifyException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Method method = getComponentType(businessServiceName).getMethod(methodName, TaskMonitor.class);
			parameters.put(PeriodicExecutionTaskConstants.PERIODICEXECUTIONINFO,
					new PeriodicExecutionInfo(businessServiceName, method));

			return periodicRunner.schedule(periodicType, PeriodicExecutionTaskConstants.PERIODIC_METHOD_TASK,
					parameters, true, inDelayInMillSec);
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
					List<ParamConfig> paramConfigList = new ArrayList<ParamConfig>();
					for (int i = 1; i < paramTypes.length; i++) {
						Class<?> paramType = paramTypes[i];
						Parameter pa = ta.parameters()[i - 1];
						ParamConfig pc = createParamConfig(pa);
						if (!paramType.isAssignableFrom(pc.getType())) {
							throw new UnifyException(UnifyCoreErrorConstants.TASKABLE_PARAMETER_TYPE_INCOMPATIBLE,
									pc.getParamName(), pa.type(), ta.name(), paramType);
						}

						paramConfigList.add(pc);
					}

					// Create configuration and store
					String idGenerator = AnnotationUtils.getAnnotationString(ta.idGenerator());
					tmc = new TaskableMethodConfig(ta.name(), resolveApplicationMessage(ta.description()),
							unifyComponentConfig.getName(), method, paramConfigList, ta.limit(), idGenerator,
							ta.schedulable());
					taskConfigByNameMap.put(tmc.getName(), tmc);
				}
			}
		}

		final int maxThreads = getContainerSetting(int.class,
				UnifyCorePropertyConstants.APPLICATION_MAX_TASKRUNNER_THREADS, DEFAULT_TASKRUNNER_MAXTHREADS);
		periodicRunner.start(maxThreads, true);
		taskableRunner.start(maxThreads, true);		
		scheduledRunner.start(maxThreads, false);		
	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

}
