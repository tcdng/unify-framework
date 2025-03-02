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
package com.tcdng.unify.common.constants;

/**
 * Interconnect entity base type.
 * 
 * @author The Code Department
 * @since 1.0
 */
public enum ConnectEntityBaseType {
    BASE_ENTITY,
    BASE_VERSION_ENTITY,
    BASE_AUDIT_ENTITY,
    BASE_WORK_ENTITY;

    public boolean isAuditType() {
        return BASE_AUDIT_ENTITY.equals(this) || BASE_WORK_ENTITY.equals(this);
    }

    public boolean isWorkEntityType() {
        return BASE_WORK_ENTITY.equals(this);
    }
}
