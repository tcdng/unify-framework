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
import java.util.Collection;
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

    public AbstractCompoundRestriction() {
        restrictionList = new ArrayList<Restriction> ();
    }
    
    @Override
    public void writeRestrictedFields(Set<String> propertyBucket) {
        for (Restriction restriction : restrictionList) {
            restriction.writeRestrictedFields(propertyBucket);
        }
    }

    @Override
    public CompoundRestriction add(Restriction restriction) {
        restrictionList.add(restriction);
        return this;
    }

    @Override
    public List<Restriction> getRestrictionList() {
        return restrictionList;
    }

    @Override
    public boolean isRestrictedField(String fieldName) {
        for (Restriction restriction : restrictionList) {
            if (restriction.isRestrictedField(fieldName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return restrictionList.isEmpty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isValid() {
        if (restrictionList.isEmpty()) {
            return false;
        }

        for (Restriction restriction : restrictionList) {
            if (!restriction.isValid()) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public int size() {
        return restrictionList.size();
    }

    @Override
    public void clear() {
        restrictionList.clear();;
    }

    @Override
    public boolean replaceAll(String propertyName, Object val) {
        boolean replaced = false;
        for (Restriction restriction : restrictionList) {
            if (restriction.getConditionType().isSingleParam()) {
                ((SingleParamRestriction) restriction).setParam(val);
                replaced = true;
            } else if (restriction.getConditionType().isCompound()) {
                ((CompoundRestriction) restriction).replaceAll(propertyName, val);
            }
        }

        return replaced;
    }

    @Override
    public boolean replaceAll(String propertyName, Object val1, Object val2) {
        boolean replaced = false;
        for (Restriction restriction : restrictionList) {
            if (restriction.getConditionType().isRange()) {
                ((DoubleParamRestriction) restriction).setParams(val1, val2);
                replaced = true;
            } else if (restriction.getConditionType().isCompound()) {
                ((CompoundRestriction) restriction).replaceAll(propertyName, val1, val2);
            }
        }

        return replaced;
    }

    @Override
    public boolean replaceAll(String propertyName, Collection<Object> val) {
        boolean replaced = false;
        for (Restriction restriction : restrictionList) {
            if (restriction.getConditionType().isAmongst()) {
                ((MultipleParamRestriction) restriction).setParams(val);
                replaced = true;
            } else if (restriction.getConditionType().isCompound()) {
                ((CompoundRestriction) restriction).replaceAll(propertyName, val);
            }
        }

        return replaced;
    }
}
