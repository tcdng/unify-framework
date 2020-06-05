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
package com.tcdng.unify.web;

/**
 * Request command data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class RequestCommand {

    private String targetId;

    private String childId;

    private String command;

    private int dataIndex;

    public RequestCommand(String targetId, String childId, int dataIndex, String command) {
        this.targetId = targetId;
        this.childId = childId;
        this.dataIndex = dataIndex;
        this.command = command;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getCommand() {
        return command;
    }

    public String getChildId() {
        return childId;
    }

    public int getDataIndex() {
        return dataIndex;
    }

    public boolean isWithChildRef() {
        return childId != null;
    }

    public boolean isWithDataIndex() {
        return dataIndex >= 0;
    }
}
