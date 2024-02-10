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

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;

/**
 * Parameter definition entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "UNPARAMDEF", uniqueConstraints = { @UniqueConstraint({ "parametersDefId", "name" }) })
public class ParameterDef extends AbstractSequencedEntity {

    @ForeignKey(ParametersDef.class)
    private Long parametersDefId;

    @Column(length = 64)
    private String name;

    @Column(length = 96)
    private String description;

    @Column(length = 128)
    private String editor;

    @Column(length = 128)
    private String type;

    @Column(name = "SORT_ORDER")
    private int order;

    @Column
    private boolean mandatory;

    public Long getParametersDefId() {
        return parametersDefId;
    }

    public void setParametersDefId(Long parametersDefId) {
        this.parametersDefId = parametersDefId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
