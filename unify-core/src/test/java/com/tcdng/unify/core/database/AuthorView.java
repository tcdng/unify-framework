/*
 * Copyright 2018-2022 The Code Department.
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

import com.tcdng.unify.core.annotation.Id;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.TableRef;
import com.tcdng.unify.core.annotation.View;
import com.tcdng.unify.core.annotation.ViewRestriction;
import com.tcdng.unify.core.constant.Gender;
import com.tcdng.unify.core.criterion.RestrictionType;

/**
 * Author view-only entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@View(
    name = "V_AUTHORVIEW", primaryAlias = "t1",
    tables = {
            @TableRef(alias = "t1", entity = Author.class),
            @TableRef(alias = "t2", entity = Office.class) },
    restrictions = {
            @ViewRestriction(leftProperty = "t1.officeId", type = RestrictionType.EQUALS, rightProperty = "t2.id")})
public class AuthorView extends AbstractTestViewEntity {

    @Id
    private Long authorId;

    @ListOnly(property = "officeId")
    private Long authorOfficeId;

    @ListOnly(property = "name", name = "AUTHOR_NM")
    private String authorName;

    @ListOnly(property = "age")
    private Integer authorAge;

    @ListOnly(property = "gender")
    private Gender authorGender;

    @ListOnly(property = "t2.address")
    private String officeAddress;

    @ListOnly(property = "t2.telephone")
    private String officeTelephone;

    @ListOnly(property = "t2.size")
    private Integer officeSize;

    @Override
    public Object getId() {
        return authorId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getAuthorOfficeId() {
        return authorOfficeId;
    }

    public void setAuthorOfficeId(Long authorOfficeId) {
        this.authorOfficeId = authorOfficeId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getAuthorAge() {
        return authorAge;
    }

    public void setAuthorAge(Integer authorAge) {
        this.authorAge = authorAge;
    }

    public Gender getAuthorGender() {
        return authorGender;
    }

    public void setAuthorGender(Gender authorGender) {
        this.authorGender = authorGender;
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

    public Integer getOfficeSize() {
        return officeSize;
    }

    public void setOfficeSize(Integer officeSize) {
        this.officeSize = officeSize;
    }

}
