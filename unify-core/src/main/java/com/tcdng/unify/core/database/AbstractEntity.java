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
package com.tcdng.unify.core.database;

import java.util.Collections;
import java.util.Set;

import com.tcdng.unify.core.annotation.Tooling;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient abstract base class for entity.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Tooling(name = "plainCommon", description = "Plain Common")
public abstract class AbstractEntity implements Entity {
    
    @Override
    public String getListKey() {
        return String.valueOf(getId());
    }

    @Override
    public String getListDescription() {
        return getDescription();
    }

    @Override
    public boolean isReserved() {
        return false;
    }

    @Override
    public boolean equals(Object object) {
        return ReflectUtils.beanEquals(this, object, ignore());
    }

    @Override
    public int hashCode() {
        return ReflectUtils.beanHashCode(this, ignore());
    }

    @Override
    public String toString() {
        return StringUtils.toXmlString(this);
    }

    protected Set<String> ignore() {
        return Collections.emptySet();
    }
}
