/*
 * Copyright 2018-2019 The Code Department.
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

import java.util.Date;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Table;

/**
 * Test user entity.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "TEST_USER")
public class User extends AbstractTestTableEntity {

    @Column
    private String name;

    @Column(length = 128, transformer = "oneway-stringcryptograph")
    private String password;

    @Column
    private Date createDt;

    public User() {

    }

    public User(String name, String password) {
        this(name, password, new Date());
    }

    public User(String name, String password, Date createDt) {
        this.name = name;
        this.password = password;
        this.createDt = createDt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }
}
