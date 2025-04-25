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

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.TableExt;
import com.tcdng.unify.core.constant.BooleanType;

/**
 * Test branch extension record.
 * 
 * @author The Code Department
 * @since 4.1
 */
@TableExt
public class BranchExt extends Branch {

    @ForeignKey(type = Office.class)
    private Long officeId;

    @Column
    private String state;

    @Column
    private String country;

    @ForeignKey
    private BooleanType closed;

    @ListOnly(key = "officeId", property = "address")
    private String officeAddress;

    @ListOnly(key = "officeId", property = "telephone")
    private String officeTelephone;

    @ListOnly(key = "closed", property = "description")
    private String closedDesc;

    public BranchExt(String code, String description, String sortCode, Long officeId, String state, String country,
            BooleanType closed) {
        super(code, description, sortCode);
        this.officeId = officeId;
        this.state = state;
        this.country = country;
        this.closed = closed;
    }

    public BranchExt() {

    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BooleanType getClosed() {
        return closed;
    }

    public void setClosed(BooleanType closed) {
        this.closed = closed;
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

    public String getClosedDesc() {
        return closedDesc;
    }

    public void setClosedDesc(String closedDesc) {
        this.closedDesc = closedDesc;
    }
}
