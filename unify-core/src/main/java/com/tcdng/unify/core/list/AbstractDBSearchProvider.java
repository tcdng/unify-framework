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
package com.tcdng.unify.core.list;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.business.GenericBusinessModule;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient base class for database search provider list commands.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDBSearchProvider extends AbstractSearchProviderListCommand {

    @Configurable(ApplicationComponents.APPLICATION_GENERICBUSINESSMODULE)
    private GenericBusinessModule genericBusinessModule;

    @Configurable("30")
    private int searchLimit;

    private Class<? extends Entity> recordType;

    private String keyProperty;

    private String descProperty;

    public AbstractDBSearchProvider(Class<? extends Entity> recordType) {
        this(recordType, "id", "description");
    }

    public AbstractDBSearchProvider(Class<? extends Entity> recordType, String keyProperty, String descProperty) {
        this.recordType = recordType;
        this.keyProperty = keyProperty;
        this.descProperty = descProperty;
    }

    @Override
    public List<? extends Listable> search(String filter) throws UnifyException {
        return execute(getSessionLocale(), new SearchProviderParams(null, filter));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<? extends Listable> execute(Locale locale, SearchProviderParams params) throws UnifyException {
        String key = params.getKey();
        if (!StringUtils.isBlank(key)) {
            return genericBusinessModule.listAll(new Query(recordType).equals(keyProperty, key).limit(searchLimit));
        }

        String filter = params.getFilter();
        if (!StringUtils.isBlank(filter)) {
            return genericBusinessModule.listAll(new Query(recordType).like(descProperty, filter).limit(searchLimit));
        }

        return Collections.emptyList();
    }

    @Override
    public String getKeyProperty() throws UnifyException {
        return keyProperty;
    }

    @Override
    public String getDescProperty() throws UnifyException {
        return descProperty;
    }

}
