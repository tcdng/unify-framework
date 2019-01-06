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
package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;

/**
 * Convenient base class for remote call gate.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractRemoteCallGate extends AbstractUnifyComponent implements RemoteCallGate {

    @Configurable("false")
    private boolean openMode;

    @Override
    public void grantPass(String applicationCode, String functionCode) throws UnifyException {
        if (!openMode) {
            doGrantPass(applicationCode, functionCode);
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected abstract void doGrantPass(String applicationCode, String functionCode) throws UnifyException;
}
