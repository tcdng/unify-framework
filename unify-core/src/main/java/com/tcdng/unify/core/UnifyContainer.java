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
package com.tcdng.unify.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tcdng.unify.common.annotation.AnnotationConstants;
import com.tcdng.unify.common.constants.UnifyStaticSettings;
import com.tcdng.unify.core.annotation.Broadcast;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicClusterOnly;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Plugin;
import com.tcdng.unify.core.annotation.Preferred;
import com.tcdng.unify.core.application.BootService;
import com.tcdng.unify.core.business.BusinessLogicUnit;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.business.internal.ProxyBusinessServiceGenerator;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.format.DateTimeFormatter;
import com.tcdng.unify.core.logging.AbstractLog4jLogger;
import com.tcdng.unify.core.logging.DummyEventLogger;
import com.tcdng.unify.core.logging.Logger;
import com.tcdng.unify.core.logging.LoggingLevel;
import com.tcdng.unify.core.message.ResourceBundles;
import com.tcdng.unify.core.system.ClusterService;
import com.tcdng.unify.core.system.Command;
import com.tcdng.unify.core.system.LockManager;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.core.task.PeriodicExecutionTaskConstants;
import com.tcdng.unify.core.task.TaskManager;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.upl.UplCompiler;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplElementAttributes;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ImageUtils;
import com.tcdng.unify.core.util.NameUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.ThreadUtils;
import com.tcdng.unify.core.util.TokenUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * Represents a container for unify components.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UnifyContainer {

	private UnifyContainerEnvironment unifyContainerEnvironment;

	private Map<String, InternalUnifyComponentInfo> internalUnifyComponentInfos;

	private Map<String, String> internalResolutionMap;

	private Map<String, Object> unifySettings;

	private Map<String, String> aliases;

	private List<UnifyStaticSettings> staticSettings;

	private RequestContextManager requestContextManager;

	private ApplicationContext applicationContext;

	private BootService applicationBootService;

	private LockManager lockManager;

	private ClusterService clusterService;

	private UplCompiler uplCompiler;

	private UserSessionManager userSessionManager;

	private ResourceBundles messages;

	private Logger logger;

	private FactoryMap<String, UnifyComponentContext> componentContextMap;

	private FactoryMap<String, UnifyComponentInst> singletonComponentMap;

	private LocaleFactoryMaps<String, UplComponent> cachedLocaleUplComponentMap;

	private List<TaskMonitor> periodicTaskMonitorList;

	private List<UnifyComponent> singletonTerminationList;

	private Set<UnifyContainerInterface> interfaces;

	private Queue<ContainerCommand> containerCommandQueue;

	private Map<String, BroadcastInfo> broadcastInfoMap;

	private Map<Class<? extends UnifyComponent>, List<String>> namelessConfigurableSuggestions;

	private String nodeId;

	private String runtimeId;

	private String deploymentVersion;

	private String auxiliaryVersion;

	private String hostAddress;

	private String hostHome;

	private String accessKey;

	private Date startTime;

	private Locale applicationLocale;

	private TimeZone applicationTimeZone;

	private short preferredPort;

	private boolean applicationIgnoreViewDirective;

	private boolean toConsole;

	private boolean productionMode;

	private boolean clusterMode;

	private boolean deploymentMode;

	private boolean started;

	private boolean shutdown;

	private boolean interfacesOpen;

	public UnifyContainer() {
		this.accessKey = UUID.randomUUID().toString();

		this.internalUnifyComponentInfos = new ConcurrentHashMap<String, InternalUnifyComponentInfo>();
		this.periodicTaskMonitorList = new ArrayList<TaskMonitor>();
		this.singletonTerminationList = new ArrayList<UnifyComponent>();
		this.interfaces = new HashSet<UnifyContainerInterface>();
		this.containerCommandQueue = new ConcurrentLinkedQueue<ContainerCommand>();
		this.broadcastInfoMap = new HashMap<String, BroadcastInfo>();
		this.namelessConfigurableSuggestions = new HashMap<Class<? extends UnifyComponent>, List<String>>();

		this.componentContextMap = new FactoryMap<String, UnifyComponentContext>() {
			@Override
			protected UnifyComponentContext create(String name, Object... params) throws Exception {
				return new UnifyComponentContext(applicationContext, getLogger(name), name);
			}
		};

		this.singletonComponentMap = new FactoryMap<String, UnifyComponentInst>() {
			@Override
			protected UnifyComponentInst create(String name, Object... params) throws Exception {
				InternalUnifyComponentInfo iuci = (InternalUnifyComponentInfo) params[0];
				UnifyComponent unifyComponent = iuci.getType().getDeclaredConstructor().newInstance();
				singletonTerminationList.add(0, unifyComponent);
				return new UnifyComponentInst(iuci, unifyComponent);
			}
		};

		this.cachedLocaleUplComponentMap = new LocaleFactoryMaps<String, UplComponent>() {
			@Override
			protected UplComponent createObject(Locale locale, String descriptor, Object... params) throws Exception {
				UplComponent localeComponent = getUplComponent(locale, descriptor, false);
				return localeComponent;
			}
		};
	}

	/**
	 * Starts the container using supplied configuration.
	 * 
	 * @param uce the environment object
	 * @param ucc the configuration used for initialization
	 * @throws UnifyException if container is already started. If an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public void startup(UnifyContainerEnvironment uce, UnifyContainerConfig ucc) throws UnifyException {
		if (started || shutdown) {
			throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_ALREADY_INITIALIZED);
		}

		// Environment
		unifyContainerEnvironment = uce;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostAddress = inetAddress.getHostAddress();
			hostHome = inetAddress.getHostName();
		} catch (UnknownHostException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.CONTAINER_ERROR);
		}

		deploymentVersion = ucc.getDeploymentVersion();
		auxiliaryVersion = ucc.getAuxiliaryVersion();
		clusterMode = ucc.isClusterMode();
		productionMode = ucc.isProductionMode();
		deploymentMode = ucc.isDeploymentMode();
		unifySettings = ucc.getProperties();
		aliases = ucc.getAliases();
		staticSettings = ucc.getStaticSettings();
		nodeId = ucc.getNodeId();
		preferredPort = ucc.getPreferredPort();
		runtimeId = UUID.randomUUID().toString();

		if (nodeId == null) {
			throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_NODEID_REQUIRED);
		}

		toConsole = false;
		if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_TOCONSOLE) != null) {
			toConsole = Boolean
					.valueOf(String.valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_TOCONSOLE)));
		}

		applicationIgnoreViewDirective = false;
		if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_VIEW_DIRECTIVE_IGNORE) != null) {
			applicationIgnoreViewDirective = Boolean.valueOf(
					String.valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_VIEW_DIRECTIVE_IGNORE)));
		}

		boolean restrictedJARMode = false;
		if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_RETRICTED_JAR_MODE) != null) {
			restrictedJARMode = Boolean.valueOf(
					String.valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_RETRICTED_JAR_MODE)));
		}
		
		if (restrictedJARMode) {
			IOUtils.enterRestrictedJARMode();
		}
		
		// Banner
		List<String> banner = getApplicationBanner();
		if (!banner.isEmpty()) {
			for (String line : banner) {
				toConsole(line);
			}
		}

		String lineSeparator = System.getProperty("line.separator");
		applicationContext = new ApplicationContext(this, getApplicationLocale(), getApplicationTimeZone(),
				lineSeparator != null ? lineSeparator : "\n", applicationIgnoreViewDirective);
		long startTimeMillis = System.currentTimeMillis();
		initializeContainerMessages();
		logger = new UnifyContainerLoggerImpl(getClass());

		toConsole("Container initialization started...");
		toConsole("Validating and loading configuration...");
		for (UnifyComponentConfig unifyComponentConfig : ucc.getComponentConfigs()) {
			// Check conflicts
			if (unifyComponentConfig.isWithConfict()) {
				StringBuilder sb = new StringBuilder();
				boolean appendSym = false;
				for (UnifyComponentConfig conflictConfig : unifyComponentConfig.getConflictList()) {
					if (appendSym) {
						sb.append(", ");
					} else {
						appendSym = true;
					}

					sb.append(conflictConfig);
				}

				throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_CONFLICTING_COMPONENTS_FOUND_IN_CONFIG,
						unifyComponentConfig, sb.toString());
			}

			// Validate configuration
			Class<?> type = unifyComponentConfig.getType();
			for (String property : unifyComponentConfig.getSettings().getPropertyNames()) {
				Field field = ReflectUtils.getField(type, property);
				if (!field.isAnnotationPresent(Configurable.class)) {
					throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_PROPERTY_NOT_CONFIGURABLE,
							unifyComponentConfig.getName(), type, property);
				} else if (UnifyComponent.class.isAssignableFrom(field.getType())) {
					// Setup nameless references
					Configurable ca = field.getAnnotation(Configurable.class);
					if (ca.value() == AnnotationConstants.NONE || ca.values().length == 0) {
						if (!namelessConfigurableSuggestions.containsKey(field.getType())) {
							namelessConfigurableSuggestions.put((Class<? extends UnifyComponent>) field.getType(),
									null);
						}
					}
				}
			}

			// Create internal information objects
			internalUnifyComponentInfos.put(unifyComponentConfig.getName(),
					new InternalUnifyComponentInfo(unifyComponentConfig));
		}

		logDebug("Detecting and replacing customized components...");
		// Resolve customization
		List<String> customizationSuffixList = DataUtils.convert(ArrayList.class, String.class,
				getSetting(UnifyCorePropertyConstants.APPLICATION_CUSTOMIZATION));
		internalResolutionMap = UnifyConfigUtils.resolveConfigurationOverrides(internalUnifyComponentInfos,
				customizationSuffixList);

		// Detect business components
		logDebug("Detecting business service components...");
		Map<String, Map<String, PeriodicInfo>> componentPeriodMethodMap = new HashMap<String, Map<String, PeriodicInfo>>();
		Map<String, Set<String>> componentPluginSocketsMap = new HashMap<String, Set<String>>();
		List<UnifyComponentConfig> managedBusinessServiceConfigList = new ArrayList<UnifyComponentConfig>();
		int businessServiceCount = 0;
		for (Map.Entry<String, InternalUnifyComponentInfo> entry : internalUnifyComponentInfos.entrySet()) {
			InternalUnifyComponentInfo iuci = entry.getValue();
			// Fetch periodic method information
			Map<String, PeriodicInfo> periodicMethodMap = new HashMap<String, PeriodicInfo>();
			Set<String> pluginSockets = new HashSet<String>();
			Method[] methods = iuci.getType().getMethods();
			for (Method method : methods) {
				// Periodic methods
				Periodic pa = method.getAnnotation(Periodic.class);
				if (pa != null) {
					if (iuci.isSingleton() && void.class.equals(method.getReturnType())
							&& method.getParameterTypes().length == 1
							&& method.getParameterTypes()[0].equals(TaskMonitor.class)) {
						periodicMethodMap.put(method.getName(), new PeriodicInfo(pa.value(), false));
					} else {
						throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_INVALID_PERIOD_METHOD,
								iuci.getName(), method.getName());
					}
				}

				PeriodicClusterOnly pac = method.getAnnotation(PeriodicClusterOnly.class);
				if (pac != null) {
					if (iuci.isSingleton() && void.class.equals(method.getReturnType())
							&& method.getParameterTypes().length == 1
							&& method.getParameterTypes()[0].equals(TaskMonitor.class)) {
						periodicMethodMap.put(method.getName(), new PeriodicInfo(pac.value(), true));
					} else {
						throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_INVALID_PERIOD_METHOD,
								iuci.getName(), method.getName());
					}
				}

				// Broadcast methods
				Broadcast ba = method.getAnnotation(Broadcast.class);
				if (ba != null) {
					boolean noParams = false;
					if (iuci.isSingleton() && void.class.equals(method.getReturnType())
							&& ((noParams = method.getParameterTypes().length == 0)
									|| (method.getParameterTypes().length == 1
											&& method.getParameterTypes()[0].equals(String[].class)))) {
						String name = NameUtils.getComponentMethodName(iuci.getName(), method.getName());
						broadcastInfoMap.put(name, new BroadcastInfo(iuci.getName(), method.getName(), noParams));
					} else {
						throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_INVALID_BROADCAST_METHOD,
								iuci.getName(), method.getName());
					}
				}
			}

			if (!periodicMethodMap.isEmpty()) {
				logDebug("[{0}] periodic methods detected for Component [{1}].", periodicMethodMap.size(),
						iuci.getName());
				componentPeriodMethodMap.put(iuci.getName(), periodicMethodMap);
			}

			if (!pluginSockets.isEmpty()) {
				logDebug("[{0}] plug-in sockets detected for Component [{1}].", periodicMethodMap.size(),
						iuci.getName());
				componentPluginSocketsMap.put(iuci.getName(), pluginSockets);
			}

			if (ReflectUtils.isInterface(iuci.getType(), BusinessService.class)) {
				managedBusinessServiceConfigList.add(iuci.getUnifyComponentConfig());
				businessServiceCount++;
			}
		}
		logDebug("Total of [{0}] business components detected.", businessServiceCount);

		// Detect business logic plug-ins
		logDebug("Detecting business logic plugins...");
		Map<String, Map<String, List<UnifyPluginInfo>>> allPluginsBySocketMap = new HashMap<String, Map<String, List<UnifyPluginInfo>>>();
		for (Map.Entry<String, InternalUnifyComponentInfo> entry : internalUnifyComponentInfos.entrySet()) {
			InternalUnifyComponentInfo iuci = entry.getValue();
			// Check if component is a BLU plug-in
			Plugin pla = iuci.getType().getAnnotation(Plugin.class);
			if (pla != null) {
				if (BusinessLogicUnit.class.isAssignableFrom(iuci.getType())) {
					InternalUnifyComponentInfo busServInfo = internalUnifyComponentInfos.get(pla.target());
					if (busServInfo == null) {
						throw new UnifyException(UnifyCoreErrorConstants.BUSINESSLOGIC_PLUGIN_TARGET_UNKNOWN,
								iuci.getName(), pla.target());
					}

					if (!BusinessService.class.isAssignableFrom(busServInfo.getType())) {
						throw new UnifyException(UnifyCoreErrorConstants.BUSINESSLOGIC_PLUGIN_TARGET_NON_BUSINESSMODULE,
								iuci.getName(), pla.target());
					}

					Method method = null;
					try {
						method = busServInfo.getType().getMethod(pla.method(), pla.paramTypes());
					} catch (Exception e) {
						throw new UnifyException(UnifyCoreErrorConstants.BUSINESSLOGIC_PLUGIN_TARGET_NON_SOCKET,
								iuci.getName(), pla.method(), pla.target(), e);
					}

					Map<String, List<UnifyPluginInfo>> map = allPluginsBySocketMap.get(pla.target());
					if (map == null) {
						map = new HashMap<String, List<UnifyPluginInfo>>();
						allPluginsBySocketMap.put(pla.target(), map);
					}

					String methodSig = ReflectUtils.getMethodSignature(busServInfo.getName(), method);
					List<UnifyPluginInfo> list = map.get(methodSig);
					if (list == null) {
						list = new ArrayList<UnifyPluginInfo>();
						map.put(methodSig, list);
					}
					list.add(new UnifyPluginInfo(iuci.getName(), pla.type()));
				}
			}
		}

		// Set some system defaults
		if (!internalUnifyComponentInfos.containsKey(ApplicationComponents.APPLICATION_EVENTSLOGGER)) {
			UnifyComponentConfig internalComponentConfig = new UnifyComponentConfig(
					ApplicationComponents.APPLICATION_EVENTSLOGGER, "Application Event Logger", DummyEventLogger.class,
					true);
			internalUnifyComponentInfos.put(internalComponentConfig.getName(),
					new InternalUnifyComponentInfo(internalComponentConfig));
		}

		// Set nameless suggestions
		logDebug("Setting nameless suggestions...");
		for (Map.Entry<Class<? extends UnifyComponent>, List<String>> entry : namelessConfigurableSuggestions
				.entrySet()) {
			entry.setValue(getComponentNames(entry.getKey()));
		}

		// Pre-calculate inject information
		for (InternalUnifyComponentInfo iuci : internalUnifyComponentInfos.values()) {
			buildPropertyInjectionInfo(iuci);
		}

		// Initialization
		started = true;
		try {
			requestContextManager = (RequestContextManager) getComponent(
					ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER);
			uplCompiler = (UplCompiler) getComponent(ApplicationComponents.APPLICATION_UPLCOMPILER);

			// Generate and install proxy business service objects
			logInfo("Generating and installing [{0}] proxy business service objects...",
					managedBusinessServiceConfigList.size());
			for (UnifyComponentConfig unifyComponentConfig : managedBusinessServiceConfigList) {
				Map<String, List<UnifyPluginInfo>> pluginMap = allPluginsBySocketMap
						.get(unifyComponentConfig.getName());
				if (pluginMap == null) {
					pluginMap = Collections.emptyMap();
				}
				UnifyComponentConfig proxyUnifyComponentConfig = generateInstallBusinessServiceProxyObjects(
						unifyComponentConfig, pluginMap);
				InternalUnifyComponentInfo iuc = getInternalUnifyComponentInfo(proxyUnifyComponentConfig.getName());
				iuc.setUnifyComponentConfig(proxyUnifyComponentConfig);
			}

			// Set proxy broadcast methods
			logInfo("Setting broadcast proxy methods...");
			for (BroadcastInfo broadcastInfo : broadcastInfoMap.values()) {
				InternalUnifyComponentInfo iuc = getInternalUnifyComponentInfo(broadcastInfo.getComponentName());
				Method method = broadcastInfo.isNoParams()
						? ReflectUtils.getMethod(iuc.getType(), broadcastInfo.getMethodName())
						: ReflectUtils.getMethod(iuc.getType(), broadcastInfo.getMethodName(), String[].class);
				broadcastInfo.setMethod(method);
			}

			logInfo("Generation and installation of [{0}] proxy objects completed.",
					managedBusinessServiceConfigList.size());

			// Cluster manager
			lockManager = (LockManager) getComponent(ApplicationComponents.APPLICATION_LOCKMANAGER);
			clusterService = (ClusterService) getComponent(ApplicationComponents.APPLICATION_CLUSTERSERVICE);
			userSessionManager = (UserSessionManager) getComponent(
					ApplicationComponents.APPLICATION_USERSESSIONMANAGER);

			// Initialize utilities
			ImageUtils.scanForPlugins();
			DataUtils.registerDefaultFormatters((DateTimeFormatter) getUplComponent(getApplicationLocale(),
					"!fixeddatetimeformat pattern:$s{yyyy-MM-dd HH:mm:ss.SSS}", false));

			// Run application startup service
			toConsole("Initializing application boot service...");
			String bootComponentName = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_BOOT);
			if (bootComponentName == null) {
				bootComponentName = ApplicationComponents.APPLICATION_DEFAULTBOOTSERVICE;
			}
			applicationBootService = (BootService) getComponent(bootComponentName);
			applicationBootService.startup();

			toConsole("Application boot service initialization completed.");

			// Initialize interfaces
			logInfo("Initializing container interfaces...");
			initializeInterfaces();
			logInfo("Container interfaces initialization complete.");

			// Schedule periodic tasks
			logInfo("Scheduling periodic tasks...");
			getComponent(PeriodicExecutionTaskConstants.PERIODIC_METHOD_TASK);
			Random random = new Random();
			TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
			for (Map.Entry<String, Map<String, PeriodicInfo>> componentEntry : componentPeriodMethodMap.entrySet()) {
				logInfo("Intializing component [{0}] with periodic methods...", componentEntry.getKey());
				getComponent(componentEntry.getKey());
				for (Map.Entry<String, PeriodicInfo> periodicEntry : componentEntry.getValue().entrySet()) {
					PeriodicInfo pin = periodicEntry.getValue();
					// Skip if periodic should run in cluster mode and container is not in cluster
					// mode
					if (pin.isClusterOnly() && !clusterMode) {
						continue;
					}

					PeriodicType periodicType = pin.getType();
					TaskMonitor taskMonitor = taskManager.schedulePeriodicExecution(periodicType,
							componentEntry.getKey(), periodicEntry.getKey(),
							UnifyCoreConstants.PERIODIC_EXECUTION_INITIAL_DELAY_SECONDS * 1000
									+ (random.nextInt() % 1000));
					periodicTaskMonitorList.add(taskMonitor);
				}
			}
			logInfo("Periodic task scheduling completed.");

			ApplicationAttributeProvider applicationAttributeProvider = getComponent(
					ApplicationAttributeProvider.class);
			AlternativePrivilegeProvider privilegeNameProvider = isComponent(AlternativePrivilegeProvider.class)
					? getComponent(AlternativePrivilegeProvider.class)
					: null;
			RolePrivilegeManager rolePrivilegeManager = isComponent(RolePrivilegeManager.class)
					? getComponent(RolePrivilegeManager.class)
					: null;
			applicationContext.setAttributeProvider(applicationAttributeProvider);
			applicationContext.setAltPrivilegeNameProvider(privilegeNameProvider);
			applicationContext.setRolePrivilegeManager(rolePrivilegeManager);

			// Open container interfaces to start servicing requests
			openInterfaces();

			// Start command processing thread
			new CommandThread().start();

			// Set start time to now
			startTime = new Date();

			// Container initialization completed
			long startupTimeMillis = startTime.getTime() - startTimeMillis;
			toConsole("Container initialization completed in " + startupTimeMillis + "ms.");
		} catch (UnifyException ue) {
			logError(ue);
			throw ue;
		}
	}

	/**
	 * Shuts down the container.
	 */
	public void shutdown() {
		if (!shutdown) {
			shutdown = true;
			logInfo("Shutting down container...");

			try {
				closeInterfaces();
			} catch (Exception e) {
			}

			for (TaskMonitor taskMonitor : new ArrayList<TaskMonitor>(periodicTaskMonitorList)) {
				taskMonitor.cancel();
				while (taskMonitor.isRunning()) {
					ThreadUtils.sleep(1000);
				}
			}

			try {
				applicationBootService.shutdown();
			} catch (Exception e) {
			}

			for (UnifyComponent unifyComponent : new ArrayList<UnifyComponent>(singletonTerminationList)) {
				try {
					unifyComponent.terminate();
				} catch (Exception e) {
				}
			}

			singletonTerminationList = null;
			singletonComponentMap = null;
			internalUnifyComponentInfos = null;
			componentContextMap = null;

			logInfo("Container shutdown completed.");
		}
	}

	/**
	 * Gets the container's access key.
	 * 
	 * @return the access key
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * Gets current container information.
	 * 
	 * @return the container information object
	 */
	public UnifyContainerInfo getInfo() throws UnifyException {
		List<UnifyComponentInfo> componentInfoList = new ArrayList<UnifyComponentInfo>();
		for (InternalUnifyComponentInfo uici : internalUnifyComponentInfos.values()) {
			List<Setting> settingInfoList = new ArrayList<Setting>();
			UnifyComponentSettings unifyComponentSettings = uici.unifyComponentConfig.getSettings();
			for (String name : unifyComponentSettings.getPropertyNames()) {
				settingInfoList.add(unifyComponentSettings.getSetting(name));
			}

			componentInfoList.add(new UnifyComponentInfo(uici.getName(), uici.getOriginalType().getName(),
					uici.getPassCount(), uici.getFailCount(), settingInfoList));
		}

		List<UnifyInterfaceInfo> interfaceInfoList = new ArrayList<UnifyInterfaceInfo>();
		for (UnifyContainerInterface ui : interfaces) {
			interfaceInfoList.add(new UnifyInterfaceInfo(ui.getName(), ui.getPort(), ui.isServicingRequests()));
		}

		List<UnifyContainerSettingInfo> settingInfoList = new ArrayList<UnifyContainerSettingInfo>();
		for (Map.Entry<String, Object> entry : unifySettings.entrySet()) {
			settingInfoList.add(new UnifyContainerSettingInfo(entry.getKey(), entry.getValue()));
		}

		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long usedMemory = totalMemory - runtime.freeMemory();
		return new UnifyContainerInfo((String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_NAME), nodeId,
				deploymentVersion, auxiliaryVersion, getApplicationLocale().toString(), hostAddress, hostHome,
				startTime, usedMemory, totalMemory, clusterMode, productionMode, deploymentMode, componentInfoList,
				interfaceInfoList, settingInfoList);
	}

	/**
	 * Receives and processes a command.
	 * 
	 * @param command the command to process
	 * @param params  the command parameters
	 * @throws UnifyException if an error occurs
	 */
	public void command(String command, String... params) throws UnifyException {
		try {
			containerCommandQueue.offer(new ContainerCommand(command, params));
		} catch (ClassCastException e) {
			throw new UnifyOperationException(e, "Unify Container");
		}
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	/**
	 * Gets all component names with types that extend or implement specific
	 * component type.
	 * 
	 * @param componentType the component type to match
	 * @return a list of component names that match with supplied type.
	 * @throws UnifyException if container is not started.
	 */
	public List<String> getComponentNames(Class<? extends UnifyComponent> componentType) throws UnifyException {
		// checkStarted();
		List<String> names = new ArrayList<String>();
		for (InternalUnifyComponentInfo iuc : internalUnifyComponentInfos.values()) {
			if (componentType.isAssignableFrom(iuc.getType())) {
				names.add(iuc.getName());
			}
		}
		return names;
	}

	/**
	 * Gets component configuration by name.
	 * 
	 * @param name the component name
	 * @return the component configuration if found, otherwise null.
	 * @throws UnifyException if container is not started. If an error occurs
	 */
	public UnifyComponentConfig getComponentConfig(String name) throws UnifyException {
		checkStarted();
		InternalUnifyComponentInfo iuci = getInternalUnifyComponentInfo(name);
		if (iuci != null) {
			return iuci.getUnifyComponentConfig();
		}
		return null;
	}

	/**
	 * Gets all component configurations with types that extend or implement
	 * specific component type.
	 * 
	 * @param componentType the component types to match
	 * @return a list of component configurations that match with supplied type.
	 * @throws UnifyException if container is not started.
	 */
	public List<UnifyComponentConfig> getComponentConfigs(Class<? extends UnifyComponent> componentType)
			throws UnifyException {
		checkStarted();
		List<UnifyComponentConfig> configList = new ArrayList<UnifyComponentConfig>();
		for (InternalUnifyComponentInfo iuc : internalUnifyComponentInfos.values()) {
			if (componentType.isAssignableFrom(iuc.getType())) {
				configList.add(iuc.getUnifyComponentConfig());
			}
		}

		return configList;
	}

	/**
	 * Fetches all component instances of a specific type.
	 * 
	 * @param componentType the component type
	 * @return the list of components.
	 * @throws UnifyException if an error occurs
	 */
	@SuppressWarnings("unchecked")
	protected <T extends UnifyComponent> List<T> getComponents(Class<T> componentType) throws UnifyException {
		List<T> componentList = new ArrayList<T>();
		for (InternalUnifyComponentInfo iuc : internalUnifyComponentInfos.values()) {
			if (componentType.isAssignableFrom(iuc.getType())) {
				componentList.add((T) getComponent(iuc.getName()));
			}
		}

		return componentList;
	}

	/**
	 * Gets a UPL component with specified descriptor.
	 * 
	 * @param locale     the locale
	 * @param descriptor the UPL descriptor
	 * @param cached     the cached flag.
	 * @return If the cached flag is set, the container returns a cached instance
	 *         with the same descriptor and locale if found. Otherwise, a new
	 *         instance is returned.
	 * @throws UnifyException if an error occurs
	 */
	public UplComponent getUplComponent(Locale locale, String descriptor, boolean cached) throws UnifyException {
		if (cached) {
			return cachedLocaleUplComponentMap.get(locale, descriptor);
		}

		UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(locale, descriptor);
		UplComponent uplComponent = (UplComponent) getComponent(uplElementAttributes.getComponentName(), null,
				uplElementAttributes);
		return uplComponent;
	}

	/**
	 * Gets a UPL component using supplied attributes key.
	 * 
	 * @param locale        the component locale
	 * @param attributesKey the UPL element attributes key
	 * @return the UPL component
	 * @throws UnifyException if container is not started. If component with name is
	 *                        unknown. If component instantiation error occurs.
	 */
	public UplComponent getUplComponent(Locale locale, String attributesKey) throws UnifyException {
		UplElementAttributes uplElementAttributes = uplCompiler.getUplElementAttributes(locale, attributesKey);
		UplComponent uplComponent = (UplComponent) getComponent(uplElementAttributes.getComponentName(), null,
				uplElementAttributes);
		return uplComponent;
	}

	/**
	 * Returns classes of a particular type annotated with a specific type of
	 * annotation.
	 * 
	 * @param classType       the annotated class type
	 * @param annotationClass the annotation
	 * @param packages        packages to restrict search to. This parameter is
	 *                        optional.
	 * @return list of annotated classes
	 * @throws UnifyException if an error occurs
	 */
	public <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
			Class<? extends Annotation> annotationClass, String... packages) throws UnifyException {
		return unifyContainerEnvironment.getTypeRepository().getAnnotatedClasses(classType, annotationClass, packages);
	}

	/**
	 * Returns classes of a particular type annotated with a specific type of
	 * annotation.
	 * 
	 * @param classType        the annotated class type
	 * @param annotationClass  the annotation
	 * @param excludedPackages packages to exclude search from. This parameter is
	 *                         optional.
	 * @return list of annotated classes
	 * @throws UnifyException if an error occurs
	 */
	public <T> List<Class<? extends T>> getAnnotatedClassesExcluded(Class<T> classType,
			Class<? extends Annotation> annotationClass, String... excludedPackages) throws UnifyException {
		return unifyContainerEnvironment.getTypeRepository().getAnnotatedClassesExcluded(classType, annotationClass,
				excludedPackages);
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getRuntimeId() {
		return runtimeId;
	}

	public String getInstanceCode() {
		return (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_CODE);
	}

	public String getInstanceName() {
		return (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_NAME);
	}

	public String getDeploymentVersion() {
		return deploymentVersion;
	}

	public String getAuxiliaryVersion() {
		return auxiliaryVersion;
	}

	public short getPreferredPort() {
		return preferredPort;
	}

	public List<UnifyStaticSettings> getStaticSettings() {
		return staticSettings;
	}

	public Object getSetting(String name) {
		return unifySettings.get(name);
	}

	public String getWorkingPath() {
		return unifyContainerEnvironment.getWorkingPath();
	}

	public String getWorkingPathFilename(String relativeFilename) throws UnifyException {
		return unifyContainerEnvironment.getWorkingPathFilename(relativeFilename);
	}

	public boolean isClusterMode() {
		return clusterMode;
	}

	public boolean isProductionMode() {
		return productionMode;
	}

	public boolean isDeploymentMode() {
		return deploymentMode;
	}

	public boolean isInterfacesOpen() {
		return interfacesOpen;
	}

	public RequestContextManager getRequestContextManager() {
		return requestContextManager;
	}

	public ResourceBundles getMessages() {
		return messages;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Gets a component by type.
	 * 
	 * @param componentType the component type
	 * @return the component
	 * @throws UnifyException if container is not started. If component of type
	 *                        unknown. If multiple implementations of type is found.
	 *                        If component instantiation error occurs.
	 */
	@SuppressWarnings("unchecked")
	public <T extends UnifyComponent> T getComponent(Class<T> componentType) throws UnifyException {
		List<UnifyComponentConfig> configs = getComponentConfigs(componentType);
		if (configs.isEmpty()) {
			throw new UnifyException(UnifyCoreErrorConstants.NO_IMPLEMENTATION_OF_TYPE_FOUND, componentType.toString());
		}

		UnifyComponentConfig config = configs.get(0);
		if (configs.size() > 1) {
			int preferred = 0;
			for (final UnifyComponentConfig _config : configs) {
				if (_config.getType().isAnnotationPresent(Preferred.class)) {
					config = _config;
					preferred++;
				}
			}

			if (preferred != 1) {
				throw new UnifyException(UnifyCoreErrorConstants.MULTIPLE_IMPLEMENTATIONS_OF_TYPE_FOUND,
						componentType.toString());
			}
		}

		return (T) getComponent(config.getName(), null, null);
	}

	/**
	 * Gets a component by name. If a component is configured as a singleton, this
	 * method would always return the same instance for specified name, otherwise a
	 * new component instance is always created.
	 * 
	 * @param name the component name
	 * @return the component
	 * @throws UnifyException if container is not started. If component with name is
	 *                        unknown. If component instantiation error occurs.
	 */
	public UnifyComponent getComponent(String name) throws UnifyException {
		return getComponent(name, null, null);
	}

	/**
	 * Gets a component by name using alternate settings. Applies to non-singletons
	 * only..
	 * 
	 * @param name        the component name
	 * @param altSettings the alternate settings
	 * @return the component
	 * @throws UnifyException if container is not started. If component with name is
	 *                        unknown. If component is a singleton. If component
	 *                        instantiation error occurs.
	 */
	public UnifyComponent getComponent(String name, Setting... altSettings) throws UnifyException {
		return getComponent(name, new UnifyComponentSettings(altSettings), null);
	}

	/**
	 * Gets a component by name using alternate settings. Applies to non-singletons
	 * only..
	 * 
	 * @param name        the component name
	 * @param altSettings the alternate settings
	 * @return the component
	 * @throws UnifyException if container is not started. If component with name is
	 *                        unknown. If component is a singleton. If component
	 *                        instantiation error occurs.
	 */
	public UnifyComponent getComponent(String name, UnifyComponentSettings altSettings) throws UnifyException {
		return getComponent(name, altSettings, null);
	}

	/**
	 * Checks if component with name is defined in container.
	 * 
	 * @param name the component name
	 * @return a true value if component with name exists otherwise false
	 * @throws UnifyException If component an error occurs.
	 */
	public boolean isComponent(String name) throws UnifyException {
		return internalResolutionMap.containsKey(name) || internalUnifyComponentInfos.containsKey(name)
				|| aliases.containsKey(name);
	}

	/**
	 * Checks if component with name is defined in container.
	 * 
	 * @param componentType the component type
	 * @return a true value if component type exists otherwise false
	 * @throws UnifyException If component an error occurs.
	 */
	public boolean isComponent(Class<? extends UnifyComponent> componentType) throws UnifyException {
		List<UnifyComponentConfig> configs = getComponentConfigs(componentType);
		return !configs.isEmpty();
	}

	/**
	 * Checks if lock is locked.
	 * 
	 * @param lockName the lock name
	 * @return true if locked otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean isLocked(String lockName) throws UnifyException {
		return lockManager.isLocked(lockName);
	}

	/**
	 * Grabs lock if available.
	 * 
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean tryGrabLock(String lockName) throws UnifyException {
		return lockManager.tryGrabLock(lockName);
	}

	/**
	 * Grabs lock with no timeout.
	 * 
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean grabLock(String lockName) throws UnifyException {
		return lockManager.grabLock(lockName);
	}

	/**
	 * Grabs lock. Waits for lock to be available.
	 * 
	 * @param lockName the lock name
	 * @param timeout  the timeout (no timeout if negetive or zero)
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean grabLock(String lockName, long timeout) throws UnifyException {
		return lockManager.grabLock(lockName, timeout);
	}

	/**
	 * Releases lock.
	 * 
	 * @param lockName the lock name
	 * @throws UnifyException if an error occurs
	 */
	public void releaseLock(String lockName) throws UnifyException {
		lockManager.releaseLock(lockName);
	}

	/**
	 * Broadcasts a cluster command to other nodes.
	 * 
	 * @param command the command to broadcast
	 * @param params  the command parameters
	 * @throws UnifyException if an error occurs
	 */
	public void broadcastToOtherNodes(String command, String... params) throws UnifyException {
		clusterService.broadcastToOtherNodes(command, params);
	}

	/**
	 * Broadcasts attribute to all sessions in this node.
	 * 
	 * @param name  the attribute name
	 * @param value the attribute value. A null value clears attribute.
	 * @throws UnifyException if an error occurs
	 */
	public void broadcastToSessions(String name, Object value) throws UnifyException {
		userSessionManager.broadcast(name, value);
	}

	/**
	 * Broadcasts attribute to specific application session context in this node.
	 * 
	 * @param sessionId the session ID
	 * @param name      the attribute name
	 * @param value     the attribute value. A null value clears attribute.
	 * @throws UnifyException if an error occurs
	 */
	public void broadcastToSession(String sessionId, String name, Object value) throws UnifyException {
		userSessionManager.broadcast(sessionId, name, value);
	}

	private UnifyComponent getComponent(String name, UnifyComponentSettings altSettings,
			UplElementAttributes uplElementAttributes) throws UnifyException {
		checkStarted();
		return createOrRetrieveComponent(name, altSettings, uplElementAttributes).getUnifyComponent();
	}

	@SuppressWarnings("unchecked")
	private <T> Map<String, T> createOrRetrieveComponents(Class<T> type, String[] names) throws UnifyException {
		Map<String, T> map = new LinkedHashMap<String, T>();
		for (String name : names) {
			UnifyComponent component = createOrRetrieveComponent(name, null, null).getUnifyComponent();
			if (!type.isAssignableFrom(component.getClass())) {
				throw new UnifyException(UnifyCoreErrorConstants.FIELD_INJECTION_INCOMPATIBLE, type,
						component.getClass());
			}

			map.put(name, (T) component);
		}

		return map;
	}

	private UnifyComponentInst createOrRetrieveComponent(String name, UnifyComponentSettings altSettings,
			UplElementAttributes uplElementAttributes) throws UnifyException {
		UnifyComponentInst inst = null;
		try {
			InternalUnifyComponentInfo iuci = getInternalUnifyComponentInfo(name);
			if (iuci == null) {
				// If supplied name is alias, get actual name and fetch configuration
				String actualName = aliases.get(name);
				if (actualName != null) {
					iuci = getInternalUnifyComponentInfo(actualName);
				}
			}

			if (iuci == null) {
				throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_UNKNOWN_COMP, name);
			}

			boolean instExist = false;
			if (iuci.isSingleton()) {
				if (altSettings != null) {
					throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_ALTSETTINGS_SINGLETON, name);
				}

				instExist = singletonComponentMap.isKey(iuci.getName());
				inst = singletonComponentMap.get(iuci.getName(), iuci);
			} else {
				if (altSettings != null) {
					// Validate alternate settings
					UnifyComponentSettings settings = iuci.getSettings();
					for (String property : altSettings.getPropertyNames()) {
						if (!settings.isProperty(property)) {
							throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_ALTSETTINGS_UNKNOWN_PROPERTY,
									name, property);
						}
					}
				}

				UnifyComponent unifyComponent = iuci.getType().getDeclaredConstructor().newInstance();
				inst = new UnifyComponentInst(iuci, unifyComponent);
			}

			if (!instExist && !inst.isInitialized()) {
				injectProperties(inst, altSettings, uplElementAttributes);
				inst.initialize(componentContextMap.get(iuci.getName()));
			}
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, name);
		}

		return inst;
	}

	private void injectProperties(UnifyComponentInst inst, UnifyComponentSettings altSettings,
			UplElementAttributes uplElementAttributes) throws UnifyException {
		boolean success = false;
		try {
			UnifyComponent unifyComponent = inst.getUnifyComponent();
			for (InternalInjectInfo internalInjectInfo : inst.getIuci().getInjectInfoList()) {
				final Field field = internalInjectInfo.getField();
				InjectType type = internalInjectInfo.getType();
				String[] configValues = null;
				Object valToInject = null;
				if (altSettings != null) {
					configValues = getPropertyConfig(altSettings, field);
					if (InjectType.DATA_CONVERTED.equals(type)) {
						valToInject = DataUtils.convert(field.getType(), configValues);
					}
				}

				if (configValues == null) {
					configValues = internalInjectInfo.getConfigValues();
					if (InjectType.DATA_CONVERTED.equals(type)) {
						valToInject = internalInjectInfo.getValueToInject();
					}
				}

				if (configValues != null) {
					Class<? extends UnifyComponent> fieldUnifyComponentType = internalInjectInfo
							.getUnifyComponentType();
					switch (type) {
					case COMPONENT_ARRAY:
						Object[] tempArray = createOrRetrieveComponents(fieldUnifyComponentType, configValues).values()
								.toArray();
						valToInject = Array.newInstance(fieldUnifyComponentType, tempArray.length);
						for (int i = 0; i < tempArray.length; i++) {
							Array.set(valToInject, i, tempArray[i]);
						}
						break;
					case COMPONENT_COLLECTION:
						Collection<Object> c = new ArrayList<Object>();
						c.addAll(createOrRetrieveComponents(fieldUnifyComponentType, configValues).values());
						valToInject = c;
						break;
					case COMPONENT_INST:
						valToInject = createOrRetrieveComponent(configValues[0], null, null).getUnifyComponent();
						break;
					case COMPONENT_MAP:
						valToInject = createOrRetrieveComponents(fieldUnifyComponentType, configValues);
						break;
					case DATA_CONVERTED:
					default:
						break;
					}
				}

				// Actual injection
				if (valToInject != null) {
					if (internalInjectInfo.isWithSetter()) { // Setters have priority
						internalInjectInfo.getSetterInfo().getSetter().invoke(unifyComponent, valToInject);
					} else if (internalInjectInfo.isFieldAccessible()) {
						internalInjectInfo.getField().set(unifyComponent, valToInject);
					}
				}
			}

			// Set UPL attributes if necessary
			if (uplElementAttributes != null) {
				((UplComponent) unifyComponent).setUplAttributes(uplElementAttributes);
			}

			success = true;
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INITIALIZATION_ERROR,
					inst.getIuci().getName());
		} finally {
			if (success) {
				inst.getIuci().incrementPassCount();
			} else {
				inst.getIuci().incrementFailCount();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private UnifyComponentConfig generateInstallBusinessServiceProxyObjects(UnifyComponentConfig businessLogicConfig,
			Map<String, List<UnifyPluginInfo>> pluginMap) throws UnifyException {
		ProxyBusinessServiceGenerator bspg = (ProxyBusinessServiceGenerator) this
				.getComponent(ApplicationComponents.APPLICATION_PROXYBUSINESSSERVICEGENERATOR);
		Class<? extends BusinessService> proxyClazz = bspg.generateCompileLoadProxyBusinessServiceClass(
				businessLogicConfig.getName(), (Class<? extends BusinessService>) businessLogicConfig.getType(),
				pluginMap);
		UnifyComponentConfig internalComponentConfig = new UnifyComponentConfig(businessLogicConfig.getSettings(),
				businessLogicConfig.getName(), businessLogicConfig.getDescription(), proxyClazz,
				businessLogicConfig.isSingleton());
		return internalComponentConfig;
	}

	private String[] resolveConfigValue(Object val) throws UnifyException {
		if (val != null) {
			List<String> names = new ArrayList<String>();
			if (val instanceof String[]) {
				for (String name : (String[]) val) {
					resolveConfig(names, name);
				}
			} else if (val instanceof String) {
				resolveConfig(names, (String) val);
			} else {
				names.add(String.valueOf(val));
			}

			return names.toArray(new String[names.size()]);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void resolveConfig(List<String> names, String value) throws UnifyException {
		if (TokenUtils.isComponentListToken(value)) {
			names.addAll(getComponentNames((Class<? extends UnifyComponent>) ReflectUtils
					.classForName(TokenUtils.extractTokenValue(value).trim())));
		} else {
			names.add(TokenUtils.getStringTokenValue(value));
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeInterfaces() throws UnifyException {
		// Initialize command interface if flagged
		if (DataUtils.convert(boolean.class,
				unifySettings.get(UnifyCorePropertyConstants.APPLICATION_COMMAND_INTERFACE))) {
			interfaces.add((UnifyContainerInterface) getComponent("unify-commandinterface"));
		}

		// Initialize other interfaces
		List<String> interfacesList = DataUtils.convert(ArrayList.class, String.class,
				unifySettings.get(UnifyCorePropertyConstants.APPLICATION_INTERFACES));
		if (interfacesList != null) {
			for (String interfaceName : interfacesList) {
				interfaces.add((UnifyContainerInterface) getComponent(interfaceName));
			}
		}
	}

	private void openInterfaces() throws UnifyException {
		toConsole("Opening container interfaces to start servicing requests...");

		for (UnifyContainerInterface unifyContainerInterface : interfaces) {
			toConsole("Opening '" + unifyContainerInterface.getName() + "' on port " + unifyContainerInterface.getPort()
					+ "...");

			unifyContainerInterface.startServicingRequests();
		}

		interfacesOpen = true;
		toConsole("Container interfaces opened.");
	}

	private void closeInterfaces() throws UnifyException {
		toConsole("Closing container interfaces...");
		for (UnifyContainerInterface unifyContainerInterface : interfaces) {
			toConsole("Closing interface '" + unifyContainerInterface.getName() + "'...");

			unifyContainerInterface.stopServicingRequests();
		}

		interfacesOpen = false;
		toConsole("Container interfaces closed..");
	}

	@SuppressWarnings("unchecked")
	private void initializeContainerMessages() throws UnifyException {
		List<String> messageBaseList = new ArrayList<String>();
		for (UnifyStaticSettings unifyStaticSettings : staticSettings) {
			String messageBase = unifyStaticSettings.getMessageBase();
			if (StringUtils.isNotBlank(messageBase)) {
				messageBaseList.add(messageBase);
			}
		}

		List<String> cfgMessageBaseList = DataUtils.convert(ArrayList.class, String.class,
				unifySettings.get(UnifyCorePropertyConstants.APPLICATION_MESSAGES_BASE));
		if (cfgMessageBaseList != null) {
			messageBaseList.addAll(cfgMessageBaseList);
		}

		messages = new ResourceBundles(messageBaseList);
	}

	public List<String> getApplicationBanner() throws UnifyException {
		String filename = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_BANNER);
		if (StringUtils.isBlank(filename)) {
			filename = UnifyCoreConstants.DEFAULT_APPLICATION_BANNER;
		}

		return IOUtils.readFileResourceLines(filename, unifyContainerEnvironment.getWorkingPath());
	}

	public Locale getApplicationLocale() throws UnifyException {
		if (applicationLocale == null) {
			synchronized (this) {
				if (applicationLocale == null) {
					String languageTag = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOCALE);
					if (StringUtils.isNotBlank(languageTag)) {
						applicationLocale = Locale.forLanguageTag(languageTag);
					} else {
						applicationLocale = Locale.getDefault();
					}
				}
			}
		}

		return applicationLocale;
	}

	public TimeZone getApplicationTimeZone() throws UnifyException {
		if (applicationTimeZone == null) {
			String timeZone = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_TIMEZONE);
			if (StringUtils.isNotBlank(timeZone)) {
				applicationTimeZone = TimeZone.getTimeZone(timeZone);
			} else {
				applicationTimeZone = TimeZone.getDefault();
			}
		}

		return applicationTimeZone;
	}

	public boolean isApplicationIgnoreViewDirective() throws UnifyException {
		return applicationIgnoreViewDirective;
	}

	private void checkStarted() throws UnifyException {
		if (!started) {
			throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_NOT_INITIALIZED);
		}
	}

	private void toConsole(String msg) throws UnifyException {
		if (toConsole) {
			System.out.println(msg);
		}

		logInfo(msg);
	}

	private Logger getLogger(String name) throws UnifyException {
		UnifyComponentConfig unifyComponentConfig = getInternalUnifyComponentInfo(name).getUnifyComponentConfig();
		if (unifyComponentConfig == null) {
			throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_UNKNOWN_COMP, name);
		}

		return new UnifyContainerLoggerImpl(unifyComponentConfig.getType());
	}

	private InternalUnifyComponentInfo getInternalUnifyComponentInfo(String name) {
		String internalName = internalResolutionMap.get(name);
		if (internalName != null) {
			return internalUnifyComponentInfos.get(internalName);
		}

		return internalUnifyComponentInfos.get(name);
	}

	private void logInfo(String message, Object... params) {
		try {
			if (params.length == 0) {
				logger.log(LoggingLevel.INFO, message);
			} else {
				logger.log(LoggingLevel.INFO, MessageFormat.format(message, params));
			}
		} catch (Exception e) {
		}
	}

	private void logDebug(String message, Object... params) {
		try {
			if (params.length == 0) {
				logger.log(LoggingLevel.DEBUG, message);
			} else {
				logger.log(LoggingLevel.DEBUG, MessageFormat.format(message, params));
			}
		} catch (Exception e) {
		}
	}

	private void logError(Exception e) {
		try {
			logger.log(LoggingLevel.ERROR, null, e);
		} catch (Exception ex) {
		}
	}

	private class PeriodicInfo {

		private PeriodicType type;

		private boolean clusterOnly;

		public PeriodicInfo(PeriodicType type, boolean clusterOnly) {
			this.type = type;
			this.clusterOnly = clusterOnly;
		}

		public PeriodicType getType() {
			return type;
		}

		public boolean isClusterOnly() {
			return clusterOnly;
		}
	}

	private class CommandThread extends Thread {
		public CommandThread() {
			super("Container command thread - " + nodeId);
		}

		@Override
		public void run() {
			while (!shutdown) {
				try {
					if (clusterMode) {
						// Handle cluster commands
						requestContextManager.getRequestContext()
								.setAttribute(UnifyCoreRequestAttributeConstants.SUPPRESS_BROADCAST, Boolean.TRUE);
						List<Command> clusterCommandList = clusterService.getClusterCommands();
						for (Command clusterCommand : clusterCommandList) {
							BroadcastInfo broadcastInfo = broadcastInfoMap.get(clusterCommand.getCommand());
							if (broadcastInfo != null) {
								if (broadcastInfo.isNoParams()) {
									broadcastInfo.getMethod().invoke(getComponent(broadcastInfo.getComponentName()));
								} else {
									List<String> params = clusterCommand.getParams();
									broadcastInfo.getMethod().invoke(getComponent(broadcastInfo.getComponentName()),
											new Object[] { params.toArray(new String[params.size()]) });
								}
							}
						}
						requestContextManager.getRequestContext()
								.setAttribute(UnifyCoreRequestAttributeConstants.SUPPRESS_BROADCAST, Boolean.FALSE);
					}

					// Handle commands from interface port
					ContainerCommand cc = containerCommandQueue.poll();
					if (cc != null) {
						String command = cc.getCommand();
						if ("shutdown".equalsIgnoreCase(command)) {
							shutdown();
						}
					}

					ThreadUtils.sleep(UnifyCoreConstants.COMMAND_THREAD_RATE_SECONDS * 1000);
				} catch (Exception e) {
					logError(e);
				}
			}
		}
	}

	private static class ContainerCommand {

		private String command;

		private String[] params;

		public ContainerCommand(String command, String[] params) {
			this.command = command;
			this.params = params;
		}

		public String getCommand() {
			return command;
		}

		@SuppressWarnings("unused")
		public String[] getParams() {
			return params;
		}
	}

	private static class BroadcastInfo {

		private final String componentName;

		private final String methodName;

		private final boolean noParams;

		private Method method;

		public BroadcastInfo(String componentName, String methodName, boolean noParams) {
			this.componentName = componentName;
			this.methodName = methodName;
			this.noParams = noParams;
		}

		public String getComponentName() {
			return componentName;
		}

		public String getMethodName() {
			return methodName;
		}

		public boolean isNoParams() {
			return noParams;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}
	}

	@Component("UnifyContainerLogger")
	private class UnifyContainerLoggerImpl extends AbstractLog4jLogger {

		public UnifyContainerLoggerImpl(Class<?> clazz) throws UnifyException {
			getLogger(clazz.getName());
		}

		@Override
		protected void onInitialize() throws UnifyException {

		}

		@Override
		protected void onTerminate() throws UnifyException {

		}
	}

	@SuppressWarnings("unchecked")
	private void buildPropertyInjectionInfo(InternalUnifyComponentInfo iuci) throws UnifyException {
		final Class<? extends UnifyComponent> clazz = iuci.getType();
		List<InternalInjectInfo> injectInfoList = new ArrayList<InternalInjectInfo>();
		UnifyComponentSettings settings = iuci.getSettings();
		for (String property : settings.getPropertyNames()) {
			final Field field = ReflectUtils.getField(clazz, property);
			Class<?> argumentType0 = ReflectUtils.getArgumentType(field.getGenericType(), 0);
			Class<?> argumentType1 = ReflectUtils.getArgumentType(field.getGenericType(), 1);
			String[] configValues = getPropertyConfig(settings, field);
			InjectType type = InjectType.DATA_CONVERTED;
			Class<? extends UnifyComponent> unifyComponentType = null;
			Class<?> fieldClass = field.getType();
			if (fieldClass.isArray()) {
				Class<?> arrFieldClass = fieldClass.getComponentType();
				if (UnifyComponent.class.isAssignableFrom(arrFieldClass)) {
					type = InjectType.COMPONENT_ARRAY;
					unifyComponentType = (Class<? extends UnifyComponent>) arrFieldClass;
				}
			} else if (Collection.class.isAssignableFrom(fieldClass)) {
				Class<?> colFieldClass = argumentType0;
				if (UnifyComponent.class.isAssignableFrom(colFieldClass)) {
					type = InjectType.COMPONENT_COLLECTION;
					unifyComponentType = (Class<? extends UnifyComponent>) colFieldClass;
				}
			} else if (Map.class.isAssignableFrom(fieldClass)) {
				Class<?> keyFieldClass = argumentType0;
				if (String.class.equals(keyFieldClass)) {
					Class<?> valFieldClass = argumentType1;
					if (UnifyComponent.class.isAssignableFrom(valFieldClass)) {
						type = InjectType.COMPONENT_MAP;
						unifyComponentType = (Class<? extends UnifyComponent>) valFieldClass;
					}
				}
			} else {
				if (UnifyComponent.class.isAssignableFrom(fieldClass)) {
					type = InjectType.COMPONENT_INST;
					unifyComponentType = (Class<? extends UnifyComponent>) fieldClass;
				}
			}

			final GetterSetterInfo setterInfo = ReflectUtils.isSettableField(clazz, property)
					? ReflectUtils.getSetterInfo(clazz, property)
					: null;
			injectInfoList.add(new InternalInjectInfo(type, field, setterInfo, configValues, unifyComponentType));
		}

		iuci.setInjectInfoList(injectInfoList);
	}

	private String[] getPropertyConfig(UnifyComponentSettings settings, Field field) throws UnifyException {
		Object value = settings.getSettingValue(field.getName());
		if (value == null && settings.isAutoInject(field.getName())) {
			if (UnifyComponent.class.isAssignableFrom(field.getType())) {
				List<String> names = namelessConfigurableSuggestions.get(field.getType());
				if (names.size() == 1) { // Check perfect suggestion
					value = names.get(0);
				} else if (names.size() > 1) {
					// TODO throw exception to many possible types to inject
				}
			}
		}

		return resolveConfigValue(value);
	}

	private static class UnifyComponentInst {

		private final InternalUnifyComponentInfo iuci;

		private final UnifyComponent unifyComponent;

		public UnifyComponentInst(InternalUnifyComponentInfo iuci, UnifyComponent unifyComponent) {
			this.iuci = iuci;
			this.unifyComponent = unifyComponent;
		}

		public InternalUnifyComponentInfo getIuci() {
			return iuci;
		}

		public UnifyComponent getUnifyComponent() {
			return unifyComponent;
		}

		public boolean isInitialized() {
			return unifyComponent.isInitialized();
		}

		public void initialize(UnifyComponentContext ctx) throws UnifyException {
			if (!unifyComponent.isInitialized()) {
				synchronized (unifyComponent) {
					if (!unifyComponent.isInitialized()) {
						unifyComponent.initialize(ctx);
					}
				}
			}
		}

		@Override
		public String toString() {
			return "{name = " + iuci.getName() + ", initialized = " + unifyComponent.isInitialized() + "}";
		}
	}

	private static class InternalUnifyComponentInfo {

		private UnifyComponentConfig unifyComponentConfig;

		private final Class<? extends UnifyComponent> originalType;

		private List<InternalInjectInfo> injectInfoList;

		private int passCount;

		private int failCount;

		public InternalUnifyComponentInfo(UnifyComponentConfig unifyComponentConfig) {
			this.unifyComponentConfig = unifyComponentConfig;
			this.originalType = unifyComponentConfig.getType();
		}

		public UnifyComponentConfig getUnifyComponentConfig() {
			return unifyComponentConfig;
		}

		public void setUnifyComponentConfig(UnifyComponentConfig unifyComponentConfig) {
			this.unifyComponentConfig = unifyComponentConfig;
		}

		public Class<? extends UnifyComponent> getType() {
			return unifyComponentConfig.getType();
		}

		public String getName() {
			return unifyComponentConfig.getName();
		}

		public UnifyComponentSettings getSettings() {
			return unifyComponentConfig.getSettings();
		}

		public Class<? extends UnifyComponent> getOriginalType() {
			return originalType;
		}

		public void setInjectInfoList(List<InternalInjectInfo> injectInfoList) {
			this.injectInfoList = DataUtils.unmodifiableList(injectInfoList);
		}

		public List<InternalInjectInfo> getInjectInfoList() {
			return injectInfoList;
		}

		public boolean isSingleton() {
			return unifyComponentConfig.isSingleton();
		}

		public void incrementPassCount() {
			passCount++;
		}

		public void incrementFailCount() {
			failCount++;
		}

		public int getPassCount() {
			return passCount;
		}

		public int getFailCount() {
			return failCount;
		}
	}

	private static enum InjectType {
		COMPONENT_ARRAY, COMPONENT_COLLECTION, COMPONENT_MAP, COMPONENT_INST, DATA_CONVERTED
	}

	private static class InternalInjectInfo {

		private final InjectType type;

		private final Field field;

		private final GetterSetterInfo setterInfo;

		private final String[] configValues;

		private final Class<? extends UnifyComponent> unifyComponentType;

		private Object valueToInject;

		public InternalInjectInfo(InjectType type, Field field, GetterSetterInfo setterInfo, String[] configValues,
				Class<? extends UnifyComponent> unifyComponentType) {
			this.type = type;
			this.field = field;
			this.setterInfo = setterInfo;
			this.configValues = configValues;
			this.unifyComponentType = unifyComponentType;
			try {
				this.field.setAccessible(true);
			} catch (SecurityException e) {
			}
		}

		public InjectType getType() {
			return type;
		}

		public GetterSetterInfo getSetterInfo() {
			return setterInfo;
		}

		public boolean isWithSetter() {
			return setterInfo != null && setterInfo.isSetter();
		}

		public String[] getConfigValues() {
			return configValues;
		}

		public Class<? extends UnifyComponent> getUnifyComponentType() {
			return unifyComponentType;
		}

		public Field getField() {
			return field;
		}

		public boolean isFieldAccessible() {
			return field.isAccessible();
		}

		public Object getValueToInject() throws UnifyException {
			if (valueToInject == null) {
				synchronized (this) {
					if (valueToInject == null) {
						if (InjectType.DATA_CONVERTED.equals(type)) {
							valueToInject = DataUtils.convert(field.getType(), configValues);
						}
					}
				}
			}

			return valueToInject;
		}
	}
}
