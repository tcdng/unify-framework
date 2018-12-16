/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.task;

import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.util.EnumUtils;

/**
 * Taskable status constants.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@StaticList("taskstatuslist")
public enum TaskStatus implements EnumConst {

    INITIALISED("I"), RUNNING("R"), COMPLETED("C"), CANCELED("X"), FAILED("F"), ABORTED("A"), CRITICAL("T");

    private final String code;

    private TaskStatus(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return this.code;
    }

    public static TaskStatus fromCode(String code) {
        return EnumUtils.fromCode(TaskStatus.class, code);
    }

    public static TaskStatus fromName(String name) {
        return EnumUtils.fromName(TaskStatus.class, name);
    }
}
