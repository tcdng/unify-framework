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

import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.Table;
import com.tcdng.unify.core.annotation.UniqueConstraint;

/**
 * Entity for storing unique string information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Table(name = "CLUSTERUNIQUESTRING", uniqueConstraints = { @UniqueConstraint({ "uniqueString" }) })
public class ClusterUniqueString extends AbstractSystemSequencedEntity {

	@Column(length = 256)
	private String uniqueString;

	public String getUniqueString() {
		return uniqueString;
	}

	public void setUniqueString(String uniqueString) {
		this.uniqueString = uniqueString;
	}

}
