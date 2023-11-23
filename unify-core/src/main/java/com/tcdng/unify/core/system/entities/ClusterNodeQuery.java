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

import java.util.Collection;
import java.util.Date;

import com.tcdng.unify.core.database.Query;

/**
 * Cluster node query.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ClusterNodeQuery extends Query<ClusterNode> {

    public ClusterNodeQuery() {
        super(ClusterNode.class);
    }

    public ClusterNodeQuery runtimeId(String runtimeId) {
        return (ClusterNodeQuery) addEquals("runtimeId", runtimeId);
    }

    public ClusterNodeQuery runtimeIdIn(Collection<String> runtimeId) {
        return (ClusterNodeQuery) addAmongst("runtimeId", runtimeId);
    }

    public ClusterNodeQuery runtimeNotEqual(String runtimeId) {
        return (ClusterNodeQuery) addNotEquals("runtimeId", runtimeId);
    }

    public ClusterNodeQuery lastHeartBeatOlderThan(Date expiryDt) {
        return (ClusterNodeQuery) addLessThan("lastHeartBeat", expiryDt);
    }
}
