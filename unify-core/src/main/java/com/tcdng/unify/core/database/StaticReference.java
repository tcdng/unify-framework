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
package com.tcdng.unify.core.database;

import com.tcdng.unify.convert.constants.EnumConst;

/**
 * Static reference.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public class StaticReference extends AbstractEntity {

    public static final int CODE_LENGTH = 10;

    public static final int DESCRIPTION_LENGTH = 48;

    private Class<? extends EnumConst> enumConstType;

    private String code;

    private String description;

    public StaticReference(Class<? extends EnumConst> enumConstType) {
        this.enumConstType = enumConstType;
    }

    @Override
    public Object getId() {
        return code;
    }

    public Class<? extends EnumConst> getEnumConstType() {
        return enumConstType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
