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
package com.tcdng.unify.web;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.data.ValueStoreFactory;

/**
 * Abstract base controller component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractController extends AbstractUnifyComponent implements Controller {

    @Configurable
    private RequestContextUtil requestContextUtil;

    @Configurable
    private ValueStoreFactory valueStoreFactory;

    private boolean secured;

    public AbstractController(boolean secured) {
        this.secured = secured;
    }

    @Override
    public boolean isSecured() {
        return this.secured;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    /**
     * Creates a value store using supplied source object.
     * 
     * @param sourceObject
     *            the source object to use
     * @return ValueStore new instance of a value store
     * @throws UnifyException
     *             if an error occurs
     */
    protected ValueStore createValueStore(Object sourceObject) throws UnifyException {
        return valueStoreFactory.getValueStore(sourceObject, 0);
    }

    /**
     * Returns application request context utility component.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected RequestContextUtil getRequestContextUtil() throws UnifyException {
        return requestContextUtil;
    }
}
