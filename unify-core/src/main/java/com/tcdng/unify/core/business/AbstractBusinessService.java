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
package com.tcdng.unify.core.business;

import java.util.Date;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.sql.DynamicSqlDatabaseManager;
import com.tcdng.unify.core.task.TaskLauncher;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskSetup;
import com.tcdng.unify.core.util.CalendarUtils;

/**
 * An abstract base class that implements the basic requirements of a business
 * service. Any concrete subclass of this class is managed by the container.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractBusinessService extends AbstractUnifyComponent implements BusinessService {

    @Configurable(ApplicationComponents.APPLICATION_DATABASE)
    private Database db;

    @Configurable
    private DatabaseTransactionManager databaseTransactionManager;

    @Configurable
    private DynamicSqlDatabaseManager dynamicSqlDatabaseManager;

    @Configurable
    private TaskLauncher taskLauncher;

    @Override
    public DatabaseTransactionManager tm() throws UnifyException {
        return databaseTransactionManager;
    }

    @Transactional
    @Override
    public Entity getNewExtensionInstance(Class<? extends Entity> entityClass) throws UnifyException {
        return db().getNewExtensionInstance(entityClass);
    }

    @Transactional
    @Override
    public Date getToday() throws UnifyException {
        return CalendarUtils.getMidnightDate(db().getNow());
    }

    @Transactional
    @Override
    public Date getNow() throws UnifyException {
        return db().getNow();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    /**
     * Returns application database
     */
    protected Database db() throws UnifyException {
        return db;
    }

    /**
     * Gets a database instance using data source with supplied configuration name.
     * 
     * @param dataSourceConfigName
     *            the data source configuration name. Data source must be already
     *            registered in default application dynamic data source manager.
     * @return the database instance
     * @throws UnifyException
     *             if an error occurs
     */
    protected Database db(String dataSourceConfigName) throws UnifyException {
        return dynamicSqlDatabaseManager.getDynamicSqlDatabase(dataSourceConfigName);
    }

    /**
     * Executes a business logic unit.
     * 
     * @param taskMonitor
     *            a task monitoring object
     * @param unitName
     *            the unit name
     * @param parameters
     *            logic input parameters
     * @return the business logic output
     * @throws UnifyException
     *             if an error occurs
     */
    protected BusinessLogicOutput executeBusinessLogic(TaskMonitor taskMonitor, String unitName,
            Map<String, Object> parameters) throws UnifyException {
        BusinessLogicUnit blu = (BusinessLogicUnit) getComponent(unitName);
        BusinessLogicInput input = new BusinessLogicInput(taskMonitor);
        input.setParameters(parameters);

        BusinessLogicOutput output = new BusinessLogicOutput();
        blu.execute(input, output);
        return output;
    }

    /**
     * Launches a task.
     * 
     * @param taskSetup
     *            the task setup
     * @return the task monitor
     * @throws UnifyException
     *             if an error occurs
     */
    protected TaskMonitor launchTask(TaskSetup taskSetup) throws UnifyException {
        return taskLauncher.launchTask(taskSetup);
    }

    /**
     * Commits all pending database transactions
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void commitTransactions() throws UnifyException {
        databaseTransactionManager.commit();
    }

    /**
     * Sets roll back on current transactions in database session.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setRollbackTransactions() throws UnifyException {
        databaseTransactionManager.setRollback();
    }
}
