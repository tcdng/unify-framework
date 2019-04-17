/*
 * Copyright 2018-2019 The Code Department.
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

import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.Database;
import com.tcdng.unify.core.database.DatabaseTransactionManager;
import com.tcdng.unify.core.task.TaskLauncher;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.task.TaskSetup;

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
    private TaskLauncher taskLauncher;

    @Override
    public DatabaseTransactionManager tm() throws UnifyException {
        return db.getTransactionManager();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    /**
     * Returns associated database
     */
    protected Database db() {
        return db;
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
        BusinessLogicInput input = new BusinessLogicInput(taskMonitor, db().getName());
        input.setParameters(parameters);

        BusinessLogicOutput output = new BusinessLogicOutput();
        blu.execute(input, output);
        return output;
    }

    protected TaskMonitor launchTask(TaskSetup taskSetup) throws UnifyException {
        return taskLauncher.launchTask(taskSetup);
    }

    protected void addTaskMonitorSessionMessage(TaskMonitor taskMonitor, String messageKey, Object... params)
            throws UnifyException {
        if (taskMonitor != null) {
            taskMonitor.addMessage(resolveSessionMessage(messageKey, params));
        }
    }

    protected void addTaskMessage(TaskMonitor taskMonitor, String message) throws UnifyException {
        if (taskMonitor != null) {
            taskMonitor.addMessage(resolveSessionMessage(message));
        }
    }

    protected void commit() throws UnifyException {
        db.getTransactionManager().commit();
    }

    protected void setRollback() throws UnifyException {
        db.getTransactionManager().setRollback();
    }
}
