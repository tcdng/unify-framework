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

import java.util.Date;
import java.util.List;

/**
 * Unify container information data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class UnifyContainerInfo {

    private String name;

    private String id;

    private String version;

    private String auxiliaryVersion;
    
    private String applicationLocale;
    
    private String hostAddress;

    private String hostHome;

    private Date startTime;

    private long usedMemory;

    private long totalMemory;

    private boolean clusterMode;

    private boolean productionMode;

    private boolean deploymentMode;

    private List<UnifyComponentInfo> componentInfoList;

    private List<UnifyInterfaceInfo> interfaceInfoList;

    private List<UnifyContainerSettingInfo> settingInfoList;

	public UnifyContainerInfo(String name, String id, String version, String auxiliaryVersion, String applicationLocale,
			String hostAddress, String hostHome, Date startTime, long usedMemory, long totalMemory, boolean clusterMode,
			boolean productionMode, boolean deploymentMode, List<UnifyComponentInfo> componentInfoList,
			List<UnifyInterfaceInfo> interfaceInfoList, List<UnifyContainerSettingInfo> settingInfoList) {
		this.name = name;
		this.id = id;
		this.version = version;
		this.auxiliaryVersion = auxiliaryVersion;
		this.hostAddress = hostAddress;
		this.applicationLocale = applicationLocale;
		this.hostHome = hostHome;
		this.startTime = startTime;
		this.usedMemory = usedMemory;
		this.totalMemory = totalMemory;
		this.clusterMode = clusterMode;
		this.productionMode = productionMode;
		this.deploymentMode = deploymentMode;
		this.componentInfoList = componentInfoList;
		this.interfaceInfoList = interfaceInfoList;
		this.settingInfoList = settingInfoList;
	}

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getAuxiliaryVersion() {
        return auxiliaryVersion;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getApplicationLocale() {
		return applicationLocale;
	}

	public String getHostHome() {
        return hostHome;
    }

    public Date getStartTime() {
        return startTime;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
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

    public List<UnifyComponentInfo> getComponentInfoList() {
        return componentInfoList;
    }

    public List<UnifyInterfaceInfo> getInterfaceInfoList() {
        return interfaceInfoList;
    }

    public List<UnifyContainerSettingInfo> getSettingInfoList() {
        return settingInfoList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(name).append(',');
        sb.append("id=").append(id).append(',');
        sb.append("version=").append(version).append(',');
        sb.append("clusterMode=").append(clusterMode).append(',');
        sb.append("productionMode=").append(productionMode).append(',');
        sb.append("deploymentMode=").append(deploymentMode);
        return sb.toString();
    }
}
