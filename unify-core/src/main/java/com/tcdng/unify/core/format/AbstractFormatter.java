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
package com.tcdng.unify.core.format;

import java.util.Locale;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.upl.AbstractUplComponent;

/**
 * Abstract formatter.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractFormatter<T> extends AbstractUplComponent implements Formatter<T> {

    private Class<T> dataType;

    public AbstractFormatter(Class<T> dataType) {
        this.dataType = dataType;
    }

    @Override
    public Locale getLocale() throws UnifyException {
        return getUplAttribute(Locale.class, "locale");
    }

    @Override
    public FormatHelper getFormatHelper() throws UnifyException {
        return (FormatHelper) getComponent(ApplicationComponents.APPLICATION_FORMATHELPER);
    }

    @Override
    public Class<T> getDataType() {
        return dataType;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
