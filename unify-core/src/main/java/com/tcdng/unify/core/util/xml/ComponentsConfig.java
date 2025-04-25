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
package com.tcdng.unify.core.util.xml;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Unify components configuration.
 * 
 * @author The Code Department
 * @since 4.1
 */
@JsonInclude(Include.NON_NULL)
public class ComponentsConfig {

	@JacksonXmlProperty(localName = "aliases")
    private AliasesConfig aliasesConfig;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "component")
    private List<ComponentConfig> componentConfigList;

    public AliasesConfig getAliasesConfig() {
        return aliasesConfig;
    }

    public void setAliasesConfig(AliasesConfig aliasesConfig) {
        this.aliasesConfig = aliasesConfig;
    }

    public List<ComponentConfig> getComponentConfigList() {
        return componentConfigList;
    }

    public void setComponentConfigList(List<ComponentConfig> componentConfigList) {
        this.componentConfigList = componentConfigList;
    }
}
