/*
 * Copyright 2018-2023 The Code Department.
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Unify configuration.
 * 
 * @author The Code Department
 */
@XmlRootElement(name = "unify")
public class UnifyConfig {

    private String version;

    private String nodeId;

    private boolean cluster;

    private boolean production;

    private PropertiesConfig propertiesConfig;

    private ComponentsConfig componentsConfig;

    public String getVersion() {
        return version;
    }

    @XmlAttribute(required = true)
    public void setVersion(String version) {
        this.version = version;
    }

    public String getNodeId() {
        return nodeId;
    }

    @XmlAttribute(required = true)
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public boolean isProduction() {
        return production;
    }

    @XmlAttribute(required = true)
    public void setProduction(boolean production) {
        this.production = production;
    }

    public boolean isCluster() {
        return cluster;
    }

    @XmlAttribute(required = true)
    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public PropertiesConfig getPropertiesConfig() {
        return propertiesConfig;
    }

    @XmlElement(name = "properties")
    public void setPropertiesConfig(PropertiesConfig propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }

    public ComponentsConfig getComponentsConfig() {
        return componentsConfig;
    }

    @XmlElement(name = "components")
    public void setComponentsConfig(ComponentsConfig componentsConfig) {
        this.componentsConfig = componentsConfig;
    }
}
