/*
 * Copyright 2018-2022 The Code Department.
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

/**
 * Restriction for a property that does not begin with a field value.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class NotBeginWithField extends AbstractSingleParamRestriction {

    public NotBeginWithField(String propertyName, String fieldName) {
        super(propertyName, new RestrictionField(fieldName));
    }

    @Override
    public FilterConditionType getConditionType() {
        return FilterConditionType.NOT_BEGIN_WITH_FIELD;
    }

}
