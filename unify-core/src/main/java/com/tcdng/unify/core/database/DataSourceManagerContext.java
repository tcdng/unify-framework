/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.database;

/**
 * Datasource manager context
 * 
 * @author The Code Department
 * @since 1.0
 */
public class DataSourceManagerContext {
	
	private final DataSourceEntityContext entityCtx;

	private final DataSourceManagerOptions options;
	
	public DataSourceManagerContext(DataSourceManagerContext ctx, DataSourceManagerOptions options) {
		this.entityCtx = ctx.getEntityCtx();
		this.options = options;
	}
	
	public DataSourceManagerContext(DataSourceEntityContext entityCtx, DataSourceManagerOptions options) {
		this.entityCtx = entityCtx;
		this.options = options;
	}
	
	public DataSourceEntityContext getEntityCtx() {
		return entityCtx;
	}

	public DataSourceManagerOptions getOptions() {
		return options;
	}

}
