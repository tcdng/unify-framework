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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Unify configuration.
 * 
 * @author The Code Department
 */
@JsonInclude(Include.NON_NULL)
@JacksonXmlRootElement(localName = "unify")
@JsonPropertyOrder({"propertiesConfig", "componentsConfig" })
public class UnifyConfig {

    @JacksonXmlProperty(isAttribute = true)
    private final String xmlns;

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
    private final String xmlnsXsi;

    @JacksonXmlProperty(isAttribute = true, localName = "xsi:schemaLocation")
    private final String xsiSchemaLocation;

    private String schemaLocation;

	@JacksonXmlProperty(isAttribute = true)
    private String version;

	@JacksonXmlProperty(isAttribute = true)
    private String nodeId;

	@JacksonXmlProperty(isAttribute = true)
    private boolean cluster;

	@JacksonXmlProperty(isAttribute = true)
    private boolean production;

	@JacksonXmlProperty(localName = "properties")
    private PropertiesConfig propertiesConfig;

	@JacksonXmlProperty(localName = "components")
    private ComponentsConfig componentsConfig;

    public UnifyConfig() {
        this.xmlns = "http://flowcentraltech.com/schema-common";
        this.xmlnsXsi = "http://www.w3.org/2001/XMLSchema-instance";
        this.xsiSchemaLocation = this.xmlns + " https://schema.flowcentralplatform.com/xsd/unify-4.0.0.xsd";
    }

    public final String getXmlns() {
        return xmlns;
    }

    public final String getXmlnsXsi() {
        return xmlnsXsi;
    }

    public final String getXsiSchemaLocation() {
        return xsiSchemaLocation;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public PropertiesConfig getPropertiesConfig() {
        return propertiesConfig;
    }

    public void setPropertiesConfig(PropertiesConfig propertiesConfig) {
        this.propertiesConfig = propertiesConfig;
    }

    public ComponentsConfig getComponentsConfig() {
        return componentsConfig;
    }

    public void setComponentsConfig(ComponentsConfig componentsConfig) {
        this.componentsConfig = componentsConfig;
    }
}
