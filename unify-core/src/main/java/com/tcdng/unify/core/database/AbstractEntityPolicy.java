/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.database;

import java.util.Date;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Abstract convenience class for entity policy.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractEntityPolicy extends AbstractUnifyComponent implements EntityPolicy {

    private boolean setNow;

    public AbstractEntityPolicy() {
        this(false);
    }

    public AbstractEntityPolicy(boolean setNow) {
        this.setNow = setNow;
    }

    @Override
    public boolean isSetNow() {
        return setNow;
    }

    @Override
    public Object preCreate(Entity record, Date now) throws UnifyException {
        return null;
    }

    @Override
    public void preUpdate(Entity record, Date now) throws UnifyException {

    }

    @Override
    public void preDelete(Entity record, Date now) throws UnifyException {

    }

    @Override
    public void preQuery(Query<? extends Entity> query) throws UnifyException {

    }

    @Override
    public void onCreateError(Entity record) {

    }

    @Override
    public void onUpdateError(Entity record) {

    }

    @Override
    public void onDeleteError(Entity record) {

    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
