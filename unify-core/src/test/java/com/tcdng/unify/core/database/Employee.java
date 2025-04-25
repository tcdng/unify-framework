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
package com.tcdng.unify.core.database;

import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.common.annotation.UniqueConstraint;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;

/**
 * Test employee entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "EMPLOYEE", uniqueConstraints = { @UniqueConstraint({ "fullName" }) })
public class Employee extends AbstractTestVersionedTableEntity {

    @ForeignKey(type = Employee.class, nullable = true)
    private Long managerId;
    
    @Column
    private String fullName;

    @ListOnly(key = "managerId", property = "fullName")
    private String managerFullName;
    
    public Employee(Long managerId, String fullName) {
        this.managerId = managerId;
        this.fullName = fullName;
    }

    public Employee(String fullName) {
        this.fullName = fullName;
    }

    public Employee() {

    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getManagerFullName() {
        return managerFullName;
    }

    public void setManagerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
    }

}
