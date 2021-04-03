/*
 * Copyright 2018-2020 The Code Department.
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

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.database.TestSqlDataSource;
import com.tcdng.unify.core.system.entities.AbstractSequencedEntity;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.ThreadUtils;
import com.tcdng.unify.core.util.TypeUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * Abstract unify component test class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractUnifyComponentTest {

    private static UnifyContainerEnvironment containerEnvironment;

    private static UnifyContainerConfig containerConfig;

    private static UnifyContainer container;

    private static UnifyContainerConfig.Builder uccb;

    private boolean clusterMode;

    public AbstractUnifyComponentTest(boolean clusterMode) {
        this.clusterMode = clusterMode;
    }

    public AbstractUnifyComponentTest() {

    }

    @BeforeClass
    public static void setupClass() throws Exception {
        if (containerEnvironment == null) {
            containerEnvironment = new UnifyContainerEnvironment(TypeUtils.getTypeRepositoryFromClasspath());
        }

        deleteSetup();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        deleteSetup();
    }

    @Before
    public void setUp() throws Exception {
        if (container == null) {
            newSetup();
        }

        ((RequestContextManager) getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER))
                .unloadRequestContext();
        onSetup();
    }

    @After
    public void tearDown() throws Exception {
        onTearDown();
    }

    /**
     * Returns names of all components that are of a particular type.
     * 
     * @param componentClass
     *            the component type
     * @throws Exception
     *             if an error occurs
     */
    protected List<String> getComponentNames(Class<? extends UnifyComponent> componentClass) throws Exception {
        return container.getComponentNames(componentClass);
    }

    /**
     * Returns all configurations that are of a particular type.
     * 
     * @param componentClass
     *            the component type
     * @throws Exception
     *             if an error occurs
     */
    protected List<UnifyComponentConfig> getComponentConfigs(Class<? extends UnifyComponent> componentClass)
            throws Exception {
        return container.getComponentConfigs(componentClass);
    }

    /**
     * Returns a component configuration using supplied name.
     * 
     * @param name
     *            the component name
     * @throws Exception
     *             if an error occurs
     */
    protected UnifyComponentConfig getComponentConfig(String name) throws Exception {
        return container.getComponentConfig(name);
    }

    /**
     * Returns a non-singleton component using supplied name and alternate settings.
     * 
     * @param name
     *            the component name
     * @param altSettings
     *            the alternate settings
     * @throws Exception
     *             if an error occurs
     */
    protected UnifyComponent getComponent(String name, Setting... altSettings) throws Exception {
        return container.getComponent(name, altSettings);
    }

    /**
     * Returns a component using supplied name.
     * 
     * @param name
     *            the component name
     * @throws Exception
     *             if an error occurs
     */
    protected UnifyComponent getComponent(String name) throws Exception {
        return container.getComponent(name);
    }

    /**
     * Returns a UPL component using supplied name.
     * 
     * @param locale
     *            the locale
     * @param descriptor
     *            the component name
     * @throws Exception
     *             if an error occurs
     */
    protected UnifyComponent getUplComponent(Locale locale, String descriptor) throws Exception {
        return container.getUplComponent(locale, descriptor, false);
    }

    /**
     * Returns application context.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected ApplicationContext getApplicationContext() throws UnifyException {
        return container.getApplicationContext();
    }

    /**
     * Adds settings and dependencies to test.
     * 
     * @throws Exception
     *             if an error occurs
     */
    private static void addSettingsAndDependencies() throws Exception {
        addContainerSetting(UnifyCorePropertyConstants.APPLICATION_TOCONSOLE, "false");
        addDependency(ApplicationComponents.APPLICATION_DATASOURCE, TestSqlDataSource.class);

        addContainerSetting(UnifyCorePropertyConstants.APPLICATION_MESSAGES_BASE,
                new String[] { "com.tcdng.unify.core.resources.messages", "com.tcdng.unify.core.resources.test",
                        "com.tcdng.unify.core.resources.test3" });
    }

    /**
     * Setup settings and dependencies required for test.
     * 
     * @throws Exception
     *             if an error occurs
     */
    protected void doAddSettingsAndDependencies() throws Exception {

    }

    /**
     * Adds a container setting.
     * 
     * @param name
     *            the setting's name
     * @param value
     *            the setting value
     * @throws Exception
     *             if an error occurs
     */
    protected static void addContainerSetting(String name, Object value) throws Exception {
        uccb.setProperty(name, value);
    }

    /**
     * Used to add components that test depends on. Call this multiple times to set
     * more than one dependency.
     * 
     * @param name
     *            the component name
     * @param componentClass
     *            the component class
     * @param settings
     *            the configuration parameters
     * @throws Exception
     *             if an error occurs
     */
    protected static void addDependency(String name, Class<? extends UnifyComponent> componentClass,
            Setting... settings) throws Exception {
        addDependency(name, componentClass, true, settings);
    }

    /**
     * Used to add components that test depends on. Call this multiple times to set
     * more than one dependency.
     * 
     * @param name
     *            the component name
     * @param componentClass
     *            the component class
     * @param singleton
     *            indicates if component should be a singleton
     * @param settings
     *            the configuration parameters
     * @throws Exception
     *             if an error occurs
     */
    protected static void addDependency(String name, Class<? extends UnifyComponent> componentClass, boolean singleton,
            Setting... settings) throws Exception {
        addDependency(name, componentClass, singleton, false, settings);
    }

    /**
     * Used to add components that test depends on. Call this multiple times to set
     * more than one dependency.
     * 
     * @param name
     *            the component name
     * @param componentClass
     *            the component class
     * @param singleton
     *            indicates if component should be a singleton
     * @param overwrite
     *            indicates if component overwrite is allowed
     * @param settings
     *            the configuration parameters
     * @throws Exception
     *             if an error occurs
     */
    protected static void addDependency(String name, Class<? extends UnifyComponent> componentClass, boolean singleton,
            boolean overwrite, Setting... settings) throws Exception {
        UnifyComponentSettings confSettings = UnifyConfigUtils.readComponentSettings(componentClass);
        UnifyComponentSettings.Builder ub = new UnifyComponentSettings.Builder(confSettings);
        for (Setting setting : settings) {
            ub.setProperty(setting.getName(), setting.getValue(), setting.isAutoInject(), setting.isHidden());
        }

        uccb.addComponentConfig(name, "", componentClass, singleton, overwrite, ub.build());
    }

    protected Object createRecord(Entity record) throws Exception {
        Database db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DatabaseTransactionManager tm =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        tm.beginTransaction();
        try {
            return db.create(record);
        } finally {
            tm.endTransaction();
        }
    }

    protected <T extends Entity> T findRecord(Class<T> clazz, Object id) throws Exception {
        Database db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DatabaseTransactionManager tm =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        tm.beginTransaction();
        try {
            return db.list(clazz, id);
        } finally {
            tm.endTransaction();
        }
    }

    @SuppressWarnings("unchecked")
    protected void deleteAll(Class<? extends Entity>... typeList) throws Exception {
        Database db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DatabaseTransactionManager tm =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        tm.beginTransaction();
        try {
            for (Class<? extends Entity> type : typeList) {
                if (AbstractSequencedEntity.class.isAssignableFrom(type)) {
                    db.deleteAll(Query.of(type).addGreaterThan("id", 0L));
                } else {
                    db.deleteAll(Query.of(type).ignoreEmptyCriteria(true));
                }
            }
        } finally {
            tm.endTransaction();
        }
    }

    protected int countAll(Class<? extends Entity> typeClass) throws Exception {
        Database db = (Database) getComponent(ApplicationComponents.APPLICATION_DATABASE);
        DatabaseTransactionManager tm =
                (DatabaseTransactionManager) getComponent(ApplicationComponents.APPLICATION_DATABASETRANSACTIONMANAGER);
        tm.beginTransaction();
        try {
            return db.countAll(Query.of(typeClass).addGreaterThan("id", 0L).ignoreEmptyCriteria(true));
        } finally {
            tm.endTransaction();
        }
    }

    protected void waitForTask(TaskMonitor tm) throws Exception {
        while (!tm.isDone()) {
            ThreadUtils.yield();
        }
    }

    protected abstract void onSetup() throws Exception;

    protected abstract void onTearDown() throws Exception;

    private void newSetup() throws Exception {
        try {
            uccb = UnifyContainerConfig.newBuilder();
            uccb.nodeId("node-001");
            uccb.clusterMode(clusterMode);
            uccb.deploymentVersion("1.0");
            uccb.deploymentMode(true);
            uccb.setProperty("logger.level", "off");
            UnifyConfigUtils.readConfigFromTypeRepository(uccb, TypeUtils.getTypeRepositoryFromClasspath());

            addSettingsAndDependencies();
            doAddSettingsAndDependencies();
            containerConfig = uccb.build();
            container = Unify.startup(containerEnvironment, containerConfig);
        } catch (Exception e) {
            e.printStackTrace();
            containerConfig = null;
            throw e;
        } finally {
            uccb = null;
        }
    }

    private static void deleteSetup() throws Exception {
        if (container != null) {
            Unify.shutdown(container.getAccessKey());
            containerConfig = null;
            container = null;
        }
    }
}
