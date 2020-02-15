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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * A unify container configuration.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyContainerConfig {

    private Map<String, UnifyComponentConfig> unifyComponentConfigs;

    private Map<String, Object> settings;

    private Map<String, String> aliases;

    private List<UnifyStaticSettings> staticSettings;

    private String deploymentVersion;

    private String nodeId;

    private boolean clusterMode;

    private boolean productionMode;

    private boolean deploymentMode;

    private UnifyContainerConfig(Map<String, UnifyComponentConfig> unifyComponentConfigs, Map<String, Object> settings,
            Map<String, String> aliases, List<UnifyStaticSettings> staticSettings, String deploymentVersion,
            String nodeId, boolean clusterMode, boolean productionMode, boolean deploymentMode) {
        this.unifyComponentConfigs = unifyComponentConfigs;
        this.settings = settings;
        this.aliases = aliases;
        this.staticSettings = staticSettings;
        this.deploymentVersion = deploymentVersion;
        this.nodeId = nodeId;
        this.clusterMode = clusterMode;
        this.productionMode = productionMode;
        this.deploymentMode = deploymentMode;
    }

    public String getDeploymentVersion() {
        return deploymentVersion;
    }

    public String getNodeId() {
        return nodeId;
    }

    public boolean isClusterMode() {
        return clusterMode;
    }

    public boolean isProductionMode() {
        return productionMode;
    }

    public boolean isDeploymentMode() {
        return deploymentMode;
    }

    public Set<String> getPropertyNames() {
        return settings.keySet();
    }

    public Object getProperty(String name) {
        return settings.get(name);
    }

    public Map<String, Object> getProperties() {
        return settings;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    public List<UnifyStaticSettings> getStaticSettings() {
        return staticSettings;
    }

    public UnifyComponentConfig getComponentConfig(String name) {
        return unifyComponentConfigs.get(name);
    }

    public Collection<UnifyComponentConfig> getComponentConfigs() {
        return unifyComponentConfigs.values();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Map<String, UnifyComponentConfig> unifyComponentConfigs;

        private Map<String, Object> settings;

        private Map<String, String> aliases;

        private List<UnifyStaticSettings> staticSettings;

        private String deploymentVersion;

        private String nodeId;

        private boolean clusterMode;

        private boolean productionMode;

        private boolean deploymentMode;

        private Builder() {
            unifyComponentConfigs = new HashMap<String, UnifyComponentConfig>();
            settings = new HashMap<String, Object>();
            aliases = new HashMap<String, String>();
            staticSettings = new ArrayList<UnifyStaticSettings>();
        }

        public Builder deploymentVersion(String deploymentVersion) {
            this.deploymentVersion = deploymentVersion;
            return this;
        }

        public Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder clusterMode(boolean clusterMode) {
            this.clusterMode = clusterMode;
            return this;
        }

        public Builder productionMode(boolean productionMode) {
            this.productionMode = productionMode;
            return this;
        }

        public Builder deploymentMode(boolean deploymentMode) {
            this.deploymentMode = deploymentMode;
            return this;
        }

        public Builder setProperty(String name, Object value) {
            settings.put(name, value);
            return this;
        }

        public Builder setPropertyIfBlank(String name, Object value) {
            if (settings.get(name) == null) {
                settings.put(name, value);
            }
            return this;
        }

        public Builder setAlias(String name, String actualName) {
            aliases.put(name, actualName);
            return this;
        }

        public Builder addStaticSettings(UnifyStaticSettings unifyStaticSettings) {
            staticSettings.add(unifyStaticSettings);
            return this;
        }

        public Builder addComponentConfig(String name, String description, Class<? extends UnifyComponent> type,
                boolean singleton) throws UnifyException {
            addComponentConfig(name, description, type, singleton, new UnifyComponentSettings());
            return this;
        }

        public Builder addComponentConfig(String name, String description, Class<? extends UnifyComponent> type,
                boolean singleton, UnifyComponentSettings settings) throws UnifyException {
            addComponentConfig(name, description, type, singleton, false, settings);
            return this;
        }

        public Builder addComponentConfig(String name, String description, Class<? extends UnifyComponent> type,
                boolean singleton, boolean overwrite, UnifyComponentSettings settings) throws UnifyException {
            if (StringUtils.isBlank(name)) {
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_HAS_NO_NAME, type);
            }

            UnifyComponentConfig newUnifyComponentConfig =
                    new UnifyComponentConfig(settings, name, description, type, singleton);

            UnifyComponentConfig existUnifyComponentConfig = unifyComponentConfigs.get(name);
            if (existUnifyComponentConfig != null && !overwrite) {
                existUnifyComponentConfig.addConflict(newUnifyComponentConfig);
                throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_WITH_NAME_EXISTS, name,
                        unifyComponentConfigs.get(name), type);
            }

            unifyComponentConfigs.put(name, newUnifyComponentConfig);
            return this;
        }

        public Builder readXml(File xmlFile) throws UnifyException {
            UnifyConfigUtils.readConfigFromXml(this, xmlFile);
            return this;
        }

        public Builder readXml(InputStream xmlInputStream) throws UnifyException {
            UnifyConfigUtils.readConfigFromXml(this, xmlInputStream);
            return this;
        }

        public Builder readXml(String xmlConfig) throws UnifyException {
            UnifyConfigUtils.readConfigFromXml(this, xmlConfig);
            return this;
        }

        public Builder scan(TypeRepository typeRepository) throws UnifyException {
            UnifyConfigUtils.readConfigFromTypeRepository(this, typeRepository);
            return this;
        }

        public UnifyContainerConfig build() throws UnifyException {
            DataUtils.sort(staticSettings, UnifyStaticSettings.class, "level", true);
            return new UnifyContainerConfig(Collections.unmodifiableMap(unifyComponentConfigs),
                    Collections.unmodifiableMap(settings), Collections.unmodifiableMap(aliases),
                    Collections.unmodifiableList(staticSettings), deploymentVersion, nodeId, clusterMode,
                    productionMode, deploymentMode);
        }
    }
}
