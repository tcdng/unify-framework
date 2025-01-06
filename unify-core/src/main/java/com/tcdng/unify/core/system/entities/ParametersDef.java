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
package com.tcdng.unify.core.system.entities;

import java.util.List;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.common.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;

/**
 * Parameters definition entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "UNPARAMETERSDEF", uniqueConstraints = { @UniqueConstraint({ "typeName" }) })
public class ParametersDef extends AbstractSystemSequencedEntity {

    @Column(name = "PARAMETERSDEF_NM", length = 64)
    private String typeName;

    @ChildList
    private List<ParameterDef> parameterDefs;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ParameterDef> getParameterDefs() {
        return parameterDefs;
    }

    public void setParameterDefs(List<ParameterDef> parameterDefs) {
        this.parameterDefs = parameterDefs;
    }

    public boolean isEmpty() {
        return parameterDefs == null || parameterDefs.isEmpty();
    }
}
