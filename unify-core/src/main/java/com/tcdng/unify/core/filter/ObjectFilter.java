/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.core.filter;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.criterion.Restriction;
import com.tcdng.unify.core.data.ValueStoreReader;
import com.tcdng.unify.core.util.FilterUtils;

/**
 * Object filter class.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class ObjectFilter {

    private Restriction restriction;

    public ObjectFilter(Restriction restriction) {
        this.restriction = restriction;
    }

    public boolean matchReader(ValueStoreReader reader) throws UnifyException {
        return FilterUtils.getBeanFilterPolicy(restriction.getConditionType()).matchReader(reader,
                restriction);
    }

    public boolean matchObject(Object bean) throws UnifyException {
        return FilterUtils.getBeanFilterPolicy(restriction.getConditionType()).matchObject(bean,
                restriction);
    }
}
