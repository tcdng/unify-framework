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
package com.tcdng.unify.core.database.sql.criterion.policy;

import com.tcdng.unify.core.database.sql.SqlDataSourceDialectPolicies;

/**
 * OR operator policy.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class OrPolicy extends CompoundPolicy {

    public OrPolicy(SqlDataSourceDialectPolicies rootPolicies) {
        super(" OR ", rootPolicies);
    }
}
