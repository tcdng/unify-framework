/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.core.data;

/**
 * Tagged XML message.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TaggedXmlMessage {

    private String tag;

    private String branchCode;

    private String departmentCode;

    private String consumer;

    private String message;

    public TaggedXmlMessage(String tag, String branchCode, String departmentCode, String consumer, String message) {
        this.tag = tag;
        this.branchCode = branchCode;
        this.departmentCode = departmentCode;
        this.consumer = consumer;
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getConsumer() {
        return consumer;
    }

    public String getMessage() {
        return message;
    }
}
