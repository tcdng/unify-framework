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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.tcdng.unify.core.annotation.Broadcast;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.annotation.Plugin;
import com.tcdng.unify.core.application.BootService;
import com.tcdng.unify.core.business.BusinessLogicUnit;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.business.internal.ProxyBusinessServiceGenerator;
import com.tcdng.unify.core.constant.AnnotationConstants;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.logging.AbstractLog4jLogger;
import com.tcdng.unify.core.logging.DummyEventLogger;
import com.tcdng.unify.core.logging.Logger;
import com.tcdng.unify.core.logging.LoggingLevel;
import com.tcdng.unify.core.message.ResourceBundles;
import com.tcdng.unify.core.system.ClusterService;
import com.tcdng.unify.core.system.Command;
import com.tcdng.unify.core.system.UserSessionManager;
import com.tcdng.unify.core.task.TaskManager;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.upl.UplCompiler;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplElementAttributes;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.NameUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.ThreadUtils;
import com.tcdng.unify.core.util.TokenUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * Represents a container for unify components.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyContainer {

    public static final String DEFAULT_APPLICATION_BANNER = "banner/banner.txt";

    public static final short DEFAULT_COMMAND_PORT = 4242;

    public static final int DEFAULT_APPLICATION_QUERY_LIMIT = 10000;

    public static final int DEFAULT_APPLICATION_SESSION_TIMEOUT = 600;

    private static final long PERIODIC_EXECUTION_INITIAL_DELAY = 200;

    private static final long COMMAND_THREAD_RATE = 1000;

    private static ThreadLocal<InitializationTrail> initializationTrailThreadLocal = new ThreadLocal<InitializationTrail>() {
        @Override
        protected InitializationTrail initialValue() {
            return new InitializationTrail();
        }
    };

    private UnifyContainerEnvironment unifyContainerEnvironment;

    private ConcurrentHashMap<String, InternalUnifyComponentInfo> internalUnifyComponentInfos;

    private Map<String, String> internalResolutionMap;

    private Map<String, Object> unifySettings;

    private Map<String, String> aliases;

    private List<UnifyStaticSettings> staticSettings;

    private RequestContextManager requestContextManager;

    private ApplicationContext applicationContext;

    private BootService applicationBootService;

    private ClusterService clusterService;

    private UplCompiler uplCompiler;

    private UserSessionManager userSessionManager;

    private ResourceBundles messages;

    private Logger logger;

    private FactoryMap<String, UnifyComponentContext> componentContextMap;

    private FactoryMap<String, UnifyComponent> singletonComponentMap;

    private LocaleFactoryMaps<String, UplComponent> cachedLocaleUplComponentMap;

    private List<TaskMonitor> periodicTaskMonitorList;

    private List<UnifyComponent> singletonTerminationList;

    private Set<UnifyContainerInterface> interfaces;

    private Queue<ContainerCommand> containerCommandQueue;

    private Map<String, BroadcastInfo> broadcastInfoMap;

    private Map<Class<? extends UnifyComponent>, List<String>> namelessConfigurableSuggestions;

    private String nodeId;

    private String deploymentVersion;

    private String hostAddress;

    private String hostHome;

    private String accessKey;

    private Date startTime;

    private boolean toConsole;

    private boolean productionMode;

    private boolean clusterMode;

    private boolean deploymentMode;

    private boolean started;

    private boolean shutdown;

    public UnifyContainer() {
        accessKey = UUID.randomUUID().toString();

        internalUnifyComponentInfos = new ConcurrentHashMap<String, InternalUnifyComponentInfo>();
        periodicTaskMonitorList = new ArrayList<TaskMonitor>();
        singletonTerminationList = new ArrayList<UnifyComponent>();
        interfaces = new HashSet<UnifyContainerInterface>();
        containerCommandQueue = new ConcurrentLinkedQueue<ContainerCommand>();
        broadcastInfoMap = new HashMap<String, BroadcastInfo>();
        namelessConfigurableSuggestions = new HashMap<Class<? extends UnifyComponent>, List<String>>();

        componentContextMap = new FactoryMap<String, UnifyComponentContext>() {
            @Override
            protected UnifyComponentContext create(String name, Object... params) throws Exception {
                return new UnifyComponentContext(applicationContext, getLogger(name), name);
            }
        };

        singletonComponentMap = new FactoryMap<String, UnifyComponent>() {
            // Added to handle cyclic dependency in singletons
            private Map<String, UnifyComponent> singletonMap = new HashMap<String, UnifyComponent>();

            @Override
            public UnifyComponent get(String name, Object... params) throws UnifyException {
                UnifyComponent unifyComponent = singletonMap.get(name);
                if (unifyComponent == null) {
                    return super.get(name, params);
                }
                return unifyComponent;
            }

            @Override
            protected UnifyComponent create(String name, Object... params) throws Exception {
                UnifyComponentConfig unifyComponentConfig = (UnifyComponentConfig) params[0];
                UplElementAttributes uplElementAttributes = (UplElementAttributes) params[1];
                UnifyComponent unifyComponent = unifyComponentConfig.getType().newInstance();
                singletonMap.put(name, unifyComponent);
                initializeComponent(unifyComponentConfig, null, unifyComponent, uplElementAttributes);
                singletonTerminationList.add(0, unifyComponent);
                return unifyComponent;
            }
        };

        cachedLocaleUplComponentMap = new LocaleFactoryMaps<String, UplComponent>() {
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
     * @param uce
     *            the environment object
     * @param ucc
     *            the configuration used for initialization
     * @throws UnifyException
     *             if container is already started. If an error occurs.
     */
    @SuppressWarnings("unchecked")
    public void startup(UnifyContainerEnvironment uce, UnifyContainerConfig ucc) throws UnifyException {
        if (started || shutdown) {
            throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_ALREADY_INITIALIZED);
        }

        unifyContainerEnvironment = uce;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostAddress = inetAddress.getHostAddress();
            hostHome = inetAddress.getHostName();
        } catch (UnknownHostException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.CONTAINER_ERROR);
        }

        deploymentVersion = ucc.getDeploymentVersion();
        clusterMode = ucc.isClusterMode();
        productionMode = ucc.isProductionMode();
        deploymentMode = ucc.isDeploymentMode();
        unifySettings = ucc.getProperties();
        aliases = ucc.getAliases();
        staticSettings = ucc.getStaticSettings();
        nodeId = ucc.getNodeId();

        if (nodeId == null) {
            throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_NODEID_REQUIRED);
        }

        toConsole = true;
        if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_CONTAINER_TOCONSOLE) != null) {
            toConsole = Boolean.valueOf(
                    String.valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_CONTAINER_TOCONSOLE)));
        }

        // Banner
        List<String> banner = getApplicationBanner();
        if (!banner.isEmpty()) {
            for (String line : banner) {
                toConsole(line);
            }
        }

        String lineSeparator = System.getProperty("line.separator");
        applicationContext = new ApplicationContext(this, Locale.getDefault(),
                lineSeparator != null ? lineSeparator : "\n");
        long startTimeMillis = System.currentTimeMillis();
        initializeContainerMessages();
        initializeContainerLogger();

        toConsole("Container initialization started...");
        toConsole("Validating and loading configuration...");
        for (UnifyComponentConfig unifyComponentConfig : ucc.getComponentConfigs()) {
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
                getSetting(UnifyCorePropertyConstants.APPLICATION_CUSTOMIZATION), null);
        internalResolutionMap = UnifyConfigUtils.resolveConfigurationOverrides(internalUnifyComponentInfos,
                customizationSuffixList);

        // Detect business components
        logDebug("Detecting business service components...");
        Map<String, Map<String, Periodic>> componentPeriodMethodMap = new HashMap<String, Map<String, Periodic>>();
        Map<String, Set<String>> componentPluginSocketsMap = new HashMap<String, Set<String>>();
        List<UnifyComponentConfig> managedBusinessServiceConfigList = new ArrayList<UnifyComponentConfig>();
        for (Map.Entry<String, InternalUnifyComponentInfo> entry : internalUnifyComponentInfos.entrySet()) {
            InternalUnifyComponentInfo iuci = entry.getValue();
            // Fetch periodic method information
            Map<String, Periodic> periodicMethodMap = new HashMap<String, Periodic>();
            Set<String> pluginSockets = new HashSet<String>();
            Method[] methods = iuci.getType().getMethods();
            for (Method method : methods) {
                // Periodic methods
                Periodic pa = method.getAnnotation(Periodic.class);
                if (pa != null) {
                    if (iuci.isSingleton() && void.class.equals(method.getReturnType())
                            && method.getParameterTypes().length == 1
                            && method.getParameterTypes()[0].equals(TaskMonitor.class)) {
                        periodicMethodMap.put(method.getName(), pa);
                    } else {
                        throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_INVALID_PERIOD_METHOD,
                                iuci.getName(), method.getName());
                    }
                }

                // Broadcast methods
                Broadcast ba = method.getAnnotation(Broadcast.class);
                if (ba != null) {
                    if (iuci.isSingleton() && void.class.equals(method.getReturnType())
                            && method.getParameterTypes().length == 1
                            && method.getParameterTypes()[0].equals(String[].class)) {
                        String name = NameUtils.getComponentMethodName(iuci.getName(), method.getName());
                        broadcastInfoMap.put(name, new BroadcastInfo(iuci.getName(), method.getName()));
                    } else {
                        throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_INVALID_BROADCAST_METHOD,
                                iuci.getName(), method.getName());
                    }
                }
            }

            if (!periodicMethodMap.isEmpty()) {
                logDebug("Periodic methods detected for component '" + iuci.getName() + "'.");
                componentPeriodMethodMap.put(iuci.getName(), periodicMethodMap);
            }

            if (!pluginSockets.isEmpty()) {
                logDebug("Plug-in sockets detected for component '" + iuci.getName() + "'.");
                componentPluginSocketsMap.put(iuci.getName(), pluginSockets);
            }

            if (ReflectUtils.isInterface(iuci.getType(), BusinessService.class)) {
                logDebug("Business component '" + iuci.getName() + "' detected.");
                managedBusinessServiceConfigList.add(iuci.getUnifyComponentConfig());
            }
        }

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

        // Initialization
        started = true;
        requestContextManager = (RequestContextManager) getComponent(
                ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER);
        uplCompiler = (UplCompiler) getComponent(ApplicationComponents.APPLICATION_UPLCOMPILER);

        // Generate and install proxy business service objects
        logInfo("Generating and installing proxy business service objects...");
        for (UnifyComponentConfig unifyComponentConfig : managedBusinessServiceConfigList) {
            Map<String, List<UnifyPluginInfo>> pluginMap = allPluginsBySocketMap.get(unifyComponentConfig.getName());
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
            Method method = ReflectUtils.getMethod(iuc.getType(), broadcastInfo.getMethodName(), String[].class);
            broadcastInfo.setMethod(method);
        }

        logInfo("Generation and installation of proxy objects completed");

        // Cluster manager
        clusterService = (ClusterService) getComponent(ApplicationComponents.APPLICATION_CLUSTERSERVICE);
        userSessionManager = (UserSessionManager) getComponent(ApplicationComponents.APPLICATION_USERSESSIONMANAGER);

        // Run application startup service
        toConsole("Initializing application bootup service...");
        String bootComponentName = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_BOOT);
        if (bootComponentName == null) {
            bootComponentName = ApplicationComponents.APPLICATION_DEFAULTBOOTSERVICE;
        }
        applicationBootService = (BootService) getComponent(bootComponentName);
        applicationBootService.startup();

        toConsole("Application bootup service initialization completed.");

        // Initialize interfaces
        logInfo("Initializing container interfaces...");
        initializeInterfaces();
        logInfo("Container interfaces initialization complete.");

        // Schedule periodic tasks
        logInfo("Scheduling periodic tasks...");
        TaskManager taskManager = (TaskManager) getComponent(ApplicationComponents.APPLICATION_TASKMANAGER);
        for (Map.Entry<String, Map<String, Periodic>> entry1 : componentPeriodMethodMap.entrySet()) {
            logInfo("Intializing component [{0}] with periodic methods...", entry1.getKey());
            getComponent(entry1.getKey());
            for (Map.Entry<String, Periodic> entry2 : entry1.getValue().entrySet()) {
                Periodic pa = entry2.getValue();
                PeriodicType periodicType = pa.value();
                String taskStatusLoggerName = AnnotationUtils.getAnnotationString(pa.taskStatusLogger());
                TaskMonitor taskMonitor = taskManager.schedulePeriodicExecution(periodicType, entry1.getKey(),
                        entry2.getKey(), taskStatusLoggerName, PERIODIC_EXECUTION_INITIAL_DELAY);
                periodicTaskMonitorList.add(taskMonitor);
            }
        }
        logInfo("Periodic task scheduling completed.");

        // Open container interfaces to start servicing requests
        openInterfaces();

        // Start command processing thread
        new CommandThread().start();

        // Set start time to now
        startTime = new Date();

        // Container initialization completed
        long startupTimeMillis = startTime.getTime() - startTimeMillis;
        toConsole("Container initialization completed in " + startupTimeMillis + "ms.");
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
     * Returns the container's access key.
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Returns current container information.
     */
    public UnifyContainerInfo getInfo() {
        List<UnifyComponentInfo> componentInfoList = new ArrayList<UnifyComponentInfo>();
        for (InternalUnifyComponentInfo uici : internalUnifyComponentInfos.values()) {
            List<Setting> settingInfoList = new ArrayList<Setting>();
            UnifyComponentSettings unifyComponentSettings = uici.unifyComponentConfig.getSettings();
            for (String name : unifyComponentSettings.getPropertyNames()) {
                settingInfoList.add(unifyComponentSettings.getSetting(name));
            }

            componentInfoList.add(new UnifyComponentInfo(uici.getName(), uici.getOriginalType(),
                    uici.getFirstPassTime(), uici.getFirstFailTime(), uici.getLastPassTime(), uici.getLastFailTime(),
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
                deploymentVersion, hostAddress, hostHome, startTime, usedMemory, totalMemory, clusterMode,
                productionMode, deploymentMode, componentInfoList, interfaceInfoList, settingInfoList);
    }

    /**
     * Receives and processes a command.
     * 
     * @param command
     *            the command to process
     * @param params
     *            the command parameters
     * @throws UnifyException
     *             if an error occurs
     */
    public void command(String command, String... params) throws UnifyException {
        try {
            containerCommandQueue.offer(new ContainerCommand(command, params));
        } catch (ClassCastException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, "Unify Container");
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
     * @param componentType
     *            the component type to match
     * @return a list of component names that match with supplied type.
     * @throws UnifyException
     *             if container is not started.
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
     * @param name
     *            the component name
     * @return the component configuration if found, otherwise null.
     * @throws UnifyException
     *             if container is not started. If an error occurs
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
     * @param componentType
     *            the component types to match
     * @return a list of component configurations that match with supplied type.
     * @throws UnifyException
     *             if container is not started.
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
     * Gets a UPL component with specified descriptor.
     * 
     * @param locale
     *            the locale
     * @param descriptor
     *            the UPL descriptor
     * @param cached
     *            the cached flag.
     * @return If the cached flag is set, the container returns a cached instance
     *         with the same descriptor and locale if found. Otherwise, a new
     *         instance is returned.
     * @throws UnifyException
     *             if an error occurs
     */
    public UplComponent getUplComponent(Locale locale, String descriptor, boolean cached) throws UnifyException {
        if (cached) {
            return cachedLocaleUplComponentMap.get(locale, descriptor);
        }

        try {
            UplElementAttributes uplElementAttributes = uplCompiler.compileDescriptor(locale, descriptor);
            UplComponent uplComponent = (UplComponent) getComponent(uplElementAttributes.getComponentName(), null,
                    uplElementAttributes);
            return uplComponent;
        } finally {
            initializationTrailThreadLocal.remove();
        }
    }

    /**
     * Gets a UPL component using supplied attributes key.
     * 
     * @param locale
     *            the component locale
     * @param attributesKey
     *            the UPL element attributes key
     * @return the UPL component
     * @throws UnifyException
     *             if container is not started. If component with name is unknown.
     *             If component instantiation error occurs.
     */
    public UplComponent getUplComponent(Locale locale, String attributesKey) throws UnifyException {
        try {
            UplElementAttributes uplElementAttributes = uplCompiler.getUplElementAttributes(locale, attributesKey);
            UplComponent uplComponent = (UplComponent) getComponent(uplElementAttributes.getComponentName(), null,
                    uplElementAttributes);
            return uplComponent;
        } finally {
            initializationTrailThreadLocal.remove();
        }
    }

    /**
     * Returns classes of a particular type annotated with a specific type of
     * annotation.
     * 
     * @param classType
     *            the annotated class type
     * @param annotationClass
     *            the annotation
     * @param packages
     *            packages to restrict search to. This parameter is optional.
     * @return list of annotated classes
     * @throws UnifyException
     *             if an error occurs
     */
    public <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
            Class<? extends Annotation> annotationClass, String... packages) throws UnifyException {
        return unifyContainerEnvironment.getTypeRepository().getAnnotatedClasses(classType, annotationClass, packages);
    }

    public String getNodeId() {
        return nodeId;
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

    public List<UnifyStaticSettings> getStaticSettings() {
        return staticSettings;
    }

    public Object getSetting(String name) {
        return unifySettings.get(name);
    }

    public String getWorkingPath() {
        return unifyContainerEnvironment.getWorkingPath();
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
     * Gets a component by name. If a component is configured as a singleton, this
     * method would always return the same instance for specified name, otherwise a
     * new component instance is always created.
     * 
     * @param name
     *            the component name
     * @return the component
     * @throws UnifyException
     *             if container is not started. If component with name is unknown.
     *             If component instantiation error occurs.
     */
    public UnifyComponent getComponent(String name) throws UnifyException {
        return getComponent(name, null, null);
    }

    /**
     * Gets a component by name using alternate settings. Applies to non-singletons
     * only..
     * 
     * @param name
     *            the component name
     * @param altSettings
     *            the alternate settings
     * @return the component
     * @throws UnifyException
     *             if container is not started. If component with name is unknown.
     *             If component is a singleton. If component instantiation error
     *             occurs.
     */
    public UnifyComponent getComponent(String name, UnifyComponentSettings altSettings) throws UnifyException {
        return getComponent(name, altSettings, null);
    }

    /**
     * Returns true if component with name is defined in container.
     * 
     * @param name
     *            the component name
     * @throws UnifyException
     *             If component an error occurs.
     */
    public boolean isComponent(String name) throws UnifyException {
        return internalResolutionMap.containsKey(name) || internalUnifyComponentInfos.containsKey(name)
                || aliases.containsKey(name);
    }

    /**
     * Tries to grab the cluster master synchronization lock.
     * 
     * @return a true value is lock is obtained otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean grabClusterMasterLock() throws UnifyException {
        return clusterService.grabMasterSynchronizationLock();
    }

    /**
     * Tries to grab a cluster synchronization lock. Lock must be released after use
     * with {@link #releaseClusterLock(String)}
     * 
     * @param lockName
     *            the lock name
     * @return a true value is lock is obtained otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean grabClusterLock(String lockName) throws UnifyException {
        return clusterService.grabSynchronizationLock(lockName);
    }

    /**
     * Releases a cluster synchronization lock.
     * 
     * @param lockName
     *            the lock name
     * @return a true value if lock was released
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean releaseClusterLock(String lockName) throws UnifyException {
        return clusterService.releaseSynchronizationLock(lockName);
    }

    /**
     * Broadcasts a cluster command to other nodes.
     * 
     * @param command
     *            the command to broadcast
     * @param params
     *            the command parameters
     * @throws UnifyException
     *             if an error occurs
     */
    public void broadcastToOtherNodes(String command, String... params) throws UnifyException {
        clusterService.broadcastToOtherNodes(command, params);
    }

    /**
     * Broadcasts attribute to all sessions in this node.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value. A null value clears attribute.
     * @throws UnifyException
     *             if an error occurs
     */
    public void broadcastToSessions(String name, Object value) throws UnifyException {
        userSessionManager.broadcast(name, value);
    }

    /**
     * Broadcasts attribute to specific application session context in this node.
     * 
     * @param sessionId
     *            the session ID
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value. A null value clears attribute.
     * @throws UnifyException
     *             if an error occurs
     */
    public void broadcastToSession(String sessionId, String name, Object value) throws UnifyException {
        userSessionManager.broadcast(sessionId, name, value);
    }

    private UnifyComponent getComponent(String name, UnifyComponentSettings altSettings,
            UplElementAttributes uplElementAttributes) throws UnifyException {
        checkStarted();
        UnifyComponent unifyComponent = null;
        try {
            UnifyComponentConfig unifyComponentConfig = null;
            InternalUnifyComponentInfo iuci = getInternalUnifyComponentInfo(name);
            if (iuci != null) {
                unifyComponentConfig = iuci.getUnifyComponentConfig();
            }

            if (unifyComponentConfig == null) {
                // If supplied name is alias, get actual name and fetch configuration
                String actualName = aliases.get(name);
                if (actualName != null) {
                    iuci = getInternalUnifyComponentInfo(actualName);

                    if (iuci != null) {
                        unifyComponentConfig = iuci.getUnifyComponentConfig();
                    }
                }
            }

            if (unifyComponentConfig == null) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_UNKNOWN_COMP, name);
            }

            if (unifyComponentConfig.isSingleton()) {
                if (altSettings != null) {
                    throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_ALTSETTINGS_SINGLETON, name);
                }

                unifyComponent = singletonComponentMap.get(unifyComponentConfig.getName(), unifyComponentConfig,
                        uplElementAttributes);
            } else {
                if (altSettings != null) {
                    // Validate alternate settings
                    UnifyComponentSettings settings = unifyComponentConfig.getSettings();
                    for (String property : altSettings.getPropertyNames()) {
                        if (!settings.isProperty(property)) {
                            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_ALTSETTINGS_UNKNOWN_PROPERTY,
                                    name, property);
                        }
                    }
                }

                // Create instance
                unifyComponent = unifyComponentConfig.getType().newInstance();
                initializeComponent(unifyComponentConfig, altSettings, unifyComponent, uplElementAttributes);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, name);
        } finally {
            initializationTrailThreadLocal.remove();
        }
        return unifyComponent;
    }

    private void initializeComponent(UnifyComponentConfig unifyComponentConfig, UnifyComponentSettings altSettings,
            UnifyComponent unifyComponent, UplElementAttributes uplElementAttributes) throws UnifyException {
        InitializationTrail initializationTrail = initializationTrailThreadLocal.get();
        initializationTrail.joinTrail(unifyComponentConfig.getName());
        boolean success = false;
        try {
            // Get component context
            UnifyComponentContext unifyComponentContext = componentContextMap.get(unifyComponentConfig.getName());
            // Fetch and set (inject) fields
            Class<?> clazz = unifyComponentConfig.getType();
            UnifyComponentSettings settings = unifyComponentConfig.getSettings();
            for (String property : settings.getPropertyNames()) {
                Object value = null;
                if (altSettings != null) {
                    value = altSettings.getSettingValue(property);
                }

                if (value == null) {
                    value = settings.getSettingValue(property);
                }

                Field field = ReflectUtils.getField(clazz, property);
                if (value == null) {
                    if (UnifyComponent.class.isAssignableFrom(field.getType())) {
                        List<String> names = namelessConfigurableSuggestions.get(field.getType());
                        if (names.size() == 1) { // Check perfect suggestion
                            value = names.get(0);
                        }
                    }
                }

                if (value != null) {
                    String[] configValues = resolveConfigValue(value);
                    injectFieldValue(unifyComponent, field, configValues);
                }
            }

            // Set UPL attributes if necessary
            if (uplElementAttributes != null) {
                ((UplComponent) unifyComponent).setUplAttributes(uplElementAttributes);
            }

            // Initialize
            unifyComponent.initialize(unifyComponentContext);
            success = true;
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INITIALIZATION_ERROR,
                    unifyComponentConfig.getName());
        } finally {
            initializationTrail.leaveTrail();
            InternalUnifyComponentInfo iuci = this.getInternalUnifyComponentInfo(unifyComponentConfig.getName());

            Date now = new Date();
            if (success) {
                if (iuci.getFirstPassTime() == null) {
                    iuci.setFirstPassTime(now);
                }
                iuci.setLastPassTime(now);
                iuci.incrementPassCount();
            } else {
                if (iuci.getFirstFailTime() == null) {
                    iuci.setFirstFailTime(now);
                }
                iuci.setLastFailTime(now);
                iuci.incrementFailCount();
            }
        }
    }

    /**
     * Injects value into component field, performing necessary conversion
     * 
     * @param unifyComponent
     *            - the component
     * @param field
     *            - the field to set
     * @param configValues
     *            - the value to inject
     */
    @SuppressWarnings("unchecked")
    private void injectFieldValue(UnifyComponent unifyComponent, Field field, String[] configValues)
            throws UnifyException {
        ReflectUtils.assertNonStaticNonFinal(field);
        try {
            Object valueToInject = null;
            Class<?> fieldClass = field.getType();
            if (fieldClass.isArray()) {
                Class<?> arrFieldClass = fieldClass.getComponentType();
                if (UnifyComponent.class.isAssignableFrom(arrFieldClass)) {
                    Object[] tempArray = getComponents(arrFieldClass, configValues).values().toArray();
                    valueToInject = Array.newInstance(arrFieldClass, tempArray.length);
                    for (int i = 0; i < tempArray.length; i++) {
                        Array.set(valueToInject, i, tempArray[i]);
                    }
                }
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Class<?> colFieldClass = ReflectUtils.getArgumentType(field.getGenericType(), 0);
                if (UnifyComponent.class.isAssignableFrom(colFieldClass)) {
                    Collection<Object> c = new ArrayList<Object>();
                    c.addAll(getComponents((Class<? extends UnifyComponent>) colFieldClass, configValues).values());
                    valueToInject = c;
                }
            } else if (Map.class.isAssignableFrom(fieldClass)) {
                Class<?> keyFieldClass = ReflectUtils.getArgumentType(field.getGenericType(), 0);
                if (String.class.equals(keyFieldClass)) {
                    Class<?> valFieldClass = ReflectUtils.getArgumentType(field.getGenericType(), 1);
                    if (UnifyComponent.class.isAssignableFrom(valFieldClass)) {
                        valueToInject = getComponents((Class<? extends UnifyComponent>) valFieldClass, configValues);
                    }
                }
            } else {
                if (UnifyComponent.class.isAssignableFrom(fieldClass)) {
                    valueToInject = getComponent(configValues[0]);
                }
            }

            if (valueToInject == null) {
                valueToInject = DataUtils.convert(fieldClass, configValues, null);
            }

            if (valueToInject != null) {
                field.set(unifyComponent, valueToInject);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.FIELD_INJECTION_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> getComponents(Class<T> type, String[] names) throws UnifyException {
        Map<String, T> map = new LinkedHashMap<String, T>();
        for (String name : names) {
            UnifyComponent component = getComponent(name);
            if (!type.isAssignableFrom(component.getClass())) {
                throw new UnifyException(UnifyCoreErrorConstants.FIELD_INJECTION_INCOMPATIBLE, type,
                        component.getClass());
            }
            map.put(name, (T) component);
        }
        return map;
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

    private String[] resolveConfigValue(Object value) throws Exception {
        List<String> names = new ArrayList<String>();
        if (value instanceof String[]) {
            for (String name : (String[]) value) {
                resolveConfig(names, name);
            }
        } else if (value instanceof String) {
            resolveConfig(names, (String) value);
        } else {
            names.add(String.valueOf(value));
        }
        return names.toArray(new String[names.size()]);
    }

    @SuppressWarnings("unchecked")
    private void resolveConfig(List<String> names, String value) throws Exception {
        if (TokenUtils.isComponentListToken(value)) {
            names.addAll(getComponentNames(
                    (Class<? extends UnifyComponent>) Class.forName(TokenUtils.extractTokenValue(value).trim())));
        } else {
            names.add(TokenUtils.getStringTokenValue(value));
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeInterfaces() throws UnifyException {
        // Initialize command interface if flagged
        if (DataUtils.convert(boolean.class,
                unifySettings.get(UnifyCorePropertyConstants.APPLICATION_COMMAND_INTERFACE), null)) {
            interfaces.add((UnifyContainerInterface) getComponent("unify-commandinterface"));
        }

        // Initialize other interfaces
        List<String> interfacesList = DataUtils.convert(ArrayList.class, String.class,
                unifySettings.get(UnifyCorePropertyConstants.APPLICATION_INTERFACES), null);
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

        toConsole("Container interfaces opened.");
    }

    private void closeInterfaces() throws UnifyException {
        toConsole("Closing container interfaces...");
        for (UnifyContainerInterface unifyContainerInterface : interfaces) {
            toConsole("Closing interface '" + unifyContainerInterface.getName() + "'...");

            unifyContainerInterface.stopServicingRequests();
        }

        toConsole("Container interfaces closed..");
    }

    @SuppressWarnings("unchecked")
    private void initializeContainerMessages() throws UnifyException {
        List<String> messageBaseList = new ArrayList<String>();
        for (UnifyStaticSettings unifyStaticSettings : staticSettings) {
            String messageBase = unifyStaticSettings.getMessageBase();
            if (!StringUtils.isBlank(messageBase)) {
                messageBaseList.add(messageBase);
            }
        }

        List<String> cfgMessageBaseList = DataUtils.convert(ArrayList.class, String.class,
                unifySettings.get(UnifyCorePropertyConstants.APPLICATION_MESSAGES_BASE), null);
        if (cfgMessageBaseList != null) {
            messageBaseList.addAll(cfgMessageBaseList);
        }

        messages = new ResourceBundles(messageBaseList);
    }

    private void initializeContainerLogger() throws UnifyException {
        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        if (!rootLogger.getAllAppenders().hasMoreElements()) {
            try {
                String loggingPattern = (String) unifySettings
                        .get(UnifyCorePropertyConstants.APPLICATION_LOGGER_PATTERN_SETTING);
                if (loggingPattern == null) {
                    loggingPattern = "%d{ISO8601} %-5p %c{1} %m%n";
                }
                PatternLayout patternLayout = new PatternLayout(loggingPattern);

                boolean logToConsole = false;
                if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_TO_CONSOLE) != null) {
                    logToConsole = Boolean.valueOf(
                            String.valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_TO_CONSOLE)));
                }

                if (logToConsole) {
                    rootLogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT));
                }

                boolean logToFile = true;
                if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_TO_FILE) != null) {
                    logToFile = Boolean.valueOf(
                            String.valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_TO_FILE)));
                }

                if (logToFile) {
                    String filename = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_FILENAME);
                    if (filename == null) {
                        filename = "application.log";
                    }
                    filename = IOUtils.buildFilename("logs", filename);
                    filename = IOUtils.buildFilename(getWorkingPath(), filename);

                    String fileMaxSize = (String) unifySettings
                            .get(UnifyCorePropertyConstants.APPLICATION_LOG_FILEMAXSIZE);
                    if (fileMaxSize == null) {
                        fileMaxSize = "1MB";
                    }

                    Integer fileMaxBackup = Integer.valueOf(3);
                    if (unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_FILEMAXBACKUP) != null) {
                        fileMaxBackup = Integer.valueOf(String
                                .valueOf(unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_FILEMAXBACKUP)));
                    }

                    RollingFileAppender rfAppender = new RollingFileAppender(patternLayout, filename);
                    rfAppender.setMaxFileSize(fileMaxSize);
                    rfAppender.setMaxBackupIndex(fileMaxBackup);
                    rootLogger.addAppender(rfAppender);
                }
            } catch (Exception e) {
                throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_LOGGER_INITIALIZATION_ERROR, e);
            }
        }

        String level = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_LOG_LEVEL);
        if (!StringUtils.isBlank(level)) {
            rootLogger.setLevel(Level.toLevel(level.toUpperCase()));
        } else {
            rootLogger.setLevel(Level.OFF);
        }
        logger = new UnifyContainerLoggerImpl(getClass());
    }

    public List<String> getApplicationBanner() throws UnifyException {
        String filename = (String) unifySettings.get(UnifyCorePropertyConstants.APPLICATION_BANNER);
        if (StringUtils.isBlank(filename)) {
            filename = DEFAULT_APPLICATION_BANNER;
        }

        return IOUtils.readFileResourceLines(filename, unifyContainerEnvironment.getWorkingPath());
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

    private class CommandThread extends Thread {
        public CommandThread() {
            super("Container command thread - " + nodeId);
        }

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    if (clusterMode) {
                        requestContextManager.getRequestContext()
                                .setAttribute(RequestAttributeConstants.SUPPRESS_BROADCAST, Boolean.TRUE);
                        List<Command> clusterCommandList = clusterService.getClusterCommands();
                        for (Command clusterCommand : clusterCommandList) {
                            BroadcastInfo broadcastInfo = broadcastInfoMap.get(clusterCommand.getCommand());
                            if (broadcastInfo != null) {
                                List<String> params = clusterCommand.getParams();
                                broadcastInfo.getMethod().invoke(getComponent(broadcastInfo.getComponentName()),
                                        new Object[] { params.toArray(new String[params.size()]) });
                            }
                        }
                        requestContextManager.getRequestContext()
                                .setAttribute(RequestAttributeConstants.SUPPRESS_BROADCAST, Boolean.FALSE);
                    }

                    ContainerCommand cc = containerCommandQueue.poll();
                    if (cc != null) {
                        String command = cc.getCommand();
                        if ("shutdown".equalsIgnoreCase(command)) {
                            shutdown();
                        }
                    }

                    ThreadUtils.sleep(COMMAND_THREAD_RATE);
                } catch (Exception e) {
                    e.printStackTrace();
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

        private String componentName;

        private String methodName;

        private Method method;

        public BroadcastInfo(String componentName, String methodName) {
            this.componentName = componentName;
            this.methodName = methodName;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getMethodName() {
            return methodName;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }

    private static class InitializationTrail {
        private List<String> componentList;

        public InitializationTrail() {
            componentList = new ArrayList<String>();
        }

        public void joinTrail(String componentName) throws UnifyException {
            boolean cyclic = componentList.contains(componentName);
            componentList.add(componentName);
            if (cyclic) {
                String trail = String.valueOf(componentList);
                componentList.clear();
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_CYCLIC_INITIALIZATION, componentName, trail);
            }
        }

        public void leaveTrail() {
            componentList.remove(componentList.size() - 1);
        }
    }

    @Component("UnifyContainerLogger")
    private class UnifyContainerLoggerImpl extends AbstractLog4jLogger {

        public UnifyContainerLoggerImpl(Class<?> clazz) {
            getLogger(clazz.getName());
        }

        @Override
        protected void onInitialize() throws UnifyException {

        }

        @Override
        protected void onTerminate() throws UnifyException {

        }
    }

    private class InternalUnifyComponentInfo {

        private UnifyComponentConfig unifyComponentConfig;

        private String originalType;

        private Date firstPassTime;

        private Date firstFailTime;

        private Date lastPassTime;

        private Date lastFailTime;

        private int passCount;

        private int failCount;

        public InternalUnifyComponentInfo(UnifyComponentConfig unifyComponentConfig) {
            this.unifyComponentConfig = unifyComponentConfig;
            originalType = unifyComponentConfig.getType().getName();
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

        public String getOriginalType() {
            return originalType;
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

        public Date getFirstPassTime() {
            return firstPassTime;
        }

        public void setFirstPassTime(Date firstPassTime) {
            this.firstPassTime = firstPassTime;
        }

        public Date getFirstFailTime() {
            return firstFailTime;
        }

        public void setFirstFailTime(Date firstFailTime) {
            this.firstFailTime = firstFailTime;
        }

        public Date getLastPassTime() {
            return lastPassTime;
        }

        public void setLastPassTime(Date lastPassTime) {
            this.lastPassTime = lastPassTime;
        }

        public Date getLastFailTime() {
            return lastFailTime;
        }

        public void setLastFailTime(Date lastFailTime) {
            this.lastFailTime = lastFailTime;
        }

        public int getPassCount() {
            return passCount;
        }

        public int getFailCount() {
            return failCount;
        }
    }

}
