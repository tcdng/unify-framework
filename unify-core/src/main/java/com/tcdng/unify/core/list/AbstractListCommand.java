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
package com.tcdng.unify.core.list;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Abstract base component for a list command.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractListCommand<T> extends AbstractUnifyComponent implements ListCommand<T> {

    private Class<T> paramType;

    public AbstractListCommand(Class<T> paramType) {
        this.paramType = paramType;
    }

    @Override
    public Class<T> getParamType() throws UnifyException {
        return paramType;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
