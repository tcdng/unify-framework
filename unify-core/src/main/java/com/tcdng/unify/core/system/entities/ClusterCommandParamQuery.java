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
package com.tcdng.unify.core.system.entities;

import java.util.Collection;

/**
 * Cluster command parameter query.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ClusterCommandParamQuery extends SequencedEntityQuery<ClusterCommandParam> {

    public ClusterCommandParamQuery() {
        super(ClusterCommandParam.class);
    }

    public ClusterCommandParamQuery clusterCommandId(Long clusterCommandId) {
        return (ClusterCommandParamQuery) addEquals("clusterCommandId", clusterCommandId);
    }

    public ClusterCommandParamQuery clusterCommandIdIn(Collection<Long> clusterCommandId) {
        return (ClusterCommandParamQuery) addAmongst("clusterCommandId", clusterCommandId);
    }

    public ClusterCommandParamQuery nodeId(String nodeId) {
        return (ClusterCommandParamQuery) addEquals("nodeId", nodeId);
    }

    public ClusterCommandParamQuery nodeIdIn(Collection<String> nodeId) {
        return (ClusterCommandParamQuery) addAmongst("nodeId", nodeId);
    }
}
