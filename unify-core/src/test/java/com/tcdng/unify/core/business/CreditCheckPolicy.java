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
package com.tcdng.unify.core.business;

import java.util.Date;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.database.AbstractEntityPolicy;

/**
 * Policy class for credit checks.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("creditcheck-policy")
public class CreditCheckPolicy extends AbstractEntityPolicy {

    @Override
    public Object preCreate(Entity record, Date now) throws UnifyException {
        return ((CreditCheck) record).getId() ;
    }

    @Override
    public void preUpdate(Entity record, Date now) throws UnifyException {
        
    }

    @Override
    public void preDelete(Entity record, Date now) throws UnifyException {
        
    }

    @Override
    public void onCreateError(Entity record) {
        
    }

    @Override
    public void onUpdateError(Entity record) {
        
    }

    @Override
    public void onDeleteError(Entity record) {
        
    }
}
