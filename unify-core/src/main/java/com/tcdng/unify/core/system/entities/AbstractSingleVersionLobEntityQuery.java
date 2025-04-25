/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.system.entities;

import com.tcdng.unify.core.database.Query;

/**
 * Convenient abstract base class for single version large object query.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class AbstractSingleVersionLobEntityQuery<T extends AbstractSingleVersionLobEntity> extends Query<T> {

    public AbstractSingleVersionLobEntityQuery(Class<T> clazz) {
        super(clazz);
    }

    public AbstractSingleVersionLobEntityQuery<T> applicationName(String applicationName) {
        return (AbstractSingleVersionLobEntityQuery<T>) addEquals("applicationName", applicationName);
    }

    public AbstractSingleVersionLobEntityQuery<T> categoryName(String categoryName) {
        return (AbstractSingleVersionLobEntityQuery<T>) addEquals("categoryName", categoryName);
    }

    public AbstractSingleVersionLobEntityQuery<T> objectName(String objectName) {
        return (AbstractSingleVersionLobEntityQuery<T>) addEquals("objectName", objectName);
    }

    public AbstractSingleVersionLobEntityQuery<T> isOlderVersion(long version) {
        return (AbstractSingleVersionLobEntityQuery<T>) addLessThan("version", version);
    }
}
