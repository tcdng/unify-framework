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
package com.tcdng.unify.core;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.util.DataUtils;

/**
 * Configuration for a unify component.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class UnifyComponentConfig implements Listable {

    private Class<? extends UnifyComponent> type;

    private String name;

    private String description;

    private boolean singleton;

    private UnifyComponentSettings settings;

    private List<UnifyComponentConfig> conflictList;
    
    public UnifyComponentConfig(String name, String description, Class<? extends UnifyComponent> type,
            boolean singleton) {
        this(UnifyComponentSettings.EMPTY_SETTINGS, name, description, type, singleton);
    }

    public UnifyComponentConfig(UnifyComponentSettings settings, String name, String description,
            Class<? extends UnifyComponent> type, boolean singleton) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.singleton = singleton;
        this.settings = settings;
    }

    @Override
	public String getListKey() {
		return name;
	}

	@Override
	public String getListDescription() {
		return description;
	}

	public Class<? extends UnifyComponent> getType() {
        return type;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (description == null) {
            return name;
        }
        return description;
    }

    public UnifyComponentSettings getSettings() {
        return settings;
    }

    public <T> T getSetting(Class<T> typeClass, String name) throws UnifyException {
        return settings.getSettingValue(typeClass, name, null);
    }

    public <T> T getSetting(Class<T> typeClass, String name, T defaultVal) throws UnifyException {
        return settings.getSettingValue(typeClass, name, defaultVal);
    }

    public void addConflict(UnifyComponentConfig conflictUnifyComponentConfig) {
        if (conflictList == null) {
            conflictList = new ArrayList<UnifyComponentConfig>();
        }
        
        conflictList.add(conflictUnifyComponentConfig);
    }
    
    public List<UnifyComponentConfig> getConflictList() {
        return conflictList;
    }

    public boolean isWithConfict() {
        return !DataUtils.isBlank(conflictList);
    }

}
