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
package com.tcdng.unify.core.database;

import java.util.Date;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.criterion.Restriction;

/**
 * Policy class for test record.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("testentity-policy")
public class TestEntityPolicy extends AbstractEntityPolicy {

    private Restriction restriction;

    private long idCounter;

    @Override
    public Object preCreate(Entity record, Date now) throws UnifyException {
        Long id = Long.valueOf(++idCounter);
        ((AbstractTestTableEntity) record).setId(id);
        return id;
    }

    @Override
    public boolean isSetNow() {
        return false;
    }

    @Override
    public void preQuery(Query<? extends Entity> query) throws UnifyException {
        if (restriction != null) {
            query.addRestriction(restriction);
        }
    }

    public void clearRestriction() {
        restriction = null;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }
}
