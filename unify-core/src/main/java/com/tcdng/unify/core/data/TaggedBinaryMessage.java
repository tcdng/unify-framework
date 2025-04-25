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

package com.tcdng.unify.core.data;

/**
 * Tagged binary message.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class TaggedBinaryMessage {

    private String tag;

    private String branchCode;

    private String departmentCode;

    private String consumer;

    private byte[] message;

    public TaggedBinaryMessage(String tag, String branchCode, String departmentCode, String consumer, byte[] message) {
        this.tag = tag;
        this.branchCode = branchCode;
        this.departmentCode = departmentCode;
        this.consumer = consumer;
        this.message = message;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getTag() {
        return tag;
    }

    public String getConsumer() {
        return consumer;
    }

    public byte[] getMessage() {
        return message;
    }
}
