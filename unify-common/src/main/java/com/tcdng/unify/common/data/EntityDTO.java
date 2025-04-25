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
package com.tcdng.unify.common.data;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.common.constants.ConnectEntityBaseType;

/**
 * Entity DTO.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class EntityDTO {

    private ConnectEntityBaseType baseType;

    private String dataSourceAlias;

    private String name;

    private String description;

    private String tableName;

    private String implClass;

    private boolean dynamic;

    private boolean actionPolicy;

    private boolean ignoreOnSync;

    private List<EntityFieldDTO> fields;

    public EntityDTO(EntityInfo entityInfo) {
        this.baseType = entityInfo.getBaseType();
        this.dataSourceAlias = entityInfo.getDataSourceAlias();
        this.name = entityInfo.getName();
        this.description = entityInfo.getDescription();
        this.tableName = entityInfo.getTableName();
        this.implClass = entityInfo.getImplClass().getName();
        this.dynamic = entityInfo.isDynamic();
        this.ignoreOnSync = entityInfo.isIgnoreOnSync();
        this.actionPolicy = entityInfo.isWithActionPolicy();
        this.fields = new ArrayList<EntityFieldDTO>();
        for (EntityFieldInfo entityFieldInfo : entityInfo.getAllFields()) {
            EntityFieldDTO entityFieldDTO = new EntityFieldDTO(entityFieldInfo);
            this.fields.add(entityFieldDTO);
        }
    }

    public EntityDTO() {

    }

    public ConnectEntityBaseType getBaseType() {
        return baseType;
    }

    public void setBaseType(ConnectEntityBaseType baseType) {
        this.baseType = baseType;
    }

    public String getDataSourceAlias() {
        return dataSourceAlias;
    }

    public void setDataSourceAlias(String dataSourceAlias) {
        this.dataSourceAlias = dataSourceAlias;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getImplClass() {
		return implClass;
	}

	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public boolean isIgnoreOnSync() {
        return ignoreOnSync;
    }

    public void setIgnoreOnSync(boolean ignoreOnSync) {
        this.ignoreOnSync = ignoreOnSync;
    }

    public boolean isActionPolicy() {
        return actionPolicy;
    }

    public void setActionPolicy(boolean actionPolicy) {
        this.actionPolicy = actionPolicy;
    }

    public List<EntityFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<EntityFieldDTO> fields) {
        this.fields = fields;
    }

}
