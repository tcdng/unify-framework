/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.core.util.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Unify components configuration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ComponentsConfig {

    private AliasesConfig aliasesConfig;

    private List<ComponentConfig> componentConfigList;

    public AliasesConfig getAliasesConfig() {
        return aliasesConfig;
    }

    @XmlElement(name = "aliases")
    public void setAliasesConfig(AliasesConfig aliasesConfig) {
        this.aliasesConfig = aliasesConfig;
    }

    public List<ComponentConfig> getComponentConfigList() {
        return componentConfigList;
    }

    @XmlElement(name = "component")
    public void setComponentConfigList(List<ComponentConfig> componentConfigList) {
        this.componentConfigList = componentConfigList;
    }
}
