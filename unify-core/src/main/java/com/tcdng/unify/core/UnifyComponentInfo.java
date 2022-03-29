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
package com.tcdng.unify.core;

import java.util.List;

/**
 * Unify component information data object.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UnifyComponentInfo {

    private String name;

    private String type;

    private int passCount;

    private int failCount;

    private List<Setting> settingInfoList;

    public UnifyComponentInfo(String name, String type, int passCount, int failCount, List<Setting> settingInfoList) {
        this.name = name;
        this.type = type;
        this.passCount = passCount;
        this.failCount = failCount;
        this.settingInfoList = settingInfoList;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public List<Setting> getSettingInfoList() {
        return settingInfoList;
    }
}
