/*
 * Copyright 2018-2024 The Code Department.
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

package com.tcdng.unify.core.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;

/**
 * Entity type information.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class EntityTypeInfo {

	private String name;

	private int depth;
	
	private List<EntityTypeFieldInfo> fields;

	private EntityTypeInfo(String name, int depth) {
		this.name = name;
		this.depth = depth;
		this.fields = Collections.emptyList();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDepth() {
		return depth;
	}

	private void setFields(List<EntityTypeFieldInfo> fields) {
		this.fields = fields;
	}

	public List<EntityTypeFieldInfo> getFields() {
		return fields;
	}
	
	public static Builder newBuilder(String name, int depth) {
		return new Builder(name, depth);
	}
	
	public static class Builder {

		private Map<String, EntityTypeFieldInfo> fields;
		
		private EntityTypeInfo prefetch;
		
		public Builder(String name, int depth) {
			this.prefetch = new EntityTypeInfo(name, depth);
			this.fields = new LinkedHashMap<String, EntityTypeFieldInfo>();
		}

		public EntityTypeInfo prefetch() {
			return prefetch;
		}
		
		public Builder addForeignKeyInfo(String parentEntityName, String name, String column) {
			if (this.fields.containsKey(name)) {
				throw new IllegalArgumentException("Type information already contains field name [" + name + "]");
			}
			
			this.fields.put(name, new EntityTypeFieldInfo(DynamicEntityFieldType.FOREIGN_KEY, DataType.LONG, parentEntityName, name, column, null));
			return this;
		}

		public Builder addFieldInfo(DataType dataType, String name, String column, String sample) {
			if (this.fields.containsKey(name)) {
				throw new IllegalArgumentException("Type information already contains field name [" + name + "]");
			}
			
			this.fields.put(name, new EntityTypeFieldInfo(DynamicEntityFieldType.FIELD, dataType, null, name, column, sample));
			return this;
		}

		public Builder addChildInfo(String childEntityName, String name) {
			if (this.fields.containsKey(name)) {
				throw new IllegalArgumentException("Type information already contains field name [" + name + "]");
			}
			
			this.fields.put(name, new EntityTypeFieldInfo(DynamicEntityFieldType.CHILD, childEntityName, name));
			return this;
		}

		public Builder addChildListInfo(String childEntityName, String name) {
			if (this.fields.containsKey(name)) {
				throw new IllegalArgumentException("Type information already contains field name [" + name + "]");
			}
			
			this.fields.put(name, new EntityTypeFieldInfo(DynamicEntityFieldType.CHILDLIST, childEntityName, name));
			return this;
		}
		
		public EntityTypeInfo build() {
			prefetch.setFields(DataUtils.unmodifiableList(fields.values()));
			return prefetch;
		}
	}
}
