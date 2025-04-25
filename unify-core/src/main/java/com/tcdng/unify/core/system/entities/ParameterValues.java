/*
 * Copyright (c) 2018-2025 The Code Department.
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
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;

/**
 * Parameter values entity.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Table(name = "UNPARAMVALUES", uniqueConstraints = { @UniqueConstraint({ "parametersDefId", "instTypeName", "instId" }) })
public class ParameterValues extends AbstractSystemSequencedEntity {

    @ForeignKey(ParametersDef.class)
    private Long parametersDefId;

    @Column(length = 64)
    private String instTypeName;

    @Column
    private Long instId;

    @ChildList
    private List<ParameterValue> parameterValues;

    @ListOnly(key = "parametersDefId", property = "typeName")
    private String typeName;

    public Long getParametersDefId() {
        return parametersDefId;
    }

    public void setParametersDefId(Long parametersDefId) {
        this.parametersDefId = parametersDefId;
    }

    public String getInstTypeName() {
        return instTypeName;
    }

    public void setInstTypeName(String instTypeName) {
        this.instTypeName = instTypeName;
    }

    public Long getInstId() {
        return instId;
    }

    public void setInstId(Long instId) {
        this.instId = instId;
    }

    public List<ParameterValue> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(List<ParameterValue> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
