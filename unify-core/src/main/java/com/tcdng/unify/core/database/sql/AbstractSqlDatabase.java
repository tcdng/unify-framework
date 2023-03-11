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
package com.tcdng.unify.core.database.sql;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.database.AbstractDatabase;
import com.tcdng.unify.core.database.DatabaseSession;

/**
 * Default SQL database implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractSqlDatabase extends AbstractDatabase implements SqlDatabase {

    @Configurable(ApplicationComponents.APPLICATION_SQLSTATEMENTEXECUTOR)
    private SqlStatementExecutor sqlStatementExecutor;

    public void setSqlStatementExecutor(SqlStatementExecutor sqlStatementExecutor) {
        this.sqlStatementExecutor = sqlStatementExecutor;
    }

    @Override
	public boolean isReadOnly() throws UnifyException {
		return getDataSource().isReadOnly();
	}

	@Override
    public DatabaseSession createDatabaseSession() throws UnifyException {
        return new SqlDatabaseSessionImpl((SqlDataSource) getDataSource(), sqlStatementExecutor);
    }
}
