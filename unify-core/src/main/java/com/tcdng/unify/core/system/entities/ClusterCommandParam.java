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
package com.tcdng.unify.core.system.entities;

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Table;

/**
 * Cluster command parameter entity.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Table("UNCLUSTERCOMMANDPRM")
public class ClusterCommandParam extends AbstractSystemSequencedEntity {

    @ForeignKey(ClusterCommand.class)
    private Long clusterCommandId;

    @Column(length = 64)
    private String parameter;

    @ListOnly(key = "clusterCommandId", property = "nodeId")
    private String nodeId;

    public Long getClusterCommandId() {
        return clusterCommandId;
    }

    public void setClusterCommandId(Long clusterCommandId) {
        this.clusterCommandId = clusterCommandId;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
