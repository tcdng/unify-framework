/*
 * Copyright 2018-2023 The Code Department.
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

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.constant.BooleanType;
import com.tcdng.unify.core.constant.Gender;

/**
 * Test author record.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table(name = "AUTHOR")
public class Author extends AbstractTestVersionedTableEntity {

    @ForeignKey(type = Office.class, onDeleteCascade = true)
    private Long officeId;

    @Column
    private String name;

    @Column
    private Integer age;

    @Column
    private Gender gender;

    @ForeignKey
    private BooleanType retired;

    @ListOnly(key = "officeId", property = "size")
    private Integer officeSize;

    @ListOnly(key = "officeId", property = "address")
    private String officeAddress;

    @ListOnly(key = "officeId", property = "telephone")
    private String officeTelephone;

    @ListOnly(key = "retired", property = "description")
    private String retiredDesc;

    public Author(String name, Integer age, Gender gender, BooleanType retired, Long officeId) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.retired = retired;
        this.officeId = officeId;
    }

    public Author() {

    }

    public Integer getOfficeSize() {
        return officeSize;
    }

    public void setOfficeSize(Integer officeSize) {
        this.officeSize = officeSize;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BooleanType getRetired() {
        return retired;
    }

    public void setRetired(BooleanType retired) {
        this.retired = retired;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getOfficeTelephone() {
        return officeTelephone;
    }

    public void setOfficeTelephone(String officeTelephone) {
        this.officeTelephone = officeTelephone;
    }

    public String getRetiredDesc() {
        return retiredDesc;
    }

    public void setRetiredDesc(String retiredDesc) {
        this.retiredDesc = retiredDesc;
    }

}
