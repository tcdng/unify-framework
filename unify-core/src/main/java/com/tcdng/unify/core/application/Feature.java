/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.application;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.database.AbstractEntity;

/**
 * Feature entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table("UNFEATURE")
public class Feature extends AbstractEntity {

    @Id(name = "FEATURE_CD", length = 40)
    private String code;

    @Column(name = "FEATURE_VALUE", length = 64)
    private String value;

    @Override
    public Object getId() {
        return code;
    }

    @Override
    public String getDescription() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
