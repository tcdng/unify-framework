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

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.ValueStoreFactory;
import com.tcdng.unify.core.database.Database;

/**
 * Convenient base class for a business logic unit.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractBusinessLogicUnit extends AbstractUnifyComponent implements BusinessLogicUnit {

    @Configurable
    private ValueStoreFactory valueStoreFactory;

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected ValueStoreFactory getValueStoreFactory() {
        return valueStoreFactory;
    }

    protected Database getDatabase(BusinessLogicInput input) throws UnifyException {
        return (Database) getComponent(input.getDbName());
    }
}
