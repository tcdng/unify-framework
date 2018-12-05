/*
 * Copyright 2014 The Code Department
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
 * Cluster command query.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ClusterCommandQuery extends SequencedEntityQuery<ClusterCommand> {

	public ClusterCommandQuery() {
		super(ClusterCommand.class);
	}

	public ClusterCommandQuery nodeId(String nodeId) {
		return (ClusterCommandQuery) this.equals("nodeId", nodeId);
	}

	public ClusterCommandQuery nodeIdIn(Collection<String> nodeId) {
		return (ClusterCommandQuery) this.amongst("nodeId", nodeId);
	}
}
