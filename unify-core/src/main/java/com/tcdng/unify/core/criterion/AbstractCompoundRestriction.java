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

package com.tcdng.unify.core.criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Convenient abstract base class for compound restrictions.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractCompoundRestriction extends AbstractRestriction implements CompoundRestriction {

    private List<Restriction> restrictionList;

    @Override
    public void writeRestrictedFields(Set<String> propertyBucket) {
        if (restrictionList != null) {
            for (Restriction restriction : restrictionList) {
                restriction.writeRestrictedFields(propertyBucket);
            }
        }
    }

    @Override
    public CompoundRestriction add(Restriction restriction) {
        if (restrictionList == null) {
            restrictionList = new ArrayList<Restriction>();
        }

        restrictionList.add(restriction);
        return this;
    }

    @Override
    public List<Restriction> getRestrictionList() {
        return restrictionList;
    }

    @Override
    public boolean isRestrictedField(String fieldName) {
        if (restrictionList != null) {
            for (Restriction restriction : restrictionList) {
                if (restriction.isRestrictedField(fieldName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return restrictionList == null || restrictionList.isEmpty();
    }

    @Override
    public int size() {
        if (restrictionList != null) {
            return restrictionList.size();
        }
        
        return 0;
    }

    @Override
    public void clear() {
        restrictionList = null;
    }
}
