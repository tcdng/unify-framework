/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.annotation.Column;

/**
 * Abstract base class for single version large objects.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractSingleVersionLobEntity extends AbstractSequencedEntity {

    @Column(name = "APPLICATION_NM", length = 128)
    private String applicationName;
    
    @Column(name = "CATEGORY_NM", length = 128)
    private String categoryName;
    
    @Column(name = "OBJECT_NM", length = 128)
    private String objectName;
    
    @Column
    private long version;
    
    @Override
    public String getDescription() {
        return null;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}
