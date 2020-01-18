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
package com.tcdng.unify.core;

/**
 * Configuration for a unify component.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyComponentConfig {

    private Class<? extends UnifyComponent> type;

    private String name;

    private String description;

    private boolean singleton;

    private UnifyComponentSettings settings;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\tname = ").append(name).append(",\n");
        sb.append("\ttype = ").append(type).append(",\n");
        sb.append("\tdescription = ").append(description).append(",\n");
        sb.append("\tsingleton = ").append(singleton).append(",\n");
        sb.append("\tsettings = ").append(this.settings);
        sb.append("}");
        return sb.toString();
    }
}
